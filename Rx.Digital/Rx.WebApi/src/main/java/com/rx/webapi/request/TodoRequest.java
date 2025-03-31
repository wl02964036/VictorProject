package com.rx.webapi.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TodoRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("groupId")
	private String groupId;
	
	@JsonProperty("todoId")
	private String todoId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private boolean value;
}
