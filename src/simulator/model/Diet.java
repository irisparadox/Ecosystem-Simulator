package simulator.model;

public enum Diet {
	HERBIVORE, CARNIVORE;

	public static Diet valueOfIgnoreCase(String param) {
		for (Diet diet: Diet.values()) {
			if(param.equalsIgnoreCase(diet.name()))
				return diet;
		}
		return null;
	}
}
