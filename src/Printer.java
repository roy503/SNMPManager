public class Printer {

private String address, name, serial, location;
private int toner, K1, K2, cyan, magenta, yellow, black;
private boolean offline = false;
	
public Printer(String address) {
	this.address = address;
}

public String getIP() {
	return address;
}

public void setName(String nam) {
	name = nam;
}

public String getName() {
	return name;
}

public void setSerial(String ser) {
	serial = ser;
}

public String getSerial() {
	return serial;
}

public void setToner(int ton) {
	toner = ton;
}

public int getToner() {
	return toner;
}

public void setLocation(String loc) {
	location = loc;
}

public String getLocation() {
	return location;
}

public void setOffline() {
	offline = true;
}

public void setK1(int ton) {
	K1 = ton;
}

public int getK1() {
	return K1;
}

public void setK2(int ton) {
	K2 = ton;
}

public int getK2() {
	return K2;
}

public void setCyan(int ton) {
	cyan = ton;
}

public int getCyan() {
	return cyan;
}

public void setMagenta(int ton) {
	magenta = ton;
}

public int getMagenta() {
	return magenta;
}

public void setYellow(int ton) {
	yellow = ton;
}

public int getYellow() {
	return yellow;
}

@Override
public String toString() {
	return String.format("%-30s %-16s %-70s %-20s %2d%%", this.getLocation(), this.getIP(), this.getName(), this.getSerial(), this.getToner());
}

}
