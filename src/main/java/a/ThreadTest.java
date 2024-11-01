package a;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

public class ThreadTest implements Runnable {
	int pageNum;
	String name;
	private Thread t;
	static ArrayList<String> nameAL=new ArrayList<String>();
	static ArrayList<Integer> priceAL=new ArrayList<Integer>();
	static ArrayList<String> idAL=new ArrayList<String>();
	ThreadTest(int i) {
		pageNum=i;
		name="thread"+pageNum;
	}
	public void run() {
		System.out.println("Thread "+pageNum+" running.");
		URL url;
		try {
			url = new URL("https://api.hypixel.net/skyblock/auctions?page="+pageNum);
	        URLConnection con = url.openConnection();
			InputStream inputStream=con.getInputStream();
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
					idAL.add(result.getString("tag.ExtraAttributes.id", ""));
					priceAL.add(o.get("starting_bid").getAsInt());
					nameAL.add(o.get("item_name").getAsString());
				}
				System.out.println("Finished");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IO Exception");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception");
			}
	        
	        
	        System.out.println("Thread "+pageNum+" done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		System.out.println("Thread "+pageNum+" starting. ");
        t = new Thread (this, name);
        t.start();
    }
	
	public Thread getThread() {
		return t;
	}
}
