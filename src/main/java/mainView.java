import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class mainView extends JFrame {
    private JTextField filterField;
    private JButton filterButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public void DatabaseViewer() {
        setTitle("Database Table Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        filterField = new JTextField(20);
        filterButton = new JButton("Filter");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Filter by column:"));
        panel.add(filterField);
        panel.add(filterButton);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data initially
        loadData("");

        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterText = filterField.getText();
                loadData(filterText);
            }
        });
    }

    private void loadData(String filter) {
        String query = "SELECT * FROM your_table_name";

        List<String[]> data = DatabaseHelper.getTableData(query);

        // Clear previous data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data.size() > 0) {
            String[] columnNames = data.get(0);
            for (String col : columnNames) {
                tableModel.addColumn(col);
            }
            for (String[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}
