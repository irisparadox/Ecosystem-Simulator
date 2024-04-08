package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;

		Vector2D pos = a.get_position();
		double distanciaActual = pos.distanceTo(as.get(0).get_position());
		Animal closest = as.get(0);

		for (Animal animal : as) {
			if (pos.distanceTo(animal.get_position()) < distanciaActual) {
				closest = animal;
				distanciaActual = pos.distanceTo(animal.get_position());
			}
		}

		return closest;
	}
}
