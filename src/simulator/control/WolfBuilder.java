package simulator.control;

import org.json.JSONArray;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> strategies;

	public WolfBuilder(Factory<SelectionStrategy> f) {
		super("wolf", "lobo");
		strategies = f;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
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
			mate_strategy_sheep = new SelectFirst();
		else
			mate_strategy_sheep = strategies.create_instance(data.getJSONObject("mate_strategy"));

		SelectionStrategy hunt_strategy_sheep;
		if (!data.has("hunt_strategy"))
			hunt_strategy_sheep = new SelectFirst();
		else
			hunt_strategy_sheep = strategies.create_instance(data.getJSONObject("hunt_strategy"));

		return new Wolf(mate_strategy_sheep, hunt_strategy_sheep, position);
	}

}
