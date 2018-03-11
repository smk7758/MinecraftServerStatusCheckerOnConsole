package com.github.smk7758.MinecraftServerStatusCheckerOnConsole;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.github.smk7758.MinecraftServerStatusAPI.StatusManager;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseFormatException;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.StatusResponse;

public class Main {
	public static boolean debug_mode = true;

	public static void main(String[] args) {
		String server_address = null, output_folder_path = null, server_name = null;
		short server_port = 25565;
		int protocol_version = 0, timeout = 0;
		printDebug(args.length + "");
		if (args.length < 2) {
			System.err.println("Too short arguments.");
			System.out.println("[server address] [server port] "
					+ "([output folder path] [server name] [protocol version] [timeout])]");
			return;
		} else if (args.length > 1) {
			server_address = args[0];
			try {
				server_port = Short.valueOf(args[1]);
			} catch (NumberFormatException ex) {
				System.err.println("Second argument of server port is for a port number.");
			}
		}
		if (args.length > 2) output_folder_path = args[2];
		else output_folder_path = System.getProperty("user.home") + "\\Desktop\\";
		if (args.length > 3) server_name = args[3];
		else server_name = server_address;
		if (args.length > 4) protocol_version = Integer.valueOf(args[4]);
		if (args.length > 5) timeout = Integer.valueOf(args[5]);
		StatusResponse status_response = null;
		InetSocketAddress host = new InetSocketAddress(server_address, server_port);
		try {
			if (args.length < 4) {
				status_response = StatusManager.receiveStatusResponse(host);
			} else {
				status_response = StatusManager.receiveStatusResponse(host, protocol_version, timeout);
			}
			if (status_response != null) printDebug("Connection successful.");
		} catch (StatusResponseFormatException | IOException ex) {
			ex.printStackTrace();
		}
		StatusResponseOutputter
				.outputStatusResponseToConsole(status_response, server_name);
		StatusResponseOutputter
				.outputStatusResponseToLogFile(status_response, Paths.get(output_folder_path), server_name);
	}

	public static void printDebug(String string) {
		if (debug_mode) System.out.println("[Debug] " + string);
	}
}
