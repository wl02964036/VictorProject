package com.rx.webapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rx.core.bean.LoginLog;
import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemRole;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.bean.TodoD;
import com.rx.core.bean.TodoM;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.support.AuthoritySupport;
import com.rx.core.support.collect.Lists;
import com.rx.core.support.collect.Maps;
import com.rx.core.util.JwtUtil;
import com.rx.webapi.exception.ElementNotFoundException;
import com.rx.webapi.model.RoleModel;
import com.rx.webapi.request.LoginRequest;
import com.rx.webapi.request.TodoRequest;
import com.rx.webapi.service.AngularService;
import com.rx.webapi.service.LoginLogService;
import com.rx.webapi.service.SystemRoleService;
import com.rx.webapi.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AngularAPIController {

	@Autowired
	protected AngularService angularService;

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected RoleUserDao roleUserDao;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected JwtUtil jwtUtil;

	@Autowired
	protected SystemUserService systemUserService;

	@Autowired
	protected LoginLogService loginLogService;

	@Autowired
	protected SystemRoleService systemRoleService;

	@GetMapping(name = "angular.group_action", path = "/angular/group_action/create")
	public void group_add(final HttpServletRequest request, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			String[] names = new String[] { "Todo", "上班待辦事項", "生活待辦事項" };
			List<TodoM> list = new ArrayList<>();
			for (String name : names) {
				String groupId = UUID.randomUUID().toString();
				TodoM todoM = new TodoM();
				todoM.setGroupId(groupId);
				todoM.setName(name);
				list.add(todoM);
			}
			angularService.create(list);

			map.put("status", "success");
			map.put("message", "儲存成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@GetMapping(name = "angular.group_action", path = "/angular/group_action")
	public void group_query(final HttpServletRequest request, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			List<TodoM> todoList = angularService.queryTodoM();
			responseOutput(response, todoList);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("error_message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}

	}

	@GetMapping(name = "angular.todo_action", path = "/angular/todo_action/{groupId}")
	public void todo_query(@PathVariable final String groupId, final HttpServletRequest request,
			final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			List<TodoD> todoList = angularService.queryTodoD(groupId);
			responseOutput(response, todoList);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("error_message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}

	}

	@PostMapping(name = "angular.todo_action", path = "/angular/todo_action")
	public void todo_add(@RequestBody final TodoRequest request, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			String groupId = request.getGroupId();
			String todoId = UUID.randomUUID().toString();
			String username = "admin";
			String name = request.getName();
			boolean value = request.isValue();
			angularService.create(groupId, todoId, name, value, username);

			map.put("status", "success");
			map.put("message", "儲存成功");
			map.put("todoId", todoId);
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@PutMapping(name = "angular.todo_action", path = "/angular/todo_action/{todoId}")
	public void todo_update(@PathVariable final String todoId, @RequestBody final TodoRequest request,
			final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			String username = "admin";
			String name = request.getName();
			boolean value = request.isValue();
			angularService.update(todoId, name, value, username);

			map.put("status", "success");
			map.put("message", "儲存成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@PutMapping(name = "angular.todo_action", path = "/angular/todo_action/{groupId}/status")
	public void todo_update_all_status(@PathVariable final String groupId, @RequestBody final boolean requestStatus,
			final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			String username = "admin";
			String name = null;
			Boolean value = requestStatus;
			angularService.updateAll(groupId, name, value, username);

			map.put("status", "success");
			map.put("message", "儲存成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@DeleteMapping(name = "angular.todo_action", path = "/angular/todo_action/{groupId}/clear_completed")
	public void todo_delete_clear_completed(@PathVariable final String groupId, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			angularService.delete(groupId);

			map.put("status", "success");
			map.put("message", "刪除成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@DeleteMapping(name = "angular.todo_action", path = "/angular/todo_action/{todoId}")
	public void todo_delete(@PathVariable final String todoId, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			angularService.delete(todoId);

			map.put("status", "success");
			map.put("message", "刪除成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@GetMapping(name = "angular.role_action", path = "/angular/role_action/{code}")
	public void edit(@RequestParam(name = "code") final String code, final HttpServletRequest httpRequest,
			final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(code);
			if (roleOpt.isEmpty()) {
				throw new ElementNotFoundException();
			}

			SystemRole role = roleOpt.get();

			RoleModel roleBind = new RoleModel();
			roleBind.setCode(role.getCode());
			roleBind.setTitle(role.getTitle());
			roleBind.setDescription(role.getDescription());
			roleBind.setAssignable(role.getAssignable());
			roleBind.setItems(systemRoleService.findUUIDsByCode(role.getCode()));

			map.put("status", "success");
			map.put("message", "儲存成功");
			map.put("roleBind", roleBind);
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@PutMapping(name = "angular.role_action", path = "/angular/role_action/{code}")
	public void update(@PathVariable final String code, @RequestBody final RoleModel request, final HttpServletRequest httpRequest,
			final HttpServletResponse response) {

		// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
		log.info("{}", request);

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(request.getCode());
			if (roleOpt.isEmpty()) {
				throw new ElementNotFoundException("此角色不存在");
			}

			// do somethings

			map.put("status", "success");
			map.put("message", "儲存成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	public void responseOutput(final HttpServletResponse response, List<?> list) {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			ServletOutputStream out = response.getOutputStream();
			out.write(Lists.toJson(list).getBytes(StandardCharsets.UTF_8));
			out.flush();
			out.close();
		} catch (IOException ex) {
			log.error("close output stream error");
		}
	}

	public void responseOutput(final HttpServletResponse response, Map<String, Object> map) {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			ServletOutputStream out = response.getOutputStream();
			out.write(Maps.toJson(map).getBytes(StandardCharsets.UTF_8));
			out.flush();
			out.close();
		} catch (IOException ex) {
			log.error("close output stream error");
		}
	}
}
