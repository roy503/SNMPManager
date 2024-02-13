package monitor;

public class Start {

	public static void main(String[] args) {
		new Thread(new Monitor()).run();
	}
}
