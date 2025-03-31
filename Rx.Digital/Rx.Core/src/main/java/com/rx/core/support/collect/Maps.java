package com.rx.core.support.collect;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Map 相關工具程式
 */
@Slf4j
public final class Maps {

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<>();
	}

	public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
		return new HashMap<>(map);
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<>();
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
		return new LinkedHashMap<>(map);
	}

	public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
		return new ConcurrentHashMap<>();
	}

	public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<>();
	}

	public static <K, V> Optional<Map<K, V>> fromJson(String json) {
		TypeReference<Map<K, V>> typeReference = new TypeReference<>() {
		};
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Optional.of(mapper.readValue(json, typeReference));
		} catch (JsonParseException e) {
			log.error("", e);
			return Optional.empty();
		} catch (JsonMappingException e) {
			log.error("", e);
			return Optional.empty();
		} catch (IOException e) {
			log.error("", e);
			return Optional.empty();
		}
	}

	public static String toJson(Map<String, Object> maps) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		try {
			return mapper.writeValueAsString(maps);
		} catch (JsonProcessingException e) {
			log.error("", e);
			return "{}";
		}
	}

	private Maps() {
		super();
	}

}
