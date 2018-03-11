package com.github.smk7758.MinecraftServerStatusCheckerOnConsole;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.github.smk7758.MinecraftServerStatusAPI.StatusManager;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.StatusResponse;

public class StatusResponseOutputter {
	private StatusResponseOutputter() {
	}

	public static void outputStatusResponseToLogFile(StatusResponse status_response, Path folder_path,
			String server_name) {
		if (status_response == null) throw new IllegalArgumentException("Response is null.");
		LocalDateTime now = LocalDateTime.now();
		Path log_file_path = folder_path.resolve(server_name + "_"
				+ now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "_"
				+ now.getHour() + "-" + now.getMinute() + "_" + now.getSecond() + ".txt");
		try (BufferedWriter bw = Files.newBufferedWriter(log_file_path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);) {
			bw.write(getConnectionInfo(now, status_response));
			bw.write(status_response.getResponseText());
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.printDebug("OutputLogPath: " + log_file_path);
	}

	public static void outputStatusResponseToConsole(StatusResponse status_response, String server_name) {
		if (status_response == null) throw new IllegalArgumentException("Response is null.");
		System.out.println("ServerName: " + server_name);
		System.out.println(getConnectionInfo(LocalDateTime.now(), status_response));
		System.out.println(StatusManager.getStatusResponseInfoText(status_response));
	}

	private static String getConnectionInfo(LocalDateTime now, StatusResponse status_response) {
		StringBuilder sb = new StringBuilder();
		sb.append("Time: ").append(now.toString()).append(System.lineSeparator());
		sb.append("ServerAddress: ").append(status_response.getHost().getHostString()).append(System.lineSeparator());
		sb.append("ServerPort: ").append(status_response.getHost().getPort()).append(System.lineSeparator());
		sb.append("ProtocolVersion: ").append(status_response.getProtocolVersion()).append(System.lineSeparator());
		sb.append("LoadJsonType: ").append(status_response.getLoadJsonTypes()).append(System.lineSeparator());
		return sb.toString();
	}
}
