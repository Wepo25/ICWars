package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class ActionAttack extends Action {


    /**
     *
     * @param unit
     * @param aire
     */
    public ActionAttack(Unit unit, ICWarsArea aire) {
        super(unit, aire, "(A)ttack", Keyboard.A);
    }

    /**
     * Attack action instructions for a RealPlayer
     * @param dt
     * @param player
     * @param keyboard
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {

        UnitsIndexes = ownerArea.getUnitIndex(ownerUnit, ICWarsActors.Faction.ENEMY);
        if (UnitsIndexes.size() >= 1) {

            if (keyboard.get(Keyboard.LEFT).isPressed()) {
                UnitIndex = (UnitIndex == 0) ? UnitsIndexes.size() - 1 : --UnitIndex;
            }
            if (keyboard.get(Keyboard.RIGHT).isPressed()) {
                UnitIndex = (UnitIndex == UnitsIndexes.size() - 1) ? 0 : ++UnitIndex;
            }

            ownerArea.setCamera(UnitsIndexes.get(UnitIndex));
            canDraw = true;

            if (keyboard.get(Keyboard.ENTER).isPressed()) {
                ownerArea.setDamage(UnitsIndexes.get(UnitIndex), ownerUnit.getPower());
                ownerUnit.setUsed();
                UnitsIndexes.clear();
                doActionBody(player);
                canDraw = false;
                UnitIndex = 0;
            }

            if (keyboard.get(Keyboard.TAB).isPressed()){
                doActionBody(player);
                UnitsIndexes.clear();
                canDraw = false;
            }

        } else {
            player.setState(1);
        }
    }

    /**
     * Attack action instructions for an AIPlayer
     * @param dt
     * @param player
     */
    @Override
    public void doAutoAction(float dt, ICWarsPlayer player) {

        UnitsIndexes = ownerArea.getUnitIndex(ownerUnit, ICWarsActors.Faction.ENEMY);
        UnitIndex = ownerArea.findWeakest(ownerUnit, "(A)ttack");
        if (UnitIndex != -1){
            ownerArea.setDamage(UnitIndex, ownerUnit.getPower());
            ownerArea.setCamera(UnitIndex);
            canDraw = true;
        }
        ownerUnit.setUsed();
        doActionBody(player);

    }

}
