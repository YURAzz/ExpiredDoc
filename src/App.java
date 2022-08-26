import java.io.File;
import org.w3c.dom.Node;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;
import org.apache.xpath.CachedXPathAPI;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class App {
    public static void main(String[] args) throws Exception {
     
  // parse doc
  try {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    eventSort(doc);

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    Result output = new StreamResult(new File("output.xml"));
    Source input = new DOMSource(doc);

transformer.transform(input, output);

} catch (ParserConfigurationException | SAXException | IOException e) {
    e.printStackTrace();
}
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


    //hour paths
    String sEndHourPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']" ;
    String sStartHourPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']";
    String sStartHourValues = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour'/values]";
    NodeList startHourPath = null;
    NodeList startHourValues = null;
    NodeList endHourPath = null;


    //date values
    String startDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']/value";
    String endDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']/value";
    NodeList endDateValues = null;
    NodeList startDateValues = null;

    //date paths
    String sStartDatePath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']";
    String sEndDatePath= "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']";
    NodeList startDatePath = null;
    NodeList endDatePath = null;

    boolean f = true;

    Node endHourNode = null;
    Node endDateNode = null;
    Node startHourNode = null;
    Node startDateNode = null;

    Element startDateElement = null;    
    Element endDateElement = null;    
    Element startHourElement = null;
    Element endHourElement = null;

    try {
        //data/hour values
        startDateValues = v.selectNodeList(doc, startDateValuesPath);
        startHourValues = v.selectNodeList(doc, sStartHourValues);
        endDateValues = v.selectNodeList(doc, endDateValuesPath);

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

    while (f){
        for (int w = 0; w<endDatePath.getLength();w++ ){
        node.item(w).removeChild(endDatePath.item(w));
        node.item(w).removeChild(endHourPath.item(w));
        node.item(w).removeChild(startDatePath.item(w));
        node.item(w).removeChild(startHourPath.item(w));
        
        }
        f=false;
    }

        //if end date > sysdate --> case -1
        switch (endDate.compareTo(sysDate)) {
            case -1:

            endDateNode = endDatePath.item(i);
            endHourNode = endHourPath.item(i);

            endDateElement = (Element) endDateNode;
            endHourElement = (Element) endHourNode;

            endDateElement.setAttribute("value", sysDate.toString());
            endHourElement.setAttribute("value", "23:59");

            endDateNode = (Node) endDateElement;
            endHourNode = (Node) endHourElement;

            node.item(i).appendChild(endDateNode);
            node.item(i).appendChild(endHourNode);

            break;
            default:
                break;
        }

        //if start date > sysdate --> case -1
        switch (startDate.compareTo(sysDate)) {
            case -1:

            startDateNode = startDateValues.item(i);
            startHourNode = startHourValues.item(i);

            startDateElement = (Element) startDateNode;
            startHourElement = (Element) startHourNode;

            startDateElement.setAttribute("value", sysDate.toString());
            startHourElement.setAttribute("value", "00:00");

            startDateNode = (Node) startDateElement;
            startHourNode = (Node) startHourElement;

            node.item(i).appendChild(startDateNode);
            node.item(i).appendChild(startHourNode);

            break;
            default:
                break;
        }
        
    } 

    
        return doc;
    }
}

/*
 * 
 * 
 * 
 * 
 *      for (int w=0; w>node.getLength();w++){
 * 
        try {
            Result output = new StreamResult(new File("output.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source input = new DOMSource(startHourNode);
            transformer.transform(input, output);
        } catch ( TransformerFactoryConfigurationError | TransformerException e) {
            e.printStackTrace();
        }
   }
 */