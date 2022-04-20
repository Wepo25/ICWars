package ch.epfl.cs107.play.game.icwars.area.screen;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;


public class ICWarsScreen extends ICWarsArea {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
    }


    @Override
    public DiscreteCoordinates getAllyPlayerSpawnPosition() {
        return null;
    }

    @Override
    public DiscreteCoordinates getEnemyPlayerSpawnPosition() {
        return null;
    }

    @Override
    public DiscreteCoordinates getAllyUnitSpawnPosition(String string) {
        return null;
    }

    @Override
    public DiscreteCoordinates getEnemyUnitSpawnPosition(String string) {
        return null;
    }

    @Override
    public Unit[] getAllyPlayerUnits() {
        return new Unit[0];
    }

    @Override
    public Unit[] getEnemyPlayerUnits() {
        return new Unit[0];
    }

    @Override
    public ICWarsPlayer[] getAreaEnemyPlayer(int key) {
        return new ICWarsPlayer[0];
    }


}
