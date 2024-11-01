package a;

import java.util.Comparator;

public class SortByPrice implements Comparator<Item>{

	public int compare(Item item1, Item item2) {
		return item1.getPrice().compareTo(item2.getPrice());
	}

}
