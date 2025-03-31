package com.rx.webapi.service.imp;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rx.core.bean.TodoD;
import com.rx.core.bean.TodoM;
import com.rx.core.dao.TodoDao;
import com.rx.webapi.service.AngularService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AngularServiceImp implements AngularService {

	@Autowired
	protected TodoDao dao;

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void create(List<TodoM> list) throws Exception {
		boolean flag = true;
		for (TodoM bean : list) {
			flag = dao.create(bean);
			if (!flag) {
				throw new Exception("储存失敗");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void create(String groupId, String todoId, String name, boolean value, String username) throws Exception {
		LocalDateTime current = LocalDateTime.now();
		TodoD bean = new TodoD(groupId, todoId, name, value, current, username);
		boolean flag = dao.create(bean);
		if (!flag) {
			throw new Exception("储存失敗");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void update(String todoId, String name, boolean value, String username) throws Exception {
		LocalDateTime current = LocalDateTime.now();
		TodoD bean = new TodoD("", todoId, name, value, current, username);
		boolean flag = dao.update(bean);
		if (!flag) {
			throw new Exception("储存失敗");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void updateAll(String groupId, String name, Boolean value, String username) throws Exception {
		LocalDateTime current = LocalDateTime.now();
		TodoD bean = new TodoD(groupId, name, value, current, username);
		boolean flag = dao.updateAll(bean);
		if (!flag) {
			throw new Exception("储存失敗");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void delete(String todoId) throws Exception {
		boolean flag = dao.delete(todoId);
		if (!flag) {
			throw new Exception("储存失敗");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void deleteAll(String groupId) throws Exception {
		boolean flag = dao.deleteAll(groupId);
		if (!flag) {
			throw new Exception("储存失敗");
		}
	}

	@Transactional(readOnly = true)
	public List<TodoD> queryTodoD(String groupId) throws Exception {
		return dao.queryTodoD(groupId);
	}

	@Transactional(readOnly = true)
	public List<TodoM> queryTodoM() throws Exception {
		return dao.queryTodoM();
	}
}
