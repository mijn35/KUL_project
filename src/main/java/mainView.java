import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class mainView extends JFrame {
    private JTextField yearField;
    private JTextField companyField;
    private JTextField firstDocumentField;
    private JTextField lastDocumentField;
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
        firstDocumentField = new JTextField(4);
        lastDocumentField = new JTextField(4);


        filterButton = new JButton("Filter");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Fiscal Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Company Code:"));
        panel.add(companyField);
        panel.add(new JLabel("Document Number range:"));
        panel.add(firstDocumentField);
        panel.add(new JLabel("-"));
        panel.add(lastDocumentField);
        panel.add(filterButton);


        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data initially
        loadData("","","","");

        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fiscalYear = yearField.getText();
                String companyCode = companyField.getText();
                String firstDocumentNumber = firstDocumentField.getText();
                String lastDocumentNumber = lastDocumentField.getText();
                loadData(fiscalYear, companyCode, firstDocumentNumber,lastDocumentNumber);
            }
        });
    }

    private void loadData(String fiscalYear, String companyCode, String firstDocumentNumber, String lastDocumentNumber) {
        StringBuilder query = new StringBuilder("SELECT * FROM bkpf WHERE 1=1");
        List<String> parameters = new ArrayList<>();

        // Append conditions based on user input
        if (!fiscalYear.isEmpty()) {
            query.append(" AND GJAHR = ?");
            parameters.add(fiscalYear);
        }
        if (!companyCode.isEmpty()) {
            query.append(" AND BUKRS = ?");
            parameters.add(companyCode);
        }
        if (!firstDocumentNumber.isEmpty() && !lastDocumentNumber.isEmpty()) {
            query.append(" AND BELNR BETWEEN ? AND ?");
            parameters.add(firstDocumentNumber);
            parameters.add(lastDocumentNumber);
        } else if (!firstDocumentNumber.isEmpty()) {
            query.append(" AND BELNR = ?");
            parameters.add(firstDocumentNumber);
        } else if (!lastDocumentNumber.isEmpty()) {
            query.append(" AND BELNR = ?");
            parameters.add(lastDocumentNumber);
        }

        // Fetch data using prepared statements
        List<String[]> data = DatabaseHelper.getTableData(query.toString(), parameters.toArray());

        // Clear previous data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (!data.isEmpty()) {
            String[] columnNames = data.getFirst();
            for (String col : columnNames) {
                tableModel.addColumn(col);
            }
            data.stream().skip(1).forEach(row -> tableModel.addRow(row));
        } else {
            throw new java.lang.Error("Database is empty or loaded incorrectly");
        }
    }

}
