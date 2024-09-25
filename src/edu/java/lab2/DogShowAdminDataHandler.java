package edu.java.lab2;

import java.io.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class DogShowAdminDataHandler {

    // Метод для сохранения данных таблицы в XML файл
    public void saveTableToFile(DefaultTableModel model, File file) throws Exception {
        if (!file.getAbsolutePath().endsWith(".xml")) {
            file = new File(file.getAbsolutePath() + ".xml");
        }

        try {
            // Создаем XML документ
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Корневой элемент
            Element rootElement = doc.createElement("DogShow");
            doc.appendChild(rootElement);

            // Проходим по каждой строке таблицы и добавляем данные в XML
            for (int i = 0; i < model.getRowCount(); i++) {
                Element dogElement = doc.createElement("Dog");
                rootElement.appendChild(dogElement);

                Element ownerElement = doc.createElement("Owner");
                ownerElement.appendChild(doc.createTextNode(model.getValueAt(i, 0).toString()));
                dogElement.appendChild(ownerElement);

                Element nameElement = doc.createElement("Name");
                nameElement.appendChild(doc.createTextNode(model.getValueAt(i, 1).toString()));
                dogElement.appendChild(nameElement);

                Element breedElement = doc.createElement("Breed");
                breedElement.appendChild(doc.createTextNode(model.getValueAt(i, 2).toString()));
                dogElement.appendChild(breedElement);

                Element judgeElement = doc.createElement("Judge");
                judgeElement.appendChild(doc.createTextNode(model.getValueAt(i, 3).toString()));
                dogElement.appendChild(judgeElement);
            }

            // Преобразуем созданный XML в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new Exception("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Метод для загрузки данных таблицы из XML файла
    public DefaultTableModel loadTableFromFile(File file) throws Exception {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Owner", "Name", "Breed", "Judge"}, 0);

        try {
            // Чтение XML файла
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Проходим по каждому элементу Dog и загружаем данные в таблицу
            NodeList nList = doc.getElementsByTagName("Dog");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String owner = eElement.getElementsByTagName("Owner").item(0).getTextContent();
                    String name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                    String breed = eElement.getElementsByTagName("Breed").item(0).getTextContent();
                    String judge = eElement.getElementsByTagName("Judge").item(0).getTextContent();

                    model.addRow(new Object[]{owner, name, breed, judge});
                }
            }
        } catch (Exception e) {
            throw new Exception("Ошибка при загрузке файла: " + e.getMessage());
        }

        return model;
    }
}
