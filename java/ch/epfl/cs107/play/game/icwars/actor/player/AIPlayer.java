package ch.epfl.cs107.play.game.icwars.actor.player;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;


public class AIPlayer extends ICWarsPlayer {

    private boolean counting = false;
    private float counter;
    private final Sprite sprite;
    private int unitIndex;
    private final ICWarsArea ownerArea;

    /**
     * @param area     (Area) : AIPlayer's area;
     * @param faction  (Faction) : AIPlayer's faction;
     * @param position (DiscreteCoordinates) : AIPlayer's coordinates;
     * @param unit     (Unit ellipse) : A list consisting of all units belonging to the AIPlayer;
     */
    public AIPlayer(Area area, Faction faction, DiscreteCoordinates position, Unit... unit) {
        super(area, faction, position, unit);
        sprite = new Sprite("icwars/enemyCursor", 1.f, 1.f, this);
        ownerArea = (ICWarsArea) area;
        resetMotion();

    }

    /**
     * @param value (float) : Time taken by the AI to perform one of its units' action;
     * @param dt    (float) : Time incrementation until elapsed time reached value;
     */
    private void waitFor(float value, float dt) {
        if (counting) {
            counter += dt;
            if (counter > value) {
                counting = false;
            }
        } else {
            counter = 0f;
            counting = true;
        }

    }


    /**
     * Update AIPlayer;
     *
     * @param deltaTime (float) : number of update each second;
     */
    @Override
    public void update(float deltaTime) {
        stateUpdate(deltaTime);
        super.update(deltaTime);
        if (selectedUnit != null) {
            setCurrentPosition(selectedUnit.getPosition());
        }
    }

    /**
     * Update state;
     *
     * @param deltaTime (float) : number of update each second;
     */
    private void stateUpdate(float deltaTime) {
        if (!counting) {
            switch (state) {
                case NORMAL:
                    if (allUsed()) {
                        state = State.IDLE;
                        unitIndex = 0;
                        break;
                    }
                    selectUnit(getUnit(unitIndex));
                    ownerArea.setViewCandidate(selectedUnit);
                    state = State.MOVE_UNIT;
                    break;

                case MOVE_UNIT:
                    if (selectedUnit.isMoved() && !selectedUnit.isUsed()) {
                        state = State.ACTION_SELECTION;
                    }
                    if (!selectedUnit.isMoved()) {
                        selectedUnit.findClosestUnit();
                        selectedUnit.setMoved();
                        state = State.ACTION_SELECTION;
                    }
                    break;

                case ACTION_SELECTION:
                    setCurrentPosition(selectedUnit.getPosition());
                    for (Action action : selectedUnit.getPossibleAction()) {
                        if (action.getKey() == Keyboard.H && selectedUnit.getName().equals("Healer") && ownerArea.areUnitInRangeWeak(selectedUnit)) {
                            execute = action;
                            state = State.ACTION;
                            break;
                        } else if (action.getKey() == Keyboard.A && (!selectedUnit.getName().equals("Healer")  || !ownerArea.areUnitInRangeWeak(selectedUnit))) {
                            execute = action;
                            state = State.ACTION;
                            break;
                        }
                    }
                    break;
                case ACTION:
                    execute.doAutoAction(10f, this);
                    ++unitIndex;
                    break;
                default:
                    break;
            }
        }
        waitFor(0.5f, deltaTime);
    }


    /**
     * Draw either the AIPlayer cursor or the action cursor;
     *
     * @param canvas (Canvas) : The context canvas, target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (!(state == State.IDLE)) {
            sprite.draw(canvas);
            centerCamera();
        }
        if (execute != null) {
            execute.draw(canvas);
        }

    }

}
