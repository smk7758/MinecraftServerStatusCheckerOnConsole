package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class StatusConnection implements AutoCloseable, StatusResponseConnectionInterface {
	private InetSocketAddress host = null;
	private int protocol_version = -1;
	private int timeout = 7000;
	private Socket socket = null;
	private InputStream is = null;
	private OutputStream os = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String response_string = null;

	/**
	 * @param host the address and the port of the server you want to access.
	 * @throws IOException some connection error.
	 */
	public StatusConnection(InetSocketAddress host) throws IOException {
		initialize(host, this.protocol_version, this.timeout);
	}

	/**
	 * @param host the address and the port of the server you want to access.
	 * @param timeout input block millisecond.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some connection error.
	 */
	public StatusConnection(InetSocketAddress host, int protocol_version, int timeout) throws IOException {
		initialize(host, protocol_version, timeout);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @throws IOException some connection error.
	 * @deprecated Can't get exception properly.
	 */
	@Deprecated
	public StatusConnection(String address, short port) throws IOException {
		initialize(address, port, this.protocol_version, this.timeout);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @param timeout input block millisecond.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some connection error.
	 * @deprecated Can't get exception properly.
	 */
	@Deprecated
	public StatusConnection(String address, short port, int protocol_version, int timeout) throws IOException {
		initialize(address, port, protocol_version, timeout);
	}

	/**
	 * @param host the address and the port of the server you want to access.
	 * @param timeout input block millisecond.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some connection error.
	 */
	private void initialize(InetSocketAddress host, int protocol_version, int timeout) throws IOException {
		this.host = host;
		this.protocol_version = protocol_version;
		this.timeout = timeout;
		socket = new Socket();
		socket.setSoTimeout(timeout); // Input block millisecond.
		socket.connect(host, timeout); // connect to host(wait untill timeout when no connect);
		is = socket.getInputStream();
		os = socket.getOutputStream();
		dis = new DataInputStream(is);
		dos = new DataOutputStream(os);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @param timeout input block millisecond.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some connection error.
	 * @deprecated Can't get exception properly.
	 */
	@Deprecated
	private void initialize(String address, short port, int protocol_version, int timeout) throws IOException {
		host = new InetSocketAddress(address, port);
		initialize(host, protocol_version, timeout);
	}

	/**
	 * if you want to stop the connection, use this. also you have to use when you want to stop the program.
	 *
	 * @throws IOException some connection error.
	 */
	@Override
	public void close() throws IOException {
		dis.close();
		dos.close();
		is.close();
		os.close();
		socket.close();
	}

	// public void flush() {
	//
	// }

	/**
	 * send a packet for Handshake.
	 *
	 * @throws IOException some connection error.
	 */
	public void sendHandshakePacket() throws IOException {
		// Send Handshake
		byte[] handshake_data = getHandshakePacketData(1);
		int handshake_length = handshake_data.length;
		writeVarInt(handshake_length);
		dos.write(handshake_data);
	}

	/**
	 * @return a packet data of byte for Handshake.
	 * @param state - 1 or 2, 1(send a request for status), 2(send a request for login).
	 * @throws IOException some connection error
	 */
	private byte[] getHandshakePacketData(int state) throws IOException {
		try (ByteArrayOutputStream output_data = new ByteArrayOutputStream();
				DataOutputStream output_dos = new DataOutputStream(output_data);) {// just a unit
			output_dos.writeByte(0x00); // PacketID: Handshake
			writeVarInt(protocol_version, output_dos); // Protocol Version
			writeString(host.getHostString(), output_dos, Charset.defaultCharset()); // Host Name String
			output_dos.writeShort(host.getPort()); // Host Port used to connect
			writeVarInt(state, output_dos); // for check status, state
			output_dos.flush();
			return output_data.toByteArray();
		}
	}

	/**
	 * send a packet to get ServerListRespond.
	 *
	 * @throws IOException some connection error
	 */
	public void sendServerStatusPacket() throws IOException {
		// Send Request
		dos.writeByte(0x01);// Packet Size.
		dos.writeByte(0x00);// PacketID: ServerStatusPacket{Request(ServerListPespond)}
	}

	public void sendPingPacket() throws IOException {
		sendPingPacket(System.currentTimeMillis());
	}

	/**
	 * send a packet for Ping.
	 *
	 * @param client_time the time you are.
	 * @throws IOException some connection error.
	 */
	public void sendPingPacket(long client_time) throws IOException {
		// Send Ping
		dos.writeByte(0x09); // ping packet size
		dos.writeByte(0x01); // PacketID: Ping
		dos.writeLong(client_time); // ping packet data
	}

	/**
	 * receive a packet of Ping.
	 *
	 * @return how long did the ping take.
	 * @throws IOException some connection error.
	 */
	public long receivePing() throws IOException {
		return receivePing(System.currentTimeMillis());
	}

	/**
	 * receive a packet of Ping.
	 *
	 * @param client_time the time you are.
	 * @return how long did the ping take.
	 * @throws IOException some connection error.
	 */
	public long receivePing(long client_time) throws IOException {
		// Receive Ping

		// Under this is likely not used.
		int ping_size = readVarInt();
		if (ping_size != 9) throw new IOException("Invalid size.");
		int id = readVarInt();
		if (id == -1) throw new IOException("End of stream.");
		if (id != 0x01) throw new IOException("Invalid PacketID.");
		long server_time = dis.readLong();
		long ping_time = client_time - server_time;
		return ping_time;
	}

	public String receiveResponseText() throws IOException {
		// Receive Respond
		if (response_string == null) {
			int response_size = readVarInt();
			if (response_size == 0) throw new IOException("Invalid size. It's too shrot.");

			int packet_id = readVarInt();
			if (packet_id == -1) throw new IOException("End of stream.");
			if (packet_id != 0x00) throw new IOException("Invalid PacketID.");

			int response_string_length = readVarInt();
			if (response_string_length == -1) throw new IOException("End of stream.");
			if (response_string_length == 0) throw new IOException("Invalid length. It's too short.");
			byte[] response_byte = new byte[response_string_length];
			dis.readFully(response_byte);
			this.response_string = new String(response_byte);
		}
		return response_string;
	}

	private int readVarInt() throws IOException {
		// int time_receive = (int)System.currentTimeMillis();
		int numRead = 0;
		int result = 0;
		byte read;
		do {
			read = dis.readByte();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			numRead++;
			if (numRead > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while ((read & 0b10000000) != 0);

		return result;
	}

	private void writeString(String string, DataOutputStream dos, Charset charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		writeVarInt(bytes.length, dos);
		dos.write(bytes);
	}

	private void writeVarInt(int value) throws IOException {
		writeVarInt(value, this.dos);
	}

	private void writeVarInt(int value, DataOutputStream dos) throws IOException {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			writeByte(temp, dos);
		} while (value != 0);
	}

	private void writeByte(byte data, DataOutputStream dos) throws IOException {
		dos.writeByte(data);
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
	public String getResponseText() {
		try {
			return response_string != null ? response_string : receiveResponseText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response_string;
	}

	@Override
	public InetSocketAddress getHost() {
		return host;
	}

	/*
	 * このクラスは、自分のフィールド(インスタンス化時のもの)を用いて、通信のみを行う。
	 */
}
