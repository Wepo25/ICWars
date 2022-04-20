package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActors;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.Level0;
import ch.epfl.cs107.play.game.icwars.area.Level1;
import ch.epfl.cs107.play.game.icwars.area.screen.*;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public class ICWars extends AreaGame {

    public final static float CAMERA_SCALE_FACTOR = 10.f;

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};

    private StateICWars stateICWars;
    private ICWarsPlayer currentPlayer;
    private ICWarsPlayer player1;

    private final List<ICWarsPlayer> players = new ArrayList<>();

    private final List<ICWarsPlayer> waitingCurrentPlayers = new ArrayList<>();

    private final List<ICWarsPlayer> waitingNextPlayers = new ArrayList<>();

    private int areaIndex;
    private int key;

    /**
     * Enumeration of different game state.
     */
    enum StateICWars {
        INIT, CHOOSE_PLAYER, START_PLAYER_TURN, PLAYER_TURN, END_PLAYER_TURN, END_TURN, END, MENU
    }


    /**
     * Add every area needed.
     */
    private void createAreas() {
        addArea(new Menu());
        addArea(new Level0());
        addArea(new Level1());
        addArea(new GameOver());
        addArea(new YouWin());
        addArea(new Player1Win());
        addArea(new Player2Win());
        addArea(new Player1WinInbetween());
        addArea(new Player2WinInbetween());
    }


    /**
     * @param window     (Window) : The game's window;
     * @param fileSystem (FileSystem) : The game's filesystem;
     * @return (boolean) : If the game started correctly.
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        if (super.begin(window, fileSystem)) {
            createAreas();
            setCurrentArea("icwars/Menu", true);
            stateICWars = StateICWars.MENU;
            MenuArea();
            return true;
        }
        return false;
    }

    /**
     * Initialize a new level.
     *
     * @param areaKey (String) : The name of the level we want to initialize;
     */
    private void initArea(String areaKey) {
        waitingCurrentPlayers.clear();
        waitingNextPlayers.clear();
        players.clear();
        ICWarsArea area = (ICWarsArea) setCurrentArea(areaKey, true);
        stateICWars = StateICWars.INIT;
        player1 = new RealPlayer(area, ICWarsActors.Faction.ALLIED, area.getAllyPlayerSpawnPosition(),
                area.getAllyPlayerUnits());
        players.add(player1);
        for (ICWarsPlayer player : area.getAreaEnemyPlayer(key)) {
            players.add(player);
        }
        for (ICWarsPlayer player : players) {
            player.enterArea(area);
        }
    }

    /**
     * @param deltaTime (float) : Number of time the game performs an update each second;
     */
    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getWindow().getKeyboard();
        Button N = keyboard.get(Keyboard.N);
        Button R = keyboard.get(Keyboard.R);
        if (N.isReleased()) {
            if (areaIndex != areas.length - 1) {
                switchArea();
            } else {
                System.out.println("Game Over");
                YouWinArea();

            }
            return;
        }
        if (R.isReleased()) {
            for (ICWarsActors player : players) {
                player.leaveArea();
            }
            players.clear();
            waitingCurrentPlayers.clear();
            waitingNextPlayers.clear();
            begin(getWindow(), getFileSystem());
        }
        switch (this.stateICWars) {
            case INIT:
                for (ICWarsPlayer player : players) {
                    waitingCurrentPlayers.add(player);
                }
                stateICWars = StateICWars.CHOOSE_PLAYER;
                break;
            case CHOOSE_PLAYER:
                if (waitingCurrentPlayers.size() == 0) {
                    stateICWars = StateICWars.END_TURN;
                } else {
                    currentPlayer = waitingCurrentPlayers.get(0);
                    currentPlayer.centerCamera();
                    waitingCurrentPlayers.remove(currentPlayer);
                    if (currentPlayer.isDefeated()) {
                        currentPlayer.leaveArea();
                        stateICWars = StateICWars.CHOOSE_PLAYER;
                        break;
                    }
                    stateICWars = StateICWars.START_PLAYER_TURN;
                }
                break;
            case START_PLAYER_TURN:
                currentPlayer.startTurn();
                stateICWars = StateICWars.PLAYER_TURN;
                break;

            case PLAYER_TURN:
                if (currentPlayer.isState() == ICWarsPlayer.State.IDLE) {
                    stateICWars = StateICWars.END_PLAYER_TURN;
                    break;
                }
                if (waitingCurrentPlayers.size() == 0 && waitingNextPlayers.size() == 0) {
                    stateICWars = StateICWars.END_PLAYER_TURN;
                    break;
                }
                if (waitingCurrentAllDefeated() && waitingNextAllDefeated()) {
                    stateICWars = StateICWars.END_PLAYER_TURN;
                    break;
                }
                break;
            case END_PLAYER_TURN:
                if (currentPlayer.isDefeated()) {
                    currentPlayer.leaveArea();
                } else if (waitingNextPlayers.size() >= 1 || waitingCurrentPlayers.size() >= 1) {
                    waitingNextPlayers.add(currentPlayer);
                    stateICWars = StateICWars.CHOOSE_PLAYER;
                    break;
                } else {
                    stateICWars = StateICWars.END_TURN;
                }
            case END_TURN:
                if (waitingNextPlayers.size() <= 1) {
                    stateICWars = StateICWars.END;
                    break;
                }
                waitingCurrentPlayers.clear();
                for (ICWarsPlayer i : waitingNextPlayers) {
                    if (i != currentPlayer) {
                        waitingCurrentPlayers.add(i);
                    }
                }
                waitingCurrentPlayers.add(currentPlayer);
                waitingNextPlayers.clear();
                stateICWars = StateICWars.CHOOSE_PLAYER;
                break;

            case END:
                if (areaIndex < areas.length - 1) {
                    if (player1.isDefeated() && key == 0) {
                        gameoverArea();
                        break;
                    } else if (player1.isDefeated() && key == 1) {
                        Player2WinAreaInbetween();
                        break;
                    } else if (!player1.isDefeated() && key == 1) {
                        Player1WinAreaInbetween();
                        break;
                    } else {
                        switchArea();
                        break;
                    }
                } else if (player1.isDefeated() && key == 0) {
                    gameoverArea();
                    break;
                } else if (player1.isDefeated() && key == 1) {
                    Player2WinArea();
                    break;
                } else if (key == 1) {
                    Player1WinArea();
                    break;
                } else {
                    YouWinArea();
                }
            case MENU:
                MenuArea();
                break;
        }
        super.update(deltaTime);

    }

    /**
     * End the game.
     */
    @Override
    public void end() {
        System.exit(0);
    }

    /**
     * @return (String) : The game's name.
     */
    @Override
    public String getTitle() {
        return "ICWars";
    }

    /**
     * Switch to the next area.
     */
    protected void switchArea() {
        removePlayers();
        areaIndex = (areaIndex == 0) ? 1 : 0;
        initArea(areas[areaIndex]);
    }

    /**
     * Remove all players from the current area.
     */
    private void removePlayers() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
    }

    private void gameoverArea() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/GameOver", true);
    }

    private void YouWinArea() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/YouWin", true);
    }

    private void Player1WinArea() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/Player1Win", true);
    }

    private void Player2WinArea() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/Player2Win", true);
    }

    private void Player1WinAreaInbetween() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/Player1WinInbetween", true);
    }

    private void Player2WinAreaInbetween() {
        for (ICWarsActors player : players) {
            player.leaveArea();
        }
        setCurrentArea("icwars/Player2WinInbetween", true);
    }

    private void MenuArea() {
        Keyboard keyboard = getWindow().getKeyboard();
        Button V = keyboard.get(Keyboard.V);
        Button B = keyboard.get(Keyboard.B);
        if (V.isPressed()) {
            key = 0;
            areaIndex = 0;
            initArea(areas[areaIndex]);
        }
        if (B.isPressed()) {
            key = 1;
            areaIndex = 0;
            initArea(areas[areaIndex]);
        }
    }

    /**
     * @return (boolean) : If every player waiting for the current round are defeated.
     */
    private boolean waitingCurrentAllDefeated() {
        for (ICWarsPlayer player : waitingCurrentPlayers) {
            if (!player.isDefeated()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return (boolean) : If every player waiting for the next round are defeated.
     */
    private boolean waitingNextAllDefeated() {
        for (ICWarsPlayer player : waitingNextPlayers) {
            if (!player.isDefeated()) {
                return false;
            }
        }
        return true;
    }
}