package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Healer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class ActionHeal extends Action  {

    /**
     *
     * @param unit this Action belongs to the unit
     * @param area
     */
    public ActionHeal(Unit unit, ICWarsArea area) {
        super(unit, area, "(H)eal", Keyboard.H);
    }

    /**
     * Heal action instructions for a RealPlayer
     * @param dt
     * @param player
     * @param keyboard
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {

        UnitsIndexes = ownerArea.getUnitIndex(ownerUnit, ICWarsActors.Faction.ALLIED);

        if (UnitsIndexes.size() >= 1) {

            if (keyboard.get(Keyboard.LEFT).isPressed()) {
                UnitIndex = (UnitIndex == 0) ? UnitsIndexes.size() - 1 : --UnitIndex;
            }
            if (keyboard.get(Keyboard.RIGHT).isPressed()) {
                UnitIndex = (UnitIndex == UnitsIndexes.size() - 1) ? 0 : ++UnitIndex;
            }

            canDraw = true;
            ownerArea.setCamera(UnitsIndexes.get(UnitIndex));

            if (keyboard.get(Keyboard.ENTER).isPressed()) {
                ownerArea.setHeal(UnitsIndexes.get(UnitIndex), ownerUnit.getHeal());
                ownerUnit.setUsed();
                doActionBody(player);
                canDraw = false;
                UnitIndex = 0;

            }
            if (keyboard.get(Keyboard.TAB).isPressed()){
                doActionBody(player);
                canDraw = false;
            }
        }}

    /**
     * Heal action instructions for an AIPlayer
     * @param dt
     * @param player
     */
    @Override
    public void doAutoAction(float dt, ICWarsPlayer player) {

        UnitsIndexes = ownerArea.getUnitIndex(ownerUnit, ICWarsActors.Faction.ALLIED);
        if ((ownerArea.findWeakest(ownerUnit, "(H)ealer") != -1)){
            ownerArea.setHeal(ownerArea.findWeakest(ownerUnit, "(H)ealer"), ownerUnit.getHeal());
            ownerArea.setCamera(UnitsIndexes.get(UnitIndex));
        }
        ownerUnit.setUsed();
        doActionBody(player);

    }

}