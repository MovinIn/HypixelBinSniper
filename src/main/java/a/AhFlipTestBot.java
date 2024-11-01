package a;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import me.nullicorn.nedit.NBTInputStream;
import me.nullicorn.nedit.NBTOutputStream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;

public class AhFlipTestBot {
	private static HashMap<String,ItemPrices> binItemsMap;
	private static String timeRefreshed="s";
	public static void main(String[]args) throws Exception {

		//Its working...
		binItemsMap=new HashMap<String,ItemPrices>();
        System.out.println("Hello! Type 'start' to feed me data from api.hypixel.net!");
        // Continuously read from the console
        Scanner sc = new Scanner(System.in);
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
            	//JSONObject x= new JSONObject("{id:397s,Count:1b,tag:{HideFlags:254,SkullOwner:{Id:\"7895e21a-8f3b-3e30-bea6-06108f64d5dc\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWIxMjY4MTRmYzNmYTg0NmRhZDkzNGMzNDk2MjhhN2ExZGU1YjQxNTAyMWEwM2VmNDIxMWQ2MjUxNGQ1In19fQ==\"}]}},display:{Lore:[\"§8Fishing Pet\",\"\",\"§7Strength: §c+40\",\"§7Intelligence: §a+75\",\"\",\"§6Cold Breeze\",\"§7§7Gives §a50 §c? Strength §7and\",\"§7§9? Crit Damage §7when near\",\"§7snow.\",\"\",\"§6Ice Shields\",\"§7§7Gain §a50% §7of your strength\",\"§7as §a? Defense.\",\"\",\"§6Held Item: §9Dwarf Turtle Shelmet\",\"§7§7Makes the pet's owner immune\",\"§7to knockback.\",\"\",\"§a(1/10) Pet Candy Used\",\"\",\"§b§lMAX LEVEL\",\"\",\"§7§eRight-click to add this pet to\",\"§eyour pet menu!\",\"\",\"§5§lEPIC\"],Name:\"§7[Lvl 100] §5Baby Yeti\"},ExtraAttributes:{petInfo:\"{\"type\":\"BABY_YETI\",\"active\":false,\"exp\":2.1559786810147103E7,\"tier\":\"EPIC\",\"hideInfo\":false,\"heldItem\":\"DWARF_TURTLE_SHELMET\",\"candyUsed\":1,\"uuid\":\"898249fe-3114-42f7-91fe-7f6203b8298a\",\"hideRightClick\":false}\",id:\"PET\",uuid:\"898249fe-3114-42f7-91fe-7f6203b8298a\",timestamp:\"12/3/22 8:37 PM\"}},Damage:3s}");
            	//JSONObject x = new JSONObject("{id:397s,Count:1b,tag:{HideFlags:254,SkullOwner:{Id:\"0c5a571d-691c-3362-a481-b8cfe9df8019\",Properties:{textures:[{Value:\"ewogICJ0aW1lc3RhbXAiIDogMTYxMDM1ODYzNjY0NSwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiOTE5ZjA0MDkyMmViMTc3MzNiNDJkMjE2YjdjZGVmZWFlYTM2NmJlMTRjMWFlNjk5M2NhN2U1OTA5ZTBmMCIKICAgIH0KICB9Cn0=\"}]}},display:{Lore:[\"§7Intelligence: §a-2\",\"\",\"§7Wear to prove you love Jerry, or\",\"§7wear to prove you hate Jerry.\",\"\",\"§eRight-click to view recipes!\",\"\",\"§9§lRARE ACCESSORY\"],Name:\"§9Blue Jerry Talisman\"},ExtraAttributes:{id:\"JERRY_TALISMAN_BLUE\",uuid:\"3075b825-7d56-4bec-a5f5-5a56eaad5755\",timestamp:\"8/30/22 10:26 AM\"}},Damage:3s}");
            	//System.out.println(x.toString(4));
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
        int startBidIndex;
        String base64Str;
		try {
			for(int i=0;i<75;i++) { //assuming skyblock doesn't have 75 pages
				System.out.print("Reading Page "+i+"... ");
				Scanner s=getData(i);
				int x=0;
				while(s.hasNext()) {
					data=s.next();
					if(data.indexOf("\"bin\":true")!=-1) {
						startBidIndex=data.indexOf("starting_bid")+14;
						int price=Integer.parseInt(data.substring(startBidIndex,data.indexOf(',', startBidIndex)));
						base64Str=data.substring(data.indexOf("item_bytes\":\"")+13,data.indexOf("\"",data.indexOf("item_bytes\":\"")+13));
						if(base64Str.indexOf("\\")!=-1) {
							base64Str=base64Str.substring(0,base64Str.indexOf("\\"));
						}
						NBTCompound result = NBTReader.readBase64(base64Str).getList(".i").getCompound(0);
						String n = result.getString("tag.display.Name", "");
						String id = result.getString("tag.ExtraAttributes.id", "");
						if(n.equals("")||id.equals("")) {
							System.out.println("failed - ");
							System.out.print(result.toString());
							System.exit(0);
						}
					}
					else if(data.indexOf("\"bin\":false")!=-1) {
						startBidIndex=data.indexOf("starting_bid")+14;
						int price=Integer.parseInt(data.substring(startBidIndex,data.indexOf(',', startBidIndex)));
						base64Str=data.substring(data.indexOf("item_bytes\":\"")+13,data.indexOf("\"",data.indexOf("item_bytes\":\"")+13));
						if(base64Str.indexOf("\\")!=-1) {
							base64Str=base64Str.substring(0,base64Str.indexOf("\\"));
						}
						NBTCompound result = NBTReader.readBase64(base64Str).getList(".i").getCompound(0);
						String n = result.getString("tag.display.Name", "");
						String id = result.getString("tag.ExtraAttributes.id", "");
						if(n.equals("")||id.equals("")) {
							System.out.println("failed - ");
							System.out.print(result.toString());
							System.exit(0);
						}
						String auctions=data.substring(data.indexOf("\"bids\":")+7);
						auctions=auctions.substring(0,auctions.indexOf("]"));
						int lastIndex = auctions.lastIndexOf("amount");
						if(lastIndex!=-1) {
							price = Integer.parseInt(auctions.substring(auctions.lastIndexOf("amount")+8,auctions.lastIndexOf(",")));
						}
					}
				}
				System.out.println("Done.");
			}
		} catch (Exception e) {
			System.out.println("Failed, all auctions grabbed");
			e.printStackTrace();
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
