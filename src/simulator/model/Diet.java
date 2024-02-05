package simulator.model;

import simulator.extra.ExceptionMessages;

public enum Diet {
    HERBIVORE, CARNIVORE;

    public static Diet valueOfIgnoreCase(String param) throws IllegalArgumentException {
        for (Diet diet : Diet.values()) {
            if (diet.name().equalsIgnoreCase(param))
                return diet;
        }
        throw new IllegalArgumentException(ExceptionMessages.INVALID_DIET);
    }
}
