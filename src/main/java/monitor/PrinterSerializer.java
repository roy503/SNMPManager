package monitor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class PrinterSerializer extends StdSerializer<Printer>{

	public PrinterSerializer() {
		this(null);
	}
	public PrinterSerializer(Class<Printer> t) {
		super(t);
	}
	
	@Override
	public void serialize(Printer printer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("Location", printer.getLocation());
        jsonGenerator.writeStringField("IP", printer.getAddress());
        jsonGenerator.writeStringField("Name", printer.getName());
        jsonGenerator.writeStringField("Serial", printer.getSerial());
        jsonGenerator.writeNumberField("Black", printer.getBlack());
        jsonGenerator.writeNumberField("Yellow", printer.getYellow());
        jsonGenerator.writeNumberField("Magenta", printer.getMagenta());
        jsonGenerator.writeNumberField("Cyan", printer.getCyan());
        jsonGenerator.writeNumberField("K1", printer.getK1());
        jsonGenerator.writeNumberField("K2", printer.getK2());
        jsonGenerator.writeNumberField("MonoPrints", printer.getMonoPrints());
        jsonGenerator.writeNumberField("MonoCopies", printer.getMonoCopies());
        jsonGenerator.writeNumberField("ColourPrints", printer.getColourPrints());
        jsonGenerator.writeNumberField("ColourCopies", printer.getColourCopies());
        jsonGenerator.writeArrayFieldStart("Reports");
        for (PrinterReports report : printer.getReports()) {
        	jsonGenerator.writeStartObject();
        	jsonGenerator.writeStringField("Month",report.getMonth());
        	jsonGenerator.writeNumberField("MonoPrints",report.getMonoPrints());
        	jsonGenerator.writeNumberField("MonoCopies",report.getMonoCopies());
        	jsonGenerator.writeNumberField("ColourPrints",report.getColourPrints());
        	jsonGenerator.writeNumberField("ColourCopies",report.getColourCopies());
        	jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
	}

}
