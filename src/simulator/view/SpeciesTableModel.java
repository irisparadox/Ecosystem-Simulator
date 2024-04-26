package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
    Controller _cntrl;
    int rows, cols;
    SpeciesTableModel(Controller ctrl) {
        // TODO inicializar estructuras de datos correspondientes
        _cntrl = ctrl;
        // TODO registrar this como observador
        _cntrl.addObserver(this);
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return cols;
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
