package simulator.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.*;

public class InfoTable extends JPanel {
    String _title;
    TableModel _tableModel;
    InfoTable(String title, TableModel tableModel) {
        _title = title;
        _tableModel = tableModel;
        initGUI();
    }
    private void initGUI() {
        this.setLayout(new BorderLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(_title);
        this.setBorder(titledBorder);
        JTable table = new JTable(_tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }
}