package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {

	public static final double CONST1 = 60.0;
	public static final double CONST2 = 5.0;
	public static final double CONST3 = 2.0;

	private double _food;
	private double _factor;

	public DynamicSupplyRegion(double ini_food, double factor) {
		this._food = ini_food;
		this._factor = factor;
	}

	@Override
	public double get_food(Animal a, double dt) {
		double ret = 0;

		if (a._diet != Diet.CARNIVORE) {
			int n = 0;
			for (Animal an : this.animal_list) {
				if (an._diet == Diet.HERBIVORE)
					n++;
			}

			ret = Math.min(_food, CONST1 * Math.exp(-Math.max(0, n - CONST2) * CONST3) * dt);

			this._food -= ret;
		}

		return ret;
	}

	@Override
	public void update(double dt) {
		if (Utils._rand.nextDouble() > 0.5) {
			this._food += dt * _factor;
		}
	}

	@Override
	public String toString() {
		return "Dynamic Region";
	}
}
