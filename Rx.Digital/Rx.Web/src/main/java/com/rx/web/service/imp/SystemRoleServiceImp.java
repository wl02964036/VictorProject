package com.rx.web.service.imp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rx.core.bean.RoleMenu;
import com.rx.core.bean.SystemRole;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleMenuDao;
import com.rx.core.dao.SystemRoleDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.table.request.RoleTableRequest;
import com.rx.web.modal.RoleModel;
import com.rx.web.modal.RoleQueryModel;
import com.rx.web.service.SystemRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemRoleServiceImp implements SystemRoleService {

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected SystemRoleDao systemRoleDao;

	@Autowired
	protected RoleMenuDao roleMenuDao;

	@Transactional(readOnly = true)
	public Tuple2<Long, List<RoleQueryModel>> query(final RoleTableRequest dataTableRequest) throws Exception {

		Long totalCount = systemRoleDao.totalCount(dataTableRequest);
		List<SystemRole> pageContent = systemRoleDao.queryByPage(dataTableRequest);

		// 轉成 顯示在 datatable 上的內容
		List<RoleQueryModel> pageViewModel = pageContent.stream().map(systemRole -> {
			RoleQueryModel viewModel = new RoleQueryModel();
			viewModel.setCode(systemRole.getCode());

			Optional<SystemUser> userOpt = systemUserDao.findByUserName(systemRole.getUpdatedBy());
			if (userOpt.isPresent()) {
				SystemUser user = userOpt.get();

				viewModel.setUpdatedBy(user.getDisplayName());
				Optional<SystemUnit> unitOpt = systemUnitDao.findByCode(user.getUnitCode());
				if (unitOpt.isPresent()) {
					viewModel.setUpdateUnit(unitOpt.get().getDisplayName());
				} else {
					viewModel.setUpdateUnit("");
				}
			} else {
				viewModel.setUpdatedBy("");
				viewModel.setUpdateUnit("");
			}
			viewModel.setTitle(systemRole.getTitle());
			viewModel.setUpdatedAt(systemRole.getUpdatedAt());

			return viewModel;
		}).collect(Collectors.toList());

		return Tuple.tuple(totalCount, pageViewModel);
	}

	@Transactional(readOnly = true)
	public Long countByCode(String code) {
		return systemRoleDao.countByCode(code);
	}

	@Transactional(readOnly = true)
	public Optional<SystemRole> findRoleByCode(String code) {
		return systemRoleDao.findByCode(code);
	}

	@Transactional(readOnly = true)
	public List<String> findUUIDsByCode(String code) {
		return roleMenuDao.findUUIDsByCode(code);
	}

	@Transactional
	public List<Tuple2<String, String>> findAllAssignableRoleTuples() {
		List<SystemRole> roles = systemRoleDao.findAllAssignable();
		return roles.stream().map(r -> Tuple.tuple(r.getCode(), r.getTitle())).collect(Collectors.toList());
	}

	@Transactional
	public List<Tuple2<String, String>> findAllAssignableRoleTuplesExcludeAct() {
		List<SystemRole> roles = systemRoleDao.findAllAssignableExcludeAct();
		return roles.stream().map(r -> Tuple.tuple(r.getCode(), r.getTitle())).collect(Collectors.toList());
	}

	@Transactional
	public List<Tuple2<String, String>> findAllRoleTuples() {
		List<SystemRole> roles = systemRoleDao.findAll();

		return roles.stream().map(r -> Tuple.tuple(r.getCode(), r.getTitle())).collect(Collectors.toList());
	}

	@Transactional
	public void createRole(RoleModel modal, SystemUser loginUser) throws Exception {
		List<String> selectItems = modal.getItems();

		LocalDateTime current = LocalDateTime.now();

		SystemRole role = new SystemRole();
		role.setCode(modal.getCode());
		role.setTitle(modal.getTitle());
		role.setDescription(modal.getDescription());
		role.setAssignable(modal.getAssignable());
		role.setCreatedBy(loginUser.getUsername());
		role.setCreatedAt(current);
		role.setUpdatedBy(loginUser.getUsername());
		role.setUpdatedAt(current);

		boolean succeed = systemRoleDao.create(role);
		if (succeed) {
			for (String item : selectItems) {
				RoleMenu roleMenu = new RoleMenu();
				roleMenu.setCode(modal.getCode());
				roleMenu.setUuid(item);
				roleMenuDao.create(roleMenu);
			}
		}
	}

	@Transactional
	public void updateRole(SystemRole role, RoleModel modal, SystemUser loginUser) throws Exception {
		LocalDateTime current = LocalDateTime.now();

		role.setTitle(modal.getTitle());
		role.setDescription(modal.getDescription());
		role.setAssignable(modal.getAssignable());
		role.setUpdatedBy(loginUser.getUsername());
		role.setUpdatedAt(current);

		boolean succeed = systemRoleDao.update(role);
		if (succeed) {
			// 刪除原來的 menu relation
			roleMenuDao.destroy(role.getCode());

			// 加上所有新的 menu relation
			List<String> selectItems = modal.getItems();
			for (String item : selectItems) {
				RoleMenu roleMenu = new RoleMenu();
				roleMenu.setCode(role.getCode());
				roleMenu.setUuid(item);
				roleMenuDao.create(roleMenu);
			}
		}
	}

	@Transactional
	public void destroy(List<String> items) throws Exception {
		for (String code : items) {
			Optional<SystemRole> roleOpt = systemRoleDao.findByCode(code);
			if (roleOpt.isPresent()) {
				SystemRole role = roleOpt.get();
				roleMenuDao.destroy(role.getCode());
				systemRoleDao.destroy(role.getCode());
			}
		}
	}

}
