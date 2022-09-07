import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class App {

  public static void main(String[] args) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc = db.parse(new File("Title.xml"));
    expiredDoc(doc);
  }

  public static Document expiredDoc(Document doc) {
    //sysdate exctraction
    int j = 1;
    int i = 0;
    LocalDate sysDate = null;
    LocalDate endDate = null;
    LocalDate startDate = null;
    CachedXPathAPI v = new CachedXPathAPI();

    //hour and date vars
    NodeList endHourValues = null;
    NodeList endDateValues = null;
    NodeList startDateValues = null;
    NodeList startHourValues = null;
    String sEndHourValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='endHour']/value";
    String sStartHourValues = "/content/group/definition/list/value/schedulation/list/value/window/text[@name='startHour']/value";
    String endDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='endDate']/value";
    String startDateValuesPath = "/content/group/definition/list/value/schedulation/list/value/window/date[@name='startDate']/value";

    //utilized for the removal of multiple events collision
    String schedulationPath = "/content/group/definition/list/value/schedulation";
    String windowPath = "list[@name='fruitionWindows']/value/window";
    NodeList windowList = null;

    try {
      NodeList schedulationList = v.selectNodeList(doc, schedulationPath);

      //cycle on schedulation lenght
      for (i = 0; i < schedulationList.getLength(); i++) {
        windowList = v.selectNodeList(schedulationList.item(i), windowPath);

        //check if more than 1 window node is present
        if (windowList.getLength() > 1) {
          //for every extra window node remove it
          for (j = 1; j < windowList.getLength(); j++) {
            windowList.item(j).getParentNode().removeChild(windowList.item(j));
          }
        }
      }

      //data/hour values

      startDateValues = v.selectNodeList(doc, startDateValuesPath);
      startHourValues = v.selectNodeList(doc, sStartHourValues);
      endDateValues = v.selectNodeList(doc, endDateValuesPath);
      endHourValues = v.selectNodeList(doc, sEndHourValuesPath);
      sysDate = LocalDate.now();
      sysDate = sysDate.minusDays(1);

      //main loop
      for (i = 0; i < windowList.getLength(); i++) {
        CharSequence sStartDate = startDateValues.item(i).getTextContent();
        CharSequence sEndDate = endDateValues.item(i).getTextContent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        startDate = LocalDate.parse(sStartDate, formatter).minusDays(1);
        endDate = LocalDate.parse(sEndDate, formatter).minusDays(1);

        //if end date > sysdate --> case -1
        switch (endDate.compareTo(sysDate)) {
          case -1:
            Element endDateElement = (Element) endDateValues.item(i);
            endDateElement.getFirstChild().setNodeValue(sysDate.toString());
            Element endHourElement = (Element) endHourValues.item(i);
            endHourElement.getFirstChild().setNodeValue("23:59");
            break;
          default:
            break;
        }

        //if start date > sysdate --> case -1
        switch (startDate.compareTo(sysDate)) {
          case -1:
            Element startDateElement = (Element) startDateValues.item(i);
            startDateElement.getFirstChild().setNodeValue(sysDate.toString());

            Element startHourElement = (Element) startHourValues.item(i);
            startHourElement.getFirstChild().setNodeValue("00:00");

            break;
          default:
            //nothing changed
            break;
        }
      }
    } catch (TransformerException e1) {
      e1.printStackTrace();
    }
    return doc;
  }
}
