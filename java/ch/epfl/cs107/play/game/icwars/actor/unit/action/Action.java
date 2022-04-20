package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;



import java.util.List;

public abstract class Action {

    protected String name;
    protected int key;
    protected ICWarsArea ownerArea;
    protected Unit ownerUnit;

    protected int UnitIndex;
    protected List<Integer> UnitsIndexes;
    protected boolean canDraw;

    protected final ImageGraphics cursor = new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"),
            1f, 1f,
            new RegionOfInterest(4 * 18, 26 * 18, 16, 16));


    /**
     *
     * @param unit (Unit) : The Unit to which belongs this action;
     * @param aire (ICWarsArea) : The Area to which belongs this action;
     * @param nom (String) : Action's name;
     * @param w (int) : Action's key;
     */
    public Action(Unit unit, ICWarsArea aire, String nom, int w) {
        ownerUnit = unit;
        ownerArea = aire;
        name = nom;
        key = w;
        cursor.setDepth(3f);
    }

    /**
     * Action instructions for a RealPlayer
     * @param dt (float) : Action's duration;
     * @param player (ICWarsPlayer) : Player which executes this action;
     * @param keyboard (Keyboard)
     */
    public abstract void doAction(float dt, ICWarsPlayer player, Keyboard keyboard);

    /**
     * Action instructions for an AIPlayer
     * @param dt (float) : Action's duration;
     * @param player (ICWarsPlayer) : Player which executes this action;
     */
    public abstract void doAutoAction(float dt, ICWarsPlayer player);

    /**
     * Common body for action methods.
     *
     * @param player (ICWarsPlayer) : ICWarsPlayer to perform action on.
     */
    protected void doActionBody(ICWarsPlayer player){
        player.setState(1);
        player.getArea().setViewCandidate(player);
    }

    /**
     *
     * @return (int) : Action's key.
     */
    public int getKey() {
        return key;
    }

    /**
     *
     * @return (String) : Action's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Draws a cursor on the unit targeted by the action.
     *
     * @param canvas (Canvas) : The context canvas, not null;
     */
    public void draw(Canvas canvas) {
        if (canDraw) {
            cursor.setAnchor(ownerArea.getUnitPosition(
                    UnitsIndexes.get(( UnitIndex >= UnitsIndexes.size()) ? 0 : UnitIndex)).add(1, 0));
            cursor.draw(canvas);
        }
        canDraw = false;
        if(UnitsIndexes != null) {
            UnitsIndexes.clear();
        }
    }
}
