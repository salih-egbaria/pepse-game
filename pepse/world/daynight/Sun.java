package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * This class creates the sun object in the game.
 * The sun moves in a circle during the day cycle.
 */
public class Sun {

	private static final float SUN_SIZE = 80.0f;
	private static final float SUN_DISTANCE = 150.0f;
	private static final float START_ANGLE = 0.0f;
	private static final float END_ANGLE = 360.0f;
	private static final float GROUND_HEIGHT_PART = 2.0f / 3.0f;
	private static final float HALF_SCREEN = 2.0f;
	private static final String SUN_NAME = "sun";

	/**
	 * This function creates the sun.
	 * It makes a yellow circle and moves it around a center point
	 * by using a transition.
	 *
	 * @param windowDimensions the size of the game window.
	 * @param cycleLength the time of one full sun cycle.
	 * @return the sun GameObject.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Vector2 sunSize = new Vector2(SUN_SIZE, SUN_SIZE);
		OvalRenderable sunShape = new OvalRenderable(Color.YELLOW);

		GameObject sun = new GameObject(Vector2.ZERO, sunSize, sunShape);
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(SUN_NAME);

		float middleX = windowDimensions.x() / HALF_SCREEN;
		float groundY = windowDimensions.y() * GROUND_HEIGHT_PART;

		Vector2 centerPoint = new Vector2(middleX, groundY);
		Vector2 startPoint = new Vector2(middleX, groundY - SUN_DISTANCE);

		sun.setCenter(startPoint);

		new Transition<Float>(
				sun,
				angle -> {
					Vector2 sunOffset = startPoint.subtract(centerPoint);
					sun.setCenter(sunOffset.rotated(angle).add(centerPoint));
				},
				START_ANGLE,
				END_ANGLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);

		return sun;
	}
}