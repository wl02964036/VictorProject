package com.rx.core.support.collect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * List 相關工具函式
 */
@Slf4j
public final class Lists {

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public static <E> ArrayList<E> newArrayList(E... elements) {
		// Avoid integer overflow when a large array is passed in
		ArrayList<E> list = new ArrayList<>();
		Collections.addAll(list, elements);
		return list;
	}

	public static <E> LinkedList<E> newLinkedList() {
		return new LinkedList<>();
	}

	@SuppressWarnings("unchecked")
	public static <E> LinkedList<E> newLinkedList(E... elements) {
		// Avoid integer overflow when a large array is passed in
		LinkedList<E> list = new LinkedList<>();
		Collections.addAll(list, elements);
		return list;
	}

	public static <T> List<T> fromSet(Set<T> set) {
		return new ArrayList<>(set);
	}

	public static <K, V> Optional<List<Map<K, V>>> fromJson(String json) {
		TypeReference<List<Map<K, V>>> typeReference = new TypeReference<>() {
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

	public static String toJson(List<?> lists) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		try {
			return mapper.writeValueAsString(lists);
		} catch (JsonProcessingException e) {
			log.error("", e);
			return "{}";
		}
	}

	private Lists() {
		super();
	}

}
