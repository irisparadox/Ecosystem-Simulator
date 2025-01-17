package simulator.control;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {
	public DefaultRegionBuilder() {
		super("default", "Default Region Object");
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		return new DefaultRegion();
	}
}
