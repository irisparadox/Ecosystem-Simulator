package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{
    private static final String GENETIC_CODE = "Sheep";
    private static final Diet DIET = Diet.HERBIVORE;
    private static final double SIGHT_RANGE = 40.0;
    private static final double INITIAL_SPEED = 35.0;
    private static final double MIN_DISTANCE = 8.0;
    private static final double ENERGY_EXP_FACTOR = 100.0;
    private static final double SPEED_EXP_MULTIPLIER = 0.007;
    private static final double ENERGY_DRAIN = 20.0;
    private static final double MIN_ENERGY = 0;
    private static final double MAX_ENERGY = 100;
    private static final double DESIRE_MULTIPLIER = 40.0;
    private static final double MIN_DESIRE = 0;
    private static final double MAX_DESIRE = 100;
    private static final double DESIRE_THRESHOLD = 65;
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
        if(this._state == State.DEAD) return;
        switch(this._state){
            case NORMAL -> this.updateNormal(dt);
            case DANGER -> this.updateDanger(dt);
            case MATE   -> this.updateMate(dt);
        }

        if (this._pos.getX() >= this._region_mngr.get_width()  ||
            this._pos.getX() < 0                               ||
            this._pos.getY() >= this._region_mngr.get_height() ||
            this._pos.getY() < 0) {

            this._pos = adjust_position();
            this._state = State.NORMAL;
        }

        if(_energy == 0.0 || _age > 8.0) _state = State.DEAD;
        if(_state != State.DEAD) {
        } //get_food(this, dt);
    }

    private void updateNormal(double dt){
        if(this._pos.distanceTo(_dest) < MIN_DISTANCE) {
            double x = Utils._rand.nextDouble(0,this._region_mngr.get_width());
            double y = Utils._rand.nextDouble(0,this._region_mngr.get_height());
            this._dest = new Vector2D(x,y);
        }
        move(_speed * dt * Math.exp((_energy - ENERGY_EXP_FACTOR) * SPEED_EXP_MULTIPLIER));
        _age += dt;
        _energy -= ENERGY_DRAIN * dt;
        _energy = Utils.constrain_value_in_range(_energy,MIN_ENERGY,MAX_ENERGY);
        _desire += DESIRE_MULTIPLIER * dt;
        _desire = Utils.constrain_value_in_range(_desire,MIN_DESIRE,MAX_DESIRE);

        if(_danger_source == null) {
            //findNewAnimal
            if(_desire > DESIRE_THRESHOLD) _state = State.MATE;
        } else _state = State.DANGER;

    }

    private void updateDanger(double dt){

    }

    private void updateMate(double dt){

    }
}
