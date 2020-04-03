// deadline: 27 april 2020
//Glenn heeft het nu ook

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        //xmlfiles inlezen
        ReadXMLFile readXMLFile = new ReadXMLFile();
        List<Instruction> instructions1 = readXMLFile.leesInstructions("Instructions_30_3.xml");
        List<Instruction> instructions2 = readXMLFile.leesInstructions("Instructions_20000_4.xml");
        List<Instruction> instructions3 = readXMLFile.leesInstructions("Instructions_20000_20.xml");

    }

}
