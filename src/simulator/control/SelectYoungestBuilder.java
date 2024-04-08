package simulator.control;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	private static final String TYPE_TAG = "youngest";
	private static final String DATA = "selecciona mas joven";

	public SelectYoungestBuilder() {
		super(TYPE_TAG, DATA);
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		return new SelectYoungest();
	}

	protected void fill_in_data(JSONObject o) {

	}
}
