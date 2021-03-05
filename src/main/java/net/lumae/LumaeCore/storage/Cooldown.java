package net.lumae.LumaeCore.storage;

/**
 * Cooldown class. Encapsulates all aspects of a "Cooldown". A cooldown is a wait/delay between something happening/being
 * allowed to be used. e.g. - a command can only be used every 5 seconds, a kit can only be redeemed every 3 days, etc.
 *
 * @author Matt Lefebvre
 */
public class Cooldown {
	// This long represents the time (System.currentTimeMillis()) that this "cooldown" would be over
	private final long offCooldown;

	/**
	 * Constructor that takes in an int representing the seconds a cooldown should last. This constructor is useful
	 * for creating a cooldown easily, e.g. - a 5 second cooldown between the use of a command
	 * @param seconds int seconds the cooldown will last
	 */
	public Cooldown(int seconds) {
		this.offCooldown = System.currentTimeMillis() + (seconds * 1000);
	}

	/**
	 * Constructor that takes in a long representing the System.currentTimeMillis() that would mark this cooldown as over.
	 * Useful for loading existing cooldown objects, e.g. - kit cooldowns. The time the cooldown is over has already been
	 * determined so calculating the seconds remaining and calling the other constructor would be a hassle
	 * @param offCooldown
	 */
	public Cooldown(long offCooldown) {
		this.offCooldown = offCooldown;
	}

	/**
	 * Method to check if the cooldown is active
	 * @return boolean is the cooldown active
	 */
	public boolean isActive() {
		return calculateRemaining() >= 0;
	}

	/**
	 * Method to check if the cooldown is over
	 * @return boolean is the cooldown over
	 */
	public boolean isOver() {
		return calculateRemaining() <= 0;
	}

	/**
	 * Method to get the long time that the Cooldown is over
	 * @return
	 */
	public long getTime() {
		return offCooldown;
	}

	/**
	 * Method to calculate the time remaining on the cooldown, in some situations this method can return a negative
	 * value. The only known case for this is if the cooldown is saved and then recreated using the Cooldown constructor
	 * with the long offCooldown parameter.
	 *
	 * @return
	 */
	public long calculateRemaining() {
		return offCooldown - System.currentTimeMillis();
	}

	public String formatTimeRemaining() {
		return String.format("%.2f", (offCooldown - System.currentTimeMillis()) / 1000.0);
	}

}
