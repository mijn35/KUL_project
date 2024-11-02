import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TreeTable {
    private String[] headings;
    private Node root;
    private DefaultTreeTableModel model;
    private JXTreeTable table;
    private List<String[]> bkpfcontent;
    private List<String[]> bsegcontent;

    public TreeTable(List<String[]> bkpfcontent,List<String[]> bsegcontent, String[] headings) {
        this.bkpfcontent = bkpfcontent;
        this.bsegcontent = bsegcontent;
        this.headings = headings;
    }

    public JXTreeTable getTreeTable(){
        root = new RootNode("Root");
        for (String[] bkpfdata : this.bkpfcontent){
            ChildNode child = new ChildNode(bkpfdata);
            root.add(child);
            for (String[] bsegdata : this.bsegcontent){
                if(Objects.equals(bsegdata[0], bkpfdata[0]) && Objects.equals(bsegdata[1], bkpfdata[1]) && Objects.equals(bsegdata[2], bkpfdata[2])){
                    ChildNode subchild = new ChildNode(bsegdata);
                    child.add(subchild);
                }
            }
        }
        model = new DefaultTreeTableModel(root, Arrays.asList(headings));
        table = new JXTreeTable(model);
        table.setShowGrid(true,true);
        table.setColumnControlVisible(true);
        table.setAutoCreateRowSorter(true);
        table.packAll(); //wraps column width around content size

        return table;
    }
}
