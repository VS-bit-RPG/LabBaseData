package edu.java.lab2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DogShowAdmin {
    // Объявление графических компонентов
    private JFrame adminFrame;
    private DefaultTableModel model;
    private JButton save;
    private JButton addDog;
    private JButton addJudge;
    private JToolBar toolBar;
    private JScrollPane scroll;
    private JTable dataTable;
    private JTextField dogName;
    private JTextField ownerName;
    private JTextField dogBreed;
    private JTextField judgeName;
    private JTextArea logArea;

    public void show() {
        // Создание окна
        adminFrame = new JFrame("Администратор выставки собак");
        adminFrame.setSize(600, 400);
        adminFrame.setLocation(100, 100);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание кнопок
        save = new JButton("Сохранить");
        save.setToolTipText("Сохранить информацию");

        addDog = new JButton("Добавить собаку");
        addDog.setToolTipText("Добавить новую собаку");

        addJudge = new JButton("Добавить судью");
        addJudge.setToolTipText("Добавить нового судью");

        // Панель инструментов
        toolBar = new JToolBar("Панель инструментов");
        toolBar.add(save);
        toolBar.add(addDog);
        toolBar.add(addJudge);

        // Настройка таблицы с данными о собаках и судьях
        String[] columns = {"Владелец", "Кличка собаки", "Порода", "Судья"};
        String[][] data = {
                {"Иванов", "Рекс", "Немецкая овчарка", "Судья Петров"},
                {"Сидоров", "Барсик", "Шпиц", "Судья Иванова"},
                {"Кузнецов", "Бобик", "Лабрадор", "Судья Сидорова"}
        };

        model = new DefaultTableModel(data, columns);
        dataTable = new JTable(model);
        scroll = new JScrollPane(dataTable);

        // Компоненты для добавления новой собаки
        JPanel inputPanel = new JPanel();
        dogName = new JTextField("Кличка собаки", 10);
        ownerName = new JTextField("Имя владельца", 10);
        dogBreed = new JTextField("Порода", 10);
        judgeName = new JTextField("Судья", 10);

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
    }

    private void addListeners() {
        // Слушатель для кнопки "Сохранить"
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logArea.append("Информация сохранена.\n");
                JOptionPane.showMessageDialog(adminFrame, "Данные успешно сохранены!");
            }
        });

        // Слушатель для кнопки "Добавить собаку"
        addDog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dog = dogName.getText();
                String owner = ownerName.getText();
                String breed = dogBreed.getText();
                String judge = judgeName.getText();

                if (!dog.isEmpty() && !owner.isEmpty() && !breed.isEmpty() && !judge.isEmpty()) {
                    model.addRow(new Object[]{owner, dog, breed, judge});
                    logArea.append("Добавлена собака: " + dog + " (Владелец: " + owner + ", Порода: " + breed + ", Судья: " + judge + ")\n");
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "Заполните все поля!");
                }
            }
        });

        // Слушатель для кнопки "Добавить судью"
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
    }

    // Метод для очистки полей ввода
    private void clearInputFields() {
        dogName.setText("");
        ownerName.setText("");
        dogBreed.setText("");
        judgeName.setText("");
    }

    public static void main(String[] args) {
        // Создание и отображение экранной формы
        new DogShowAdmin().show();
    }
}
