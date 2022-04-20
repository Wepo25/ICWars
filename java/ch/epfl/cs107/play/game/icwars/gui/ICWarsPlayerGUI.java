package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class ICWarsPlayerGUI implements Graphics {

    public static final float FONT_SIZE = 20f;
    private final RealPlayer owner;
    private final ICWarsInfoPanel infoPanel;
    private final ICWarsActionsPanel actionPanel;

    /**
     *
     * @param cameraScaleFactor (float) : The camera scale factor;
     * @param player (ICWarsPlayer) : The ICWarsPlayer which is monitored by the GUI.
     */
    public ICWarsPlayerGUI(float cameraScaleFactor, ICWarsPlayer player) {
        infoPanel = new ICWarsInfoPanel(cameraScaleFactor);
        actionPanel = new ICWarsActionsPanel(cameraScaleFactor);
        owner = (RealPlayer) player;
    }

    /**
     *
     * @param canvas (Canvas) : The context canvas, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (owner.isState() == ICWarsPlayer.State.ACTION_SELECTION) {
            setAction();
            actionPanel.draw(canvas);
        }

        if (owner.isState() == ICWarsPlayer.State.MOVE_UNIT && owner.getSelectedUnit() != null && !owner.getSelectedUnit().isMoved()) {
            List<DiscreteCoordinates> currentCell = owner.getCurrentCells();
            DiscreteCoordinates position = new DiscreteCoordinates(currentCell.get(0).x, currentCell.get(0).y);
            owner.getSelectedUnit().drawRangeAndPathTo(position, canvas);
        }
        if (owner.isState() == ICWarsPlayer.State.SELECT_CELL || owner.isState() == ICWarsPlayer.State.NORMAL)
            infoPanel.draw(canvas);

    }

    /**
     * Shows the characteristics of a given unit on bottom right screen.
     *
     * @param unit (Unit) : The unit which we display information of.
     */
    public void setPanelUnit(Unit unit) {
        infoPanel.setUnit(unit);
    }

    /**
     * Shows the characteristics of a given cell on bottom right screen.
     *
     * @param cell (Cell) : The cell which we display information of.
     */
    public void setPanelCell(ICWarsBehavior.ICWarsCellType cell) {
        infoPanel.setCurrentCell(cell);
    }

    /**
     * Shows the possible actions of a given unit on top right screen.
     */
    public void setAction() {
        actionPanel.setActions(owner.getSelectedUnit().getPossibleAction());
    }

}
