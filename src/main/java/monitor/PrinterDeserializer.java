package monitor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
@SuppressWarnings("serial")
public class PrinterDeserializer extends StdDeserializer<Printer>{
	
	public PrinterDeserializer() {
		this(null);
	}
	
	public PrinterDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Printer deserialize(JsonParser parser, DeserializationContext deserializer) {
		Printer printer = new Printer();
		ObjectCodec codec = parser.getCodec();
		JsonNode node = null;
		try {
			node = codec.readTree(parser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonNode locationNode = node.get("Location");
		String location = locationNode.asText();
		printer.setLocation(location);
		
		JsonNode addressNode = node.get("IP");
		String address = addressNode.asText();
		printer.setAddress(address);
		
		JsonNode nameNode = node.get("Name");
		String name = nameNode.asText();
		printer.setName(name);
		
		JsonNode serialNode = node.get("Serial");
		String serial = serialNode.asText();
		printer.setSerial(serial);
		
		JsonNode blackNode = node.get("Black");
		String black = blackNode.asText();
		printer.setBlack(Integer.parseInt(black));
		JsonNode yellowNode = node.get("Yellow");
		String yellow = yellowNode.asText();
		printer.setYellow(Integer.parseInt(yellow));
		
		JsonNode magentaNode = node.get("Magenta");
		String magenta = magentaNode.asText();
		printer.setMagenta(Integer.parseInt(magenta));
		
		JsonNode cyanNode = node.get("Cyan");
		String cyan = cyanNode.asText();
		printer.setCyan(Integer.parseInt(cyan));
		
		JsonNode k1Node = node.get("K1");
		String k1 = k1Node.asText();
		printer.setK1(Integer.parseInt(k1));
		
		JsonNode k2Node = node.get("K2");
		String k2 = k2Node.asText();
		printer.setK2(Integer.parseInt(k2));
		
		JsonNode MonoPrintsNode = node.get("MonoPrints");
		String monoPrints = MonoPrintsNode.asText();
		printer.setMonoPrints(Integer.parseInt(monoPrints));
		
		JsonNode monoCopiesNode = node.get("MonoCopies");
		String monoCopies = monoCopiesNode.asText();
		printer.setMonoCopies(Integer.parseInt(monoCopies));
		
		JsonNode ColourPrintsNode = node.get("ColourPrints");
		String colourPrints = ColourPrintsNode.asText();
		printer.setColourPrints(Integer.parseInt(colourPrints));
		
		JsonNode ColourCopiesNode = node.get("ColourCopies");
		String colourCopies = ColourCopiesNode.asText();
		printer.setColourCopies(Integer.parseInt(colourCopies));
		
		JsonNode Reports = node.get("Reports");
		int i =0;
		
		for (JsonNode month : Reports) {
			//parse monthly data
			
			JsonNode MonthReportNode = month.get("Month");
			String monthReport = MonthReportNode.asText();
			
			JsonNode MonoPrintsReportNode = month.get("MonoPrints");
			String monoPrintsReport = MonoPrintsReportNode.asText();
			
			JsonNode MonoCopiesReportNode = month.get("MonoCopies");
			String monoCopiesReport = MonoCopiesReportNode.asText();
			
			JsonNode ColourPrintsReportNode = month.get("ColourPrints");
			String colourPrintsReport = ColourPrintsReportNode.asText();
			
			JsonNode ColourCopiesReportNode = month.get("ColourCopies");
			String colourCopiesReport = ColourCopiesReportNode.asText();
			
			printer.setReports(i,monthReport,Integer.parseInt(monoPrintsReport),Integer.parseInt(monoCopiesReport),Integer.parseInt(colourPrintsReport),Integer.parseInt(colourCopiesReport));
			
			i++;
		}
		
		return printer;
	}
}

