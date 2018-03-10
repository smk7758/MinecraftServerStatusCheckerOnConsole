package com.github.smk7758.MinecraftServerStatusCheckerOnConsole;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.github.smk7758.MinecraftServerStatusAPI.StatusManager;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseFormatException;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.Response;

public class Main {
	public static boolean debug_mode = true;

	public static void main(String[] args) {
		Response response = null;
		try {
			response = StatusManager.receiveResponse(new InetSocketAddress("kazu0617.net", 25020));
		} catch (StatusResponseFormatException | IOException e) {
			e.printStackTrace();
		}
		StatusFileOutputter.outputResponseToLogFile(response, Paths.get("F:\\users\\smk7758\\Desktop"), "kazu");
	}

	public static void printDebug(String string) {
		if (debug_mode) System.out.println("[Debug] " + string);
	}
}
