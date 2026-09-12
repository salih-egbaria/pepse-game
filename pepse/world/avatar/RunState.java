package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;

/**
 * Represents the state of the avatar when it is moving horizontally on the ground.
 * This state handles continuous horizontal movement, sprite orientation (flipping),
 * and the continuous draining of energy. It transitions to the Idle state if
 * the player stops moving or runs out of energy, and to the Jump state if the
 * player initiates a jump or walks off a ledge.
 */
public class RunState implements AvatarState {

	/**
	 * Constructs the run state.
	 */
	public RunState() {
	}

	/**
	 * Handles jump and fall detection, horizontal movement, sprite flipping, and energy drain.
	 * * @param avatar    the owning avatar; used to read input, physics, and energy
	 *
	 * @param deltaTime seconds elapsed since the last frame
	 * @return the next state to transition to (IdleState or JumpState), or this state if continuing to run
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
		if (!goingLeft && !goingRight) {
			avatar.setVelocityX(0f);
			return new IdleState();
		}
		if (goingLeft) {
			avatar.setVelocityX(-Avatar.MOVE_PLAYER);
			avatar.flipRenderer(true);
		} else {
			avatar.setVelocityX(Avatar.MOVE_PLAYER);
			avatar.flipRenderer(false);
		}
		avatar.getEnergy().add(-Avatar.MOVE_COST);
		if (avatar.getEnergy().getCurrentEnergy() <= 0f) {
			avatar.setVelocityX(0f);
			return new IdleState();
		}
		return this;
	}

	/**
	 * Retrieves the running animation clip for the avatar.
	 * * @param avatar the owning avatar; provides access to its loaded animation clips
	 *
	 * @return the run AnimationRenderable to be displayed while moving
	 */
	@Override
	public AnimationRenderable getAnimation(Avatar avatar) {
		return avatar.getRunAnimation();
	}
}
