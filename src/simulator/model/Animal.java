package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.extra.ExceptionMessages;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements AnimalInfo {
    protected final static double POSITION_SCALE_FACTOR = 60.0;
    protected final static double RANDOM_TOLERANCE = 0.2;
    protected final static double INITIAL_ENERGY = 100.0;
    protected final static double RANDOM_SPEED_TOLERANCE = 0.1;
    protected String _genetic_code;
    protected Diet _diet;
    protected State _state;
    protected Vector2D _pos;
    protected Vector2D _dest;
    protected double _energy;
    protected double _speed;
    protected double _age;
    protected double _desire;
    protected double _sight_range;
    protected Animal _mate_target;
    protected Animal _baby;
    protected AnimalMapView _region_mngr;
    protected SelectionStrategy _mate_strategy;

    protected Animal(String genetic_code, Diet diet, double sight_range,
                     double init_speed, SelectionStrategy mate_strategy, Vector2D pos) throws IllegalArgumentException {
        if(genetic_code.length() == 0)
            throw new IllegalArgumentException(ExceptionMessages.INVALID_GENETIC_CODE_LENGTH);
        this._diet = diet;
        this._genetic_code = genetic_code;
        this._sight_range = sight_range;
        this._speed = Utils.get_randomized_parameter(init_speed, RANDOM_SPEED_TOLERANCE);
        this._mate_strategy = mate_strategy;
        this._pos = pos;
        this._age = 0;
        this._state = State.NORMAL;
        this._energy = INITIAL_ENERGY;
        this._desire = 0.0;
        this._dest = null;
        this._mate_target = null;
        this._baby = null;
        this._region_mngr = null;
    }

    protected Animal(Animal p1, Animal p2) {
        this._genetic_code = p1._genetic_code;
        this._diet = p1._diet;
        this._energy = (p1._energy + p2._energy) / 2;
        this._pos = p1._pos.plus(
                Vector2D.get_random_vector(-1, 1)
                        .scale(POSITION_SCALE_FACTOR * (Utils._rand.nextGaussian() + 1))
        );
        this._sight_range = Utils.get_randomized_parameter((p1._sight_range + p2._sight_range) / 2,
                RANDOM_TOLERANCE);
        this._speed = Utils.get_randomized_parameter((p1._speed + p2._speed) / 2,
                RANDOM_TOLERANCE);
        this._age = 0;
        this._state = State.NORMAL;
        this._desire = 0.0;
        this._dest = null;
        this._mate_target = null;
        this._baby = null;
        this._region_mngr = null;
    }

    void init(AnimalMapView reg_mngr) {
        this._region_mngr = reg_mngr;
        double x, y;
        double width = _region_mngr.get_width();
        double height = _region_mngr.get_height();

        if (this._pos == null) {
            x = Utils._rand.nextDouble(0,width - 1);
            y = Utils._rand.nextDouble(0,height - 1);
        } else {
            x = this._pos.getX();
            y = this._pos.getY();
            while (x >= width) x = (x - width);
            while (x < 0) x = (x + width);
            while (y >= height) y = (y - height);
            while (y < 0) y = (y + height);
        }

        this._pos = new Vector2D(x, y);
        x = Utils._rand.nextDouble(0,width - 1);
        y = Utils._rand.nextDouble(0,height - 1);
        this._dest = new Vector2D(x, y);
    }

    Animal deliverBaby(){
        Animal baby = this._baby;
        this._baby = null;
        return baby;
    }

    protected void move(double speed) {
        this._pos = this._pos.plus(_dest.minus(_pos).direction().scale(speed));
    }

    public JSONObject asJSON(){
        JSONObject o = new JSONObject();
        o.put("pos",this._pos.asJSONArray());
        o.put("gcode",this._genetic_code);
        o.put("diet",this._diet.toString());
        o.put("state",this._state.toString());

        return o;
    }

    @Override
    public State get_state() {
        return this._state;
    }

    @Override
    public Vector2D get_position() {
        return this._pos;
    }

    @Override
    public String get_genetic_code() {
        return this._genetic_code;
    }

    @Override
    public Diet get_diet() {
        return this._diet;
    }

    @Override
    public double get_speed() {
        return this._speed;
    }

    @Override
    public double get_sight_range() {
        return this._sight_range;
    }

    @Override
    public double get_energy() {
        return this._energy;
    }

    @Override
    public double get_age() {
        return this._age;
    }

    @Override
    public Vector2D get_destination() {
        return this._dest;
    }

    @Override
    public boolean is_pregnant() {
        return this._baby != null;
    }
}