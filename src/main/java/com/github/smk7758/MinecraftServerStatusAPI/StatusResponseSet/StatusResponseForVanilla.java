package com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet;

/**
 * the class from JSON for ServerStatusResponse.
 */
public class StatusResponseForVanilla extends StatusResponse {
	private Description description;
	private Players players;
	private Version version;
	private String favicon;
	private int time;

	/**
	 * @return description.
	 */
	@Override
	public Description getDescription() {
		return description;
	}

	/**
	 * @return Players json block.
	 */
	@Override
	public Players getPlayers() {
		return players;
	}

	/**
	 * @return Version json block.
	 */
	@Override
	public Version getVersion() {
		return version;
	}

	/**
	 * @return Favicon string.
	 */
	@Override
	public String getFavicon() {
		return favicon;
	}

	/**
	 * @return how long did the ping take.
	 */
	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void setTime(int time) {
		this.time = time;
	}
}