package com.rx.webapi.service;

import java.util.List;

import com.rx.core.bean.TodoD;
import com.rx.core.bean.TodoM;

public interface AngularService {

	public void create(List<TodoM> list) throws Exception;

	public void create(String groupId, String todoId, String name, boolean value, String username) throws Exception;

	public void update(String todoId, String name, boolean value, String username) throws Exception;

	public void updateAll(String groupId, String name, Boolean value, String username) throws Exception;

	public void delete(String todoId) throws Exception;

	public void deleteAll(String groupId) throws Exception;

	public List<TodoD> queryTodoD(String groupId) throws Exception;

	public List<TodoM> queryTodoM() throws Exception;
}
