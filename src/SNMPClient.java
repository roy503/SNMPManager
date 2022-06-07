
public class SNMPClient {
	public static void main(String[] args) {
		new Thread(new SNMPManager()).start();
	}
}
