package a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemPrices extends ArrayList<Item> {
	private String name;
	private int min2;
	private Item minItem;
	ItemPrices(String name,Item item) {
		this.name=name;
		minItem=item;
		min2=item.getPrice()+1;
		super.add(item);
	}
	public boolean add(Item item) {
		if(item.getPrice()<minItem.getPrice()) {
			min2=minItem.getPrice();
			minItem=item;
		}
		else if(min2>item.getPrice())
			min2=item.getPrice();
		return super.add(item);
	}
	void printFlips() {
		if(FlipChecker.testFlip(minItem.getPrice(), min2, size(),name)) 
			System.out.println(name+" "+minItem.getPrice()+", /viewauction "+minItem.getUuid());
	}
    public String getName() {
    	return name;
    }
    public int getMin2() {
    	return min2;
    }
    public String toString() {
    	String retVal="";
    	for(Item i:this) {
    		retVal+=i.getPrice()+",";
    	}
    	return retVal.substring(0,retVal.length()-1);
    }
}
