package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * This class creates the trees in the game.
 * It creates trunks, leaves and fruits according to random values.
 */
public class Flora {
	private static final Color TRUNK_COLOR = new Color(100, 50, 20);
	private static final Color LEAF_COLOR = new Color(50, 200, 30);
	private static final float TREE_PROBABILITY = 0.1f;
	private static final int MIN_TRUNK_BLOCKS = 4;
	private static final int MAX_TRUNK_BLOCKS = 8;
	private static final int CANOPY_RADIUS = 2;
	private static final float LEAF_PROBABILITY = 0.7f;
	private static final float FRUIT_PROBABILITY = 0.15f;
	private static final float CYCLE_LENGTH = 30f;
	private static final float HALF_BLOCK_DIVISOR = 2.0f;

	private final Function<Float, Float> groundHeightAt;
	private final int seed;

	/**
	 * The constructor of the Flora class.
	 * It gets a function that tells where the ground is,
	 * so the trees can start from the ground height.
	 *
	 * @param groundHeightAt a function that returns the ground height by x.
	 * @param seed           the seed for creating the same random trees every time.
	 */
	public Flora(Function<Float, Float> groundHeightAt, int seed) {
		this.groundHeightAt = groundHeightAt;
		this.seed = seed;
	}

	/**
	 * This function creates trees in the given x range.
	 * It checks every block position and maybe creates a tree there.
	 *
	 * @param minX the first x value of the range.
	 * @param maxX the last x value of the range.
	 * @return a list of all tree objects that were created.
	 */
	public List<GameObject> createInRange(int minX, int maxX) {
		List<GameObject> result = new ArrayList<>();
		int startX = (int) Math.floor((double) minX / Block.SIZE) * Block.SIZE;
		int endX = (int) Math.ceil((double) maxX / Block.SIZE) * Block.SIZE;

		for (int x = startX; x < endX; x += Block.SIZE) {
			Random rand = new Random(Objects.hash(x, seed));
			if (rand.nextFloat() >= TREE_PROBABILITY) {
				continue;
			}

			int trunkBlocks = MIN_TRUNK_BLOCKS + rand.nextInt(MAX_TRUNK_BLOCKS - MIN_TRUNK_BLOCKS + 1);
			float surfaceY = groundHeightAt.apply((float) x);
			int groundY = (int) Math.floor(surfaceY / Block.SIZE) * Block.SIZE;
			int trunkTopY = groundY - trunkBlocks * Block.SIZE;

			buildTrunk(x, groundY, trunkTopY, result);
			buildCanopy(x, trunkTopY, result, rand);
		}
		return result;
	}

	/**
	 * Builds the vertical column of blocks representing the tree trunk.
	 * We loop from the calculated top of the trunk down to the ground.
	 *
	 * @param x         the x-coordinate where the trunk is placed.
	 * @param groundY   the y-coordinate of the ground level.
	 * @param trunkTopY the y-coordinate of the highest trunk block.
	 * @param result    the list of game objects to add the trunk blocks to.
	 */
	private void buildTrunk(int x, int groundY, int trunkTopY, List<GameObject> result) {
		for (int y = trunkTopY; y < groundY; y += Block.SIZE) {
			Block trunk = new Block(
					Vector2.of(x, y),
					new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR))
			);
			trunk.setTag("trunk");
			result.add(trunk);
		}
	}

	/**
	 * Generates the leaves and fruits around the top of the tree trunk.
	 * We loop through a square area around the trunk top and randomly place items.
	 *
	 * @param trunkX    the x-coordinate of the tree trunk.
	 * @param trunkTopY the y-coordinate of the top of the trunk.
	 * @param result    the list of game objects to add the leaves and fruits to.
	 * @param rand      the random number generator used for this specific tree.
	 */
	private void buildCanopy(int trunkX, int trunkTopY, List<GameObject> result, Random rand) {
		for (int dx = -CANOPY_RADIUS; dx <= CANOPY_RADIUS; dx++) {
			for (int dy = -CANOPY_RADIUS; dy <= CANOPY_RADIUS; dy++) {
				int lx = trunkX + dx * Block.SIZE;
				int ly = trunkTopY + dy * Block.SIZE;
				if (rand.nextFloat() < LEAF_PROBABILITY) {
					result.add(new Leaf(Vector2.of(lx, ly), rand));
				}
				if (rand.nextFloat() < FRUIT_PROBABILITY) {
					float halfSize = Block.SIZE / HALF_BLOCK_DIVISOR;
					Vector2 center = Vector2.of(lx + halfSize, ly + halfSize);
					result.add(new Fruit(center, CYCLE_LENGTH));
				}
			}
		}
	}
}