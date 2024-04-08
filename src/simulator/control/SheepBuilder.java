package simulator.control;

import org.json.JSONArray;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.SelectClosest;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

import simulator.misc.Vector2D;

public class SheepBuilder extends Builder<Animal> {

	private static final String TYPE_TAG = "sheep";
	private static final String DATA = "oveja";
	private Factory<SelectionStrategy> strategies;

	public SheepBuilder(Factory<SelectionStrategy> f) {
		super(TYPE_TAG, DATA);
		strategies = f;
	}

	@Override
	protected Sheep create_instance(JSONObject data) {
		Vector2D position = null;
		if (data.has("pos")) {
			JSONObject pos = data.getJSONObject("pos");
			JSONArray x_range = pos.getJSONArray("x_range");
			JSONArray y_range = pos.getJSONArray("y_range");

			double xmin = x_range.getDouble(0);
			double xmax = x_range.getDouble(1);
			double ymin = y_range.getDouble(0);
			double ymax = y_range.getDouble(1);

			double x = Utils._rand.nextDouble(xmin, xmax);
			double y = Utils._rand.nextDouble(ymin, ymax);

			position = new Vector2D(x, y);
		}

		SelectionStrategy mate_strategy_sheep;
		if (!data.has("mate_strategy"))
			mate_strategy_sheep = new SelectFirst();// new SelectClosest();
		else
			mate_strategy_sheep = strategies.create_instance(data.getJSONObject("mate_strategy"));

		SelectionStrategy danger_strategy_sheep;
		if (!data.has("danger_strategy"))
			danger_strategy_sheep = new SelectFirst();// new SelectClosest();
		else
			danger_strategy_sheep = strategies.create_instance(data.getJSONObject("danger_strategy"));

		return new Sheep(mate_strategy_sheep, danger_strategy_sheep, position);
	}

	protected void fill_in_data(JSONObject o) {

	}
}
