package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;
import java.util.Random;

/**
 * This class represents a leaf in the game.
 * The leaf has a small wind animation that moves it from side to side.
 */
public class Leaf extends GameObject {
	private static final Color LEAF_COLOR = new Color(50, 200, 30);
	private static final float WIND_CYCLE = 2f;
	private static final float MAX_ANGLE = 10f;

	/**
	 * The constructor of the Leaf class.
	 * It creates a leaf block and starts the wind animation after a random delay.
	 *
	 * @param topLeftCorner the top left position of the leaf.
	 * @param rand          the random object used to choose the animation delay.
	 */
	public Leaf(Vector2 topLeftCorner, Random rand) {
		super(topLeftCorner, Vector2.ONES.mult(Block.SIZE),
				new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
		setTag("leaf");

		float startDelay = rand.nextFloat() * WIND_CYCLE;
		new ScheduledTask(this, startDelay, false, () -> startWindAnimation());
	}

	/**
	 * Starts the wind animation for the leaf.
	 * This method creates a continuous back-and-forth rotation transition
	 * to simulate the leaf swaying naturally in the wind.
	 */
	private void startWindAnimation() {
		new Transition<Float>(
				this,
				this.renderer()::setRenderableAngle,
				-MAX_ANGLE,
				MAX_ANGLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				WIND_CYCLE,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);
	}
}