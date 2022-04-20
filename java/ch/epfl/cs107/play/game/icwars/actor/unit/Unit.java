package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.City;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.ActionCapture;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class Unit extends ICWarsActors implements Interactable, Interactor {


    private final String name;
    private final int hpmax;
    private int hp;

    protected final int radius;
    private final int power;
    private final int healing;

    protected Sprite sprite;
    protected ICWarsPlayer owner;

    private boolean used = false;
    private boolean moved = false;
    private int memorisedStars;

    private final ICWarcUnitInteractionHandler handler = new ICWarcUnitInteractionHandler();
    private ICWarsRange range = new ICWarsRange();
    protected List<Action> possibleAction = new ArrayList<>();
    private City capturable;


    /**
     * @param area     (Area) : The Area to which belongs this unit;
     * @param faction  (Faction) : Unit's faction;
     * @param position (DiscreteCoordinates) : Unit's coordinates;
     * @param hpmax    (int) : Unit's maximum health points;
     * @param radius   (int) : Unit's travel and action range;
     * @param name     (String) : Unit's name;
     */
    public Unit(Area area, Faction faction, DiscreteCoordinates position, int hpmax, int radius, String name, int power, int healing) {
        super(area, faction, position);
        this.hpmax = hpmax;
        this.hp = hpmax;
        this.radius = radius;
        this.name = name;
        this.power = power;
        this.healing = healing;
    }


    /**
     * @return (int) : Unit's remaining health points.
     */
    public int getHp() {
        return hp;
    }

    /**
     * @return (int) : Unit's max health points.
     */
    public int getMaxHp() {
        return hpmax;
    }

    /**
     * Inflicts damage to unit if damage received are greater than its cell's defense stars
     *
     * @param damage (int) : Amount of health the unit might lose.
     */
    public void receiveDamage(int damage) {
        if (damage >= memorisedStars) {
            hp = (hp - damage + memorisedStars >= 0 && damage <= hp) ? hp - damage + memorisedStars : 0;
        }
    }


    /**
     * Heal a unit.
     *
     * @param healing (int) : Amount of health the unit might gain;
     */
    public void heal(int healing) {
        hp = (hp + healing <= hpmax) ? hp + healing : hpmax;
    }

    /**
     * @return (int) : Unit's power.
     */
    public int getPower() {
        return power;
    }

    /**
     * @return (int) : Unit's healing power.
     */
    public int getHeal() {
        return healing;
    }

    /**
     * @return (String) : Unit's name;
     */
    public String getName() {
        return name;
    }

    /**
     * @return (boolean) : If unit is used.
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Set unit to "used".
     */
    public void setUsed() {
        used = true;
    }

    /**
     * Set the unit to "unused".
     */
    public void resetUsed() {
        used = false;
    }

    /**
     * @return (boolean) : If unit has already been moved
     */
    public boolean isMoved() {
        return moved;
    }

    /**
     * Set unit to "moved".
     */
    public void setMoved() {
        moved = true;
    }

    /**
     * Set the unit to "unmoved".
     */
    public void resetMoved() {
        moved = false;
    }

    /**
     * @return (List of actions) : Actions that can be performed by the unit
     */
    public List<Action> getPossibleAction() {
        return possibleAction;
    }

    /**
     * @return (boolean) : If unit takes' cell space.
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Draws, sets depth and changes the opacity of the unit.
     *
     * @param canvas (Canvas) : The context canvas, target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.setDepth(2);
        if (hp != 0) {
            if (used && owner.isState() != ICWarsPlayer.State.IDLE) {
                sprite.setAlpha(0.4f);
            } else {
                sprite.setAlpha(1f);
            }

            sprite.draw(canvas);
        }
    }

    /**
     * @return (boolean) : If unit allows cell interaction.
     */
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Draws a red line between the unit and its destination.
     *
     * @param destination (DiscreteCoordinates) : Coordinates of cell where ICWarsPlayer wants to move this unit;
     * @param canvas      (Canvas) : The context canvas;
     */
    public void drawRangeAndPathTo(DiscreteCoordinates destination, Canvas canvas) {
        range.draw(canvas);
        Queue<Orientation> path = range.shortestPath(getCurrentMainCellCoordinates(),
                destination);
        //Draw path only if it exists (destination inside the range)
        if (path != null) {
            new Path(getCurrentMainCellCoordinates().toVector(), path).draw(canvas);
        }
    }

    /**
     * Creates all the node where the unit can move and perform action.
     *
     * @param radius (int) : Unit's travel and action radius;
     * @param X      (int) : Unit's X coordinates;
     * @param Y      (int) : Unit's Y coordinates;
     * @param area   (Area) : Area where the unit sets its nodes;
     */
    protected void setNode(int radius, int X, int Y, Area area) {
        range = new ICWarsRange();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x + X >= 0 && x + X < area.getWidth() && y + Y >= 0 && y + Y < area.getHeight()) {
                    DiscreteCoordinates node = new DiscreteCoordinates(x + X, y + Y);
                    boolean hasLeftEdge = (x > -radius && x + X > 0);
                    boolean hasRightEdge = (x < radius && x + X < area.getWidth());
                    boolean hasUpEdge = (y < radius && y + Y < area.getHeight());
                    boolean hasDownEdge = (y > -radius && y + Y > 0);
                    range.addNode(node, hasLeftEdge, hasUpEdge, hasRightEdge, hasDownEdge);
                }
            }
        }
    }

    /**
     * Changes unit position if certain requirements are met.
     *
     * @param newPosition (DiscreteCoordinates) : Unit's new position
     * @return (boolean) : If the position modification was a success or a failure.
     */
    @Override
    public boolean changePosition(DiscreteCoordinates newPosition) {
        if (newPosition.equals(getCurrentMainCellCoordinates()))
            return false;

        if (!getOwnerArea().canEnterAreaCells(this, List.of(newPosition)))
            return false;

        if (!range.nodeExists(newPosition))
            return false;

        getOwnerArea().leaveAreaCells(this, getCurrentCells());
        setCurrentPosition(newPosition.toVector());
        getOwnerArea().enterAreaCells(this, getCurrentCells());
        setNode(radius, newPosition.x, newPosition.y, getOwnerArea());
        return true;
    }

    /**
     * Find unit's closest enemy unit in range and then checks the closest cell to it's enemy unit where unit can move.
     */
    public void findClosestUnit() {

        List<Integer> enemyIndex = getArea().getUnitIndex(this, Faction.ENEMY);
        int X = (int) getPosition().x;
        int Y = (int) getPosition().y;
        float closest = DiscreteCoordinates.distanceBetween(new DiscreteCoordinates(getArea().getWidth(), getArea().getHeight()), new DiscreteCoordinates(0, 0));
        float closestPossiblePosition = closest;
        int closestIndex = 0;
        DiscreteCoordinates newPosition = new DiscreteCoordinates(0, 0);
        for (int index : enemyIndex) {
            float distance = DiscreteCoordinates.distanceBetween(getArea().getEnemyUnitPosition(index), new DiscreteCoordinates(X, Y));
            if (distance < closestPossiblePosition) {
                closestPossiblePosition = distance;
                closestIndex = index;
            }
        }

        DiscreteCoordinates unitPosition = getArea().getEnemyUnitPosition(closestIndex);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                DiscreteCoordinates possibleNewPosition = new DiscreteCoordinates(x + X, y + Y);
                float distance = DiscreteCoordinates.distanceBetween(unitPosition, possibleNewPosition);
                if (range.nodeExists(possibleNewPosition) && distance < closest && getArea().canEnterAreaCells(this, List.of(possibleNewPosition))) {
                    closest = distance;
                    newPosition = possibleNewPosition;
                }
            }
        }
        changePosition(newPosition);
    }

    /**
     * @param coordinates (DiscreteCoordinates) : Coordinates to be checked;
     * @return (boolean) : If given coordinates are in unit's range.
     */
    public boolean unitNodeExists(DiscreteCoordinates coordinates) {
        return range.nodeExists(coordinates);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    /**
     * @return (boolean) : If unit wants cell interaction.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * @return (boolean) : If unit wants view interaction.
     */
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Accept to be interacted with other throughout a handler.
     *
     * @param v (AreaInteractionVisitor) : the handler of the interaction.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor) v).interactWith(this);

    }

    /**
     * Interact with other throughout handler.
     *
     * @param other (Interactable). Not null
     */
    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }


    /**
     * Unit knows his owner.
     *
     * @param player (ICWarsPlayer) : Unit's ICWarsPlayer owner;
     */
    public void setOwner(ICWarsPlayer player) {
        owner = player;
    }

    /**
     * Add action Capture when unit interact with a City type cell.
     */
    private void addActionCapture() {
        boolean test = false;
        for (Action action : possibleAction) {
            if (action.getKey() == Keyboard.C) {
                test = true;
                break;
            }
        }
        if (!test) {
            possibleAction.add(new ActionCapture(this, (ICWarsArea) getOwnerArea()));
        }
    }

    /**
     * Remove action Capture when unit leave a City type cell.
     */
    private void removeCapturable() {
        for (int i = 0; i < possibleAction.size(); i++) {
            if (possibleAction.get(i).getKey() == Keyboard.C) {
                possibleAction.remove(possibleAction.get(i));
            }
        }
    }

    /**
     * Capture a city.
     */
    public void capture() {
        owner.captureCity(capturable);
    }

    /**
     * Makes sure that Capture action only appears when the unit is on a City type cell
     * and disappears when it leaves one;
     *
     * @param coordinates (DiscreteCoordinates) : Left cell coordinates
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        removeCapturable();
    }


    /**
     * Changes Unit's sprite : Useful when cities change faction.
     */
    public abstract void changeSprite();

    class ICWarcUnitInteractionHandler implements ICWarsInteractionVisitor {


        /**
         * Memorises defense stars from the cell.
         *
         * @param cell (Cell) : Cell with which the unit is trying to interact;
         */
        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell cell) {
            memorisedStars = cell.getStars();
            if (cell.getType() != ICWarsBehavior.ICWarsCellType.CITY && getPossibleAction().size() == 3) {
                capturable = null;
                removeCapturable();
            }

        }

        /**
         * @param city (City) : City with which the unit is trying to interact with;
         */
        public void interactWith(City city) {
            if (city.getFaction() != getFaction()) {
                capturable = city;
                addActionCapture();
            }
        }

    }

}




