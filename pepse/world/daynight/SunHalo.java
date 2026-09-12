package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * This class creates the halo around the sun.
 * The halo follows the sun while the sun is moving.
 */
public class SunHalo {
	private static final Color LIGHT_COLOR = new Color(255, 255, 0, 20);
	private static final float LIGHT_SIZE = 120f;
	private static final String HALO_TAG = "sunHalo";

	/**
	 * This function creates the sun halo.
	 * It creates a light yellow circle and keeps it in the same center
	 * as the sun.
	 *
	 * @param sun the sun object that the halo should follow.
	 * @return the halo GameObject.
	 */
	public static GameObject create(GameObject sun) {
		OvalRenderable circle = new OvalRenderable(LIGHT_COLOR);
		Vector2 size = new Vector2(LIGHT_SIZE, LIGHT_SIZE);

		GameObject halo = new GameObject(Vector2.ZERO, size, circle);
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag(HALO_TAG);

		halo.setCenter(sun.getCenter());

		// Keep halo centered on the sun every frame
		halo.addComponent(dt -> halo.setCenter(sun.getCenter()));

		return halo;
	}
}