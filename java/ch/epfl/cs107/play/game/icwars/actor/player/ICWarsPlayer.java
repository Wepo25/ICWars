package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.City;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;


public class ICWarsPlayer extends ICWarsActors implements Interactor {

    private final List<Unit> units = new ArrayList<>();
    protected Unit selectedUnit;
    protected State state;
    protected Action execute;


    /**
     * @param area     (Area) : Player's area;
     * @param faction  (Faction) : Player's faction;
     * @param position (DiscreteCoordinates) : Player's coordinates;
     */
    public ICWarsPlayer(Area area, Faction faction, DiscreteCoordinates position) {
        super(area, faction, position);
        state = State.IDLE;
    }

    /**
     * @param area     (Area) : Player's area;
     * @param faction  (Faction) : Player's faction;
     * @param position (DiscreteCoordinates) : Player's coordinates;
     * @param unit     (Unit ellipse): A list consisting of all the units belonging to the player.
     */
    public ICWarsPlayer(Area area, Faction faction, DiscreteCoordinates position, Unit... unit) {
        this(area, faction, position);
        for (Unit u : unit) {
            u.setOwner(this);
            area.registerActor(u);
            units.add(u);
        }
    }

    /**
     * Update ICWarsPlayer.
     *
     * @param deltaTime (float) : Number of time per second an update is performed on an ICWarsPlayer;
     */
    @Override
    public void update(float deltaTime) {
        for (int i = units.size() - 1; i >= 0; i--) {
            if (units.get(i).getHp() == 0) {
                units.get(i).leaveArea();
                units.get(i).getArea().removeUnit(units.get(i));
                units.remove(units.get(i));
            }
        }
        super.update(deltaTime);
    }


    /**
     * ICWarsPlayer's state which determines the way he behaves.
     */
    public enum State {
        IDLE(false), NORMAL(true), SELECT_CELL(true),
        MOVE_UNIT(true), ACTION_SELECTION(true), ACTION(false);

        private final boolean canMove;

        /**
         * @param canMove (boolean) : Tells the ICWarsPlayer if he is able to move;.
         */
        State(boolean canMove) {
            this.canMove = canMove;
        }

        /**
         * @return (boolean) : if the ICWarsPlayer is able to move;
         */
        public boolean getCanMove() {
            return canMove;
        }
    }

    /**
     * Set the state of ICWarsPlayer.
     *
     * @param i (int) : Index of specific state;
     */
    public void setState(int i) {
        state = State.values()[i];
    }


    /**
     * @return (State) : ICWarsPlayer's current state.
     */
    public State isState() {
        return state;
    }

    /**
     * Set the ICWarsPlayer state to NORMAL and reset his units.
     */
    public void startTurn() {
        state = State.NORMAL;
        for (Unit unit : units) {
            unit.resetMoved();
            unit.resetUsed();
        }
        ((ICWarsArea) getOwnerArea()).doCityAction(this);
    }


    /**
     * @param index (int) : Index of wanted unit;
     * @return (Unit) : The unit at the given index.
     */
    public Unit getUnit(int index) {
        return units.get(index);

    }

    /**
     * Set ICWarsPlayer's selectedUnit;
     *
     * @param unit (Unit) : Unit to select;
     */
    public void selectUnit(Unit unit) {
        selectedUnit = unit;
    }

    /**
     * @return (Unit) : ICWarsPlayer's selectedUnit.
     */
    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    /**
     * Add units spawned by owned cities to Player's unit list.
     *
     * @param unit (Unit) : Unit spawned by the city;
     */
    public void addCityUnit(Unit unit) {
        units.add(unit);

    }

    /**
     * ICWarsPlayer captures the desired City.
     *
     * @param city (City) : City to be captured;
     */
    public void captureCity(City city) {
        city.beCaptured(this);
        ((ICWarsArea) getOwnerArea()).registerCity(city);
    }

    /**
     * @return (boolean) : If all units have been used for this turn.
     */
    public boolean allUsed() {
        for (Unit unit : units) {
            if (!unit.isUsed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return (boolean) : If the player doesn't have any units left.
     */
    public boolean isDefeated() {
        return units.size() == 0;
    }

    /**
     * Register the ICWarsPlayer and its units in the area.
     *
     * @param area (Area) : Area in which the player enters;
     */
    @Override
    public void enterArea(Area area) {
        super.enterArea(area);

        for (Unit unit : units) {
            area.registerActor(unit);
            ((ICWarsArea) area).registerUnit(unit);
        }
    }

    /**
     * Unregister all its units from current area;
     */
    @Override
    public void leaveArea() {
        for (Unit unit : units) {
            unit.leaveArea();
        }
        super.leaveArea();
    }

    /**
     * Reset the player state to Normal if requirements are matched.
     *
     * @param discreteCoordinates (DiscreteCoordinates) : Coordinates of the cell ICWarsPlayer just left.
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> discreteCoordinates) {
        if (state == State.ACTION_SELECTION) {
            state = State.NORMAL;
        }
    }

    /**
     * Center the camera on this ICWarsPlayer.
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }


    /**
     * @return (List of DiscreteCoordinates) : The player's field of view in case view interaction are implemented.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }


    /**
     * @return (boolean) : If the player wants to interact by contact.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * @return (boolean) : If the player wants interaction by view
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * @param other (Interactable) :
     */
    @Override
    public void interactWith(Interactable other) {
    }

    /**
     * @param v (AreaInteractionVisitor) :
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        v.interactWith(this);
    }




}
