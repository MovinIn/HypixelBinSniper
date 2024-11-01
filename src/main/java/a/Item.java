package a;

public class Item {
	private Integer price;
	private String uuid;
	Item(Integer price, String uuid) {
		this.price=price;
		this.uuid=uuid;
	}
	public Integer getPrice() {
		return price;
	}
	public String getUuid() {
		return uuid;
	}
	
}
