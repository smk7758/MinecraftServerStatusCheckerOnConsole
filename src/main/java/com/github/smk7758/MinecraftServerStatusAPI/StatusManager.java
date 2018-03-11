package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.StatusResponse;

public class StatusManager {

	private StatusManager() {
	}

	public static StatusResponse receiveStatusResponse(InetSocketAddress host)
			throws IOException, StatusResponseFormatException {
		StatusResponse status_response = null;
		try (StatusConnection status_connection = new StatusConnection(host)) {
			status_response = recieveStatusResponse(status_connection);
		}
		return status_response;
	}

	public static StatusResponse receiveStatusResponse(InetSocketAddress host, int protocol_version, int timeout)
			throws IOException, StatusResponseFormatException {
		StatusResponse status_response = null;
		try (StatusConnection status_connection = new StatusConnection(host, protocol_version, timeout)) {
			status_response = recieveStatusResponse(status_connection);
		}
		return status_response;
	}

	public static StatusResponse recieveStatusResponse(StatusConnection status_connection)
			throws IOException, StatusResponseFormatException {
		StatusResponse status_response = null;
		status_connection.sendHandshakePacket();
		status_connection.sendServerStatusPacket();
		status_response = StatusConverter.convertStatusResponse(status_connection);
		status_connection.sendPingPacket();
		int time_receive = (int) status_connection.receivePing();
		status_response.setTime(time_receive);
		status_connection.close();
		return status_response;
	}

	@Deprecated
	public static void printStatusResponse(StatusResponse status_response) {
		System.out.println(getStatusResponseInfoText(status_response));
	}

	public static String getStatusResponseInfoText(StatusResponse status_response) {
		StringBuilder sb = new StringBuilder();
		String is_favicon = "true";
		if (status_response.getFavicon() == null || status_response.getFavicon().isEmpty()) is_favicon = "false";
		sb.append("Version: ").append(status_response.getVersion().getName()).append(System.lineSeparator());
		sb.append("OnlinePlayers / MaximumPlayers: ").append(status_response.getPlayers().getOnline());
		sb.append(" / ").append(status_response.getPlayers().getMax()).append(System.lineSeparator());
		sb.append("Ping: ").append(status_response.getTime()).append(System.lineSeparator());
		sb.append("isFavicon(Icon): ").append(is_favicon).append(System.lineSeparator());
		sb.append("Description(MOTD): ").append(status_response.getDescription().getText());
		return sb.toString();
	}

	public static InputStream getFaviconAsInputStream(String favicon) {
		if (favicon == null) throw new NullPointerException("String of favicon must not be null.");
		else if (favicon.isEmpty()) throw new IllegalArgumentException("String of favicon must not be empty.");
		favicon = favicon.split(",")[1];
		byte[] image_byte = javax.xml.bind.DatatypeConverter.parseBase64Binary(favicon);
		return new ByteArrayInputStream(image_byte);
	}

	/*
	 * このクラスは、StatusAPI全体の親的な、または機能的なものとする。また、ユーティリティクラスともする。
	 */
}
