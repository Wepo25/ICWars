package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class ActionWait extends Action {

    /**
     * 
     * @param unit
     * @param aire
     */
    public ActionWait(Unit unit, ICWarsArea aire) {
        super(unit, aire, "(W)ait", Keyboard.W);

    }

    /**
     * Wait action instructions for a RealPlayer
     * @param dt
     * @param player
     * @param keyboard
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        ownerUnit.setUsed();
        doActionBody(player);

    }

    /**
     * Wait action instructions for an AIPlayer
     * @param dt
     * @param player
     */
    @Override
    public void doAutoAction(float dt, ICWarsPlayer player) {
        ownerUnit.setUsed();
        doActionBody(player);
    }


}
