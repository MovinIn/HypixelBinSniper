package a;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

public class AaTest {
	
	public static void main(String[]args) throws InterruptedException,ExecutionException,IOException {
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
        		List<String> list = new ArrayList<String>();
        		long millis=System.currentTimeMillis();
        		for(int i=1; i<50; i++) {
        			list.add("https://api.hypixel.net/skyblock/auctions?page="+i);
        		}
        		getData(list);
        		System.out.println("Time Taken: "+(System.currentTimeMillis()-millis));
            }
            else if(line.equalsIgnoreCase("test")) {

            	try {
            		for(int i=1; i<50; i++) {
                    	URL u=new URL("https://api.hypixel.net/skyblock/auctions?page="+i);
                    	URLConnection c=u.openConnection();
    					tokenTest(c.getInputStream());
            		}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
		/*
    	URL url=new URL("https://api.hypixel.net/skyblock/auctions?page=1");
    	URLConnection c = url.openConnection();
    	InputStream i = c.getInputStream();
    	try{
    		tokenProcessor(i);
    	}
    	catch(Exception e) {
    		
    	}*/
	}
	public static void tokenProcessor(InputStream i) throws Exception {
		JsonFactory j=new JsonFactory();
		System.out.println("Start time");
		long millis=System.currentTimeMillis();
		JsonParser x=j.createParser(i);
		System.out.println((millis-System.currentTimeMillis()));
		while(x.nextToken()!=null) {
			String s=x.getValueAsString();
			if(s!=null&&s.equals("starting_bid")) {
				System.out.println("?");
				x.nextToken();
				System.out.println("starting_bid: "+x.getValueAsInt());
				x.nextToken();x.nextToken(); //skip to item bytes
				System.out.println("item_bytes: "+x.getValueAsString());
				x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();
				System.out.println("highest_bid_amount: "+x.getValueAsInt());
				x.nextToken();x.nextToken();x.nextToken();x.nextToken();
				System.out.println("bin: "+x.getValueAsBoolean());
			}
		}
	}
	
	public static void tokenTest(InputStream i) throws Exception {
		JsonFactory j=new JsonFactory();
		long millis=System.currentTimeMillis();
		JsonParser x=j.createParser(i);
		int c=0;
		int lowest=-1;
		while(x.nextToken()!=null) {
			String s=x.getValueAsString();
			if(s!=null&&s.equals("bin")) {
				x.nextToken();
				String r=x.getValueAsString();
				while(!r.equals("starting_bid")) {
					c++;
					if(x.nextToken()==null) break;
					r=x.getValueAsString();
					if(r==null) {
						r="";
					}
				}
				if(lowest==-1) {
					lowest=Integer.MAX_VALUE;
				}
				else {
					System.out.println(c);
					lowest=Math.min(lowest, c);
				}
				c=0;
			}
		}
		System.out.println(lowest);
	}
	
	
	    /**
	 * Gets an InputStream to MP3Data for the returned information from a request
	 * @param synthText List of Strings you want to be synthesized into MP3 data
	 * @return Returns an input stream of all the MP3 data that is returned from Google
	 * @throws IOException Throws exception if it cannot complete the request
	 */
	public static List<InputStream> getData(List<String> synthText) throws IOException, ExecutionException, InterruptedException {
	    //Uses an executor service pool for concurrency
	    ExecutorService pool = Executors.newFixedThreadPool(synthText.size());
	    //Stores the Future (Data that will be returned in the future)
	    Set<Future<InputStream>> set = new LinkedHashSet<Future<InputStream>>();
	    //Iterates through the list
	    for(String part: synthText){
	    	System.out.println(part);
	        Callable<InputStream> callable = new URLFetcher(part);//Creates Callable
	        Future<InputStream> future = pool.submit(callable);//Runs the Callable
	        set.add(future);//Adds the response that will be returned to a set.
	    }
	    List<InputStream> inputStreams = new ArrayList<InputStream>(set.size());
	    for(Future<InputStream> future: set){
	        inputStreams.add(future.get());//Gets the response that will be returned, returned.
	    }
	    return inputStreams;
	}
	
	     /**
	 * This class is a callable.
	 * A callable is like a runnable except that it can return data and throw exceptions.
	 * Useful when using futures. 
	 * @author Skylion
	 *
	 */
	private static class URLFetcher implements Callable<InputStream>{
	    private String synthText;
	    private static ArrayList<Long> hBidAL,sBidAL;
	    private static ArrayList<String> idAL;
	    private static ArrayList<Boolean> bAL;
	    public URLFetcher(String synthText){
	        this.synthText = synthText;
	        hBidAL=new ArrayList<Long>();
	        sBidAL=new ArrayList<Long>();
	        idAL=new ArrayList<String>();
	        bAL=new ArrayList<Boolean>();
	    }
	
	    public InputStream call() throws Exception{
	    	URL url=new URL(synthText);
	    	URLConnection c = url.openConnection();
	    	InputStream i = c.getInputStream();
			JsonFactory j=new JsonFactory();
			JsonParser x=j.createParser(i);
			//skip the header
			x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();
			while(x.nextToken()!=null) {
				String s=x.getValueAsString();
				if(s!=null&&s.equals("starting_bid")) {
					x.nextToken();
					x.getValueAsLong();
					x.nextToken();x.nextToken(); //skip to item bytes
					NBTCompound result = NBTReader.readBase64(x.getValueAsString())
							.getList(".i").getCompound(0);
					result.getString("tag.ExtraAttributes.id", "Check For This");
					x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();
					x.getValueAsLong();
					x.nextToken();x.nextToken();x.nextToken();x.nextToken();
					x.getValueAsBoolean();
					x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();x.nextToken();
				}
			}
	        return i;
	    }
	}
	/*
	 * 			com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
			JsonObject jsonObject = (JsonObject)jsonParser.parse(
			      new InputStreamReader(i, "UTF-8"));
			JsonArray auctions = jsonObject.get("auctions").getAsJsonArray();
			System.out.println(auctions.size());
			for(JsonElement e: auctions.asList()) {
				JsonObject o = e.getAsJsonObject();
				NBTCompound result = NBTReader.readBase64(o.get("item_bytes").getAsString())
						.getList(".i").getCompound(0);
				o.get("starting_bid").getAsInt();
				o.get("item_name").getAsString();
			}
			
	 * 
	 * 
	 * 
	 * */
}
