import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TreeTable {
    private final String[] headings;
    private DefaultTreeTableModel model;
    private JXTreeTable table;
    private List<String[]> bkpfcontent;
    private List<String[]> bsegcontent;

    public TreeTable(List<String[]> bkpfcontent,List<String[]> bsegcontent, String[] headings) {
        this.bkpfcontent = bkpfcontent;
        this.bsegcontent = bsegcontent;
        this.headings = headings;
        buildTree();
    }

    private void buildTree() {
        Node root = new RootNode("Root");
        for (String[] bkpfdata : this.bkpfcontent) {
            ChildNode child = new ChildNode(bkpfdata);
            root.add(child);
            for (String[] bsegdata : this.bsegcontent) {
                if (Objects.equals(bsegdata[0], bkpfdata[0]) && Objects.equals(bsegdata[1], bkpfdata[1]) && Objects.equals(bsegdata[2], bkpfdata[2])) {
                    // bsegdata with open places for bkpf columns
                    String[] combinedData = new String[headings.length];

                    // first 3 columns are shared
                    System.arraycopy(bsegdata, 0, combinedData, 0, 3); // Copy the first 3 columns from bsegdata

                    // the rest of the bkpf columns are blank
                    int bkpflength = headings.length-bsegdata.length+3; //headings = bkpf+bseg-3 for the 3 shared columns
                    for (int k = 3; k < bkpflength; k++) {
                        combinedData[k] = "";
                    }

                    // Fill in the remaining BSEG data, starting from index 3
                    System.arraycopy(bsegdata, 3, combinedData, bkpflength, bsegdata.length - 3); // Copy BSEG data from index 3 onwards

                    ChildNode subchild = new ChildNode(combinedData);
                    child.add(subchild);
                }
            }
        }
        model = new DefaultTreeTableModel(root, Arrays.asList(headings));
        if (table != null){
            table.setTreeTableModel(model);
        }
        else {
            table  = new JXTreeTable(model);
            table.setShowGrid(true,true);
            table.setColumnControlVisible(true);
            table.setAutoCreateRowSorter(true);
        }
        table.packAll(); //wraps column width around content size

    }

    public void updateData(List<String[]> newBkpfContent, List<String[]> newBsegContent) {
        this.bkpfcontent = newBkpfContent;
        this.bsegcontent = newBsegContent;
        buildTree(); // Rebuild the tree with new data
    }

    public JXTreeTable getTreeTable(){
        if (model == null) {
            buildTree(); // Build the initial tree if not already done
        }
        return table;
    }
}
