package com.github.smk7758.MinecraftServerStatusCheckerOnConsole;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.Response;

public class StatusFileOutputter {
	private StatusFileOutputter() {
	}

	public static void outputResponseToLogFile(Response response, Path folder_path, String server_name) {
		if (response == null) throw new IllegalArgumentException("Response is null.");
		LocalDateTime now = LocalDateTime.now();
		Path log_file_path = folder_path.resolve(server_name + "_"
				+ now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "_"
				+ now.getHour() + "-" + now.getMinute() + "_" + now.getSecond() + ".txt");
		try (BufferedWriter bw = Files.newBufferedWriter(log_file_path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);) {
			bw.write("Time: " + now.toString() + System.lineSeparator()
					+ "ServerAddress: " + response.getHost().getHostString() + System.lineSeparator()
					+ "ServerPort: " + response.getHost().getPort() + System.lineSeparator()
					+ "ProtocolVersion: " + response.getProtocolVersion() + System.lineSeparator()
					+ "LoadJsonType: " + response.getLoadJsonTypes() + System.lineSeparator());
			bw.write(response.getResponseText());
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.printDebug("OutputLogPath: " + log_file_path);
	}

}
