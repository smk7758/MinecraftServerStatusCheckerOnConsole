package com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet;

import java.util.List;

public class ResponseForBungeeCord extends Response {
	private DescriptionForBungeeCord description;
	private Players players;
	private Version version;
	private String favicon;
	private int time;

	/**
	 * @return description.
	 */
	@Override
	public DescriptionForBungeeCord getDescription() {
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

	public class DescriptionForBungeeCord extends Description {
		List<Extra> extra;

		@Override
		/**
		 * @return description text(MOTD).
		 */
		public String getText() {
			String text = "";
			for (Extra extra_item : extra) {
				text += extra_item.getText() + System.lineSeparator();
			}
			return text;
		}
	}

	public class Extra {
		String text;
		ClickEvent clickEvent;

		public String getText() {
			return text;
		}
	}

	public class ClickEvent {
		String action;
		String value;
	}
}
