package a;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

public class SBAuctionsTest {
	private static int x;
	private static ArrayList<String> nameAL=new ArrayList<String>();
	private static ArrayList<Integer> priceAL=new ArrayList<Integer>();
	private static ArrayList<String> idAL=new ArrayList<String>();
	public static void main(String[]args) throws Exception {
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
            	start();
            }
            else if(line.equalsIgnoreCase("test")) {
            	test();
            }
            else if(line.equalsIgnoreCase("test2")) {
            	test2();
            }
            else if(line.equalsIgnoreCase("inputDelay")) {
            	inputStreamMSTest();
            }
            else if(line.equalsIgnoreCase("inputDelay2")) {
            	inputStreamMSTest2();
            }
            else {
            	System.out.println("Feed me data. Type 'start' to grab data from api.hypixel.net");
            }
        }
	}
	
	private static void start() {
		try {
			InputStream inputStream = getData(1);
			System.out.println("got input stream");
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject)jsonParser.parse(
			      new InputStreamReader(inputStream, "UTF-8"));
			JsonArray auctions = jsonObject.get("auctions").getAsJsonArray();
			System.out.println(auctions.size());
			for(JsonElement e: auctions.asList()) {
				JsonObject o = e.getAsJsonObject();
				NBTCompound result = NBTReader.readBase64(o.get("item_bytes").getAsString())
						.getList(".i").getCompound(0);
				priceAL.add(o.get("starting_bid").getAsInt());
				nameAL.add(o.get("item_name").getAsString());
				System.out.println(result);
				System.out.println(result.getCompound("tag"));
				System.out.println(result.getCompound("tag.ExtraAttributes"));
				System.out.println(result.getString("tag.ExtraAttributes.id", "failed to get string"));
				idAL.add(result.getString("tag.ExtraAttributes.id", ""));
				System.out.println(result.getString("tab.ExtraAttributes.id", ""));
			}
			System.out.println("Finished");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Exception");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception");
		}
	}
	
	private static void test() throws Exception {
		long millis = System.currentTimeMillis();
		ArrayList<ThreadTest> t=new ArrayList<ThreadTest>();
		for(int i=1; i<50; i++) {
			ThreadTest test = new ThreadTest(i);
			test.start();
			t.add(test);
		}
		
		for(ThreadTest threadTest:t) {
			threadTest.getThread().join();
		}
		ThreadTest.idAL.subList(0, 100).forEach(System.out::println);
		ThreadTest.nameAL.subList(0, 100).forEach(System.out::println);
		ThreadTest.priceAL.subList(0, 100).forEach(System.out::println);
		System.out.println("Time taken: "+(System.currentTimeMillis()-millis));
	}
	
	private static synchronized InputStream testSync() throws IOException {
		URL url;
		x++;
		System.out.println("Thread "+x+" running.");
		url = new URL("https://api.hypixel.net/skyblock/auctions?page="+x);
        URLConnection con = url.openConnection();
		return con.getInputStream();
	}
	
	
	private static void test2() throws Exception {
		ExecutorService es = Executors.newCachedThreadPool();
		long initMillis=System.currentTimeMillis();
		for(x=0; x<52;) {
			es.execute(new Runnable() {
				public void run() {
					URL url;
					try {
						InputStream inputStream = testSync();
				        Thread.sleep(50);
				        
						try {
							JsonParser jsonParser = new JsonParser();
							JsonObject jsonObject = (JsonObject)jsonParser.parse(
							      new InputStreamReader(inputStream, "UTF-8"));
							JsonArray auctions = jsonObject.get("auctions").getAsJsonArray();
							System.out.println(auctions.size());
							for(JsonElement e: auctions.asList()) {
								JsonObject o = e.getAsJsonObject();
								NBTCompound result = NBTReader.readBase64(o.get("item_bytes").getAsString())
										.getList(".i").getCompound(0);
								o.get("starting_bid").getAsInt();
								o.get("item_name").getAsString();
							}
							System.out.println("Finished");
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("IO Exception");
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Exception");
						}
				        
				        
				        System.out.println("Thread "+x+" done.");
					} catch (IOException e) {
						e.printStackTrace();
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			System.out.println("outside");
		}
		es.shutdown();
		while(!es.awaitTermination(1, TimeUnit.MINUTES)) {
			Thread.sleep(100);
		}
		String finalTime="Finished: "+(System.currentTimeMillis()-initMillis)+" ms to complete.";
		System.out.println(finalTime);
	}
	
	
	private static InputStream x(int pageNum) throws Exception {
		URL url = new URL("https://api.hypixel.net/skyblock/auctions?page="+pageNum);
        URLConnection con = url.openConnection();
       return con.getInputStream();
	}
	
	public static void inputStreamMSTest() throws IOException {
		URL url = new URL("https://api.hypixel.net/skyblock/auctions?page="+1);
		long millis=System.currentTimeMillis();
        URLConnection con = url.openConnection();
		InputStream inputStream=con.getInputStream();
		System.out.println((System.currentTimeMillis()-millis));
	}
	
	public static void inputStreamMSTest2() throws IOException {
		long millis=System.currentTimeMillis();
		for(int i=1; i<30; i++) {
			URL url = new URL("https://api.hypixel.net/skyblock/auctions?page="+i);
	        URLConnection con = url.openConnection();
			InputStream inputStream=con.getInputStream();
		}
		System.out.println((System.currentTimeMillis()-millis));
	}

	public static void inputStreamMSTest3() throws IOException,InterruptedException {

		long millis = System.currentTimeMillis();
		ArrayList<ThreadTest> t=new ArrayList<ThreadTest>();
		for(int i=1; i<50; i++) {
			ThreadTest test = new ThreadTest(i);
			test.start();
			t.add(test);
		}
		
		for(ThreadTest threadTest:t) {
			threadTest.getThread().join();
		}
		System.out.println("Time taken: "+(System.currentTimeMillis()-millis));
	
	}
	
	private static InputStream getData(int pageNum) throws Exception {
		URL url = new URL("https://api.hypixel.net/skyblock/auctions?page="+pageNum);
        URLConnection con = url.openConnection();
       return con.getInputStream();
	}
	
}
