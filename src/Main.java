import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static double resulSum(Element element) {
        String a = element.getAttribute("fine_amount");
        return Double.parseDouble(a.trim());
    }

    public static double [] getSum(String fileName) throws ParserConfigurationException, IOException, SAXException {
        double speeding = 0d, withoutDriveLicense = 0d, alcoholIntoxication = 0d;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));
        document.getDocumentElement().normalize();
        NodeList persons = document.getElementsByTagName("person");

        for (int i = 0; i < persons.getLength(); i++) {
            Element person = (Element) persons.item(i);
            switch (person.getAttribute("type")) {
                case "Speeding" :
                    speeding += resulSum(person);
                    break;
                case "Without rights" :
                    withoutDriveLicense += resulSum(person);
                    break;
                case "Alcohol intoxication" :
                    alcoholIntoxication += resulSum(person);
                    break;
                default:
                    break;
            }
        }
        double[] arrResult = new double[] { speeding, withoutDriveLicense, alcoholIntoxication };
        return arrResult;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String fileArray[] = new String[] { "Person.xml", "Person2.xml" };
        double sumSpeeding = 0d, sumWithoutRights = 0d, sumAlcoholIntoxication = 0d;
        for (String file: fileArray) {
            double sumArray[] = getSum(file);
            sumSpeeding += sumArray[0];
            sumWithoutRights += sumArray[1];
            sumAlcoholIntoxication += sumArray[2];
        }

        Map <String, Double> map = new HashMap<>();
        map.put("Speeding", sumSpeeding);
        map.put("Without Rights", sumWithoutRights);
        map.put("Alcohol intoxication",sumAlcoholIntoxication);

        Map<String, Double> result = map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        String json = "{" + result.entrySet().stream()
                .map(e -> "\""+ e.getKey() + "\" : " + e.getValue())
                .collect(Collectors.joining(", "))+"}";

        FileWriter writer = new FileWriter("result.json");
        writer.write(json);
        writer.flush();
        writer.close();
    }
}