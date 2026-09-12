package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Represents the main player entity in the game.
 * This class handles initialization of physics and animations, tracks collisions to detect the ground,
 * and delegates all movement and action logic to its current AvatarState.
 * Energy management is encapsulated within the Energy component.
 */
public class Avatar extends GameObject {

	/**
	 * Width and height of the avatar sprite in pixels.
	 */
	public static final int PLAYER_SIZE = 50;

	/**
	 * Horizontal movement speed in pixels per second.
	 */
	static final float MOVE_PLAYER = 400f;
	/**
	 * Upward velocity applied on a jump (negative = upward in DanoGameLab).
	 */
	static final float JUMP_PLAYER = -650f;

	/**
	 * Energy drained per frame while running.
	 */
	static final float MOVE_COST = 2f;
	/**
	 * Energy regenerated per frame while idle.
	 */
	static final float REST_ENERGY = 0.5f;
	/**
	 * Energy cost for a standard jump from the ground.
	 */
	static final float JUMP_COST = 20f;
	/**
	 * Energy cost for a mid-air double-jump.
	 */
	static final float DOUBLE_JUMP_COST = 50f;


	private static final float FALL_SPEED = 600f;
	private static final float MAX_ENERGY = 100f;
	private static final float ANIMATION_TIME = 0.1f;
	private static final float ZERO_SPEED = 0f;
	private static final int STAND_FRAMES = 4;
	private static final int MOVE_FRAMES = 6;
	private static final int JUMP_FRAMES = 4;


	private final UserInputListener keyboard;
	private final AnimationRenderable standAnimation;
	private final AnimationRenderable moveAnimation;
	private final AnimationRenderable jumpAnimation;
	private final Energy energy;

	private AvatarState currentState;

	private int floorContacts;
	private boolean wasSpacePressed;
	private boolean jumpJustPressed;

	/**
	 * Constructs the Avatar instance, initializes its physics, and sets the starting state.
	 *
	 * @param startPlace  the initial coordinate position of the top-left corner of the avatar
	 * @param keyboard    the input listener to read user key presses
	 * @param imageReader the image reader to load the avatar's animation sprites
	 */
	public Avatar(Vector2 startPlace, UserInputListener keyboard, ImageReader imageReader) {
		super(startPlace, Vector2.ONES.mult(PLAYER_SIZE), null);
		this.keyboard = keyboard;
		this.energy = new Energy(MAX_ENERGY);
		this.currentState = new IdleState();
		this.floorContacts = 0;
		this.wasSpacePressed = false;
		this.jumpJustPressed = false;

		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(FALL_SPEED);

		standAnimation = buildAnim(imageReader, "idle", STAND_FRAMES);
		moveAnimation = buildAnim(imageReader, "run", MOVE_FRAMES);
		jumpAnimation = buildAnim(imageReader, "jump", JUMP_FRAMES);

		renderer().setRenderable(standAnimation);
		setTag("avatar");
	}

	/**
	 * Called automatically by the engine every frame.
	 * Pre-calculates input intents and delegates logic to the current State object.
	 *
	 * @param deltaTime the time elapsed since the last frame
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		boolean spaceNow = keyboard.isKeyPressed(KeyEvent.VK_SPACE);
		jumpJustPressed = spaceNow && !wasSpacePressed;
		wasSpacePressed = spaceNow;

		currentState = currentState.update(this, deltaTime);
		renderer().setRenderable(currentState.getAnimation(this));
	}

	/**
	 * Triggered when the avatar collides with another game object.
	 * Used to keep an exact count of ground blocks the avatar is touching.
	 *
	 * @param other     the object the avatar collided with
	 * @param collision details about the collision event
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (isGroundLike(other)) {
			floorContacts++;
			if (isFalling()) {
				float groundTop = other.getTopLeftCorner().y();
				transform().setTopLeftCorner(new Vector2(getTopLeftCorner().x(),
						groundTop - PLAYER_SIZE));
				transform().setVelocityY(0);
			}
		}
	}

	/**
	 * Triggered when the avatar stops colliding with another game object.
	 * Decrements the ground contact counter to detect when the player walks off a ledge.
	 *
	 * @param other the object the avatar stopped colliding with
	 */
	@Override
	public void onCollisionExit(GameObject other) {
		super.onCollisionExit(other);
		if (isGroundLike(other)) {
			floorContacts--;
			if (floorContacts < 0) {
				floorContacts = 0;
			}
		}
	}

	/**
	 * Wrapper method to safely add or subtract energy.
	 *
	 * @param amount the amount of energy to modify
	 */
	public void addEnergy(float amount) {
		energy.add(amount);
	}

	/**
	 * Wrapper method to register a callback for when the energy level changes.
	 *
	 * @param callback the function to call to update the UI
	 */
	public void setOnEnergyChange(Consumer<Float> callback) {
		energy.setEnergyUpdate(callback);
	}

	/**
	 * Checks if the left arrow key is currently pressed.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return true if moving left
	 */
	boolean isMovingLeft() {
		return keyboard.isKeyPressed(KeyEvent.VK_LEFT);
	}

	/**
	 * Checks if the right arrow key is currently pressed.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return true if moving right
	 */
	boolean isMovingRight() {
		return keyboard.isKeyPressed(KeyEvent.VK_RIGHT);
	}

	/**
	 * Checks if the jump key (Spacebar) was newly pressed this exact frame.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return true if the spacebar was just pressed
	 */
	boolean isJumpPressed() {
		return jumpJustPressed;
	}

	/**
	 * Determines if the avatar is currently touching the ground.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return true if touching at least one ground block
	 */
	boolean isGrounded() {
		return floorContacts > 0;
	}

	/**
	 * Directly modifies the avatar's horizontal velocity.
	 * Exposed to package scope for use by State classes.
	 *
	 * @param vx the new horizontal velocity
	 */
	void setVelocityX(float vx) {
		transform().setVelocityX(vx);
	}

	/**
	 * Directly modifies the avatar's vertical velocity.
	 * Exposed to package scope for use by State classes.
	 *
	 * @param vy the new vertical velocity
	 */
	void setVelocityY(float vy) {
		transform().setVelocityY(vy);
	}

	/**
	 * Flips the avatar's rendered sprite horizontally.
	 * Exposed to package scope for use by State classes.
	 *
	 * @param flipped true to face left, false to face right
	 */
	void flipRenderer(boolean flipped) {
		renderer().setIsFlippedHorizontally(flipped);
	}

	/**
	 * Retrieves the idle animation clip.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return the idle AnimationRenderable
	 */
	AnimationRenderable getIdleAnimation() {
		return standAnimation;
	}

	/**
	 * Retrieves the running animation clip.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return the run AnimationRenderable
	 */
	AnimationRenderable getRunAnimation() {
		return moveAnimation;
	}

	/**
	 * Retrieves the jumping animation clip.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return the jump AnimationRenderable
	 */
	AnimationRenderable getJumpAnimation() {
		return jumpAnimation;
	}

	/**
	 * Retrieves the encapsulated energy manager.
	 * Exposed to package scope for use by State classes.
	 *
	 * @return the Energy instance
	 */
	Energy getEnergy() {
		return energy;
	}

	/**
	 * Checks if the avatar has a downward velocity.
	 *
	 * @return true if the avatar is falling
	 */
	private boolean isFalling() {
		return getVelocity().y() > ZERO_SPEED;
	}

	/**
	 * Determines if a given object is solid enough to stand on.
	 *
	 * @param obj the game object to check
	 * @return true if the object is tagged as ground or trunk
	 */
	private static boolean isGroundLike(GameObject obj) {
		String tag = obj.getTag();
		return "ground".equals(tag) || "trunk".equals(tag);
	}

	/**
	 * Helper method to load a series of images into an animated renderable.
	 *
	 * @param reader the image reader utility
	 * @param name   the base name of the image files
	 * @param frames the number of frames in the animation
	 * @return an AnimationRenderable containing the loaded frames
	 */
	private static AnimationRenderable buildAnim(ImageReader reader, String name, int frames) {
		Renderable[] images = new Renderable[frames];
		for (int i = 0; i < frames; i++) {
			String path = "assets/" + name + "_" + i + ".png";
			images[i] = reader.readImage(path, true);
		}
		return new AnimationRenderable(images, ANIMATION_TIME);
	}
}