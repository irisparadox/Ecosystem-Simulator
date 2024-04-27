package simulator.view;

import simulator.control.Controller;
import simulator.model.*;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
    private Controller _cntrl;
    private Map<String, Map<State, Integer>> _data;
    private String[] _columnLabels;
    private int _rows, _cols;
    SpeciesTableModel(Controller ctrl) {
        _cntrl = ctrl;
        _data = new HashMap<>();
        _cntrl.addObserver(this);

        _columnLabels = getColumnLabels();
        _cols = _columnLabels.length;
        _rows = _data.size();
    }

    private String[] getColumnLabels() {
        State[] states = State.values();
        String[] names = new String[states.length + 1];
        names[0] = "Species";
        for(int i = 1; i < states.length + 1; i++){
            names[i] = states[i - 1].name();
        }
        return names;
    }

    @Override
    public String getColumnName(int column) {
        return _columnLabels[column];
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value;
        String[] speciesArray = _data.keySet().toArray(new String[0]);
        String species = speciesArray[rowIndex];
        Map<State, Integer> speciesData = _data.get(species);

        if(columnIndex == 0) {
            value = species;
        } else {
            State state = State.values()[columnIndex - 1];
            value = speciesData.getOrDefault(state, 0);
        }
        return value;
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        for(AnimalInfo animal : animals) {
            countAnimalOnTable(animal);
        }
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        _data.clear();
        _cols = 0;
        _rows = 0;
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        countAnimalOnTable(a);
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        for(AnimalInfo animal : animals) {
            countAnimalOnTable(animal);
        }
    }

    private void countAnimalOnTable(AnimalInfo animal) {
        String geneticCode = animal.get_genetic_code();
        State state = animal.get_state();
        Map<State, Integer> speciesMap = _data.getOrDefault(geneticCode, new HashMap<>());
        speciesMap.put(state, speciesMap.getOrDefault(state, 0) + 1);
        _data.put(geneticCode, speciesMap);
    }
}
