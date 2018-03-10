package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.Response;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseForBungeeCord;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseForSpigot_1_8_x;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseForVanilla;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class StatusOutputter {
	private StatusOutputter() {
	}

	/**
	 * The type of JSON response string. Vanilla: has description and
	 */
	public enum JsonTypes {
		Vanilla, Spigot_1_8_x, BungeeCord
	}

	/**
	 * gives you the response from the server.
	 *
	 * @return the class from JSON.
	 * @throws IOException some connection error.
	 * @throws StatusResponseFormatException If the string is invalid.
	 */
	public static Response receiveResponse(StatusConnection status_connection)
			throws IOException, StatusResponseFormatException {
		Response response = null;
		try {
			switch (getJsonTypes(status_connection)) {
				case Vanilla:
					response = new Gson().fromJson(status_connection.getResponseText(), ResponseForVanilla.class);
					response.setLoadJsonType(JsonTypes.Vanilla);
					break;
				case BungeeCord:
					response = new Gson().fromJson(status_connection.getResponseText(), ResponseForBungeeCord.class);
					response.setLoadJsonType(JsonTypes.BungeeCord);
					break;
				case Spigot_1_8_x:
					response = new Gson().fromJson(status_connection.getResponseText(), ResponseForSpigot_1_8_x.class);
					response.setLoadJsonType(JsonTypes.Spigot_1_8_x);
					break;
				default:
					// Vanilla (I can write more better here but I did not.)
					response = new Gson().fromJson(status_connection.getResponseText(), ResponseForVanilla.class);
					response.setLoadJsonType(JsonTypes.Vanilla);
					break;
			}
		} catch (JsonSyntaxException ex) {
			ex.printStackTrace();
			// throw new StatusResponseFormatException(status_connection.getResponseText(),
			// "Can't convert string to Response class.");
		}

		response.setConnection(status_connection);

		return response;
	}

	@Deprecated
	/**
	 * @param response_string
	 * @return ResponseInterface with a proper type.
	 * @throws StatusResponseFormatException If the string is invalid.
	 */
	public static Response convertResponse(String response_string) throws StatusResponseFormatException {
		Response response = null;
		JsonTypes json_types = getJsonTypes(response_string);
		try {
			if (json_types.equals(JsonTypes.Vanilla)) {
				response = new Gson().fromJson(response_string, ResponseForVanilla.class);
			} else if (json_types.equals(JsonTypes.BungeeCord)) {
				response = new Gson().fromJson(response_string, ResponseForBungeeCord.class);
			} else if (json_types.equals(JsonTypes.Spigot_1_8_x)) {
				response = new Gson().fromJson(response_string, ResponseForSpigot_1_8_x.class);
			}
		} catch (JsonSyntaxException ex) {
			throw new StatusResponseFormatException(response_string, "Can't convert string to Response class.");
		}
		return response;
	}

	/**
	 * This will returns you the type of the string.
	 *
	 * @param StatusConnction
	 * @return The string is what type: JsonTypes.
	 * @throws StatusResponseFormatException If the string is invalid.
	 */
	public static JsonTypes getJsonTypes(StatusConnection status_connection) throws StatusResponseFormatException {
		return getJsonTypes(status_connection.getResponseText());
	}

	/**
	 * This will returns you the type of the string.
	 *
	 * @param response_string
	 * @return The string is what type: JsonTypes.
	 * @throws StatusResponseFormatException If the string is invalid.
	 */
	public static JsonTypes getJsonTypes(String response_string) throws StatusResponseFormatException {
		Set<Entry<String, JsonElement>> first_elements;
		try {
			first_elements = new Gson().fromJson(response_string, JsonObject.class).entrySet();
		} catch (JsonSyntaxException ex) {
			throw new StatusResponseFormatException(response_string, "The string is not JSON.");
		}
		boolean has_description = false;
		for (Entry<String, JsonElement> first_element : first_elements) {
			if (first_element.getKey().equals("description")) {
				has_description = true;
				if (first_element.getValue().isJsonObject()) {
					Set<Entry<String, JsonElement>> second_elements = first_element.getValue()
							.getAsJsonObject().entrySet();
					for (Entry<String, JsonElement> second_element : second_elements) {
						if (second_element.getKey().equals("text")) return JsonTypes.Vanilla;
						else if (second_element.getKey().equalsIgnoreCase("extra")) {
							if (second_element.getValue().isJsonArray()) {
								JsonArray third_elements = second_element.getValue().getAsJsonArray();
								if (hasJsonArrayString(third_elements, "text")) return JsonTypes.BungeeCord;
								throw new StatusResponseFormatException(response_string,
										"The string has description, and extra, but doesn't have text in extra.");
							}
						}
					}
				}
				return JsonTypes.Spigot_1_8_x;
			}
		}
		if (has_description) throw new StatusResponseFormatException(response_string,
				"The string doesn't have description.");
		throw new StatusResponseFormatException(response_string, "The string doesn't have description.");
	}

	private static boolean hasJsonArrayString(JsonArray elements, String string) {
		for (JsonElement element : elements) {
			if (element.getAsJsonObject().has("text")) {
				return true;
				// あったら実行される。→ 抜けてしまったらBungeeCord型でない。
			}
		}
		return false;
	}

	/*
	 * このクラスは、JSON->Java 変換用のユーティリティクラスとする。
	 */
}