package simulator.control;

import java.util.Collections;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;

import simulator.factories.Factory;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		this._builders = new HashMap<>();
		this._builders_info = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		for (Builder<T> b : builders) {
			add_builder(b);
		}
	}

	public void add_builder(Builder<T> b) {
		_builders.put(b.get_type_tag(), b);
		_builders_info.add(b.get_info());
	}

	@Override
	public T create_instance(JSONObject info) {
		if (info == null)
			throw new IllegalArgumentException("’info’ cannot be null");

		if (info.has("type")) {
			Builder<T> b = _builders.get(info.getString("type"));
			if (b != null) {
				T ret = b.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());
				if (ret != null) {
					return ret;
				}
			}
		}
		throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());

	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}

}
