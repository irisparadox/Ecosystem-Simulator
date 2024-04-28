package simulator.view;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

public class MapWindow extends JFrame implements EcoSysObserver {
    private Controller _ctrl;
    private AbstractMapViewer _viewer;
    private Frame _parent;
    MapWindow(Frame parent, Controller ctrl) {
        super("Map Viewer");
        _ctrl = ctrl;
        _parent = parent;
        intiGUI();
        _ctrl.addObserver(this);
    }
    private void intiGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        _viewer = new MapViewer();
        mainPanel.add(_viewer, BorderLayout.CENTER);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                _ctrl.removeObserver(MapWindow.this);
            }

            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        pack();
        if (_parent != null)
            setLocation(
                    _parent.getLocation().x + _parent.getWidth()/2 - getWidth()/2,
                    _parent.getLocation().y + _parent.getHeight()/2 - getHeight()/2);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        SwingUtilities.invokeLater(() -> { _viewer.reset(time, map, animals); pack();});
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        SwingUtilities.invokeLater(() -> { _viewer.reset(time, map, animals); pack();});
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        SwingUtilities.invokeLater(() -> { _viewer.update(animals, time);});
    }
}