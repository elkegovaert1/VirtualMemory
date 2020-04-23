import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadXMLFile {

    public List<Instruction> leesInstructions(String bestandnaam) throws SAXException, IOException, ParserConfigurationException {

        List<Instruction> instructions = new ArrayList<>();

        // ELKE
        // File xmlFile = new File("C:\\Users\\elkeg\\IdeaProjects\\VirtualMemory\\"+bestandnaam);
        // GLENN
        File xmlFile = new File("C:\\Users\\glenn\\IntelliJ-workspace\\VirtualMemory\\"+bestandnaam);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("instruction");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Instruction instruction = new Instruction();
                NodeList children = eElement.getChildNodes();

                for (int j = 0; j < children.getLength(); j++) {
                    Node n = children.item(j);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        Element etmp = (Element) n;
                        if (etmp.getNodeName().equals("processID")) {
                            instruction.setProcessID(Integer.parseInt(etmp.getTextContent()));
                        }
                        if (etmp.getNodeName().equals("operation")) {
                            instruction.setOperation(etmp.getTextContent());
                        }
                        if (etmp.getNodeName().equals("address")) {
                            instruction.setAdress(Integer.parseInt(etmp.getTextContent()));
                        }

                    }
                }
                instructions.add(instruction);
            }
        }
        return instructions;
    }

}
