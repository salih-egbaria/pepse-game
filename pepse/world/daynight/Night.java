package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * This class creates the night object in the game.
 * The night object is a black rectangle that becomes darker and lighter
 * during the day and night cycle.
 */
public class Night {

	private static final float NIGHT_OPACITY = 0.5f;
	private static final float DAY_OPACITY = 0.0f;
	private static final float HALF_TIME = 2.0f;

	/**
	 * This function creates the night effect.
	 * It adds a black rectangle on the screen and changes its opacity
	 * with a transition, so the game looks like day and night.
	 *
	 * @param windowDimensions the size of the game window.
	 * @param cycleLength the full length of the day and night cycle.
	 * @return the night GameObject.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		RectangleRenderable blackBox = new RectangleRenderable(Color.BLACK);
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, blackBox);

		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag("night");

		new Transition<Float>(
				night,
				night.renderer()::setOpaqueness, // Using a method reference looks cleaner here
				DAY_OPACITY,
				NIGHT_OPACITY,
				Transition.CUBIC_INTERPOLATOR_FLOAT,
				cycleLength / HALF_TIME,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);

		return night;
	}
}