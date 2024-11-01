import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class mainView extends JFrame {
    private JTextField yearField;
    private JTextField companyField;
    private JTextField documentField;
    private JButton filterButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public void DatabaseViewer() {
        setTitle("Database Table Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Filter fields
        yearField = new JTextField(4);
        companyField = new JTextField(4);
        documentField = new JTextField(10);


        filterButton = new JButton("Filter");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Fiscal Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Company Code:"));
        panel.add(companyField);
        panel.add(new JLabel("Document Number:"));
        panel.add(documentField);
        panel.add(filterButton);


        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data initially
        loadData("","","");

        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fiscalYear = yearField.getText();
                String companyCode = companyField.getText();
                String documentNumber = documentField.getText();
                loadData(fiscalYear, companyCode, documentNumber);
            }
        });
    }

    private void loadData(String fiscalYear, String companyCode, String documentNumber) {
        String query = "SELECT * FROM bkpf WHERE 1=1";

        // Append conditions based on user input
        if (!fiscalYear.isEmpty()) {
            query += " AND GJAHR = '" + fiscalYear + "'";
        }
        if (!companyCode.isEmpty()) {
            query += " AND BUKRS = '" + companyCode + "'";
        }
        if (!documentNumber.isEmpty()) {
            query += " AND BELNR = '" + documentNumber + "'";
        }

        List<String[]> data = DatabaseHelper.getTableData(query);

        // Clear previous data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (!data.isEmpty()) {
            String[] columnNames = data.getFirst();
            for (String col : columnNames) {
                tableModel.addColumn(col);
            }
            data.stream().skip(1).forEach((row) -> tableModel.addRow(row));
        }
        else{
            throw new java.lang.Error("database is empty or loaded incorrectly");
        }
    }
}
