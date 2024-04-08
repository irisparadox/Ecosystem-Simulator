package simulator.control;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	private static final String TYPE_TAG = "first";
	private static final String DATA = "selecciona primero";

	public SelectFirstBuilder() {
		super(TYPE_TAG, DATA);
	}

	@Override
	protected SelectFirst create_instance(JSONObject data) {
		return new SelectFirst();
	}

}
