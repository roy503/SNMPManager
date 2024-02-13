package monitor;

import java.time.Month;

public class Printer {
	
	private static int total = 0;
	private String address, name, serial, location;
	private int K1, K2, cyan, magenta, yellow, black, monoPrints, colourPrints, monoCopies, colourCopies;
	private boolean offline = false, colour = false, kprinter = false, labelprinter = false, scanner = false;
	private PrinterReports[] reports = new PrinterReports[12];
	
	public Printer() {
		for (int i = 0; i < 12; i++) {
			reports[i] = new PrinterReports();
		}
	}
	
	public Printer(String address) {
		this.address = address;
		name = "";
		serial = "";
		location = "";
		K1 = -1;
		K2 = -1;
		cyan = -1;
		magenta = -1;
		yellow = -1;
		black = -1;
		monoPrints = -1;
		monoCopies = -1;
		colourPrints = -1;
		colourCopies = -1;
		total++;
		for (int i = 0; i < 12; i++) {
			Month month = Month.of(i+1);
			reports[i] = new PrinterReports(month.toString());
		}
	}
	
	public void setMonthReport(int month) {
		reports[month].setMonoPrints(monoPrints);
		reports[month].setMonoCopies(monoCopies);
		reports[month].setColourPrints(colourPrints);
		reports[month].setColourCopies(colourCopies);
	}
	
	public void zeroReports() {
		for (int i = 0; i < 12; i++) {
			reports[i].zeroReport();
		}
	}
	
	public PrinterReports getReport(int month) {
		return reports[month];
	}
	public void setReport(int month, PrinterReports report) {
		reports[month] = report;
	}
	public void setReports(int month,String monthName, int monoPrints, int monoCopies, int colourPrints, int colourCopies) {
		reports[month] = new PrinterReports(monthName.toString(),monoPrints,monoCopies,colourPrints,colourCopies);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setReports(PrinterReports[] rep) {
		reports = rep;
	}
	public PrinterReports[] getReports(){
		return reports;
	}
	public void setScanner() {
		scanner = true;
	}
	
	public boolean getScanner() {
		return scanner;
	}
	
	public int getTotal() {
		return total;
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

	public void setBlack(int ton) {
		black = ton;
	}

	public int getBlack() {
		return black;
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public String getLocation() {
		if(offline) {
			return "Offline";
		}
		return location;
	}
	
	public void setMonoPrints(int prs) {
		monoPrints = prs;
	}
	
	public int getMonoPrints() {
		return monoPrints;
	}
	
	public void setColourPrints(int prs) {
		colourPrints = prs;
	}
	
	public int getColourPrints() {
		return colourPrints;
	}
	
	public void setMonoCopies(int cps) {
		monoCopies = cps;
	}
	
	public int getMonoCopies() {
		return monoCopies;
	}
	
	public void setColourCopies(int cps) {
		colourCopies = cps;
	}
	
	public int getColourCopies() {
		return colourCopies;
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

	public void setOffline() {
		offline = true;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setColour() {
		colour = true;
	}

	public boolean isColour() {
		return colour;
	}

	public boolean isNotColour() {
		return !colour;
	}
	public void setkprinter() {
		kprinter = true;
	}

	public boolean iskprinter() {
		return kprinter;
	}

	public void setLabelPrinter() {
		labelprinter = true;
	}

	public boolean isLabelPrinter() {
		return labelprinter;
	}
}
