package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class TombStone extends ICWarsActors {

    /**
     * @param area     (Area) : Area which the TombStone belongs to;
     * @param faction  (Faction) : TombStone's faction;
     * @param position (DiscreteCoordinates) : TombStone's coordinates (Where unit died);
     */
    public TombStone(Area area, Faction faction, DiscreteCoordinates position) {
        super(area, faction, position);
        sprite = new Sprite("icwars/rip", 1.5f, 1.5f, this, null, new Vector(-0.25f, 0.1f));
        sprite.setDepth(1f);
        sprite.setAlpha(0.2f);
    }


}