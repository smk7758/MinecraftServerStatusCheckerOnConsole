package com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet;

import java.net.InetSocketAddress;
import java.util.List;

import com.github.smk7758.MinecraftServerStatusAPI.StatusConnection;
import com.github.smk7758.MinecraftServerStatusAPI.StatusConverter.JsonTypes;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseConnectionInterface;

public abstract class StatusResponse implements StatusResponseConnectionInterface {
	protected String response_text;
	protected int protocol_version;
	protected int timeout;
	protected InetSocketAddress host;
	protected JsonTypes load_json_type;

	public void setConnection(StatusConnection status_connection) {
		setConnection(status_connection.getResponseText(),
				status_connection.getProtocolVersion(),
				status_connection.getTimeout(),
				status_connection.getHost());
	}

	public void setConnection(String response_text, int protocol_version, int timeout, InetSocketAddress host) {
		this.response_text = response_text;
		this.protocol_version = protocol_version;
		this.timeout = timeout;
		this.host = host;
	}

	@Override
	public String getResponseText() {
		return response_text;
	}

	@Override
	public int getProtocolVersion() {
		return protocol_version;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public InetSocketAddress getHost() {
		return host;
	}

	public void setLoadJsonType(JsonTypes load_json_type) {
		this.load_json_type = load_json_type;
	}

	public JsonTypes getLoadJsonTypes() {
		return load_json_type;
	}

	/**
	 * @return Description json block.
	 */
	public abstract Description getDescription();

	/**
	 * @return Players json block.
	 */
	public abstract Players getPlayers();

	/**
	 * @return Version json block.
	 */
	public abstract Version getVersion();

	/**
	 * @return Favicon string.
	 */
	public abstract String getFavicon();

	/**
	 * @return how long did the ping take.
	 */
	public abstract int getTime();

	/**
	 * Set the ping time.
	 *
	 * @param time ping time.
	 */
	public abstract void setTime(int time);

	public class Description {
		private String text;

		public Description() {
		}

		public Description(String text) {
			this.text = text;
		}

		/**
		 * @return description text(MOTD).
		 */
		public String getText() {
			return text;
		}
	}

	/**
	 * set of players.
	 */
	public class Players {
		private int max;
		private int online;
		private List<Player> players;

		public Players() {
		}

		public Players(int max, int online, List<Player> players) {
			this.max = max;
			this.online = online;
			this.players = players;
		}

		/**
		 * @return maximum of how many player can connect.
		 */
		public int getMax() {
			return max;
		}

		/**
		 * @return number of how many players are in the server.
		 */
		public int getOnline() {
			return online;
		}

		public List<Player> getSample() {
			return players;
		}
	}

	/**
	 * player data.
	 */
	public class Player {
		private String name;
		private String id;

		public Player() {
		}

		public Player(String name, String id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

	}

	/**
	 * versions.
	 */
	public class Version {
		private String name;
		private String protocol;

		public Version() {
		}

		public Version(String name, String protocol) {
			this.name = name;
			this.protocol = protocol;
		}

		/**
		 * @return server version.
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return protocol version.
		 */
		public String getProtocol() {
			return protocol;
		}
	}
}