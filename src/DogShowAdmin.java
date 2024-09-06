package edu.java.lab2;

import java.awt.BorderLayout;
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

        // Добавление панели и таблицы в окно
        adminFrame.setLayout(new BorderLayout());
        adminFrame.add(toolBar, BorderLayout.NORTH);
        adminFrame.add(scroll, BorderLayout.CENTER);
        adminFrame.add(inputPanel, BorderLayout.SOUTH);

        // Отображение окна
        adminFrame.setVisible(true);
    }

    public static void main(String[] args) {
        // Создание и отображение экранной формы
        new DogShowAdmin().show();
    }
}
