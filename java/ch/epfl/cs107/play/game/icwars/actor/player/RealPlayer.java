package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class RealPlayer extends ICWarsPlayer implements Interactable, Interactor {

    private final static int MOVE_DURATION = 4;
    private final Sprite sprite;
    private final ICWarsPlayerGUI playerGUI;
    private final ICWarsPlayerInteractionHandler handler = new ICWarsPlayerInteractionHandler();

    /**
     * @param area     (Area) : Player's area;
     * @param faction  (Faction) : Player's faction;
     * @param position (DiscreteCoordinates) : Player's coordinates;
     * @param unit        (Unit ellipse) : A list consisting of all units belonging to the player;
     */
    public RealPlayer(Area area, Faction faction, DiscreteCoordinates position, Unit... unit) {
        super(area, faction, position, unit);
        if (faction == Faction.ALLIED) {
            sprite = new Sprite("icwars/allyCursor", 1.f, 1.f, this);
        } else {
            sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
        }
        playerGUI = new ICWarsPlayerGUI(10f, this);
        sprite.setDepth(4f);
        resetMotion();
    }


    /**
     * Update RealPlayer.
     *
     * @param deltaTime (float) : Number of time per second an update is performed on an ICWarsPlayer;
     */
    @Override
    public void update(float deltaTime) {
        if (state.getCanMove()) {
            Keyboard keyboard = getOwnerArea().getKeyboard();
            if (state != State.ACTION) {
                moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
                moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
                moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
                moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
            }
        }
        stateUpdate();
        super.update(deltaTime);


    }

    /**
     * Description of the player's behavior depending on his state.
     */
    private void stateUpdate() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button ENTER = keyboard.get(Keyboard.ENTER);
        Button TAB = keyboard.get(Keyboard.TAB);
        switch (state) {
            case NORMAL:
                selectedUnit = null;
                if (allUsed()) {
                    state = State.IDLE;
                }

                if (ENTER.isPressed()) {
                    state = State.SELECT_CELL;
                    break;
                }

                if (TAB.isPressed()) {
                    state = State.IDLE;
                    break;
                }
                break;

            case SELECT_CELL:
                if (TAB.isPressed()) {
                    state = State.IDLE;
                    break;
                }

                if (selectedUnit != null && !selectedUnit.isUsed()) {
                    state = State.MOVE_UNIT;
                    break;
                }
                break;
            case MOVE_UNIT:
                if (TAB.isPressed()) {
                    state = State.IDLE;
                    break;
                }

                if (selectedUnit.isMoved() && !selectedUnit.isUsed()) {
                    state = State.ACTION_SELECTION;
                }

                if ((keyboard.get(Keyboard.ENTER)).isDown()) {
                    if (!selectedUnit.isMoved()) {
                        if (selectedUnit.changePosition(getCurrentMainCellCoordinates())) {
                            selectedUnit.setMoved();
                            state = State.ACTION_SELECTION;
                        }
                    }
                }
                break;
            case ACTION_SELECTION:
                for (Action action : selectedUnit.getPossibleAction()) {
                    if (keyboard.get(action.getKey()).isDown()) {
                        execute = action;
                        state = State.ACTION;
                        break;
                    }
                }
                break;
            case ACTION:
                execute.doAction(10f, this, keyboard);
                break;
            default:
                break;
        }
    }

    /**
     * Orientate and Move this player in the given orientation if the given button is down.
     *
     * @param orientation (Orientation): Given orientation, not null;
     * @param b           (Button): Button corresponding to the given orientation, not null;
     */
    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Reset the unit from the Panel;
     *
     * @param discreteCoordinates (DiscreteCoordinates) : Coordinates of the cell ICWarsPlayer just left.
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> discreteCoordinates) {
        playerGUI.setPanelUnit(null);
        super.onLeaving(discreteCoordinates);
    }

    /**
     * Draw the player's attributes depending on his state.
     *
     * @param canvas (Canvas) : The context canvas, target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (!(state == State.IDLE)) {
            sprite.draw(canvas);
            playerGUI.draw(canvas);
        }
        if (execute != null) {
            execute.draw(canvas);
        }
    }

    /**
     * Call the interaction handler to handle his interactions.
     *
     * @param other (Interactable) : Not null
     */
    @Override
    public void interactWith(Interactable other) {
        if (!isDisplacementOccurs())
            other.acceptInteraction(handler);
    }


    /**
     * Handle player's interactions.
     */
    private class ICWarsPlayerInteractionHandler implements ICWarsInteractionVisitor {

        /**
         * Gives information to panel about the unit and make the player select a unit depending on conditions.
         *
         * @param unit (Unit) : Unit to interact with.
         */
        @Override
        public void interactWith(Unit unit) {
            playerGUI.setPanelUnit(unit);
            Keyboard keyboard = getOwnerArea().getKeyboard();

            if (state == State.SELECT_CELL && getFaction() == unit.getFaction() && keyboard.get(Keyboard.ENTER).isPressed()) {
                selectUnit(unit);

            }
        }

        /**
         * Gives information to the panel to show current cell.
         *
         * @param cell (Cell) : cell to interact with
         */
        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell cell) {
            playerGUI.setPanelCell(cell.getType());
        }

    }


}

