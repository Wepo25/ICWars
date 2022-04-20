package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.City;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.TombStone;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldat;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ICWarsArea extends Area {

    private final List<Unit> units = new ArrayList<>();
    private final List<City> activeCities = new ArrayList<>();
    private final Random random = new Random();


    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();


    /**
     * @return (float) : The camera scale factor.
     */
    @Override
    public final float getCameraScaleFactor() {
        return ICWars.CAMERA_SCALE_FACTOR;
    }

    /**
     * @param window     (Window) : Game window;
     * @param fileSystem (FileSystem) : Game FileSystem;
     * @return (boolean) : If the game began successfully
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            ICWarsBehavior behavior = new ICWarsBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            behavior.registerActor(this);
            return true;
        }
        return false;
    }

    /**
     * Add given unit to this area's list of units.
     *
     * @param unit (Unit) : Unit to add to the list of units;
     */
    public void registerUnit(Unit unit) {
        units.add(unit);
    }

    /**
     * Add given city to this area's list of cities.
     *
     * @param city (City) : City to add to the list of units;
     */
    public void registerCity(City city) {
        activeCities.add(city);
    }

    /**
     * @return (DiscreteCoordinates) : Ally players spawn position in a given area
     */
    public abstract DiscreteCoordinates getAllyPlayerSpawnPosition();

    /**
     * @return (DiscreteCoordinates) : Enemy players spawn position in a given area
     */
    public abstract DiscreteCoordinates getEnemyPlayerSpawnPosition();

    /**
     * @param string (String) : The unit's name;
     * @return (DiscreteCoordinates) : Ally player's units spawn position in a given area
     */
    public abstract DiscreteCoordinates getAllyUnitSpawnPosition(String string);

    /**
     * @param string (String) : The unit's name;
     * @return (DiscreteCoordinates) : Enemy player's units spawn position in a given area
     */
    public abstract DiscreteCoordinates getEnemyUnitSpawnPosition(String string);

    /**
     * @return (Unit[]) : A list of units to give to ally player in a given area
     */
    public abstract Unit[] getAllyPlayerUnits();

    /**
     * @return (Unit[]) : A list of units to give to enemy player in a given area
     */
    public abstract Unit[] getEnemyPlayerUnits();

    /**
     * @param key (int) : Tells the game if we play against another person or against an AI;
     * @return (ICWarsPlayer[]) : A list of ICWarsplayers depending on the given key (if key = 0 : AIPlayer, if key = 1 : RealPlayer).
     */
    public abstract ICWarsPlayer[] getAreaEnemyPlayer(int key);


    /**
     * @param u (Unit) : Unit which wants either its enemies coordinates or its allies coordinates;
     * @return (List of Integers) : Indexes of allies or enemies in range of unit u
     */
    public List<Integer> getUnitIndex(Unit u, ICWarsActors.Faction faction) {
        List<Integer> UnitIndex = new ArrayList<>();
        for (Unit unit : units) {
            boolean condition = (faction == ICWarsActors.Faction.ALLIED) ?
                    u.getFaction() == unit.getFaction() : u.getFaction() != unit.getFaction();
            boolean coordinates = u.unitNodeExists(new DiscreteCoordinates((int) unit.getPosition().x,
                    (int) unit.getPosition().y));
            if (condition && coordinates) {
                UnitIndex.add(units.indexOf(unit));
            }
        }
        return UnitIndex;
    }




    /**
     * @param u (Unit) : Unit which wants to know if it has allies in range which can be healed;
     * @return (boolean) : If unit u has weak allies.
     */
    public boolean areUnitInRangeWeak(Unit u) {
        List<Integer> allyUnitHealth = getUnitIndex(u, ICWarsActors.Faction.ALLIED);
        for (int i : allyUnitHealth) {
            if (units.get(i).getHp() < units.get(i).getMaxHp()) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param index (int) : Index of unit we want to access information;
     * @return (DiscreteCoordinates) : Position of the unit at the requested index in the list.
     */
    public DiscreteCoordinates getEnemyUnitPosition(int index) {
        return new DiscreteCoordinates((int) units.get(index).getPosition().x, (int) units.get(index).getPosition().y);
    }

    /**
     * Used by actions to inflict damage to a certain unit without breaking encapsulation
     *
     * @param index  (int) : Index of unit we want to attack;
     * @param damage (int) : Amount of damage we want to inflict to given unit;
     */
    public void setDamage(int index, int damage) {
        units.get(index).receiveDamage(damage);
        if (units.get(index).getHp() == 0) {
            this.registerActor(new TombStone(this, ICWarsActors.Faction.NEUTRAL, new DiscreteCoordinates((int) units.get(index).getPosition().x, (int) units.get(index).getPosition().y)));
            units.get(index).leaveArea();
        }
    }

    /**
     * Used by actions to heal a certain unit without breaking encapsulation
     *
     * @param index (int) : Index of unit we want to heal;
     * @param life  (int) : Amount of life we want to give back to given unit;
     */
    public void setHeal(int index, int life) {
        units.get(index).heal(life);
    }

    /**
     * Find the weakest unit in the range of a given unit
     *
     * @param unit       (Unit) : Unit in range of which we want to find the weakest unit (ally or enemy depending on the action);
     * @param actionName (String) : Name of action which tells if we want to find the weakest ally or the weakest enemy;
     * @return (int) : The index of the weakest unit in range (Or -1 if do not exist).
     */
    public int findWeakest(Unit unit, String actionName) {
        List<Integer> unitIndex = getUnitIndex(unit,(actionName.equals("(H)eal")) ?  ICWarsActors.Faction.ALLIED : ICWarsActors.Faction.ENEMY);
        if (unitIndex.isEmpty()) {
            return -1;
        }
        int hp = units.get(unitIndex.get(0)).getHp();
        int unitHp;
        int UnitIndex = unitIndex.get(0);

        for (int index : unitIndex) {
            unitHp = units.get(index).getHp();

            if (unitHp < hp) {
                hp = unitHp;
                UnitIndex = index;
            }
        }
        return UnitIndex;

    }

    /**
     * Center camera on desired unit.
     *
     * @param index (int) : Index of the unit we want to center camera on;
     */
    public void setCamera(int index) {
        setViewCandidate(units.get(index));
    }

    public Vector getUnitPosition(int index){
        return units.get(index).getPosition();
    }
    /**
     * Remove unit from area's list.
     *
     * @param unit (Unit) : Unit we want to remove from area's list;
     */
    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    /**
     * Used to apply city effect to make a Unit spawn.
     * Random chance of spawning a new soldier to owner ICWarsPlayer every round.
     *
     * @param player (ICWarsPlayer) : ICWarsPlayer to add unit to.
     */
    public void doCityAction(ICWarsPlayer player) {
        for (City city : activeCities) {
            if (city.getOwner().equals(player)) {
                DiscreteCoordinates possibleSpawn = findSpawn(city);
                if (possibleSpawn != null) {
                    if (random.ints(1, 10).findFirst().getAsInt() % 3 == 0) {
                        Soldat soldat = new Soldat(this, city.getFaction(), possibleSpawn);
                        soldat.setOwner(player);
                        soldat.enterArea(this);
                        units.add(soldat);
                        city.addUnitToCity(soldat);
                        registerActor(soldat);
                        player.addCityUnit(soldat);
                    }

                }
            }
        }
    }


    /**
     * Looks in its closest neighbours if a viable spawn position exists.
     *
     * @param city (City) : City which wants to find if a possible spawn position exists near it;
     * @return (DiscreteCoordinates) : The position where a city can make spawn a unit if this position does exist (else null).
     */
    private DiscreteCoordinates findSpawn(City city) {
        DiscreteCoordinates pos = city.getCurrentCells().get(0);
        for (DiscreteCoordinates spawnPos : pos.getNeighbours()) {
            boolean canSpawn = true;
            for (Unit unit : units) {
                if (spawnPos.equals(unit.getCurrentCells().get(0))) {
                    canSpawn = false;
                }
            }
            if (canSpawn) {
                return spawnPos;
            }
        }
        return null;
    }
}