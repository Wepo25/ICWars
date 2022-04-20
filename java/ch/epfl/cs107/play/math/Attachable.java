package ch.epfl.cs107.play.math;

/**
 * Represents a positionable object that can be placed or attached.
 */
public interface Attachable extends Positionable {

    /**
     * @return (Positionable): reference object, may be null
     */
    Positionable getParent();

    /**
     * Chooses reference object.
     *
     * @param parent (Positionable): any positionable, may be null
     */
    void setParent(Positionable parent);

    /**
     * @return (Transform): relative affine transformation, not null
     */
    Transform getRelativeTransform();

    /**
     * Sets relative affine transformation.
     *
     * @param transform (Transform): any transform, not null
     */
    void setRelativeTransform(Transform transform);

}
