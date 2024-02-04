package home.file.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.file.IExporter;
import home.file.Tag;
import home.model.AbstractVehicle;
import home.model.AbstractVehicleWithTrailer;
import home.model.Car;
import home.model.Motorcycle;
import home.model.Truck;
import home.utils.LogUtils;
import home.utils.Utils;

public final class XmlExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(XmlExporter.class);

    private static final String XML_VERSION = "1.0";
    private static final String XML_INDENT = "yes";
    private static final String XML_STANDALONE = "yes";

    @Override
    public String exportAllDataObjsToString() {
        try (var dataObjsOutputStream = new ByteArrayOutputStream()) {
            writeDataObjsToXmlOutput(dataObjsOutputStream);
            String xml = new String(dataObjsOutputStream.toByteArray(), StandardCharsets.UTF_8);
            return formatXML(xml);
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("XML converter error", LOG, e);
        }
    }

    private void writeDataObjsToXmlOutput(OutputStream dataObjsOutputStream) {
        try (var xmlWriterAutoCloseWrapper = new XmlWriterAutoCloseWrapper(XMLOutputFactory
                .newInstance().createXMLStreamWriter(dataObjsOutputStream, StandardCharsets.UTF_8.name()))) {
            XMLStreamWriter xmlWriter = xmlWriterAutoCloseWrapper.writer();
            xmlWriter.writeStartDocument(StandardCharsets.UTF_8.name(), XML_VERSION);
            xmlWriter.writeStartElement(Tag.VEHICLES.getTagName());

            for (AbstractVehicle dataObj : Storage.INSTANCE.getAll()) {
                addObjToXmlWriter(xmlWriter, dataObj);
            }

            xmlWriter.writeEndDocument();
        } catch (Exception e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while convert data objects to xml", LOG, e);
        }

    }

    private void addObjToXmlWriter(XMLStreamWriter xmlWriter,
            AbstractVehicle dataObj) throws XMLStreamException {
        xmlWriter.writeStartElement(Tag.VEHICLE.getTagName());
        xmlWriter.writeAttribute(Tag.TYPE.getTagName(), dataObj.getType().name());

        xmlWriter.writeStartElement(Tag.COLOR.getTagName());
        xmlWriter.writeCharacters(dataObj.getColor());
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement(Tag.NUMBER.getTagName());
        xmlWriter.writeCharacters(dataObj.getNumber());
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement(Tag.DATE.getTagName());
        xmlWriter.writeCharacters(Utils.getFormattedDate(dataObj.getDateTime()));
        xmlWriter.writeEndElement();

        if (dataObj instanceof AbstractVehicleWithTrailer vehicleWithTrailer) {
            xmlWriter.writeStartElement(Tag.HAS_TRAILER.getTagName());
            xmlWriter.writeCharacters(Boolean.toString(vehicleWithTrailer.hasTrailer()));
            xmlWriter.writeEndElement();
        }

        if (dataObj instanceof Car car) {
            xmlWriter.writeStartElement(Tag.IS_TRANSPORTS_PASSENGERS.getTagName());
            xmlWriter.writeCharacters(Boolean.toString(car.isTransportsPassengers()));
            xmlWriter.writeEndElement();
        }

        if (dataObj instanceof Truck truck) {
            xmlWriter.writeStartElement(Tag.IS_TRANSPORTS_CARGO.getTagName());
            xmlWriter.writeCharacters(Boolean.toString(truck.isTransportsCargo()));
            xmlWriter.writeEndElement();
        }

        if (dataObj instanceof Motorcycle moto) {
            xmlWriter.writeStartElement(Tag.HAS_CRADLE.getTagName());
            xmlWriter.writeCharacters(Boolean.toString(moto.hasCradle()));
            xmlWriter.writeEndElement();
        }

        xmlWriter.writeEndElement();
    }

    private String formatXML(String xml) {
        try (var inputReader = new StringReader(xml);
                var outputWriter = new StringWriter()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // pretty print by indention
            transformer.setOutputProperty(OutputKeys.INDENT, XML_INDENT);
            // add standalone="yes", add line break before the root element
            transformer.setOutputProperty(OutputKeys.STANDALONE, XML_STANDALONE);

            StreamSource streamSrc = new StreamSource(inputReader);
            transformer.transform(streamSrc, new StreamResult(outputWriter));
            outputWriter.flush();

            return outputWriter.toString();
        } catch (IOException | TransformerException e) {
            throw LogUtils.logAndCreateIllegalStateException(
                    "Error while writing formatted xml text to temporary storage in memory",
                    LOG, e);
        }
    }

    private record XmlWriterAutoCloseWrapper(XMLStreamWriter writer) implements AutoCloseable {

        @Override
        public void close() throws Exception {
            writer.close();
        }
    }
}