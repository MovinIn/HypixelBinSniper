package a;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CobbleGenerator {
	public static void main(String[]args) throws Exception {
		Robot r=new Robot();
		Thread.sleep(3000);
		r.keyPress(KeyEvent.VK_W);
		r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	}
}
