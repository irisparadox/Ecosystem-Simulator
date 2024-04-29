package simulator.view;

import org.json.JSONObject;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.misc.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

public class ControlPanel extends JPanel {
    private Controller _ctrl;
    private ChangeRegionsDialog _changeRegionsDialog;
    private JToolBar _toolaBar;
    private JFileChooser _fc;
    private boolean _stopped = true; // utilizado en los botones de run/stop
    private JButton _fcButton;
    private JButton _mapButton;
    private JButton _changeRegionsButton;
    private JButton _playButton;
    private JButton _stopButton;
    private JButton _quitButton;
    private JSpinner _steps;
    private JTextField _deltaTime;

    ControlPanel(Controller ctrl) {
        _ctrl = ctrl;
        _changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
        initGUI();
    }
    private void initGUI() {
        setLayout(new BorderLayout());
        _toolaBar = new JToolBar();
        add(_toolaBar, BorderLayout.PAGE_START);

        // FC Button
        _fcButton = new JButton();
        _fcButton.setToolTipText("Choose file");
        _fcButton.setIcon(new ImageIcon("resources/icons/open.png"));
        _fcButton.addActionListener((e) -> fileChooserActionEvent());
        _toolaBar.add(_fcButton);

        _toolaBar.addSeparator();

        // Map button
        _mapButton = new JButton();
        _mapButton.setToolTipText("Open map viewer");
        _mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
        _mapButton.addActionListener((e) -> mapViewerActionEvent());
        _toolaBar.add(_mapButton);

        // Change regions button
        _changeRegionsButton = new JButton();
        _changeRegionsButton.setToolTipText("Change regions");
        _changeRegionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
        _changeRegionsButton.addActionListener((e) -> _changeRegionsDialog.open(ViewUtils.getWindow(this)));
        _toolaBar.add(_changeRegionsButton);

        _toolaBar.addSeparator();

        // Play button
        _playButton = new JButton();
        _playButton.setToolTipText("Run simulation");
        _playButton.setIcon(new ImageIcon("resources/icons/run.png"));
        _playButton.addActionListener((e) -> runSimulationActionEvent());
        _toolaBar.add(_playButton);

        // Stop button
        _stopButton = new JButton();
        _stopButton.setToolTipText("Stop simulation");
        _stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
        _stopButton.addActionListener((e) -> stopSimulationActionEvent());
        _toolaBar.add(_stopButton);

        // Steps JSpinner
        _toolaBar.add(Box.createGlue());
        JLabel stepsLabel = new JLabel("Steps: ");
        _toolaBar.add(stepsLabel);
        _steps = new JSpinner(new SpinnerNumberModel(10000, 0, 10000, 10));
        _steps.setPreferredSize(new Dimension(80,40));
        _steps.setMaximumSize(new Dimension(80,40));
        _toolaBar.add(_steps);

        _toolaBar.addSeparator();

        // Delta time

        JLabel dtLabel = new JLabel("Delta-time: ");
        _toolaBar.add(dtLabel);
        _deltaTime = new JTextField(String.valueOf(Main.dt));
        _deltaTime.setPreferredSize(new Dimension(80,40));
        _deltaTime.setMaximumSize(new Dimension(80,40));
        _toolaBar.add(_deltaTime);

        // Quit Button

        _toolaBar.add(Box.createGlue()); // this aligns the button to the right
        _toolaBar.addSeparator();
        _quitButton = new JButton();
        _quitButton.setToolTipText("Quit");
        _quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
        _quitButton.addActionListener((e) -> ViewUtils.quit(this));
        _toolaBar.add(_quitButton);
    }

    private void fileChooserActionEvent() throws RuntimeException {
        _fc = new JFileChooser();
        _fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
        _fc.setFileFilter(new FileNameExtensionFilter("JSON file", "json"));
        _fc.showOpenDialog(ViewUtils.getWindow(this));
        File selectedFile = _fc.getSelectedFile();
        if(selectedFile == null)
            return;
        JSONObject jsonObject;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            // parse del file al json
            jsonObject = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int cols = jsonObject.getInt("cols");
        int rows = jsonObject.getInt("rows");
        int height = jsonObject.getInt("height");
        int width = jsonObject.getInt("width");
        _ctrl.reset(cols, rows, width, height);
        _changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
        _ctrl.load_data(jsonObject);
    }

    private void mapViewerActionEvent(){
        MapWindow mapWindow = new MapWindow(ViewUtils.getWindow(this), _ctrl);
    }

    private void runSimulationActionEvent() {
        change_buttons_state(false);
        _stopped = false;
        run_sim((int) _steps.getValue(), Double.parseDouble(_deltaTime.getText()));
    }

    private void stopSimulationActionEvent() {
        _stopped = true;
    }

    private void run_sim(int n, double dt) {
        if (n > 0 && !_stopped) {
            try {
                _ctrl.advance(dt);
                SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
            } catch (Exception e) {
                ViewUtils.showErrorMsg(e.getMessage());
                change_buttons_state(true);
                _stopped = true;
            }
        } else {
            change_buttons_state(true);
            _stopped = true;
        }
    }

    private void change_buttons_state(boolean state){
        _fcButton.setEnabled(state);
        _changeRegionsButton.setEnabled(state);
        _playButton.setEnabled(state);
    }
}
