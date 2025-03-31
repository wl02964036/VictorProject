package com.rx.web.service;

import java.util.List;
import java.util.Optional;

import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.web.modal.PasswordModel;
import com.rx.web.modal.UnitModel;
import com.rx.web.modal.UserModel;

public interface SystemOrganizationService {

	public Optional<SystemUnit> findUnitByCode(String code);

	public void createUnit(UnitModel model, SystemUser loginUser) throws Exception;

	public void updateUnit(SystemUnit unit, UnitModel model, SystemUser loginUser) throws Exception;

	public Long countUnitChildrenByParent(String code);

	public Long countUsersByUnitCode(String code);

	public void deleteUnit(String code) throws Exception;

	public Long countUnitByCode(String code);

	public Long countUserByUsername(String username);

	public Optional<SystemUser> findUserByUsername(String username);

	public void createUser(UserModel userBind, SystemUser loginUser) throws Exception;

	public String getRolesByUsername(String username);

	public void updateUser(SystemUser user, UserModel userBind, SystemUser loginUser) throws Exception;

	public void deleteUser(String username) throws Exception;

	public void updateUserPassword(SystemUser user, PasswordModel passwordBind, SystemUser loginUser) throws Exception;

	public List<String> findChildrenAndIncludeUnitCode(final String unitCode);

	public boolean resetLoginErrorsAndLockedAtByUsername(final String username) throws Exception;
}
