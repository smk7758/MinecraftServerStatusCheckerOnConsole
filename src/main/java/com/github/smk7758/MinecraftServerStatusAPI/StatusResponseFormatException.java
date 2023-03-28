package com.github.smk7758.MinecraftServerStatusAPI;

public class StatusResponseFormatException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9132813255373846234L;

	public StatusResponseFormatException() {
		super();
	}

	public StatusResponseFormatException(String response_string) {
		super(response_string);
	}

	public StatusResponseFormatException(String response_string, String reason) {
		super(reason + System.lineSeparator() + "The stirng: " + response_string);
	}
}
