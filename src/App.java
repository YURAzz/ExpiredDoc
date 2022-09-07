import java.io.File;
import org.w3c.dom.Element;
import java.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.parsers.DocumentBuilder;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

public class App {
    public static void main(String[] args) throws Exception {
     
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    expiredDoc(doc);
    }

public static Document expiredDoc(Document doc) {

    //sysdate exctraction
    int i = 0;
    LocalDate sysDate = null;
    LocalDate endDate = null;
    LocalDate startDate = null;
    CharSequence sEndDate = null;
    CharSequence sStartDate = null;
    DateTimeFormatter formatter = null;
    CachedXPathAPI v = new CachedXPathAPI();

    //hour and date vars
    NodeList endHourValues = null;
    NodeList endDateValues = null;
    Element endDateElement = null;    
    Element endHourElement = null;
    NodeList startDateValues = null;
    NodeList startHourValues = null;
    Element startDateElement = null;    
    Element startHourElement = null;
    String sEndHourValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']/value";
    String sStartHourValues = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']/value";
    String endDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']/value";
    String startDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']/value";

    //utilized for the removal of multiple events collision
    Element newNode = null;
    NodeList wholeList = null;
    NodeList removaList = null;
    NodeList newNodesList = null;
    String str2 = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']";
    String str1 = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']/value";
    String str = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']/value/window[@id='0']";

        try {

        newNodesList = v.selectNodeList(doc, str);
        removaList = v.selectNodeList(doc, str1);
        wholeList = v.selectNodeList(doc, str2);

        } catch (TransformerException e) {
            e.printStackTrace();
        }

        for (i = 0; i < newNodesList.getLength(); i++) {
            
        newNode = (Element)  newNodesList.item(i);
        wholeList.item(i).removeChild(removaList.item(i));
        wholeList.item(i).appendChild(newNode);
        
        }

        //data/hour values
        try {

            startDateValues = v.selectNodeList(doc, startDateValuesPath);
            startHourValues = v.selectNodeList(doc, sStartHourValues);
            endDateValues = v.selectNodeList(doc, endDateValuesPath);
            endHourValues = v.selectNodeList(doc, sEndHourValuesPath);
            sysDate = LocalDate.now();
            sysDate = sysDate.minusDays(1);
    
    
        } catch (TransformerException e1) {
            e1.printStackTrace();
        }

        //main loop
        for (i=0; i < newNodesList.getLength(); i++) {

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