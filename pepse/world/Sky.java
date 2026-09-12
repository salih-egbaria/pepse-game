package pepse.world;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * This class creates the sky object in the game.
 * The sky is a blue rectangle that stays fixed on the screen.
 */
public class Sky {

	private static final Color SKY_COLOR = Color.decode("#80C6E5");

	/**
	 * This function creates the sky.
	 * It makes a blue rectangle with the size of the window
	 * and puts it in camera coordinates.
	 *
	 * @param screenSize the size of the game window.
	 * @return the sky GameObject.
	 */
	public static GameObject create(Vector2 screenSize) {
		GameObject sky = new GameObject(
				Vector2.ZERO,
				screenSize,
				new RectangleRenderable(SKY_COLOR)
		);

		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag("sky");

		return sky;
	}
}