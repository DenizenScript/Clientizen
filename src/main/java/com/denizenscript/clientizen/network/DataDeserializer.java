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

	public List<String> readStringList() {
		List<String> stringList = new ArrayList<>();
		int size = readInt();
		for (int i = 0; i < size; i++) {
			stringList.add(readString());
		}
		return stringList;
	}

	public Map<String, String> readStringMap() {
		Map<String, String> stringMap = new HashMap<>();
		int size = readInt();
		for (int i = 0; i < size; i++) {
			String key = readString();
			String value = readString();
			stringMap.put(key, value);
		}
		return stringMap;
	}

	public Map<String, List<String>> readStringListMap() {
		Map<String, List<String>> stringListMap = new HashMap<>();
		int size = readInt();
		for (int i = 0; i < size; i++) {
			String key = readString();
			List<String> value = readStringList();
			stringListMap.put(key, value);
		}
		return stringListMap;
	}
}
