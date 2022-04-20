package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Healer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldat;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends ICWarsArea {


    /**
     * @return (String) : The name of the area.
     */
    @Override
    public String getTitle() {
        return "icwars/Level0";
    }

    /**
     * Create area background.
     */
    @Override
    protected void createArea() {
        registerActor(new Background(this));
    }

    /**
     * @return (DiscreteCoordinates) : Ally players spawn position in a given area
     */
    public DiscreteCoordinates getAllyPlayerSpawnPosition() {
        DiscreteCoordinates spawn = new DiscreteCoordinates(6, 4);
        return spawn;
    }

    /**
     * @return (DiscreteCoordinates) : Enemy players spawn position in a given area
     */
    public DiscreteCoordinates getEnemyPlayerSpawnPosition() {
        DiscreteCoordinates spawn = new DiscreteCoordinates(7, 4);
        return spawn;
    }

    /**
     * @param name (String) : The unit's name;
     * @return (DiscreteCoordinates) : Ally player's units spawn position in a given area
     */
    @Override
    public DiscreteCoordinates getAllyUnitSpawnPosition(String name) {
        if (name.equals("Soldat")) {
            return new DiscreteCoordinates(4, 5);
        }
        if (name.equals("Tank")) {
            return new DiscreteCoordinates(1, 5);
        }
        if (name.equals("Healer")) {
            return new DiscreteCoordinates(2, 5);
        } else {
            return new DiscreteCoordinates(0, 0);
        }
    }

    /**
     * @param name (String) : The unit's name;
     * @return (DiscreteCoordinates) : Enemy player's units spawn position in a given area
     */
    @Override
    public DiscreteCoordinates getEnemyUnitSpawnPosition(String name) {
        if (name.equals("Soldat")) {
            return new DiscreteCoordinates(8, 5);
        }
        if (name.equals("Tank")) {
            return new DiscreteCoordinates(9, 5);
        }
        if (name.equals("Healer")) {
            return new DiscreteCoordinates(7, 5);
        } else {
            return new DiscreteCoordinates(0, 0);
        }

    }

    /**
     * @return (Unit[]) : A list of units to give to ally player in a given area
     */
    @Override
    public Unit[] getAllyPlayerUnits() {
        return new Unit[]{new Tank(this, ICWarsActors.Faction.ALLIED, getAllyUnitSpawnPosition("Tank")),
                new Soldat(this, ICWarsActors.Faction.ALLIED, getAllyUnitSpawnPosition("Soldat")),
                new Healer(this, ICWarsActors.Faction.ALLIED, getAllyUnitSpawnPosition("Healer"))};
    }

    /**
     * @return (Unit[]) : A list of units to give to enemy player in a given area
     */
    @Override
    public Unit[] getEnemyPlayerUnits() {
        return new Unit[]{new Tank(this, ICWarsActors.Faction.ENEMY, getEnemyUnitSpawnPosition("Tank")),
                new Soldat(this, ICWarsActors.Faction.ENEMY, getEnemyUnitSpawnPosition("Soldat")),
                new Healer(this, ICWarsActors.Faction.ENEMY, getEnemyUnitSpawnPosition("Healer"))};
    }

    /**
     * @param key (int) : Tells the game if we play against another person or against an AI;
     * @return (ICWarsPlayer[]) : A list of ICWarsplayers depending on the given key (if key = 0 : AIPlayer, if key = 1 : RealPlayer).
     */
    @Override
    public ICWarsPlayer[] getAreaEnemyPlayer(int key) {
        if (key == 0) {
            return new ICWarsPlayer[]{new AIPlayer(this, ICWarsActors.Faction.ENEMY, this.getEnemyPlayerSpawnPosition(),
                    this.getEnemyPlayerUnits())};
        } else {
            return new ICWarsPlayer[]{new RealPlayer(this, ICWarsActors.Faction.ENEMY, this.getEnemyPlayerSpawnPosition(),
                    this.getEnemyPlayerUnits())};
        }
    }


}
