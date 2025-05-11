package com.rx.core.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Student implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String classroom;

	public Student(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.classroom = "Empty Room";
	}

	public Student(Integer id, String name, String classroom) {
		this.id = id;
		this.name = name;
		this.classroom = classroom;
	}

	@Override
	public String toString() {
		return "Student{id=" + id + ", name='" + name + "\'" + ", classroom='" + classroom + "\'" + "}";
	}

}
