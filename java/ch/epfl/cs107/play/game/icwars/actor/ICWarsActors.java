package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class ICWarsActors extends MovableAreaEntity {

    private Faction faction;
    private final DiscreteCoordinates position;
    protected Sprite sprite;


    /**
     * @param area     (Area) : Area which the ICWarsActors belongs to;
     * @param faction  (Faction) : Actor's faction;
     * @param position (DiscreteCoordinates) : Actor's position
     */
    public ICWarsActors(Area area, Faction faction, DiscreteCoordinates position) {
        super(area, Orientation.UP, position);
        this.faction = faction;
        this.position = position;
    }

    /**
     * Enumeration of possible factions.
     */
    public enum Faction {
        ALLIED, ENEMY, NEUTRAL
    }

    /**
     * @return (List of DiscreteCoordinates) : A list of cells occupied by an actor
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Unregistering of the player and its units from the area;
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Add an actor to the given area
     *
     * @param area (Area) : Area to add actor to;
     */
    public void enterArea(Area area) {
        setOwnerArea(area);
        area.registerActor(this);

        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * @return (boolean) : If actor takes cell space;
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * @return (boolean) : If ICWarsActors wants cell interactions.
     */
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    /**
     * @return (boolean) : If ICWarsActors wants view interactions.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * @param v (AreaInteractionVisitor) : it allows his interaction to be handle by an interface made for it.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
    }

    /**
     * @param canvas (Canvas) : The context canvas, target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * @return actor's faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Used by city when creating a new unit or when it has to change the faction of alive previously created units.
     */
    protected void setFaction(Faction faction) {
        this.faction = faction;

    }

    /**
     * @return actor's transtyped area
     */
    public ICWarsArea getArea() {
        return (ICWarsArea) getOwnerArea();
    }



}
