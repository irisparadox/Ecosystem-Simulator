package simulator.view;

import org.json.JSONObject;
import simulator.control.Controller;
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

    ControlPanel(Controller ctrl) {
        _ctrl = ctrl;
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
        //_mapButton.addActionListener((e) -> _changeRegionsDialog.open(ViewUtils.getWindow(this)));
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
        _ctrl.load_data(jsonObject);
    }

    private void mapViewerActionEvent(){

    }

    private void runSimulationActionEvent(){

    }

    private void stopSimulationActionEvent() {

    }
}
