package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class City extends ICWarsActors implements Interactable {

    private ICWarsPlayer owner;
    private List<Unit> units = new ArrayList<Unit>();

    /**
     * @param area     (Area) : Area which the city belongs to;
     * @param position (DiscreteCoordinates) : City's coordinates;
     */
    public City(Area area, DiscreteCoordinates position) {
        super(area, Faction.NEUTRAL, position);
        sprite = new Sprite("icwars/neutralBuilding", 1.2f, 1.2f, this);
        sprite.setDepth(1);
    }

    /**
     * Changes appearance and owner when is capture.
     *
     * @param player (ICWarsPlayer) : Player which captures the city;
     */
    public void beCaptured(ICWarsPlayer player) {
        owner = player;
        setFaction(player.getFaction());
        sprite = new Sprite((player.getFaction() == Faction.ALLIED) ? "icwars/friendlyBuilding" : "icwars/enemyBuilding", 1.2f, 1.2f, this);
        for(Unit unit : units){
            unit.setFaction((unit.getFaction() == Faction.ALLIED) ? Faction.ENEMY : Faction.ALLIED);
            unit.changeSprite();
        }
    }

    /**
     *
     * @return (ICWarsPlayer) : Owner
     */
    public ICWarsPlayer getOwner() {
        return owner;
    }

    /**
     * Add spawned Unit to city units to monitor them regarding the city's owner.
     *
     * @param unit (Unit) : Add spawned Unit to city units.
     */
    public void addUnitToCity(Unit unit){
        units.add(unit);
    }

    /**
     *
     * @param v (AreaInteractionVisitor) : the visitor
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor) v).interactWith(this);

    }

    /**
     *
     * @return (boolean) : If city allows cell interaction.
     */
    public boolean isCellInteractable() {
        return true;
    }

}
