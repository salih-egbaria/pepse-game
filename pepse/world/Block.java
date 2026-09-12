package pepse.world;
import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * This class represents one block in the game.
 * It is used for ground blocks and also for tree trunks.
 */
public class Block extends GameObject {
	/**
	 * The standard size of a single block (both width and height) in pixels.
	 */
	public static final int SIZE = 30;

	/**
	 * The constructor of the Block class.
	 * It creates a square block with fixed size and makes it immovable.
	 *
	 * @param pos the top left position of the block.
	 * @param renderable the shape or image of the block.
	 */
	public Block(Vector2 pos, Renderable renderable) {
		super(pos, new Vector2(SIZE, SIZE), renderable);

		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
	}
}