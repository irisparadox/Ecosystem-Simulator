package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal{
    private static final String GENETIC_CODE = "Sheep";
    private static final Diet DIET = Diet.HERBIVORE;
    private static final double SIGHT_RANGE = 40.0;
    private static final double INITIAL_SPEED = 35.0;
    private Animal _danger_source;
    private SelectionStrategy _danger_strategy;
    public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos){
        super(GENETIC_CODE, DIET, SIGHT_RANGE, INITIAL_SPEED, mate_strategy, pos);
        this._danger_source = null;
        this._danger_strategy = danger_strategy;
    }

    protected Sheep(Sheep p1, Animal p2){
        super(p1, p2);
        this._danger_strategy = p1._danger_strategy;
        this._danger_source = null;
    }

    public void update(double dt){
        switch(this._state){
            case DEAD -> {
                break;
            }
            case NORMAL -> {
                this.updateNormal(dt);
                break;
            }
            case DANGER -> {
                this.updateDanger(dt);
                break;
            }
            case MATE -> {
                this.updateMate(dt);
                break;
            }
        }
    }

    private void updateNormal(double dt){

    }

    private void updateDanger(double dt){

    }

    private void updateMate(double dt){

    }
}
