package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.City;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Keyboard;

public class ActionCapture extends Action {

    public ActionCapture(Unit unit, ICWarsArea area) {
        super(unit, area, "(C)apture", Keyboard.C);
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        ownerUnit.capture();
        ownerUnit.setUsed();
        doActionBody(player);

    }

    @Override
    public void doAutoAction(float dt, ICWarsPlayer player) {

    }

}
