package simulator.model;

public class DefaultRegion extends Region {

	public static final double CONST1 = 60.0;
	public static final double CONST2 = 5.0;
	public static final double CONST3 = 2.0;

	@Override
	public double get_food(Animal a, double dt) {
		double ret = 0.0;
		if (a.get_diet() != Diet.CARNIVORE) {
			int n = 0;
			for (Animal an : this.animal_list) {
				if (an._diet == Diet.HERBIVORE)
					n++;
			}

			ret = CONST1 * Math.exp(-Math.max(0, n - CONST2) * CONST3) * dt;
		}

		return ret;
	}

	@Override
	public void update(double dt) {

	}
}
