package com.github.smk7758.MinecraftServerStatusAPI;

import java.net.InetSocketAddress;

public interface StatusResponseConnectionInterface {

	public int getProtocolVersion();

	public int getTimeout();

	/**
	 * @return Response text.
	 */
	public String getResponseText();

	public InetSocketAddress getHost();

}
