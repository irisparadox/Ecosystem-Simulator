package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {
	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;

		double age = as.get(0).get_age();
		Animal youngest = as.get(0);

		for (Animal animal : as) {
			if (animal.get_age() < age) {
				youngest = animal;
				age = animal.get_age();
			}
		}

		return youngest;
	}

}
