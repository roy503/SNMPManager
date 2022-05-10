import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.*;
import org.snmp4j.event.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.*;


public class SNMPManager {

Snmp snmp = null;
static List<String> addressList = null;
static List<Printer> printers = null;
String address = null;
/**
* Constructor
* @param add
*/
public SNMPManager(String add)
{
	address = add;
	
}

public static void main(String[] args) throws IOException {
/**
* Port 161 is used for Read and Other operations
* Port 162 is used for the trap generation
*/
	printers = new ArrayList<Printer>();
	addressList = new ArrayList<String>();
	//This gets the current working directory and adds the input file "printers.txt" to it
	inputAddresses(System.getProperty("user.dir")+"\\src\\"+args[0]);
	System.out.println(String.format("%-30s %-16s %-70s %-20s %2s", "Location","IP","Model","Serial","Toner"));
	for(int i = 0;i < printers.size();i++) {
		SNMPManager client = new SNMPManager("udp:"+printers.get(i).getIP()+"/161"); // this needs to be looped somehow
		client.start();
		client.getAsString(printers.get(i));
	}
}

private static void inputAddresses(String input) throws IOException {
	//input addresses from text file
	File file = new File(input);
	BufferedReader reader = null;
	try {
		reader = new BufferedReader(new FileReader(file));
	} catch (FileNotFoundException e) {
		System.out.println("File not found");
	}
	String line;
	while((line = reader.readLine()) != null) {
		//while line exists, add to list
		Printer newPrinter = new Printer(line);
		addressList.add(line);
		printers.add(newPrinter);
	}
}

/**
* Start the Snmp session. If you forget the listen() method you will not
* get any answers because the communication is asynchronous
* and the listen() method listens for answers.
* @throws IOException
*/
private void start() throws IOException {
	TransportMapping transport = new DefaultUdpTransportMapping();
	snmp = new Snmp(transport);
	transport.listen();
}

/**
* Method which takes a single OID and returns the response from the agent as a String.
* @param oid
* @return
* @throws IOException
*/
public void getAsString(Printer obj) throws IOException {

	ResponseEvent event;
	//Print room printers use 2 toners, K1 and K2
	if(address.equals("udp:10.214.192.79/161") || address.equals("udp:10.214.192.91/161")){
		event = get(new OID[] { new OID(".1.3.6.1.2.1.1.5.0"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.30"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.30"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.31"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.31"), new OID(".1.3.6.1.2.1.1.6.0"),new OID(".1.3.6.1.2.1.43.5.1.1.17.1")}, obj);
		//0 name, 1 K1 Curr, 2 K1 Max, 3 K2 Curr, 4 K2 Max, 5 Location, 6 Serial
	}
	//Office label printer
	else if(address.equals("udp:10.214.192.250/161")) {
		event = get(new OID[] { new OID(".1.3.6.1.2.1.1.5.0"), new OID(".1.3.6.1.2.1.1.6.0")}, obj);
		//0 desc, 1 location
	}
	//All other printers
	else {
		event = get(new OID[] { new OID(".1.3.6.1.2.1.25.3.2.1.3.1"),new OID(".1.3.6.1.2.1.43.11.1.1.9.1.1"),new OID(".1.3.6.1.2.1.43.11.1.1.8.1.1"),new OID(".1.3.6.1.2.1.1.6.0"),new OID(".1.3.6.1.2.1.43.5.1.1.17.1")}, obj);
		// 0 name, 1 tonerCurr, 2 tonerMax, 3 location, 4 serial
	}
	//not sure if this toner works for colour printers?
	if(event.getResponse() != null) {
		//Print room printers
		if(address.equals("udp:10.214.192.79/161") || address.equals("udp:10.214.192.91/161")) {
			float tonerK1 = Float.parseFloat(event.getResponse().get(1).getVariable().toString())/Float.parseFloat(event.getResponse().get(2).getVariable().toString());
			float tonerK2 = Float.parseFloat(event.getResponse().get(3).getVariable().toString())/Float.parseFloat(event.getResponse().get(4).getVariable().toString());
			obj.setK1(Math.round(tonerK1*100));
			obj.setK2(Math.round(tonerK2*100));
			obj.setName(event.getResponse().get(0).getVariable().toString());
			obj.setLocation(event.getResponse().get(5).getVariable().toString());
			obj.setSerial(event.getResponse().get(6).getVariable().toString());
			System.out.println(String.format("%-30s %-16s %-70s %-20s %2d%% %2d%%", obj.getLocation(), obj.getIP(), obj.getName(), obj.getSerial(), obj.getK1(), obj.getK2()));
		}
		//label printer
		else if(address.equals("udp:10.214.192.250/161")) {
			obj.setName(event.getResponse().get(0).getVariable().toString());
			obj.setLocation(event.getResponse().get(1).getVariable().toString());
			System.out.println(String.format("%-30s %-16s %-70s", obj.getLocation(), obj.getIP(), obj.getName()));
		}
		else {
			//all others
			float tonerP = Float.parseFloat(event.getResponse().get(1).getVariable().toString())/Float.parseFloat(event.getResponse().get(2).getVariable().toString());
			obj.setToner(Math.round(tonerP*100));
			obj.setName(event.getResponse().get(0).getVariable().toString());
			obj.setLocation(event.getResponse().get(3).getVariable().toString());
			obj.setSerial(event.getResponse().get(4).getVariable().toString());
			System.out.println(obj.toString());
		}
	}
	else {
		//can't contact printer
		obj.setOffline();
		System.out.println(String.format("%-30s %-14s", "Printer Offline", obj.getIP()));
		}
}

/**
* This method is capable of handling multiple OIDs
* @param oids
* @return
* @throws IOException
*/
public ResponseEvent get(OID oids[], Printer obj) throws IOException{
	PDU pdu = new PDU();
	for (OID oid : oids) {
		pdu.add(new VariableBinding(oid));
	}
	pdu.setType(PDU.GET);
	ResponseEvent event = snmp.send(pdu, getTarget(obj), null);
	if(event.getResponse() != null) {
		return event;
	}
	//GET timed out
	return event;
}

/**
* This method returns a Target, which contains information about
* where the data should be fetched and how.
* @return
*/
private CommunityTarget getTarget(Printer obj) {
	Address targetAddress = GenericAddress.parse(address);
	CommunityTarget target = new CommunityTarget();
	target.setCommunity(new OctetString("public"));
	target.setAddress(targetAddress);
	target.setRetries(2);
	target.setTimeout(1500);
	//SNMP Version 1, 2c, or 3
	target.setVersion(SnmpConstants.version1);
	return target;
}
}
