package monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class Monitor implements Runnable{
	
	private Snmp snmp;
	private String printserver = "8585DIP000SF002";
	private ArrayList<Printer> printers;
	
	public Monitor() {
		snmp = null;
		printers = new ArrayList<Printer>();
	}

	@Override
	public void run() {	
		loopGetPrinters();
		start();
		sendMessages();
		createReports();
		System.exit(0);
	}
	
	private void toJSON(String type) {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(Printer.class, new PrinterSerializer());
		mapper.registerModule(module);
		File file = null;
		try {
			int year = LocalDate.now().getYear();
			if(type.equals("NewYear")) {
				String path = Monitor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				File jarFile = new File(path);
				String jarDir = jarFile.getParentFile().getAbsolutePath();
				file = new File(jarDir+"\\reports\\"+year+".json");
			}
			else if (type.equals("SameYear")) {
				if(LocalDate.now().getMonthValue() == 1 && LocalDate.now().getDayOfMonth() == 1) {
					year--;
				}
				String path = Monitor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				File jarFile = new File(path);
				String jarDir = jarFile.getParentFile().getAbsolutePath();
				file = new File(jarDir+"\\reports\\"+year+".json");
			}
			file.getParentFile().mkdirs();
			mapper.writeValue(file,printers);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Create JSON reports
	private void createReports() {
		String  lastReportDate = "0";
		File[] reportList = sortFiles();	
		if(reportList.length > 0) {
			// If reports exists
			lastReportDate = reportList[0].getName();
			lastReportDate = lastReportDate.substring(0,lastReportDate.length()-5);
		}
		int month = 0;
		if(LocalDate.now().getYear() == Integer.parseInt(lastReportDate)){
			// same year
			month = LocalDate.now().getMonth().getValue()-2;
			List<Printer> lastReport = mapJSON(reportList[0]);
			for (int i = 0; i < printers.size(); i++) {
				//currentYearReport.get(i).setReports(month, printers.get(i).getMonoPrints(), printers.get(i).getMonoCopies(), printers.get(i).getColourPrints(), printers.get(i).getColourCopies());;
				if(LocalDate.now().getDayOfMonth() == 1) {
					// same year but first of month
					lastReport.get(i).setReport(month,printers.get(i).getReport(month));
				}
				printers.get(i).setReports(lastReport.get(i).getReports());
			}
			toJSON("SameYear");
		}
		else {
			//new year
			if(LocalDate.now().getMonthValue() == 1 && LocalDate.now().getDayOfMonth() == 1) {
				// Jan 1st
				month = 11;
				List<Printer> currentYearReport = mapJSON(reportList[0]);
				for (int i = 0; i < printers.size(); i++) {
					//currentYearReport.get(i).setReports(month, printers.get(i).getMonoPrints(), printers.get(i).getMonoCopies(), printers.get(i).getColourPrints(), printers.get(i).getColourCopies());;
					currentYearReport.get(i).setReport(month,printers.get(i).getReport(month));
					printers.get(i).setReports(currentYearReport.get(i).getReports());
				}
				toJSON("Monthly");
				//create 2024 report aswell
				for (int i = 0; i < printers.size(); i++) {
					printers.get(i).zeroReports();
				}
				toJSON("NewYear");
			}
			else {
				// No files found, make new report
				toJSON("NewYear");
			}
		}
	}
	
	// Searches directory for reports
	private File[] sortFiles() {
		URL url = Monitor.class.getProtectionDomain().getCodeSource().getLocation();
		File f = null;
		try {
			
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f = f.getParentFile();
		f = new File(f.getPath()+"/reports");
		File[] fileList = new File[0];
		if(f.isDirectory()) {
			fileList = f.listFiles();
			if(fileList.length > 0) {
				Arrays.sort(fileList, Collections.reverseOrder());
			}
		}
		return fileList;
	}
	
	// Maps JSON file to ArrayList<Printer>
	private List<Printer> mapJSON(File file) {
		List<Printer> list = new ArrayList<Printer>();

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Printer.class, new PrinterDeserializer());
		mapper.registerModule(module);
		try {
			list = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Printer.class));
		} catch (StreamReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	// Loops over each printer and calls getAsString()
	private void sendMessages() {
		for(int i = 0;i < printers.size();i++) { 
			getAsString(printers.get(i));
			//progressBar = progressBar.substring(0, i+1) +'=' + progressBar.substring(i+2);
			//System.out.print(progressBar+"\r");
		}
		try { 
			snmp.close();
		} catch (IOException e) { 
			// Error
			System.out.println("Error trying to close() snmp"); 
			System.exit(1); 
		}
	}
	
	// Send SNMP message to printer
	private void getAsString(Printer obj){
		//These are OID's that work for the FUJI printers on my network
		ResponseEvent<?> event;
		event = getV2C(new OID[] {new OID(".1.3.6.1.2.1.1.5.0"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.1"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.1"),
				new OID(".1.3.6.1.2.1.43.11.1.1.9.1.2"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.2"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.3"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.3"),
				new OID(".1.3.6.1.2.1.43.11.1.1.9.1.4"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.4"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.30"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.30"),
				new OID(".1.3.6.1.2.1.43.11.1.1.9.1.31"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.31"),new OID(".1.3.6.1.2.1.1.6.0"),new OID(".1.3.6.1.2.1.43.5.1.1.17.1"), new OID(".1.3.6.1.2.1.43.12.1.1.4.1.2"),
				new OID(".1.3.6.1.4.1.253.8.53.13.2.1.6.1.20.7"),new OID(".1.3.6.1.4.1.253.8.53.13.2.1.6.1.20.29"),new OID(".1.3.6.1.4.1.253.8.53.13.2.1.6.103.20.3"),new OID(".1.3.6.1.4.1.253.8.53.13.2.1.6.103.20.25"),
				new OID(".1.3.6.1.4.1.297.1.111.1.41.1.1.2.2"),new OID(".1.3.6.1.4.1.297.1.111.1.41.1.1.2.1")}, obj);
		// 0 name, 1 black curr, 2 black max, 3 yellow curr, 4 yellow max, 5 magenta curr, 6 magenta max, 7 cyan curr, 8 cyan max, 
		// 9 k1 curr, 10 k1 max, 11 k2 curr , 12 k2 max, 13 location, 14 serial, 15 "yellow", 
		// 16 blk prints, 17 clr prints, 18 black copies, 19 clr copies, 20 lib/art student blkPrints, 21 lib/art student clrPrints  

		if(event.getResponse() != null) {
			// Set name
			obj.setName(event.getResponse().get(0).getVariable().toString());
			// Set black
			if(!event.getResponse().get(1).getVariable().isException() && !event.getResponse().get(2).getVariable().isException()){
				obj.setBlack(Math.round(Float.parseFloat(event.getResponse().get(1).getVariable().toString())/Float.parseFloat(event.getResponse().get(2).getVariable().toString())*100));
			}
			// No black
			else {
				
			}
			// Set colour printer
			if(event.getResponse().get(15).getVariable().toString().equals("yellow")) {
				//if colour printer
				obj.setColour();
				obj.setYellow(Math.round(Float.parseFloat(event.getResponse().get(3).getVariable().toString())/Float.parseFloat(event.getResponse().get(4).getVariable().toString())*100));
				obj.setMagenta(Math.round(Float.parseFloat(event.getResponse().get(5).getVariable().toString())/Float.parseFloat(event.getResponse().get(6).getVariable().toString())*100));
				obj.setCyan(Math.round(Float.parseFloat(event.getResponse().get(7).getVariable().toString())/Float.parseFloat(event.getResponse().get(8).getVariable().toString())*100));
				if(!event.getResponse().get(17).getVariable().isException()){
					obj.setColourPrints(Integer.parseInt(event.getResponse().get(17).getVariable().toString()));
				}
				else if(!event.getResponse().get(21).getVariable().isException()){
					obj.setColourPrints(Integer.parseInt(event.getResponse().get(21).getVariable().toString()));
				}
				if(!event.getResponse().get(19).getVariable().isException()){
					obj.setColourCopies(Integer.parseInt(event.getResponse().get(19).getVariable().toString()));
					obj.setScanner();
				}	
			}
			// Set K1
			if(!event.getResponse().get(9).getVariable().isException() && !event.getResponse().get(10).getVariable().isException()){
				float tonerK1 = Float.parseFloat(event.getResponse().get(9).getVariable().toString())/Float.parseFloat(event.getResponse().get(10).getVariable().toString());
				obj.setK1(Math.round(tonerK1*100));
				obj.setkprinter();
			}
			// Set K2
			if(!event.getResponse().get(11).getVariable().isException() && !event.getResponse().get(12).getVariable().isException()){
				float tonerK2 = Float.parseFloat(event.getResponse().get(11).getVariable().toString())/Float.parseFloat(event.getResponse().get(12).getVariable().toString());
				obj.setK2(Math.round(tonerK2*100));
			}
			// Label printers don't have serial OID's so just leave as is
			if(("10.214.192.215").equals(obj.getAddress()) || ("10.214.192.250").equals(obj.getAddress())) {
				obj.setLabelPrinter();
			}
			// Set Serial + Prints
			else
			{
				String serial = event.getResponse().get(14).getVariable().toString();
				obj.setSerial(serial.substring(serial.length() - 6));
				if(!event.getResponse().get(16).getVariable().isException()){
					obj.setMonoPrints(Integer.parseInt(event.getResponse().get(16).getVariable().toString()));
				}
				else if(!event.getResponse().get(20).getVariable().isException()){
					obj.setMonoPrints(Integer.parseInt(event.getResponse().get(20).getVariable().toString()));
				}
				if(!event.getResponse().get(18).getVariable().isException()){
					obj.setMonoCopies(Integer.parseInt(event.getResponse().get(18).getVariable().toString()));
					obj.setScanner();
				}	
				// Set non scanner
			}
			// Set Location
			obj.setLocation(event.getResponse().get(13).getVariable().toString());
			if(LocalDate.now().getMonthValue() == 1 && LocalDate.now().getDayOfMonth() == 1) {
				obj.setMonthReport(11);
			}
			else if (LocalDate.now().getDayOfMonth() == 1) {
				obj.setMonthReport(LocalDate.now().getMonthValue()-2);
			}
		}
		// Can't contact printer
		else {
			obj.setOffline();
		}
	}
	
	// This method is capable of handling multiple OIDs
	private ResponseEvent<?> getV2C(OID oids[], Printer obj){
		PDU pdu = new PDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
		pdu.setType(PDU.GET);
		ResponseEvent<?> event = null;
		try {
			event = snmp.send(pdu, getTarget(obj, 2), null);
		} catch (IOException e) {
			// Error on V2
		}
		return event;
	}
	
	// This method returns a Target, which contains information about where the data should be fetched and how.
	private CommunityTarget<Address> getTarget(Printer obj, int ver) {
		Address targetAddress = GenericAddress.parse("udp:"+obj.getAddress()+"/161");
		CommunityTarget<Address> target = new CommunityTarget<Address>();
		target.setCommunity(new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		//SNMP Version 1, 2c, or 3
		if(ver==2) {
			target.setVersion(SnmpConstants.version2c);
		}
		else if(ver==1) {
			target.setVersion(SnmpConstants.version1);
		}
		return target;
	}
	
	// This method loops over getPrinters() 3 times if it can't find any printers it will exit the program
	private void loopGetPrinters() {
		getPrinters();
		int attempts = 0;
		// No spooler service, or no print server
		while(printers.isEmpty()) {
			if(attempts > 3) {
				System.out.println("Unable to find print server / Print spooler is offline");
				System.exit(1);
			}
			startSpooler();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Error trying to sleep");
			}
			getPrinters();
			attempts++;
		}
	}
	
	// Start spooler through powershell (requires admin privileges)
		private void startSpooler() {
			// powershell.exe Set-Service -Name Spooler -StartupType Automatic
			String cmd = "powershell.exe Set-Service -Name Spooler -StartupType Automatic";
			Runtime runtime = Runtime.getRuntime();
			Process process;
			try {
				process = runtime.exec(cmd);
				process.getOutputStream().close();
			} catch (IOException e) {
				System.out.println("Error running powershell process.");
			}	
			
			cmd = "powershell.exe Start-Service -Name Spooler";
			//runtime = Runtime.getRuntime();
			try {
				process = runtime.exec(cmd);
				process.getOutputStream().close();
			} catch (IOException e) {
				System.out.println("Error running powershell process.");
			}		
		}
	
	// This method finds the IP of printers on the print server by running a powershell command 
	private void getPrinters() {
		String cmd = "powershell.exe Get-PrinterPort -ComputerName " + printserver;
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(cmd);
			process.getOutputStream().close();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = reader.readLine()) != null) {
				Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				Matcher m = p.matcher(line);
				while(m.find()) {
					printers.add(new Printer(m.group(0)));
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error running powershell process.");
		}
	}
	
	// Begins SNMP listener
	private void start() {
		TransportMapping<?> transport;
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
		} catch (IOException e) {
			// Error
			System.out.println("Error trying to listen()");
			System.exit(1);
		}
	}
	
}
