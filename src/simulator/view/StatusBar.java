package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatusBar extends JPanel implements EcoSysObserver {
    private double _simulation_time;
    private int _animal_count;
    private int _sim_height, _sim_width;
    private int _sim_rows, _sim_cols;
    private Controller _cntrl;
    StatusBar(Controller ctrl) {
        initGUI();
        _cntrl = ctrl;
        _cntrl.addObserver(this);

    }
    private void initGUI() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createBevelBorder(1));
        JLabel simulation_timer_label = new JLabel("Time: ");
        JLabel simulation_timer = new JLabel(String.valueOf(_simulation_time));
        JLabel animals_label = new JLabel("Total animals: ");
        JLabel animals_counter = new JLabel(String.valueOf(_animal_count));
        JLabel dimensions_label = new JLabel("Dimensions: ");
        JLabel dimension_size = new JLabel(_sim_height + "x" + _sim_width);
        JLabel dimension_cols_rows = new JLabel(_sim_rows + "x" + _sim_cols);

        this.add(simulation_timer_label);
        this.add(simulation_timer);

        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(10, 20));
        this.add(s);

        this.add(animals_label);
        this.add(animals_counter);

        JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
        s2.setPreferredSize(new Dimension(10, 20));
        this.add(s2);

        this.add(dimensions_label);
        this.add(dimension_size);
        this.add(dimension_cols_rows);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        init(time, map, animals);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        init(time, map, animals);
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        this._animal_count = animals.size();
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
        this._sim_height = map.get_height();
        this._sim_width = map.get_width();
        this._sim_cols = map.get_cols();
        this._sim_rows = map.get_rows();
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        this._simulation_time = time;
        this._animal_count = animals.size();
    }

    private void init(double time, MapInfo map, List<AnimalInfo> animals) {
        this._sim_height = map.get_height();
        this._sim_width = map.get_width();
        this._sim_cols = map.get_cols();
        this._sim_rows = map.get_rows();
        this._simulation_time = time;
        this._animal_count = animals.size();
    }

}
