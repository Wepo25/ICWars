package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.City;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public class ICWarsBehavior extends AreaBehavior {

    private final List<Cell> cells = new ArrayList<Cell>();

    /**
     * @param window (Window) : The game window;
     * @param name   (String) : The name of the behavior's file;
     */
    public ICWarsBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICWarsCellType color = ICWarsCellType.toType(getRGB(height - 1 - y, x));
                setCell(x, y, new ICWarsCell(x, y, color));
            }
        }
    }

    /**
     * Register an actor in the given area.
     *
     * @param area (Area) : Area in which we want to register an actor;
     */
    public void registerActor(Area area) {
        for (int i = 0; i < cells.size(); i++) {
            if (((ICWarsCell) cells.get(i)).getType().equals(ICWarsCellType.CITY)) {
                City test = new City(area, cells.get(i).getCurrentCells().get(0));
                area.registerActor(test);
            }
        }
    }

    /**
     * Enumeration of cell types which determine the cells behavior.
     */
    public enum ICWarsCellType {
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NONE(0, 0), // Should never be used except
        // in the toType method
        ROAD(-16777216, 0), // the second value is the number
        // of defense stars
        PLAIN(-14112955, 1),
        WOOD(-65536, 3),
        RIVER(-16776961, 0),
        MOUNTAIN(-256, 4),
        CITY(-1, 2);


        private final int type;
        private final int stars;

        /**
         * @param type  (int) : The type of cell given in the enumeration;
         * @param stars (int) : defense offered by a given type;
         */
        ICWarsCellType(int type, int stars) {
            this.type = type;
            this.stars = stars;
        }

        public static ICWarsCellType toType(int type) {
            for (ICWarsCellType ict : ICWarsCellType.values()) {
                if (ict.type == type)
                    return ict;
            }
            return NONE;
        }

        /**
         * @return (String) : The type in the form of String.
         */
        public String typeToString() {
            return "" + this;

        }

        /**
         * @return (String) : The defense in form of String.
         */
        public String getDefenseStar() {
            return "" + this.stars;
        }
    }

    public class ICWarsCell extends AreaBehavior.Cell {

        private final ICWarsCellType type;

        /**
         * @param x     (int) : X-axis coordinate;
         * @param y     (int) : Y-axis coordinate;
         * @param color (ICWarsCellType) : The type of the cell;
         */

        public ICWarsCell(int x, int y, ICWarsCellType color) {
            super(x, y);
            this.type = color;
            cells.add(this);
        }

        /**
         * @param entity (Interactable) : not null
         * @return (boolean) : If the given entity can leave its cell.
         */
        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }


        /**
         * Check if an entity can Enter
         *
         * @param entity (Interactable), not null.
         * @return (boolean) : If the given entity can enter this cell.
         */
        @Override
        protected boolean canEnter(Interactable entity) {
            for (Interactable i : entities) {
                if (i.takeCellSpace() && entity.takeCellSpace()) {
                    return false;
                }
            }
            return true;


        }

        /**
         * @return (int) : Defense stars from the cell.
         */
        public int getStars() {
            return type.stars;
        }

        /**
         * @return (ICWarsCellType) : Type of the cell.
         */
        public ICWarsCellType getType() {
            return type;
        }


        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        /**
         * Call the interaction on this from the handler.
         *
         * @param v (AreaInteractionVisitor) : The visitor handle the interaction with this.
         */
        @Override
        public void acceptInteraction(AreaInteractionVisitor v) {
            ((ICWarsInteractionVisitor) v).interactWith(this);
        }

    }

}