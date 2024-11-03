import org.jdesktop.swingx.JXTreeTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class mainView extends JFrame {
    private JTextField yearField;
    private JTextField companyField;
    private JTextField firstDocumentField;
    private JTextField lastDocumentField;
    private JButton filterButton;
    private TreeTable treeTable;

    public mainView() {
        setTitle("Database Table Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Filter fields
        yearField = new JTextField(4);
        companyField = new JTextField(4);
        firstDocumentField = new JTextField(4);
        lastDocumentField = new JTextField(4);

        // Button
        filterButton = new JButton("Filter");

        // Pannels
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

        // Initial load data
        loadData();

        // ScrollPannel
        JScrollPane scrollPane = new JScrollPane(treeTable.getTreeTable());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fiscalYear = yearField.getText();
                String companyCode = companyField.getText();
                String firstDocumentNumber = firstDocumentField.getText();
                String lastDocumentNumber = lastDocumentField.getText();
                loadData(fiscalYear, companyCode, firstDocumentNumber, lastDocumentNumber);
            }
        });
    }

    private void loadData() {
        loadData("", "", "", "");
    }

    private void loadData(String fiscalYear, String companyCode, String firstDocumentNumber, String lastDocumentNumber) {
        List<String[]> bkpfData = loadBkpf(fiscalYear, companyCode, firstDocumentNumber, lastDocumentNumber);
        List<String[]> bsegData = loadBseg(fiscalYear, companyCode, firstDocumentNumber, lastDocumentNumber);

        // Create the TreeTable with the loaded data
        if (treeTable != null) {
            treeTable.updateData(bkpfData.subList(1, bkpfData.size()), bsegData.subList(1, bsegData.size()));
        } else {
            // make the header
            String[] bkpfHeaders = bkpfData.getFirst();
            String[] bsegHeaders = bsegData.getFirst();
            String[] headers = new String[bkpfHeaders.length + bsegHeaders.length - 3];
            System.arraycopy(bkpfHeaders, 0, headers, 0, bkpfHeaders.length); // Copy bkpf headers
            System.arraycopy(bsegHeaders, 3, headers, bkpfHeaders.length, bsegHeaders.length - 3); // Skip first 3 bseg headers

            // make treetable with headers and data nicely seperated
            treeTable = new TreeTable(bkpfData.subList(1, bkpfData.size()), bsegData.subList(1, bsegData.size()), headers);
        }
    }


    private List<String[]> loadBkpf(String fiscalYear, String companyCode, String firstDocumentNumber, String lastDocumentNumber) {

        // Build the SQL query for main documents
        StringBuilder query = new StringBuilder("SELECT * FROM bkpf WHERE 1=1");
        List<String> parameters = new ArrayList<>();

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

        // Fetch main documents from the database
        List<String[]> mainData = DatabaseHelper.getTableData(query.toString(), parameters.toArray());
        return mainData;
    }

    private List<String[]> loadBseg(String fiscalYear, String companyCode, String firstDocumentNumber, String lastDocumentNumber) {

        // Build the SQL query for sub documents
        StringBuilder query = new StringBuilder("SELECT * FROM bseg WHERE 1=1");
        List<String> parameters = new ArrayList<>();

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

        // Fetch main documents from the database
        List<String[]> subData = DatabaseHelper.getTableData(query.toString(), parameters.toArray());
        return subData;
    }
}
