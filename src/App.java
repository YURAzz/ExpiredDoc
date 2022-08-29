import java.io.File;
import org.w3c.dom.Element;
import java.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.parsers.DocumentBuilder;
import java.time.format.DateTimeFormatter;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;

public class App {
    public static void main(String[] args) throws Exception {
     
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    eventSort(doc);
    }

public static Document eventSort(Document doc) {
    
    int i = 0;
    LocalDate sysDate = null;
    LocalDate startDate = null;
    LocalDate endDate = null;
    CachedXPathAPI v = new CachedXPathAPI();
    CharSequence sStartDate = null;
    CharSequence sEndDate = null;
    
    //hour/date path
    NodeList node = null;
    String nodes = "/content/group/definition/list/value/schedulation/list/value/window";

    //hour
    String sEndHourPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']" ;
    String sEndHourValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']/value";
    String sStartHourPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']";
    String sStartHourValues = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']/value";
    NodeList endHourPath = null;
    NodeList endHourValues = null;
    NodeList startHourPath = null;
    NodeList startHourValues = null;

    //date
    String startDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']/value";
    String sStartDatePath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']";
    String endDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']/value";
    String sEndDatePath= "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']";
    NodeList startDateValues = null;
    NodeList startDatePath = null;
    NodeList endDateValues = null;
    NodeList endDatePath = null;

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

        //data/hour paths
        startDatePath = v.selectNodeList(doc, sStartDatePath);
        endDatePath = v.selectNodeList(doc, sEndDatePath);
        startHourPath = v.selectNodeList(doc, sStartHourPath);
        endHourPath = v.selectNodeList(doc, sEndHourPath);

        node = v.selectNodeList(doc, nodes);
        sysDate = LocalDate.now();
        sysDate = sysDate.minusDays(1);

    } catch (TransformerException e1) {
        e1.printStackTrace();
    }  

    for (i = 0; i < startDateValues.getLength(); i++) {
        sStartDate = startDateValues.item(i).getTextContent();
        sEndDate = endDateValues.item(i).getTextContent();
        
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
            startDate = LocalDate.parse(sStartDate, formatter).minusDays(1);
            endDate = LocalDate.parse(sEndDate, formatter).minusDays(1);

        //if end date > sysdate --> case -1
        switch (endDate.compareTo(sysDate)) {
            case -1:

            endDateElement = (Element) endDateValues.item(i);
            endDateElement.getFirstChild().setNodeValue(sysDate.toString());
            node.item(i).replaceChild(endDatePath.item(i), endDatePath.item(i));

            endHourElement = (Element) endHourValues.item(i);
            endHourElement.getFirstChild().setNodeValue("23:59");
            node.item(i).replaceChild(endHourPath.item(i), endHourPath.item(i));

            break;
            default:
                break;
        }

        //if start date > sysdate --> case -1
        switch (startDate.compareTo(sysDate)) {
            case -1:

            startDateElement = (Element) startDateValues.item(i);
            startDateElement.getFirstChild().setNodeValue(sysDate.toString());
            node.item(i).replaceChild(startDatePath.item(i), startDatePath.item(i));

            startHourElement = (Element) startHourValues.item(i);
            startHourElement.getFirstChild().setNodeValue("00:00");
            node.item(i).replaceChild(startHourPath.item(i), startHourPath.item(i));

            break;
            default:
            //nothing changed
            break;
        }
    } 
        return doc;
    }
}