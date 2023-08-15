import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snmp4j.*;
import org.snmp4j.event.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.*;

import com.opencsv.CSVWriter;


public class SNMPManager implements Runnable{

private Snmp snmp;
private ArrayList<String> addressList;
private ArrayList<Printer> printers;
/**
* Constructor
* 
*/
public SNMPManager()
{
	addressList = new ArrayList<String>();
	printers = new ArrayList<Printer>();
	snmp = null;	
}

public void run() {
	begin();
}
private void begin() {
/**
* Port 161 is used for Read and Other operations
* Port 162 is used for the trap generation
*/
	//System.out.println("Gaethring IP Addresses...");
	parsePrintServer();
	inputAddresses();
	//System.out.println(printers.size()+" printers found.");
	start();
	//System.out.println("Sending SNMP Messages");
	//String progressBar = "|                                   |";
	//System.out.print(progressBar+"\r");
	for(int i = 0;i < printers.size();i++) { 
		getAsString(printers.get(i));
		//progressBar = progressBar.substring(0, i+1) +'=' + progressBar.substring(i+2);
		//System.out.print(progressBar+"\r");
	}
	//System.out.println("\nDone");
	try { 
		snmp.close();
	} catch (IOException e) { 
		// Error
		System.out.println("Error trying to close() snmp"); 
		System.exit(1); 
	}
	//Sorts ascending order by black ink levels
	printers.sort(Comparator.comparing(Printer::isOffline)
			.thenComparing(Printer::isLabelPrinter)
			.thenComparing(Printer::iskprinter)
			.thenComparing(Printer::isNotColour)
			.thenComparingInt(Printer::getBlack));
	//System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	//System.out.println(String.format("%-30s %-16s %-30s %-22s %3s","Location","IP","Model","Serial","B    Y    M    C      K1     K2"));
	//System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	//Make a print string instead?
	//print 
	File file = null;
	file = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"printers.csv");
		 // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);
  
        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);
  
        // adding header to csv
        String[] header = { "Location", "IP","Name","Serial","Black", "Yellow", "Magenta", "Cyan", "K1", "K2" };
        writer.writeNext(header);
  
        // add data to csv
        
        for(int i = 0; i < printers.size(); i ++) {
    		//System.out.println(printers.get(i).toString());
    		writer.writeNext(printers.get(i).tocsv());
    	}
        // closing writer connection
        writer.close();
    }
    catch (IOException e) {
        e.printStackTrace();
    }
	
	System.exit(0);
}

private void parsePrintServer() {
	//Retrieves ip address line by line from txt file printserver.txt
	try(InputStream in = SNMPManager.class.getResourceAsStream("printserver.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-16"))){
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("printers.txt"), false));
		
		String line;
		while((line = reader.readLine()) != null) {
			Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			Matcher m = p.matcher(line);
			while(m.find()) {
				bw1.write(m.group(0));
				bw1.newLine();			
			}
		}
		bw1.close();
		
	} catch (IOException | NullPointerException e) {
		//error
		System.out.println("Can't find printserver.txt");
		System.exit(1);
	}
}
private void inputAddresses(){
	//input addresses from text file
	try(InputStream in = SNMPManager.class.getResourceAsStream("printers.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
		String line;
		while((line = reader.readLine()) != null) {
			//while line exists, add to list
			Printer newPrinter = new Printer(line);
			addressList.add(line);
			printers.add(newPrinter);
		}
		reader.close();
	} catch (IOException | NullPointerException e) {
		//error
		System.out.println("Can't find printers.txt");
		System.exit(1);
	} 
}

/**
* Start the Snmp session. If you forget the listen() method you will not
* get any answers because the communication is asynchronous
* and the listen() method listens for answers.
* @throws IOException
*/
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

/**
* Method which takes a single OID and returns the response from the agent as a String.
* @param oid
* @return
* @throws IOException
*/
private void getAsString(Printer obj){

	//These care custom IP settings for the printers on my network
	ResponseEvent<?> event;
	event = getV2C(new OID[] {new OID(".1.3.6.1.2.1.1.5.0"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.1"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.1"),
			new OID(".1.3.6.1.2.1.43.11.1.1.9.1.2"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.2"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.3"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.3"),
			new OID(".1.3.6.1.2.1.43.11.1.1.9.1.4"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.4"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.30"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.30"),
			new OID(".1.3.6.1.2.1.43.11.1.1.9.1.31"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.31"),new OID(".1.3.6.1.2.1.1.6.0"),new OID(".1.3.6.1.2.1.43.5.1.1.17.1"), new OID(".1.3.6.1.2.1.43.12.1.1.4.1.2")}, obj);
	// 0 name, 1 black curr, 2 black max, 3 yellow curr, 4 yellow max, 5 magenta curr, 6 magenta max, 7 cyan curr, 8 cyan max, 9 k1 curr, 10 k1 max, 11 k2 curr , 12 k2 max, 13 location, 14 serial, 15 "yellow"  

	if(event.getResponse() != null) {
		obj.setName(event.getResponse().get(0).getVariable().toString());
		if(!event.getResponse().get(1).getVariable().isException() && !event.getResponse().get(2).getVariable().isException()){
			obj.setBlack(Math.round(Float.parseFloat(event.getResponse().get(1).getVariable().toString())/Float.parseFloat(event.getResponse().get(2).getVariable().toString())*100));
		}
		
		if(event.getResponse().get(15).getVariable().toString().equals("yellow")) {
			//if colour printer
			obj.setColour();
			obj.setYellow(Math.round(Float.parseFloat(event.getResponse().get(3).getVariable().toString())/Float.parseFloat(event.getResponse().get(4).getVariable().toString())*100));
			obj.setMagenta(Math.round(Float.parseFloat(event.getResponse().get(5).getVariable().toString())/Float.parseFloat(event.getResponse().get(6).getVariable().toString())*100));
			obj.setCyan(Math.round(Float.parseFloat(event.getResponse().get(7).getVariable().toString())/Float.parseFloat(event.getResponse().get(8).getVariable().toString())*100));	
		}
		
		if(!event.getResponse().get(9).getVariable().isException() && !event.getResponse().get(10).getVariable().isException()){
			float tonerK1 = Float.parseFloat(event.getResponse().get(9).getVariable().toString())/Float.parseFloat(event.getResponse().get(10).getVariable().toString());
			obj.setK1(Math.round(tonerK1*100));
			obj.setkprinter();
		}
		
		if(!event.getResponse().get(11).getVariable().isException() && !event.getResponse().get(12).getVariable().isException()){
			float tonerK2 = Float.parseFloat(event.getResponse().get(11).getVariable().toString())/Float.parseFloat(event.getResponse().get(12).getVariable().toString());
			obj.setK2(Math.round(tonerK2*100));
		}
		
		if(("10.214.192.215").equals(obj.getIP()) || ("10.214.192.250").equals(obj.getIP())) {
			//manually set label printers
			obj.setLabelPrinter();
		}
		else
		{
			String serial = event.getResponse().get(14).getVariable().toString();
			obj.setSerial(serial.substring(serial.length() - 6));
		}
		
		if(("10.214.192.95").equals(obj.getIP())) {
			obj.setLocation("Careers Office Colour (FR0037)");
		}
		else {
			obj.setLocation(event.getResponse().get(13).getVariable().toString());
		}	
	}
	else {
		//can't contact printer
		obj.setOffline();
	}
}

/**
* This method is capable of handling multiple OIDs
* @param oids
* @return
* @throws IOException
*/
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

/**
* This method returns a Target, which contains information about
* where the data should be fetched and how.
* @return
*/
private CommunityTarget<Address> getTarget(Printer obj, int ver) {
	Address targetAddress = GenericAddress.parse("udp:"+obj.getIP()+"/161");
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
}
