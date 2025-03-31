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

import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.table.request.PermissionTableRequest;
import com.rx.core.util.StringUtil;
import com.rx.web.modal.PermissionQueryModel;
import com.rx.web.modal.UserModel;
import com.rx.web.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemUserServiceImp implements SystemUserService {

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected RoleUserDao roleUserDao;

	@Transactional(readOnly = true)
	public Tuple2<Long, List<PermissionQueryModel>> query(final PermissionTableRequest dataTableRequest) throws Exception {
		Long totalCount = systemUserDao.totalCount(dataTableRequest);
		List<SystemUser> pageContent = systemUserDao.queryByPage(dataTableRequest);

		// 轉成 顯示在 datatable 上的內容
		List<PermissionQueryModel> pageViewModel = pageContent.stream().map(systemUser -> {
			PermissionQueryModel viewModel = new PermissionQueryModel();
			viewModel.setDisplayName(systemUser.getDisplayName());
			viewModel.setUsername(systemUser.getUsername());

			Optional<SystemUnit> unitOpt = systemUnitDao.findByCode(systemUser.getUnitCode());
			if (unitOpt.isPresent()) {
				viewModel.setUnitName(unitOpt.get().getDisplayName());
			} else {
				viewModel.setUnitName("");
			}

			List<RoleUser> roleUsers = roleUserDao.findByUserName(systemUser.getUsername());
			List<String> roles = roleUsers.stream().map(ru -> ru.getCode()).collect(Collectors.toList());

			viewModel.setRoles(StringUtil.join(roles, ","));
			return viewModel;
		}).collect(Collectors.toList());

		return Tuple.tuple(totalCount, pageViewModel);
	}

	@Transactional(readOnly = true)
	public Tuple2<Long, List<PermissionQueryModel>> queryIncludeUnitsOnly(final PermissionTableRequest dataTableRequest,
			List<String> unitCodes) throws Exception {
		Long totalCount = systemUserDao.totalIncludeUnitsOnlyCount(dataTableRequest, unitCodes);
		List<SystemUser> pageContent = systemUserDao.queryIncludeUnitsOnlyByPage(dataTableRequest, unitCodes);

		// 轉成 顯示在 datatable 上的內容
		List<PermissionQueryModel> pageViewModel = pageContent.stream().map(systemUser -> {
			PermissionQueryModel viewModel = new PermissionQueryModel();
			viewModel.setDisplayName(systemUser.getDisplayName());
			viewModel.setUsername(systemUser.getUsername());

			Optional<SystemUnit> unitOpt = systemUnitDao.findByCode(systemUser.getUnitCode());
			if (unitOpt.isPresent()) {
				viewModel.setUnitName(unitOpt.get().getDisplayName());
			} else {
				viewModel.setUnitName("");
			}

			List<RoleUser> roleUsers = roleUserDao.findByUserName(systemUser.getUsername());
			List<String> roles = roleUsers.stream().map(ru -> ru.getCode()).collect(Collectors.toList());

			viewModel.setRoles(StringUtil.join(roles, ","));
			return viewModel;
		}).collect(Collectors.toList());

		return Tuple.tuple(totalCount, pageViewModel);
	}

	@Transactional
	public void updateRoles(SystemUser user, UserModel userBind, SystemUser loginUser) throws Exception {
		LocalDateTime current = LocalDateTime.now();
		user.setUpdatedBy(loginUser.getUsername());
		user.setUpdatedAt(current);

		boolean succeed = systemUserDao.updateChanged(user);
		if (succeed) {
			roleUserDao.destroy(userBind.getUsername());

			String[] roles = StringUtil.split(userBind.getRoles(), ",");
			for (String r : roles) {
				RoleUser ru = new RoleUser();
				ru.setCode(r);
				ru.setUsername(userBind.getUsername());
				roleUserDao.create(ru);
			}
		}
	}

	@Transactional
	public boolean updateUnitCode(String code, String username) throws Exception {
		return systemUserDao.updateUnitCode(code, username);
	}

	@Transactional
	public boolean setLoginErrorsByUsername(int loginErrors, String username) throws Exception {
		return systemUserDao.setLoginErrorsByUsername(loginErrors, username);
	}

	@Transactional
	public boolean setLockedAtByUsername(String username) throws Exception {
		return systemUserDao.setLockedAtByUsername(username);
	}

	@Transactional
	public boolean resetLoginErrorsAndLockedAtByUsername(String username) throws Exception {
		return systemUserDao.resetLoginErrorsAndLockedAtByUsername(username);
	}
}
