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
    private JLabel simulation_timer;
    private JLabel animals_counter;
    private JLabel dimension_size;
    private JLabel dimension_cols_rows;
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
        simulation_timer = new JLabel();
        JLabel animals_label = new JLabel("Total animals: ");
        animals_counter = new JLabel();
        JLabel dimensions_label = new JLabel("Dimensions: ");
        dimension_size = new JLabel();
        dimension_cols_rows = new JLabel();

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
         this.animals_counter.setText(String.valueOf(animals.size()));
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        this.simulation_timer.setText(String.valueOf(time));
        this.animals_counter.setText(String.valueOf(animals.size()));
    }

    private void init(double time, MapInfo map, List<AnimalInfo> animals) {
        this.dimension_size.setText(map.get_height() + "x" + map.get_width());
        this.dimension_cols_rows.setText(map.get_rows() + "x" + map.get_cols());
        this.simulation_timer.setText(String.valueOf(time));
        this.animals_counter.setText(String.valueOf(animals.size()));
    }

}
