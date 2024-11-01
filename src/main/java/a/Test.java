package a;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;

import me.nullicorn.nedit.NBTReader;

public class Test {
	private static HashMap<String,ItemPrices> binItemsMap;
	private static Timer minRefresh;
	private static String timeRefreshed="s";
	public static void main(String[] args) {
		//Its working...
		binItemsMap=new HashMap<String,ItemPrices>();
        System.out.println("Hello! Type 'start' to feed me data from api.hypixel.net!");
        // Continuously read from the console
        Scanner sc = new Scanner(System.in);
        minRefresh=new Timer();
        while (sc.hasNextLine()) {
            // Get the line of text that was typed
            String line = sc.nextLine().replaceAll("\n", "");

            // Make sure some text was typed
            if (line.length() == 0) {
                continue;
            }
            // Quit if the input is "Q" or "q"
            if (line.equalsIgnoreCase("Q")) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            else if(line.equalsIgnoreCase("start")) {
            	whenToStart();
            }
            else if(line.equalsIgnoreCase("test")) {
            	for(int i=0; i<20; i++) {
            		try {
						getData(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    System.out.println("Read page and data");
            	}
            }
            else if(binItemsMap.size()!=0) {
            	boolean foundMatch=false;
            	ArrayList<Item> prices;
            	for(String s:binItemsMap.keySet()) {
            		if(s.toLowerCase().contains(line.toLowerCase())) {
            			foundMatch=true;
            			System.out.print(s);
            			prices=binItemsMap.get(s);
            			Collections.sort(prices,new SortByPrice());
            			System.out.println(" "+prices);
            		}
            	}
            	if(!foundMatch) {
            		System.out.println("No matches found.");
            	}
            }
            else {
            	System.out.println("Feed me data. Type 'start' to grab data from api.hypixel.net");
            }
        }


    }
	private static Scanner getData(int pageNum) throws Exception {
		URL url = new URL("https://api.hypixel.net/skyblock/auctions?page="+pageNum);
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        return new Scanner(is).useDelimiter("},");
	}
	private static void findFlips() {
    	binItemsMap.clear();
        String data=new String();
        int price,nameIndex,startBidIndex,itemName,uuidIndex;
        String base64Str,name,uuid;
        Item item;
        ItemPrices priceAL;
		try {
			for(int i=0;i<75;i++) { //assuming skyblock doesn't have 75 pages
				System.out.print("Reading Page "+i+"... ");
				Scanner s=getData(i);
				while(s.hasNext()) {
					data=s.next();
					if(data.indexOf("\"bin\":true")!=-1) {
						startBidIndex=data.indexOf("starting_bid")+14;
						price=Integer.parseInt(data.substring(startBidIndex,data.indexOf(',', startBidIndex)));
						base64Str=data.substring(data.indexOf("item_bytes\":\"")+13,data.indexOf("\"",data.indexOf("item_bytes\":\"")+13));
						if(base64Str.indexOf("\\")!=-1) {
							base64Str=base64Str.substring(0,base64Str.indexOf("\\"));
						}
						name=NBTReader.readBase64(base64Str).toString();
						nameIndex=name.indexOf("id:\"")+4;
						uuidIndex=data.indexOf("uuid")+7;
						uuid=data.substring(uuidIndex,data.indexOf("\"",uuidIndex));
						if(nameIndex==name.indexOf("uuid")+6) {
							nameIndex=name.lastIndexOf("id:\"")+4;
						}
						name=name.substring(nameIndex,name.indexOf("\"",nameIndex));
						priceAL=binItemsMap.get(name);
						item=new Item(price,uuid);
						if(priceAL==null) {
							itemName=data.indexOf("item_name")+12;
							priceAL=new ItemPrices(data.substring(itemName,data.indexOf("\"",itemName)),item);
							binItemsMap.put(name, priceAL);
						}
						else {
							priceAL.add(item);
						}
					}
				}
				System.out.println("Done.");
			}
		} catch (Exception e) {
			System.out.println("Failed, all auctions grabbed");
			for(ItemPrices i:binItemsMap.values()) {
				i.printFlips();
			}
		}
		whenToStart();
	}
	private static void whenToStart() {
		try {
			Scanner s = getData(0);
			String lastRefreshed=timeRefreshed;
			String str;
			int updatedIndex;
			while(lastRefreshed.equals(timeRefreshed)) {
				s = getData(0);
				str=s.next();
				updatedIndex=str.indexOf("lastUpdated")+13;
				lastRefreshed=str.substring(updatedIndex,str.indexOf(",",updatedIndex));
			}
			timeRefreshed=lastRefreshed;
			findFlips();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
