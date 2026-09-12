package pepse.world.avatar;

import danogl.gui.rendering.AnimationRenderable;

/**
 * Represents the state of the avatar when it is airborne (jumping or falling).
 * This state handles mid-air horizontal control, double-jump execution,
 * and detects when the avatar successfully lands back on the ground to
 * transition into the Idle or Run states.
 */
public class JumpState implements AvatarState {

	private boolean doubleJumpUsed;

	/**
	 * Constructs the jump state with the double-jump available.
	 */
	public JumpState() {
		this.doubleJumpUsed = false;
	}

	/**
	 * Handles in-air horizontal movement, double-jump logic, and landing detection.
	 * * @param avatar    the owning avatar; used to read input, physics, and energy
	 *
	 * @param deltaTime seconds elapsed since the last frame
	 * @return the next state to transition to (IdleState or RunState), or this state if still airborne
	 */
	@Override
	public AvatarState update(Avatar avatar, float deltaTime) {
		boolean goingLeft = avatar.isMovingLeft() && !avatar.isMovingRight();
		boolean goingRight = avatar.isMovingRight() && !avatar.isMovingLeft();
		if (goingLeft) {
			avatar.setVelocityX(-Avatar.MOVE_PLAYER);
			avatar.flipRenderer(true);
		} else if (goingRight) {
			avatar.setVelocityX(Avatar.MOVE_PLAYER);
			avatar.flipRenderer(false);
		} else {
			avatar.setVelocityX(0f);
		}
		boolean isFalling = avatar.getVelocity().y() > 0f;
		if (isFalling && !doubleJumpUsed && avatar.isJumpPressed()
				&& avatar.getEnergy().getCurrentEnergy() >= Avatar.DOUBLE_JUMP_COST) {
			avatar.setVelocityY(Avatar.JUMP_PLAYER);
			avatar.getEnergy().add(-Avatar.DOUBLE_JUMP_COST);
			doubleJumpUsed = true;
		}
		if (avatar.isGrounded()) {
			if (goingLeft || goingRight) {
				return new RunState();
			}
			return new IdleState();
		}
		return this;
	}

	/**
	 * Retrieves the airborne animation clip for the avatar.
	 * * @param avatar the owning avatar; provides access to its loaded animation clips
	 *
	 * @return the jump AnimationRenderable to be displayed while in the air
	 */
	@Override
	public AnimationRenderable getAnimation(Avatar avatar) {
		return avatar.getJumpAnimation();
	}
}
