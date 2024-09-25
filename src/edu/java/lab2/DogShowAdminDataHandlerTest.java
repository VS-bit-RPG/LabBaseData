package edu.java.lab2;

import org.junit.jupiter.api.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DogShowAdminDataHandlerTest {
    private DogShowAdminDataHandler handler;
    private DefaultTableModel model;

    @BeforeEach
    public void setUp() {
        handler = new DogShowAdminDataHandler();
        model = new DefaultTableModel(new Object[]{"Owner", "Name", "Breed", "Judge"}, 0);
        model.addRow(new Object[]{"John Doe", "Rex", "German Shepherd", "Judge A"});
        model.addRow(new Object[]{"Jane Smith", "Bella", "Labrador", "Judge B"});
    }

    @Test
    public void testSaveTableToFile() throws Exception {
        File file = new File("test_output.xml");
        handler.saveTableToFile(model, file);

        assertTrue(file.exists(), "XML файл должен быть создан");
    }

    @Test
    public void testLoadTableFromFile() throws Exception {
        File file = new File("test_output.xml");
        handler.saveTableToFile(model, file);

        DefaultTableModel loadedModel = handler.loadTableFromFile(file);
        assertEquals(model.getRowCount(), loadedModel.getRowCount(), "Число строк должно совпадать");
        assertEquals(model.getColumnCount(), loadedModel.getColumnCount(), "Число столбцов должно совпадать");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                assertEquals(model.getValueAt(i, j), loadedModel.getValueAt(i, j), "Данные должны совпадать");
            }
        }
    }

    @AfterEach
    public void tearDown() {
        File file = new File("test_output.xml");
        if (file.exists()) {
            file.delete();
        }
    }
}
