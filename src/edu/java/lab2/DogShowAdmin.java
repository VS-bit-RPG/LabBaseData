package edu.java.lab2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

// Собственное исключение для проверки данных собаки
class InvalidDogDataException extends Exception {
    public InvalidDogDataException(String message) {
        super(message);
    }
}

public class DogShowAdmin {
    private JFrame adminFrame;
    private DefaultTableModel model;
    private JButton save;
    private JButton addDog;
    private JButton addJudge;
    private JButton deleteDog;
    private JButton saveToFile;
    private JButton loadFromFile;
    private JButton generateReportButton;
    private JToolBar toolBar;
    private JScrollPane scroll;
    private JTable dataTable;
    private JTextField dogName;
    private JTextField ownerName;
    private JTextField dogBreed;
    private JTextField judgeName;
    private JTextArea logArea;
    private JFileChooser fileChooser;
    private final Object lock = new Object(); // объект синхронизации

    public void show() {
        // Создание окна
        adminFrame = new JFrame("Администратор выставки собак");
        adminFrame.setSize(1200, 400);
        adminFrame.setLocation(100, 100);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание кнопок
        save = new JButton("Сохранить");
        save.setToolTipText("Сохранить информацию");

        addDog = new JButton("Добавить собаку");
        addDog.setToolTipText("Добавить новую собаку");

        addJudge = new JButton("Добавить судью");
        addJudge.setToolTipText("Добавить нового судью");

        deleteDog = new JButton("Удалить собаку");
        deleteDog.setToolTipText("Удалить выбранную собаку");

        saveToFile = new JButton("Сохранить в файл");
        saveToFile.setToolTipText("Сохранить таблицу в файл");

        loadFromFile = new JButton("Загрузить из файла");
        loadFromFile.setToolTipText("Загрузить таблицу из файла");

        generateReportButton = new JButton("Создать отчет");
        generateReportButton.setToolTipText("Сгенерировать отчет");

        // Панель инструментов
        toolBar = new JToolBar("Панель инструментов");
        toolBar.add(save);
        toolBar.add(addDog);
        toolBar.add(addJudge);
        toolBar.add(deleteDog);
        toolBar.add(saveToFile);
        toolBar.add(loadFromFile);
        toolBar.add(generateReportButton);

        // Настройка таблицы с данными о собаках и судьях
        String[] columns = {"Владелец", "Кличка собаки", "Порода", "Судья"};
        model = new DefaultTableModel(columns, 0);
        dataTable = new JTable(model);
        scroll = new JScrollPane(dataTable);

        // Компоненты для добавления новой собаки
        JPanel inputPanel = new JPanel();
        dogName = new JTextField(10);
        ownerName = new JTextField(10);
        dogBreed = new JTextField(10);
        judgeName = new JTextField(10);

        inputPanel.add(new JLabel("Кличка собаки:"));
        inputPanel.add(dogName);
        inputPanel.add(new JLabel("Владелец:"));
        inputPanel.add(ownerName);
        inputPanel.add(new JLabel("Порода:"));
        inputPanel.add(dogBreed);
        inputPanel.add(new JLabel("Судья:"));
        inputPanel.add(judgeName);

        // Добавление текстовой области для логов
        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        // Добавление панели и таблицы в окно
        adminFrame.setLayout(new BorderLayout());
        adminFrame.add(toolBar, BorderLayout.NORTH);
        adminFrame.add(scroll, BorderLayout.CENTER);
        adminFrame.add(inputPanel, BorderLayout.SOUTH);
        adminFrame.add(logScrollPane, BorderLayout.EAST);

        // Добавление слушателей к кнопкам
        addListeners();

        // Отображение окна
        adminFrame.setVisible(true);

        // Инициализация FileChooser
        fileChooser = new JFileChooser();
    }

    private void addListeners() {
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.append("Информация сохранена.\n");
                JOptionPane.showMessageDialog(adminFrame, "Данные успешно сохранены!");
            }
        });

        addDog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addDogToTable();
                } catch (InvalidDogDataException ex) {
                    JOptionPane.showMessageDialog(adminFrame, "Ошибка: " + ex.getMessage());
                    logArea.append("Ошибка при добавлении собаки: " + ex.getMessage() + "\n");
                }
            }
        });

        addJudge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String judge = judgeName.getText();
                if (!judge.isEmpty()) {
                    logArea.append("Добавлен судья: " + judge + "\n");
                    judgeName.setText("");
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "Введите имя судьи!");
                }
            }
        });

        deleteDog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow != -1) {
                    String dogName = (String) model.getValueAt(selectedRow, 1);
                    model.removeRow(selectedRow);
                    logArea.append("Удалена собака: " + dogName + "\n");
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "Выберите собаку для удаления!");
                }
            }
        });

        saveToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTableToFile();
            }
        });

        loadFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableFromFile();
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

    // Многопоточный метод для генерации отчета
    private void generateReport() {
        Thread loadThread = new Thread(new LoadTask());
        Thread editThread = new Thread(new EditTask());
        Thread reportThread = new Thread(new ReportTask());

        loadThread.start();
        try {
            loadThread.join();
            editThread.start();
            editThread.join();
            reportThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Поток для загрузки данных из файла
    class LoadTask implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                logArea.append("Загрузка данных из XML...\n");
                loadTableFromFile();
                lock.notify();
            }
        }
    }

    // Поток для редактирования данных
    class EditTask implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                try {
                    lock.wait();
                    logArea.append("Редактирование данных...\n");
                    // Здесь могут быть операции по редактированию
                    saveTableToFile();
                    lock.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Поток для генерации отчета
    class ReportTask implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                try {
                    lock.wait();
                    logArea.append("Создание отчета...\n");
                    // Генерация отчета
                    logArea.append("Отчет готов.\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveTableToFile() {
        if (fileChooser.showSaveDialog(adminFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
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

                logArea.append("Таблица сохранена в XML файл: " + file.getName() + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                logArea.append("Ошибка при сохранении файла: " + e.getMessage() + "\n");
            }
        }
    }

    private void loadTableFromFile() {
        if (fileChooser.showOpenDialog(adminFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Чтение XML файла
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

                // Очистка текущей таблицы перед загрузкой данных
                model.setRowCount(0);

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

                logArea.append("Таблица загружена из XML файла: " + file.getName() + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                logArea.append("Ошибка при загрузке файла: " + e.getMessage() + "\n");
            }
        }
    }


    private void addDogToTable() throws InvalidDogDataException {
        String dog = dogName.getText();
        String owner = ownerName.getText();
        String breed = dogBreed.getText();
        String judge = judgeName.getText();

        if (dog.isEmpty()) {
            throw new InvalidDogDataException("Кличка собаки не может быть пустой.");
        }
        if (owner.isEmpty()) {
            throw new InvalidDogDataException("Имя владельца не может быть пустым.");
        }
        if (breed.isEmpty()) {
            throw new InvalidDogDataException("Порода не может быть пустой.");
        }
        if (judge.isEmpty()) {
            throw new InvalidDogDataException("Имя судьи не может быть пустым.");
        }

        model.addRow(new Object[]{owner, dog, breed, judge});
        logArea.append("Добавлена собака: " + dog + " (Владелец: " + owner + ", Порода: " + breed + ", Судья: " + judge + ")\n");
        clearInputFields();
    }

    private void clearInputFields() {
        dogName.setText("");
        ownerName.setText("");
        dogBreed.setText("");
        judgeName.setText("");
    }

    public static void main(String[] args) {
        new DogShowAdmin().show();
    }
}
