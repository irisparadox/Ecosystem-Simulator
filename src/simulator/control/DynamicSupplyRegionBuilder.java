package simulator.control;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private double _food, _factor;
	private static final double FOOD_DEFAULT = 100.0;
	private static final double FACTOR_DEFAULT = 2.0;
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "region dinamica");
		this._factor = FACTOR_DEFAULT;
		this._food = FOOD_DEFAULT;
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		_food = data.getDouble("food");
		_factor = data.getDouble("factor");
		return new DynamicSupplyRegion(_food, _factor);
	}

	@Override
	protected void fill_in_data(JSONObject o){
		//TODO
		o.put("factor", "food increase factor (optional, default " + FACTOR_DEFAULT + ")");
		o.put("food", "initial amount of food (optional, default " + FOOD_DEFAULT + ")");
	}
}
