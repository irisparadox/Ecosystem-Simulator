package simulator.view;

import simulator.control.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainWindow extends JFrame {

    ControlPanel controlPanel;
    StatusBar statusBar;
    InfoTable speciesTable;
    InfoTable regionsTable;
    private Controller _ctrl;

    public MainWindow(Controller ctrl) {
        super("Ecosystem Simulator");
        _ctrl = ctrl;
        initGUI();
    }

    private void initGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        controlPanel = new ControlPanel(_ctrl);
        mainPanel.add(controlPanel, BorderLayout.PAGE_START);

        statusBar = new StatusBar(_ctrl);
        mainPanel.add(statusBar, BorderLayout.PAGE_END);

        // Definición del panel de tablas (usa un BoxLayout vertical)

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        speciesTable = new InfoTable("Species", new SpeciesTableModel(_ctrl));
        speciesTable.setPreferredSize(new Dimension(500, 250));

        contentPanel.add(speciesTable, BorderLayout.PAGE_START);

        regionsTable = new InfoTable("Regions", new RegionsTableModel(_ctrl));
        regionsTable.setPreferredSize(new Dimension(500, 250));

        contentPanel.add(regionsTable, BorderLayout.PAGE_END);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                ViewUtils.quit(MainWindow.this);
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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
