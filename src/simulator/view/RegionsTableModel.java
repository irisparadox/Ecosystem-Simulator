package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
        private Controller _cntrl;
        private int _cols, _rows;
        private String[] _columnLabels;

        private class RegionData {
            private int _region_col, _region_row;
            private String _description;
            private Map<Diet, Integer> _dietData;
            private RegionData() {
            }
            private RegionData(int row, int col, String description, Map<Diet, Integer> dietData) {
                this._region_col = col;
                this._region_row = row;
                this._description = description;
                this._dietData = dietData;
            }
        }
        List<RegionData> _data;
    RegionsTableModel(Controller ctrl) {
        _cntrl = ctrl;
        _data = new LinkedList<>();
        _cntrl.addObserver(this);
        _columnLabels = get_columnLabels();
        _cols = _columnLabels.length;
        _rows = _data.size();
    }

    private String[] get_columnLabels(){
        Diet[] diets = Diet.values();
        String[] labels = new String[diets.length + 3];
        labels[0] = "Row";
        labels[1] = "Column";
        labels[2] = "Description";
        for(int i = 3; i < diets.length + 3; i++) {
            labels[i] = diets[i - 3].name();
        }

        return labels;
    }

    @Override
    public int getRowCount() {
        return _rows;
    }

    @Override
    public int getColumnCount() {
        return _cols;
    }

    @Override
    public String getColumnName(int column) {
        return _columnLabels[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value;
        switch (columnIndex) {
            case 0 -> {
                value = _data.get(rowIndex)._region_row;
            }
            case 1 -> {
                value = _data.get(rowIndex)._region_col;
            }
            case 2 -> {
                value = _data.get(rowIndex)._description;
            }
            default -> {
                Diet diet = Diet.valueOfIgnoreCase(_columnLabels[columnIndex]);
                value = _data.get(rowIndex)._dietData.get(diet);
            }
        }
        return value;
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        update(map);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        _data.clear();
        _columnLabels = get_columnLabels();
        _cols = _columnLabels.length;
        _rows = 0;
        this.fireTableDataChanged();
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        update(map);
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
        update(map);
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        update(map);
    }

    private void update(MapInfo map){
        int i = 0;
        while(i < map.get_rows() * map.get_cols()) {
           MapInfo.RegionData dataRecord = map.iterator().next();
           int row = dataRecord.row();
           int col = dataRecord.col();
           String description = dataRecord.r().toString();
           Map<Diet, Integer> dietMap = new HashMap<>();
           List<AnimalInfo> regionAnimals = dataRecord.r().getAnimalsInfo();
           for(Diet diet : Diet.values()){
               dietMap.put(diet, 0);
           }
           for(AnimalInfo animal : regionAnimals) {
               Diet diet = animal.get_diet();
               dietMap.put(diet, dietMap.getOrDefault(diet, 0) + 1);
           }
           RegionData data = new RegionData(row, col, description, dietMap);
           if(i < _data.size())
               _data.set(i, data);
           else
               _data.add(data);
           i++;
        }
        _rows = _data.size();
        this.fireTableDataChanged();
    }
}
