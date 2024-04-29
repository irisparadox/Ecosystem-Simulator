package simulator.control;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {

	private Simulator _sim;

	public Controller(Simulator sim) {
		this._sim = sim;
	}

	public void load_data(JSONObject data) {
		JSONArray animals = data.getJSONArray("animals");
		set_regions(data);

		for (int i = 0; i < animals.length(); i++) {
			for (int j = 0; j < animals.getJSONObject(i).getInt("amount"); j++) {
				JSONObject o = animals.getJSONObject(i).getJSONObject("spec");
				_sim.add_animal(o);
			}
		}
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		JSONObject o = new JSONObject();
		o.put("in", _sim.as_JSON());

		while (_sim.get_time() < t) {
			advance(dt);

			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		o.put("out", _sim.as_JSON());
		try {
			out.write(o.toString(3).getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (sv)
			view.close();
	}

	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(),
					(int) Math.round(a.get_age()) + 2));

		return ol;
	}

	public void reset(int cols, int rows, int width, int height) {
		_sim.reset(cols, rows, width, height);

	}

	public void set_regions(JSONObject data){
		if (data.has("regions")) {
			JSONArray regions = data.getJSONArray("regions");
			Iterator<?> a = regions.iterator();

			JSONObject region;
			int rf, rt, cf, ct;
			JSONObject spec;
			while (a.hasNext()) {
				region = (JSONObject) a.next();

				JSONArray row = region.getJSONArray("row");
				JSONArray col = region.getJSONArray("col");
				spec = region.getJSONObject("spec");

				rf = row.getInt(0);
				rt = row.getInt(1);
				cf = col.getInt(0);
				ct = col.getInt(1);

				for (int i = rf; i < rt; i++) {
					for (int j = cf; j < ct; j++) {
						_sim.set_region(i, j, spec);
					}
				}
			}

		}
	}

	public void advance(double dt) {
		_sim.advance(dt);
	}

	public void addObserver(EcoSysObserver o){
		_sim.addObserver(o);
	}

	public void removeObserver(EcoSysObserver o){
		_sim.removeObserver(o);
	}
}
