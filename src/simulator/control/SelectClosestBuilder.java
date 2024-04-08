package simulator.control;

import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {

	private static final String TYPE_TAG = "closest";
	private static final String DATA = "selecciona mas cercano";

	public SelectClosestBuilder() {
		super(TYPE_TAG, DATA);
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
		return new SelectClosest();
	}

	protected void fill_in_data(JSONObject o) {

	}
}
