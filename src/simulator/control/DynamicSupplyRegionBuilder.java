package simulator.control;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private double _food, _factor;
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "region dinamica");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		_food = data.getDouble("food");
		_factor = data.getDouble("factor");
		return new DynamicSupplyRegion(_food, _factor);
	}

	@Override
	protected void fill_in_data(JSONObject o){
		o.put("food", "food increase factor (optional, default " + _food + ")");
		o.put("factor", "initial amount of food (optional, default " + _factor + ")");
	}
}
