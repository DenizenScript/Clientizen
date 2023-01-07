package com.denizenscript.clientizen.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDeserializer {

	private final ByteBuf buf;

	public DataDeserializer(ByteBuf buf) {
		this.buf = buf;
	}

	public boolean readBoolean() {
		return buf.readBoolean();
	}

	public int readInt() {
		return buf.readInt();
	}

	public byte[] readByteArray() {
		byte[] bytes = new byte[readInt()];
		buf.readBytes(bytes);
		return bytes;
	}

	public String readString() {
		return new String(readByteArray(), StandardCharsets.UTF_8);
	}

	public List<Integer> readIntList() {
		int size = readInt();
		List<Integer> intList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			intList.add(readInt());
		}
		return intList;
	}

	public List<String> readStringList() {
		int size = readInt();
		List<String> stringList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			stringList.add(readString());
		}
		return stringList;
	}

	public Map<String, String> readStringMap() {
		int size = readInt();
		Map<String, String> stringMap = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			String key = readString();
			String value = readString();
			stringMap.put(key, value);
		}
		return stringMap;
	}

	public Map<String, List<String>> readStringListMap() {
		int size = readInt();
		Map<String, List<String>> stringListMap = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			String key = readString();
			List<String> value = readStringList();
			stringListMap.put(key, value);
		}
		return stringListMap;
	}
}
