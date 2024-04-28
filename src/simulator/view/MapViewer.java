package simulator.view;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura/ de la simulación -- se supone que siempre van a ser iguales
	// al tamaño del componente
	private int _width;
	private int _height;

	// Número de filas/columnas de la simulación
	private int _rows;
	private int _cols;

	// Anchura/altura de una región
	int _rwidth;
	int _rheight;

	// Mostramos sólo animales con este estado. Los posibles valores de _currState
	// son null, y los valores deAnimal.State.values(). Si es null mostramos todo.
	State _currState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	// Un mapa para la información sobre las especies
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto
	private Font _font = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no
	private boolean _showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'h':
						_showHelp = !_showHelp;
						repaint();
						break;
					case 's':
						int index = 0;
						State[] values = State.values();
						if(_currState != null){
							index = _currState.ordinal() + 1;
						}
						if(index >= values.length){
							_currState = null;
						} else {
							_currState = values[index];
						}
						repaint();
					default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus(); // Esto es necesario para capturar las teclas cuando el ratón está sobre este
				// componente.
			}
		});

		// Por defecto mostramos todos los animales
		_currState = null;

		// Por defecto mostramos el texto de ayuda
		_showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto
		g.setFont(_font);

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		// h: toggle help
		// s: show animals of a specific state

		if(_showHelp){
			g.drawString("[H]: Toggle help", 10, 15);
			g.drawString("[S]: Cycle between animals of a specific state", 10, 30);
		}

	}

	private boolean visible(AnimalInfo a) {
		return _currState == null || a.get_state() == _currState;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		for(int i = 0; i < _rows; i++){
			int y = i * _rheight;
			g.drawLine(0, y, _width, y);
		}
		for(int j = 0; j < _cols; j++){
			int x = j * _rwidth;
			g.drawLine(x, 0, x, _height);
		}

		// Dibujar los animales
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

			if(esp_info == null) {
				_kindsInfo.put(a.get_genetic_code(), new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code())));
				esp_info = _kindsInfo.get(a.get_genetic_code());
			}
			esp_info._count++;

			int size = (int) (a.get_age() / 2) + 2;
			int x = (int) a.get_position().getX();
			int y = (int) a.get_position().getY();
			g.setColor(esp_info._color);
			g.fillRoundRect(x, y, size, size, 1, 1);
		}

		if(_currState != null) {
			g.setColor(Color.RED);
			drawStringWithRect(g, 10, _height - 30, "State: " + _currState.name());
		}

		g.setColor(Color.MAGENTA);
		drawStringWithRect(g, 10, _height - 10, "Time: " + String.format("%.3f", time));

		int y = _height - 50;
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			g.setColor(e.getValue()._color);
			drawStringWithRect(g, 10, y, e.getKey() + ": " + e.getValue()._count);
			y -= 20;
			SpeciesInfo info = e.getValue();
			info._count = 0;
			e.setValue(info);
		}
	}

	// Un método que dibujar un texto con un rectángulo
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		_objs = new LinkedList<>(objs);
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		_width = map.get_width();
		_height = map.get_height();
		_rwidth = map.get_region_width();
		_rheight = map.get_region_height();
		_cols = map.get_cols();
		_rows = map.get_rows();

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset
		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		// Dibuja el estado
		update(animals, time);
	}

}
