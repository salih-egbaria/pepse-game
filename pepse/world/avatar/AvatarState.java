package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;

/**
 * Interface representing a specific behavioral state of the Avatar.
 * This interface allows the Avatar to delegate its movement, physics, energy management,
 * and animations to separate state objects (e.g., Idle, Run, Jump) to avoid complex conditional logic.
 */
public interface AvatarState {

	/**
	 * Runs every frame to handle movement and check if the avatar should change to a different state.
	 *
	 * @param avatar    the main avatar object
	 * @param deltaTime time since the last frame
	 * @return the new state to switch to, or this if the state stays the same
	 */
	AvatarState update(Avatar avatar, float deltaTime);

	/**
	 * Gets the right animation to play for this state.
	 *
	 * @param avatar the main avatar object
	 * @return the animation to show on screen
	 */
	AnimationRenderable getAnimation(Avatar avatar);
}

