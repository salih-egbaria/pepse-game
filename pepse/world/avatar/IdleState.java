package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;

/**
 * Represents the state of the avatar when it is standing still on the ground.
 * This state handles resting energy regeneration and ensures the avatar comes to a halt.
 * It transitions to the Run state when horizontal movement is initiated (and energy permits),
 * or to the Jump state if the player initiates a jump or walks off a ledge.
 */
public class IdleState implements AvatarState {

	/**
	 * Constructs the idle state.
	 */
	public IdleState() {
	}

	/**
	 * Evaluates jump/fall conditions, movement inputs, and regenerates energy while idle.
	 *
	 * @param avatar    the owning avatar; used to read input, physics, and energy
	 * @param deltaTime seconds elapsed since the last frame
	 * @return the next state to transition to (RunState or JumpState), or this state if remaining idle
	 */
	@Override
	public AvatarState update(Avatar avatar, float deltaTime) {
		if (avatar.isJumpPressed() && avatar.getEnergy().getCurrentEnergy() >= Avatar.JUMP_COST) {
			avatar.setVelocityY(Avatar.JUMP_PLAYER);
			avatar.getEnergy().add(-Avatar.JUMP_COST);
			return new JumpState();
		}
		if (!avatar.isGrounded()) {
			return new JumpState();
		}
		boolean goingLeft = avatar.isMovingLeft() && !avatar.isMovingRight();
		boolean goingRight = avatar.isMovingRight() && !avatar.isMovingLeft();
		if ((goingLeft || goingRight) && avatar.getEnergy().getCurrentEnergy() >= Avatar.MOVE_COST) {
			return new RunState();
		}
		avatar.setVelocityX(0f);
		avatar.getEnergy().add(Avatar.REST_ENERGY);
		return this;
	}

	/**
	 * Retrieves the idle animation clip for the avatar.
	 *
	 * @param avatar the owning avatar; provides access to its loaded animation clips
	 * @return the idle AnimationRenderable to be displayed while standing still
	 */
	@Override
	public AnimationRenderable getAnimation(Avatar avatar) {
		return avatar.getIdleAnimation();
	}
}
