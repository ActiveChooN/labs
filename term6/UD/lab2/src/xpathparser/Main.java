package xpathparser;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        DocumentBuilderFactory builderFactory =  DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse("./data.xml");
            XPathFactory pathFactory = XPathFactory.newInstance();
            XPath xPath = pathFactory.newXPath();

            List<String> titles = List.of("Моби Дик", "Математический анализ");
            System.out.println("Количество книг в наличии:\n--------------------------");
            for (String title: titles) {
                System.out.printf("%s:\t%s\n", title, getBookQuantity(document, xPath, title));
            }

            List<String> workingHours = getWorkingHours(document, xPath);
            System.out.print("\n\nЧасы работы:\n------------\n");
            for (String workingHour: workingHours) {
                System.out.println(workingHour);
            }

        }

        catch (ParserConfigurationException | SAXException | IOException | XPathException e) {
            e.printStackTrace();
        }
    }

    public static Integer getBookQuantity(Document document, XPath xPath, String title) throws DOMException, XPathException {
        XPathExpression expression = xPath.compile(String.format("//books/book[title=\"%s\"]/available", title));
        Number quantity = (Number)expression.evaluate(document, XPathConstants.NUMBER);
        return quantity.intValue();
    }

    public static List<String> getWorkingHours(Document document, XPath xPath) throws DOMException, XPathException {
        List<String> result = new ArrayList<>();
        XPathExpression expression = xPath.compile("//workinghours/hours");
        NodeList nodeList = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++){
            result.add(String.format("%-12s:\tс %2s:%2s до %2s:%2s",
                    nodeList.item(i).getAttributes().getNamedItem("day").getNodeValue(),
                    nodeList.item(i).getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue(),
                    nodeList.item(i).getChildNodes().item(1).getChildNodes().item(3).getFirstChild().getNodeValue(),
                    nodeList.item(i).getChildNodes().item(3).getChildNodes().item(1).getFirstChild().getNodeValue(),
                    nodeList.item(i).getChildNodes().item(3).getChildNodes().item(3).getFirstChild().getNodeValue()
                    ));
        }

        return result;
    }
}
