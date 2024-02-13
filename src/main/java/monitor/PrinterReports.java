package monitor;

public class PrinterReports {
	private int monoPrints, monoCopies, colourPrints, colourCopies;
	private String month;
	
	public PrinterReports() {
		monoPrints = -1;
		monoCopies = -1;
		colourPrints = -1;
		colourCopies = -1;
		month ="";
	}

	public PrinterReports(String month, int monoPrints, int monoCopies, int colourPrints, int colourCopies) {
		this.month = month;
		this.monoPrints = monoPrints;
		this.monoCopies = monoCopies;
		this.colourPrints = colourPrints;
		this.colourCopies = colourCopies;
	}
	
	public PrinterReports(String month) {
		monoPrints = -1;
		monoCopies = -1;
		colourPrints = -1;
		colourCopies = -1;
		this.month = month;
	}
	//current?
	
	public void zeroReport() {
		monoPrints = -1;
		monoCopies = -1;
		colourPrints = -1;
		colourCopies = -1;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getMonth() {
		return month;
	}
	public int getMonoPrints() {
		return monoPrints;
	}

	public void setMonoPrints(int monoPrints) {
		this.monoPrints = monoPrints;
	}

	public int getMonoCopies() {
		return monoCopies;
	}

	public void setMonoCopies(int monoCopies) {
		this.monoCopies = monoCopies;
	}

	public int getColourPrints() {
		return colourPrints;
	}

	public void setColourPrints(int colourPrints) {
		this.colourPrints = colourPrints;
	}

	public int getColourCopies() {
		return colourCopies;
	}

	public void setColourCopies(int colourCopies) {
		this.colourCopies = colourCopies;
	}

}
