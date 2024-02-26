package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.List;

public class Sheep extends Animal {
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
    private static final double ALTERED_SPEED_MULTIPLIER = 2.0;
    private  static final double ALTERED_ENERGY_DRAIN_MULTIPLIER = 1.2;
    private static final double PREGNANT_PROBABILITY = 0.9;
    private Animal _danger_source;
    private final SelectionStrategy _danger_strategy;
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

    @Override
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
            changeToNormal();
        }

        if(_energy == 0.0 || _age > 8.0) _state = State.DEAD;
        if(_state != State.DEAD) {
            _energy += _region_mngr.get_food(this, dt);
            _energy = Utils.constrain_value_in_range(_energy,MIN_ENERGY,MAX_ENERGY);
        }
    }

    private void updateNormal(double dt){
        updateMove(dt);
        _age += dt;
        _energy -= ENERGY_DRAIN * dt;
        _energy = Utils.constrain_value_in_range(_energy,MIN_ENERGY,MAX_ENERGY);
        _desire += DESIRE_MULTIPLIER * dt;
        _desire = Utils.constrain_value_in_range(_desire,MIN_DESIRE,MAX_DESIRE);

        if(_danger_source == null) {
            selectNewDangerSource();
            if(_desire > DESIRE_THRESHOLD) _state = State.MATE;
        } else changeToNormal();
    }

    private void updateDanger(double dt){
        if(_danger_source != null && _state == State.DEAD) {
            _danger_source = null;
        } else if(_danger_source != null) {
            _dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
            updateAttributes(dt);
        } else {
            updateMove(dt);
            if(_desire < DESIRE_THRESHOLD) changeToNormal();
            else changeToMate();
        }

        if(_danger_source == null || _pos.distanceTo(_danger_source.get_position()) > SIGHT_RANGE) selectNewDangerSource();
    }

    private void updateMate(double dt){
        if(_mate_target != null && _state == State.DEAD) {
            _mate_target = null;
        } else if (_mate_target == null) {
            List<Animal> list = _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.HERBIVORE);
            _mate_target = _mate_strategy.select(this, list);
            if(_mate_target == null){
                updateMove(dt);
            } else {
                _dest = _mate_target.get_position();
                updateAttributes(dt);
                if(_pos.distanceTo(_dest) < MIN_DISTANCE){
                    _desire = 0;
                    _mate_target._desire = 0;
                    if(!(this.is_pregnant()) && Utils._rand.nextDouble(0,1) < PREGNANT_PROBABILITY)
                        _baby = new Sheep(this, _mate_target);
                    _mate_target = null;
                }
            }
        }

        if(_danger_source == null) {
            selectNewDangerSource();
            if(_desire < DESIRE_THRESHOLD)
                changeToNormal();
        } else
            changeToDanger();
    }

    private void selectNewDangerSource() {
        List<Animal> list = _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.CARNIVORE);
        _danger_source = _danger_strategy.select(this, list);
    }

    private void changeToNormal(){
        _state = State.NORMAL;
        _danger_source = null;
        _mate_target = null;
    }

    private void changeToMate(){
        _state = State.MATE;
        _danger_source = null;
    }

    private void changeToDanger(){
        _state = State.DANGER;
        _mate_target = null;
    }

    private void updateAttributes(double dt){
        move(ALTERED_SPEED_MULTIPLIER * _speed * dt * Math.exp(
                (_energy - ENERGY_EXP_FACTOR) * SPEED_EXP_MULTIPLIER));
        _age += dt;
        _energy -= ENERGY_DRAIN * ALTERED_ENERGY_DRAIN_MULTIPLIER * dt;
        _energy = Utils.constrain_value_in_range(_energy, MIN_ENERGY, MAX_ENERGY);
        _desire += DESIRE_MULTIPLIER * dt;
        _desire = Utils.constrain_value_in_range(_desire, MIN_DESIRE, MAX_DESIRE);
    }

    private void updateMove(double dt){
        if(this._pos.distanceTo(_dest) < MIN_DISTANCE) {
            double x = Utils._rand.nextDouble(0,this._region_mngr.get_width());
            double y = Utils._rand.nextDouble(0,this._region_mngr.get_height());
            this._dest = new Vector2D(x,y);
        }
        move(_speed * dt * Math.exp((_energy - ENERGY_EXP_FACTOR) * SPEED_EXP_MULTIPLIER));
    }
}
