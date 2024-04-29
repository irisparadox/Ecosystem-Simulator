package simulator.view;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
    private DefaultComboBoxModel<String> _regionsModel;
    private DefaultComboBoxModel<String> _fromRowModel;
    private DefaultComboBoxModel<String> _toRowModel;
    private DefaultComboBoxModel<String> _fromColModel;
    private DefaultComboBoxModel<String> _toColModel;
    private DefaultTableModel _dataTableModel;
    private Controller _ctrl;
    private List<JSONObject> _regionsInfo;
    private String[] _headers = { "Key", "Value", "Description" };
    private JTable table;
    private JComboBox<String> _regionComboBox;
    private JComboBox<String> _fromRowComboBox;
    private JComboBox<String> _toRowComboBox;
    private JComboBox<String> _fromColComboBox;
    private JComboBox<String> _toColComboBox;
    private JButton okButton;
    private JButton cancelButton;
    ChangeRegionsDialog(Controller ctrl) {
        super((Frame)null, true);
        _ctrl = ctrl;
        initGUI();
        _ctrl.addObserver(this);
    }
    private void initGUI() {
        setTitle("Change Regions");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel helpTextPanel = new JPanel();
        helpTextPanel.setLayout(new BoxLayout(helpTextPanel, BoxLayout.LINE_AXIS));
        JLabel helpText = new JLabel("<html>Select a region type, the rows/columns interval, and provide values"
        + " for te parameters in the Value column (default values are used for parameters with no value)</html>");
        helpText.setHorizontalAlignment(SwingConstants.LEFT);
        helpText.setVerticalAlignment(SwingConstants.TOP);
        helpTextPanel.add(helpText);
        mainPanel.add(helpTextPanel);

        // _regionsInfo se usará para establecer la información en la tabla
        _regionsInfo = Main._region_factory.get_info();
        // _dataTableModel es un modelo de tabla que incluye todos los parámetros de
        // la region
        _dataTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        _dataTableModel.setColumnIdentifiers(_headers);
        table = new JTable(_dataTableModel);
        JScrollPane tablePanel = new JScrollPane(table);
        mainPanel.add(tablePanel);

        JPanel comboBoxes = new JPanel();
        comboBoxes.setLayout(new BoxLayout(comboBoxes, BoxLayout.X_AXIS));
        _regionsModel = new DefaultComboBoxModel<>();
        for(int i = 0; i < _regionsInfo.size(); i++){
            _regionsModel.addElement(_regionsInfo.get(i).getString("type"));
        }
        _regionComboBox = new JComboBox<>(_regionsModel);
        JLabel region = new JLabel("Region type: ");
        comboBoxes.add(region);
        comboBoxes.add(_regionComboBox);

        _regionComboBox.addActionListener(e -> {
            updateTableValues();
        });

        _fromRowModel = new DefaultComboBoxModel<>();
        _toRowModel = new DefaultComboBoxModel<>();
        _fromColModel = new DefaultComboBoxModel<>();
        _toColModel = new DefaultComboBoxModel<>();

        _fromRowComboBox = new JComboBox<>(_fromRowModel);
        _toRowComboBox = new JComboBox<>(_toRowModel);
        _fromColComboBox = new JComboBox<>(_fromColModel);
        _toColComboBox = new JComboBox<>(_toColModel);

        JLabel rowFromTo = new JLabel("Row from/to: ");
        comboBoxes.add(rowFromTo);
        comboBoxes.add(_fromRowComboBox);
        comboBoxes.add(_toRowComboBox);

        JLabel colFromTo = new JLabel("Column from/to: ");
        comboBoxes.add(colFromTo);
        comboBoxes.add(_fromColComboBox);
        comboBoxes.add(_toColComboBox);
        mainPanel.add(comboBoxes);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        okButton = new JButton("Ok");
        okButton.setToolTipText("Modify the selected region with the current changes.");
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Cancel the operation.");

        okButton.addActionListener(e -> {
            try {
                changeRegionsProperties();
                setVisible(false);
            } catch (IllegalArgumentException ex){
                ViewUtils.showErrorMsg(ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> {
            setVisible(false);
        });

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        mainPanel.add(buttonsPanel);

        setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
        pack();
        setResizable(false);
        setVisible(false);
    }
    public void open(Frame parent) {
        setLocation(//
                parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
                parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        for(int i = 0; i < map.get_rows(); i++){
            _fromRowModel.addElement(String.valueOf(i));
            _toRowModel.addElement(String.valueOf(i));
        }
        for(int i = 0; i < map.get_cols(); i++){
            _fromColModel.addElement(String.valueOf(i));
            _toColModel.addElement(String.valueOf(i));
        }
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        _fromRowModel.removeAllElements();
        _fromColModel.removeAllElements();
        _toRowModel.removeAllElements();
        _toColModel.removeAllElements();
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {

    }

    private void updateTableValues(){
        _dataTableModel.setRowCount(0);
        int regionType = _regionComboBox.getSelectedIndex();
        JSONObject selectedRegion = _regionsInfo.get(regionType);

        if(selectedRegion != null){
            JSONObject data = selectedRegion.getJSONObject("data");
            Iterator<String> keys = data.keys();
            while (keys.hasNext()){
                String key = keys.next();
                Object value = data.get(key);
                _dataTableModel.addRow(new Object[]{key, 0, value});
            }
        }
        _dataTableModel.fireTableDataChanged();
    }

    private void changeRegionsProperties() throws IllegalArgumentException {
        JSONObject jsonObject = new JSONObject();
        JSONObject regionObject = new JSONObject();
        JSONArray regionsArray = new JSONArray();

        JSONArray rowArray = new JSONArray();
        JSONArray colArray = new JSONArray();
        rowArray.put(_fromRowModel.getSelectedItem());
        rowArray.put(_toRowModel.getSelectedItem());
        colArray.put(_fromColModel.getSelectedItem());
        colArray.put(_toColModel.getSelectedItem());
        regionObject.put("row", rowArray);
        regionObject.put("col", colArray);

        JSONObject spec = new JSONObject();
        JSONObject region_data = new JSONObject();

        for(int i = 0; i < _dataTableModel.getRowCount(); i++) {
            Object key = _dataTableModel.getValueAt(i, 0);
            Object value = _dataTableModel.getValueAt(i, 1);

            if(value != null && !value.toString().isEmpty()){
                region_data.put(key.toString(), value);
            } else throw new IllegalArgumentException("Table values cannot be null");
        }

        spec.put("type", _regionsModel.getSelectedItem());
        spec.put("data", region_data);
        regionObject.put("spec", spec);
        regionsArray.put(regionObject);
        jsonObject.put("regions",regionsArray);

        _ctrl.set_regions(jsonObject);
    }
}
