import java.io.File;
import org.w3c.dom.Element;
import java.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.format.DateTimeFormatter;
import javax.xml.transform.TransformerException;

public class App {
    public static void main(String[] args) throws Exception {
     
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    expiredDoc(doc);
    }

public static Document expiredDoc(Document doc) {
    
    int i = 0;
    LocalDate sysDate = null;
    LocalDate startDate = null;
    LocalDate endDate = null;
    CharSequence sStartDate = null;
    CharSequence sEndDate = null;
    DateTimeFormatter formatter = null;
    CachedXPathAPI v = new CachedXPathAPI();

    //hour
    String sEndHourValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']/value";
    String sStartHourValues = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']/value";
    NodeList endHourValues = null;
    NodeList startHourValues = null;

    //date
    String startDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']/value";
    String endDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']/value";
    NodeList startDateValues = null;
    NodeList endDateValues = null;

    Element startDateElement = null;    
    Element endDateElement = null;    
    Element startHourElement = null;
    Element endHourElement = null;

    try {
        //data/hour values
        startDateValues = v.selectNodeList(doc, startDateValuesPath);
        startHourValues = v.selectNodeList(doc, sStartHourValues);
        endDateValues = v.selectNodeList(doc, endDateValuesPath);
        endHourValues = v.selectNodeList(doc, sEndHourValuesPath);

        sysDate = LocalDate.now();
        sysDate = sysDate.minusDays(1);

    } catch (TransformerException e1) {
        e1.printStackTrace();
    }  

    for (i = 0; i < startDateValues.getLength(); i++) {
        sStartDate = startDateValues.item(i).getTextContent();
        sEndDate = endDateValues.item(i).getTextContent();
        
        formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        startDate = LocalDate.parse(sStartDate, formatter).minusDays(1);
        endDate = LocalDate.parse(sEndDate, formatter).minusDays(1);

        //if end date > sysdate --> case -1
        switch (endDate.compareTo(sysDate)) {
            case -1:

            endDateElement = (Element) endDateValues.item(i);
            endDateElement.getFirstChild().setNodeValue(sysDate.toString());

            endHourElement = (Element) endHourValues.item(i);
            endHourElement.getFirstChild().setNodeValue("23:59");

            break;

            default:
            break;
        }

        //if start date > sysdate --> case -1
        switch (startDate.compareTo(sysDate)) {
            case -1:
            
            startDateElement = (Element) startDateValues.item(i);
            startDateElement.getFirstChild().setNodeValue(sysDate.toString());

            startHourElement = (Element) startHourValues.item(i);
            startHourElement.getFirstChild().setNodeValue("00:00");

            break;
            default:
            //nothing changed
            break;
        }
    } 
        return doc;
    }
}