package ch.epfl.cs107.play.game.icwars.area.screen;

import ch.epfl.cs107.play.game.areagame.actor.Background;

public class GameOver extends ICWarsScreen {

    public String getTitle() {
        return "icwars/GameOver";
    }

    protected void createArea() {
        registerActor(new Background(this));
    }

}
