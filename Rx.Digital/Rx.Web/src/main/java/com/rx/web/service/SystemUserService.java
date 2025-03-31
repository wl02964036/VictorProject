package com.rx.web.service;

import java.util.List;

import org.jooq.lambda.tuple.Tuple2;

import com.rx.core.bean.SystemUser;
import com.rx.core.table.request.PermissionTableRequest;
import com.rx.web.modal.PermissionQueryModel;
import com.rx.web.modal.UserModel;

public interface SystemUserService {

	public Tuple2<Long, List<PermissionQueryModel>> query(final PermissionTableRequest dataTableRequest) throws Exception;

	public Tuple2<Long, List<PermissionQueryModel>> queryIncludeUnitsOnly(final PermissionTableRequest dataTableRequest,
			List<String> unitCodes) throws Exception;

	public void updateRoles(SystemUser user, UserModel userBind, SystemUser loginUser) throws Exception;

	public boolean updateUnitCode(String code, String username) throws Exception;

	public boolean setLoginErrorsByUsername(int loginErrors, String username) throws Exception;

	public boolean setLockedAtByUsername(String username) throws Exception;

	public boolean resetLoginErrorsAndLockedAtByUsername(String username) throws Exception;
}
