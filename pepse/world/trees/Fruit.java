package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.Color;

/**
 * This class represents a fruit in the game.
 * The avatar can eat the fruit, then the fruit disappears
 * and comes back again after some time.
 */
public class Fruit extends GameObject {
	private static final Color FRUIT_COLOR = new Color(255, 80, 0);
	private static final float FRUIT_SIZE_OFFSET = 5f;
	private static final float FRUIT_SIZE = Block.SIZE - FRUIT_SIZE_OFFSET;
	private static final float FULL_OPACITY = 1f;
	private static final float ZERO_OPACITY = 0f;

	private final float cycleLength;
	private boolean isEaten = false;
	private Runnable onEaten;

	/**
	 * The constructor of the Fruit class.
	 * It creates a fruit object in the given center position.
	 *
	 * @param center      the center position of the fruit.
	 * @param cycleLength the time until the fruit appears again after being eaten.
	 */
	public Fruit(Vector2 center, float cycleLength) {
		super(center.subtract(Vector2.ONES.mult(FRUIT_SIZE / 2f)),
				Vector2.ONES.mult(FRUIT_SIZE),
				new OvalRenderable(FRUIT_COLOR));
		physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		setTag("fruit");
		this.cycleLength = cycleLength;
	}

	/**
	 * This function sets what should happen when the fruit is eaten.
	 * In the game, it is used to give energy to the avatar.
	 *
	 * @param callback the action to run when the fruit is eaten.
	 */
	public void setOnEaten(Runnable callback) {
		this.onEaten = callback;
	}

	/**
	 * Triggered when another game object collides with the fruit.
	 * If the avatar touches the fruit, it becomes invisible, triggers the eaten callback,
	 * and sets a timer to reappear later.
	 *
	 * @param other     the game object colliding with the fruit.
	 * @param collision details about the collision event.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (!isEaten && "avatar".equals(other.getTag())) {
			isEaten = true;
			renderer().setOpaqueness(ZERO_OPACITY);
			if (onEaten != null) {
				onEaten.run();
			}
			new ScheduledTask(this, cycleLength, false, () -> {
				isEaten = false;
				renderer().setOpaqueness(FULL_OPACITY);
			});
		}
	}
}