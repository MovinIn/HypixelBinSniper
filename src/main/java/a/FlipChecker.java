package a;

public abstract class FlipChecker {
	private static final int PRICECAP=200000000; //200 million
	static boolean testFlip(int min,int min2,int size,String name) {
		int profit=(min2-min)-(int)(min2*0.01);
		double equation=0.275*(min2/1000000)-4.5;
		return (profit/1000000)>(equation)&&
				size>10&&
				min2<PRICECAP&&
				profit>1000000&&
				!name.toLowerCase().contains("skin");
	}
}
