package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.utils.NoiseGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates the terrain in the game.
 * It uses noise to make the ground height change smoothly.
 */
public class Terrain {

	private static final Color GROUND_COLOR = new Color(212, 123, 74);
	private static final int GROUND_DEPTH = 20;
	private static final float NOISE_POWER = 7f;
	private static final float SCREEN_PART = 2f / 3f;

	private final float startGroundHeight;
	private final float windowHeight;
	private final NoiseGenerator noise;

	/**
	 * The constructor of the Terrain class.
	 * It saves the first ground height and creates the noise generator.
	 *
	 * @param screenSize the size of the game window.
	 * @param seed       the seed used for the noise generator.
	 */
	public Terrain(Vector2 screenSize, int seed) {
		startGroundHeight = screenSize.y() * SCREEN_PART;
		windowHeight = screenSize.y();
		noise = new NoiseGenerator(seed, (int) startGroundHeight);
	}

	/**
	 * This function returns the ground height at a given x position.
	 * The noise makes the ground look natural and not flat.
	 *
	 * @param x the x position we want to check.
	 * @return the ground height at this x position.
	 */
	public float groundHeightAt(float x) {
		float noiseSize = Block.SIZE * NOISE_POWER;
		float groundNoise = (float) noise.noise(x, noiseSize);
		return startGroundHeight + groundNoise;
	}

	/**
	 * This function creates ground blocks inside a given x range.
	 * It creates columns of blocks from the ground height downward.
	 *
	 * @param minX the first x value of the range.
	 * @param maxX the last x value of the range.
	 * @return a list of ground blocks.
	 */
	public List<Block> createInRange(int minX, int maxX) {
		List<Block> blocks = new ArrayList<>();
		int startX = (int) Math.floor((double) minX / Block.SIZE) * Block.SIZE;
		int endX = (int) Math.ceil((double) maxX / Block.SIZE) * Block.SIZE;
		for (int x = startX; x < endX; x += Block.SIZE) {
			float groundY = groundHeightAt(x);
			int firstBlockY = (int) Math.floor(groundY / Block.SIZE) * Block.SIZE;
			int bottomY = (int) (Math.ceil((windowHeight + GROUND_DEPTH * Block.SIZE)
					/ Block.SIZE) * Block.SIZE);
			for (int y = firstBlockY; y < bottomY; y += Block.SIZE) {
				Block block = new Block(
						Vector2.of(x, y),
						new RectangleRenderable(ColorSupplier.approximateColor(GROUND_COLOR))
				);
				block.setTag("ground");
				blocks.add(block);
			}
		}

		return blocks;
	}
}
