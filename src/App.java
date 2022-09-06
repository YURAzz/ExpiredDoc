import java.io.File;
import org.w3c.dom.Element;
import java.time.LocalDate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class App {
    public static void main(String[] args) throws Exception {
     
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    expiredDoc(doc);
    try {
        StreamResult output = new StreamResult(new File("output.xml"));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    } catch ( TransformerFactoryConfigurationError | TransformerException e) {
        e.printStackTrace();
    }
    }

public static Document expiredDoc(Document doc) {
    
    int f = 0;
    int i = 0;
    LocalDate sysDate = null;
    LocalDate startDate = null;
    LocalDate endDate = null;
    CharSequence sStartDate = null;
    CharSequence sEndDate = null;
    DateTimeFormatter formatter = null;
    CachedXPathAPI v = new CachedXPathAPI();

    String sWindowId = null;
    String valuePath = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']/value";

    NodeList valueNodeList = null;

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

    NodeList nodelist = null;
    NodeList nodelist1 = null;
    NodeList nodelist2 = null;

    NodeList nodelistCopy = null;

    ArrayList arrayList = new ArrayList();

    Element newNode = null;
    try {
        //data/hour values
        startDateValues = v.selectNodeList(doc, startDateValuesPath);
        startHourValues = v.selectNodeList(doc, sStartHourValues);
        endDateValues = v.selectNodeList(doc, endDateValuesPath);
        endHourValues = v.selectNodeList(doc, sEndHourValuesPath);
        valueNodeList = v.selectNodeList(doc, valuePath);

        sysDate = LocalDate.now();
        sysDate = sysDate.minusDays(1);


    } catch (TransformerException e1) {
        e1.printStackTrace();
    }  



        String str = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']/value/window[@id='0']";
        String str1 = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']/value";
        String str2 = "/content/group/definition/list/value/schedulation/list[@name='fruitionWindows']";

        try {

        nodelist = v.selectNodeList(doc, str);
        nodelist1 = v.selectNodeList(doc, str1);
        nodelist2 = v.selectNodeList(doc, str2);
        nodelistCopy = nodelist;

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for (i = 0; i < nodelist.getLength() ; i++) {

            arrayList.add(nodelist.item(i));
            
        }
        
        for (i = 0; i < nodelist.getLength(); i++) {
            
        newNode = (Element)  arrayList.get(i);
        nodelist2.item(i).removeChild(nodelist1.item(i));
        nodelist2.item(i).appendChild(newNode);
        
        }

     for (int w=0; w<nodelist1.getLength();w++){
        try {
            StreamResult output = new StreamResult(new File("output.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source input = new DOMSource(newNode);
            transformer.transform(input, output);
        } catch ( TransformerFactoryConfigurationError | TransformerException e) {
            e.printStackTrace();
        }
   }


        for (i=0; i < nodelist.getLength(); i++)

        {
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