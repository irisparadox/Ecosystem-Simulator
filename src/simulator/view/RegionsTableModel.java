package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
        private Controller _cntrl;
        private int _cols, _rows;
        private Map<Integer, Map<Integer, Map<String, Map<Diet, Integer>>>> _data;
    RegionsTableModel(Controller ctrl) {
        // TODO inicializar estructuras de datos correspondientes
        _cntrl = ctrl;
        _data = new HashMap<>();
        // TODO registrar this como observador
    }

    @Override
    public int getRowCount() {
        return _rows;
    }

    @Override
    public int getColumnCount() {
        return _rows;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {

    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {

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
}
