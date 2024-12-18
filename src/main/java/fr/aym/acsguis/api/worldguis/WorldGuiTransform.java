package fr.aym.acsguis.api.worldguis;

import javax.vecmath.Vector3f;

/**
 * The world transform of an InWorldGui, can change dynamically
 */
public class WorldGuiTransform {
    private final Vector3f position;
    private float rotationYaw;
    private float rotationPitch;

    /**
     * The world transform of an InWorldGui
     *
     * @param position      The position of the gui
     * @param rotationYaw   The rotation yaw (y rotation axis) of the gui
     * @param rotationPitch The rotation pitch (x rotation axis of the gui)
     */
    public WorldGuiTransform(Vector3f position, float rotationYaw, float rotationPitch) {
        this.position = position;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }
}
