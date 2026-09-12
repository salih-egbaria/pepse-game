package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.avatar.Avatar;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the main manager of the Pepse game.
 * It creates the game objects, updates the world, and starts the game.
 */
public class PepseGameManager extends GameManager {
	private static final float DAY_LEN = 30.0f;
	private static final int GAME_SEED = 42;
	private static final int SUN_UP = 20;
	private static final int HALO_UP = 10;
	private static final int LEAF_DOWN = -5;
	private static final int FRUIT_UP = 5;
	private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
	private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + LEAF_DOWN;
	private static final int FRUIT_LAYER = Layer.DEFAULT + FRUIT_UP;
	private static final int SUN_LAYER = Layer.BACKGROUND + SUN_UP;
	private static final int HALO_LAYER = Layer.BACKGROUND + HALO_UP;
	private static final float START_SCREEN_BACK = -1.0f;
	private static final float START_SCREEN_FRONT = 2.0f;
	private static final float LOAD_AREA = 1.5f;
	private static final float DELETE_AREA = 2.5f;
	private static final float CENTER_DIV = 2.0f;
	private static final float TEXT_START_X = 10.0f;
	private static final float TEXT_START_Y = 10.0f;
	private static final float TEXT_SIZE_X = 80.0f;
	private static final float TEXT_SIZE_Y = 30.0f;
	private static final String FIRST_ENERGY = "100%";
	private static final String ENERGY_SIGN = "%";
	private static final float FRUIT_POWER = 10.0f;

	private Terrain ground;
	private Flora trees;
	private Avatar player;
	private Vector2 screenSize;
	private TextRenderable energyShow;
	private final Map<Integer, List<GameObject>> loadedCols = new HashMap<>();

	/**
	 * This function creates the first objects in the game.
	 * It creates the sky, night, sun, halo, terrain, trees, player,
	 * energy text, collisions and camera.
	 *
	 * @param imageReader      reads images from the assets folder.
	 * @param soundReader      reads sounds for the game.
	 * @param inputListener    reads the keyboard input.
	 * @param windowController gives information about the game window.
	 */
	@Override
	public void initializeGame(ImageReader imageReader, SoundReader soundReader,
							   UserInputListener inputListener,
							   WindowController windowController) {

		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		screenSize = windowController.getWindowDimensions();
		float screenWidth = screenSize.x();

		GameObject sky = Sky.create(screenSize);
		gameObjects().addGameObject(sky, Layer.BACKGROUND);

		GameObject night = Night.create(screenSize, DAY_LEN);
		gameObjects().addGameObject(night, Layer.FOREGROUND);

		GameObject sun = Sun.create(screenSize, DAY_LEN);
		gameObjects().addGameObject(sun, SUN_LAYER);

		GameObject halo = SunHalo.create(sun);
		gameObjects().addGameObject(halo, HALO_LAYER);

		ground = new Terrain(screenSize, GAME_SEED);
		trees = new Flora(x -> ground.groundHeightAt(x), GAME_SEED);

		float playerX = screenWidth / CENTER_DIV;
		float groundY = ground.groundHeightAt(playerX);
		float fixedGroundY = (float) Math.floor(groundY / Block.SIZE) * Block.SIZE;
		float playerY = fixedGroundY - Avatar.PLAYER_SIZE;

		player = new Avatar(new Vector2(playerX, playerY), inputListener, imageReader);
		gameObjects().addGameObject(player, Layer.DEFAULT);

		energyShow = new TextRenderable(FIRST_ENERGY);
		GameObject energyObj = new GameObject(
				new Vector2(TEXT_START_X, TEXT_START_Y),
				new Vector2(TEXT_SIZE_X, TEXT_SIZE_Y),
				energyShow
		);

		player.setOnEnergyChange(energy -> energyShow.setString(energy.intValue() + ENERGY_SIGN));
		energyObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(energyObj, Layer.UI);

		int firstX = (int) (screenWidth * START_SCREEN_BACK);
		int lastX = (int) (screenWidth * START_SCREEN_FRONT);
		loadRange(firstX, lastX);

		// Fake placeholder elements used strictly to safely establish layer definitions
		GameObject leafHelp = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		GameObject fruitHelp = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		gameObjects().addGameObject(leafHelp, LEAF_LAYER);
		gameObjects().addGameObject(fruitHelp, FRUIT_LAYER);
		gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, LEAF_LAYER, false);
		gameObjects().layers().shouldLayersCollide(LEAF_LAYER, LEAF_LAYER, false);
		gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, FRUIT_LAYER, true);
		gameObjects().layers().shouldLayersCollide(FRUIT_LAYER, GROUND_LAYER, false);
		gameObjects().layers().shouldLayersCollide(FRUIT_LAYER, LEAF_LAYER, false);
		gameObjects().removeGameObject(leafHelp, LEAF_LAYER);
		gameObjects().removeGameObject(fruitHelp, FRUIT_LAYER);
		Camera camera = new Camera(player, Vector2.ZERO, screenSize, screenSize);
		setCamera(camera);
	}

	/**
	 * Called every frame to update the game logic.
	 * Manages the infinite world generation by tracking the player's movement.
	 *
	 * @param deltaTime the time passed since the last frame.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		updateWorld();
	}

	/**
	 * Checks the player's current position and dynamically loads new terrain
	 * columns while removing old ones that are too far away to save memory.
	 */
	private void updateWorld() {
		float playerCenterX = player.getCenter().x();
		float screenWidth = screenSize.x();
		float loadDist = screenWidth * LOAD_AREA;
		int minX = (int) (playerCenterX - loadDist);
		int maxX = (int) (playerCenterX + loadDist);

		loadRange(alignFloor(minX), alignCeil(maxX));
		float deleteDist = screenWidth * DELETE_AREA;
		List<Integer> colsToRemove = new ArrayList<>();

		java.util.Iterator<Integer> keyIterator = loadedCols.keySet().iterator();
		while (keyIterator.hasNext()) {
			int col = keyIterator.next();
			if (Math.abs(playerCenterX - col) > deleteDist) {
				colsToRemove.add(col);
			}
		}

		int i = 0;
		while (i < colsToRemove.size()) {
			unloadColumn(colsToRemove.get(i));
			i++;
		}
	}

	/**
	 * Loads a range of terrain and flora objects into the game world.
	 *
	 * @param minX the starting x-coordinate of the range.
	 * @param maxX the ending x-coordinate of the range.
	 */
	private void loadRange(int minX, int maxX) {
		int x = alignFloor(minX);
		while (x < maxX) {
			if (!loadedCols.containsKey(x)) {
				loadColumn(x);
			}
			x += Block.SIZE;
		}
	}

	/**
	 * Creates and adds all the game objects (ground, trees, fruits) for a specific
	 * vertical column in the world.
	 *
	 * @param x the exact x-coordinate of the column to load.
	 */
	private void loadColumn(int x) {
		List<GameObject> objects = new ArrayList<>();
		for (Block block : ground.createInRange(x, x + Block.SIZE)) {
			gameObjects().addGameObject(block, GROUND_LAYER);
			objects.add(block);
		}
		for (GameObject obj : trees.createInRange(x, x + Block.SIZE)) {
			String tag = obj.getTag();

			if ("fruit".equals(tag)) {
				((pepse.world.trees.Fruit) obj).setOnEaten(() -> player.addEnergy(FRUIT_POWER));
			}
			gameObjects().addGameObject(obj, layerForTag(tag));
			objects.add(obj);
		}
		loadedCols.put(x, objects);
	}

	/**
	 * Removes an entire column of game objects from the game to free up memory
	 * when the player moves far enough away.
	 *
	 * @param x the exact x-coordinate of the column to unload.
	 */
	private void unloadColumn(int x) {
		List<GameObject> objects = loadedCols.remove(x);
		if (objects != null) {
			int i = 0;
			while (i < objects.size()) {
				GameObject obj = objects.get(i);
				gameObjects().removeGameObject(obj, layerForTag(obj.getTag()));
				i++;
			}
		}
	}

	/**
	 * Helper method to determine the correct rendering and collision layer
	 * based on the specific tag of the object.
	 *
	 * @param tag the string tag of the object (e.g., "leaf", "fruit").
	 * @return the integer representing the correct layer.
	 */
	private int layerForTag(String tag) {
		if ("leaf".equals(tag)) return LEAF_LAYER;
		if ("fruit".equals(tag)) return FRUIT_LAYER;
		return GROUND_LAYER;
	}

	/**
	 * Aligns a given x-coordinate down to the nearest block boundary.
	 *
	 * @param x the raw x-coordinate.
	 * @return the x-coordinate snapped to the left edge of a block.
	 */
	private static int alignFloor(int x) {
		int remainder = x % Block.SIZE;
		int fixedX = x - remainder;
		if (x < 0 && remainder != 0) {
			fixedX -= Block.SIZE;
		}
		return fixedX;
	}

	/**
	 * Aligns a given x-coordinate up to the nearest block boundary.
	 *
	 * @param x the raw x-coordinate.
	 * @return the x-coordinate snapped to the right edge of a block.
	 */
	private static int alignCeil(int x) {
		int blockNum = Math.floorDiv(x, Block.SIZE);
		int fixedX = blockNum * Block.SIZE;
		if (fixedX != x) {
			fixedX += Block.SIZE;
		}
		return fixedX;
	}

	/**
	 * This is the main function.
	 * It starts the game.
	 *
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		PepseGameManager game = new PepseGameManager();
		game.run();
	}
}