package simulator.control;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "region dinamica");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		double ini_food = data.getDouble("food");
		double factor = data.getDouble("factor");
		return new DynamicSupplyRegion(ini_food, factor);
	}

}
