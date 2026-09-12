package pepse.world.avatar;

import java.util.function.Consumer;

/**
 * Manages the energy level of the avatar.
 * This class encapsulates the energy currentEnergy, ensuring it stays within defined bounds (0 to 100),
 * and notifies an external listener whenever the energy level changes.
 */
public class Energy {

	private static final float MIN_ENERGY = 0f;
	private static final float MAX_ENERGY = 100f;

	private float currentEnergy;
	private Consumer<Float> energyUpdate;

	/**
	 * Constructs an Energy instance with an initial currentEnergy and a change listener.
	 *
	 * @param initial  the starting energy level
	 * @param onChange a callback function triggered whenever the energy currentEnergy changes
	 */
	public Energy(float initial, Consumer<Float> onChange) {
		this.currentEnergy = bound(initial);
		this.energyUpdate = onChange;
	}

	/**
	 * Constructs an Energy instance with an initial currentEnergy and no listener.
	 *
	 * @param initial the starting energy level
	 */
	public Energy(float initial) {
		this(initial, null);
	}

	/**
	 * Adds a specified amount to the current energy level.
	 * The new currentEnergy is bounded between the minimum and maximum energy limits.
	 * If the currentEnergy changes, the registered listener is notified.
	 *
	 * @param amount the amount of energy to add (can be negative to subtract)
	 */
	public void add(float amount) {
		float newEnergy = bound(currentEnergy + amount);
		if (newEnergy != currentEnergy) {
			currentEnergy = newEnergy;
			if (energyUpdate != null) {
				energyUpdate.accept(currentEnergy);
			}
		}
	}

	/**
	 * Retrieves the current energy level.
	 *
	 * @return the current energy currentEnergy
	 */
	public float getCurrentEnergy() {
		return currentEnergy;
	}

	/**
	 * Registers or updates the listener to be notified when the energy level changes.
	 * When set, the listener is immediately triggered with the current energy currentEnergy.
	 *
	 * @param energyUpdate a callback function to handle energy updates
	 */
	public void setEnergyUpdate(Consumer<Float> energyUpdate) {
		this.energyUpdate = energyUpdate;
		if (energyUpdate != null) {
			energyUpdate.accept(currentEnergy);
		}
	}

	/**
	 * Helper method to ensure a given currentEnergy remains within the valid energy bounds.
	 *
	 * @param v the currentEnergy to bound
	 * @return the bounded currentEnergy between MIN_ENERGY and MAX_ENERGY
	 */
	private float bound(float v) {
		if (v > MAX_ENERGY) {
			return MAX_ENERGY;
		} else if (v < MIN_ENERGY) {
			return MIN_ENERGY;
		}
		return v;
	}
}
