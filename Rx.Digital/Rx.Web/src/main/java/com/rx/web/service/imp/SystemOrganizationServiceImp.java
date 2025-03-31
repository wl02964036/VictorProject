package com.rx.web.service.imp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.web.Exception.ElementNotFoundException;
import com.rx.web.modal.PasswordModel;
import com.rx.web.modal.UnitModel;
import com.rx.web.modal.UserModel;
import com.rx.web.service.SystemOrganizationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemOrganizationServiceImp implements SystemOrganizationService {

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected RoleUserDao roleUserDao;

	@Transactional(readOnly = true)
	public Optional<SystemUnit> findUnitByCode(String code) {
		return systemUnitDao.findByCode(code);
	}

	@Transactional
	public void createUnit(UnitModel model, SystemUser loginUser) throws Exception {
		Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(model.getParentCode());
		if (parentUnitOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemUnit parentUnit = parentUnitOpt.get();

		LocalDateTime current = LocalDateTime.now();

		SystemUnit unit = new SystemUnit();
		unit.setCode(model.getCode().toUpperCase());
		unit.setDisplayName(model.getDisplayName());
		unit.setFax(model.getFax());
		unit.setEmail(model.getEmail());
		unit.setTel(model.getTel());
		unit.setParent(parentUnit.getCode());
		unit.setPath(parentUnit.getPath() + "," + parentUnit.getCode());
		unit.setWeight(model.getWeight());
		unit.setCreatedBy(loginUser.getUsername());
		unit.setCreatedAt(current);
		unit.setUpdatedBy(loginUser.getUsername());
		unit.setUpdatedAt(current);
		systemUnitDao.create(unit);
	}

	@Transactional
	public void updateUnit(SystemUnit unit, UnitModel model, SystemUser loginUser) throws Exception {
		LocalDateTime current = LocalDateTime.now();

		unit.setDisplayName(model.getDisplayName());
		unit.setFax(model.getFax());
		unit.setEmail(model.getEmail());
		unit.setTel(model.getTel());
		unit.setWeight(model.getWeight());
		unit.setUpdatedBy(loginUser.getUsername());
		unit.setUpdatedAt(current);

		systemUnitDao.update(unit);
	}

	@Transactional(readOnly = true)
	public Long countUnitChildrenByParent(String code) {
		return systemUnitDao.countChildrenByParent(code);
	}

	@Transactional(readOnly = true)
	public Long countUsersByUnitCode(String code) {
		return systemUserDao.countAllByUnitCode(code);
	}

	@Transactional
	public void deleteUnit(String code) throws Exception {
		systemUnitDao.delete(code);
	}

	@Transactional(readOnly = true)
	public Long countUnitByCode(String code) {
		return systemUnitDao.countByCode(code);
	}

	@Transactional(readOnly = true)
	public Long countUserByUsername(String username) {
		return systemUserDao.countAllByUsername(username);
	}

	@Transactional(readOnly = true)
	public Optional<SystemUser> findUserByUsername(String username) {
		return systemUserDao.findByUserName(username);
	}

	@Transactional
	public void createUser(UserModel userBind, SystemUser loginUser) throws Exception {
		Optional<SystemUnit> unitOpt = systemUnitDao.findByCode(userBind.getUnitCode());
		if (unitOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		LocalDateTime current = LocalDateTime.now();

		SystemUnit unit = unitOpt.get();

		SystemUser user = new SystemUser();
		user.setUsername(userBind.getUsername());
		user.setPassword(passwordEncoder.encode(userBind.getCode()));
		user.setDisplayName(userBind.getDisplayName());
		user.setSex(userBind.getSex());
		user.setEmail(userBind.getEmail());
		user.setTel(userBind.getTel());
		user.setEnabled(userBind.getEnabled());
		user.setExpired(userBind.getExpired());
		user.setLocked(userBind.getLocked());
		user.setUnitCode(unit.getCode());
		user.setPwdUpdateAt(current);
		user.setCreatedBy(loginUser.getUsername());
		user.setCreatedAt(current);
		user.setUpdatedBy(loginUser.getUsername());
		user.setUpdatedAt(current);

		boolean succeed = systemUserDao.create(user);
		if (succeed) {
			String[] roles = StringUtils.split(userBind.getRoles(), ",");
			for (String r : roles) {
				RoleUser ru = new RoleUser();
				ru.setCode(r);
				ru.setUsername(userBind.getUsername());
				roleUserDao.create(ru);
			}
		}
	}

	@Transactional(readOnly = true)
	public String getRolesByUsername(String username) {
		List<RoleUser> roleUsers = roleUserDao.findByUserName(username);
		List<String> roles = roleUsers.stream().map(ru -> ru.getCode()).collect(Collectors.toList());
		return StringUtils.join(roles, ",");
	}

	@Transactional
	public void updateUser(SystemUser user, UserModel userBind, SystemUser loginUser) throws Exception {
		LocalDateTime current = LocalDateTime.now();

		user.setDisplayName(userBind.getDisplayName());
		user.setSex(userBind.getSex());
		user.setEmail(userBind.getEmail());
		user.setTel(userBind.getTel());
		user.setEnabled(userBind.getEnabled());
		user.setExpired(userBind.getExpired());
		user.setLocked(userBind.getLocked());

		user.setUpdatedBy(loginUser.getUsername());
		user.setUpdatedAt(current);

		boolean succeed = systemUserDao.update(user);
		if (succeed) {
			// 刪除已存在 role mapping，再加入更改過後的
			roleUserDao.destroy(userBind.getUsername());

			String[] roles = StringUtils.split(userBind.getRoles(), ",");
			for (String r : roles) {
				RoleUser ru = new RoleUser();
				ru.setCode(r);
				ru.setUsername(userBind.getUsername());
				roleUserDao.create(ru);
			}
		}

	}

	@Transactional
	public void deleteUser(String username) throws Exception {
		boolean succeed = systemUserDao.delete(username);
		if (succeed) {
			roleUserDao.destroy(username);
		}
	}

	@Transactional
	public void updateUserPassword(SystemUser user, PasswordModel passwordBind, SystemUser loginUser) throws Exception {
		LocalDateTime current = LocalDateTime.now();

		user.setPassword(passwordEncoder.encode(passwordBind.getCode()));
		user.setPwdUpdateAt(current);
		user.setUpdatedBy(loginUser.getUsername());
		user.setUpdatedAt(current);

		systemUserDao.updatePassword(user);
	}

	@Transactional(readOnly = true)
	public List<String> findChildrenAndIncludeUnitCode(final String unitCode) {
		List<SystemUnit> units = systemUnitDao.findChildrenByPathLike("%" + unitCode + "%");
		List<String> codes = units.stream().map(unit -> unit.getCode()).collect(Collectors.toList());

		codes.add(0, unitCode);
		return codes;
	}

	@Transactional
	public boolean resetLoginErrorsAndLockedAtByUsername(final String username) throws Exception {
		return systemUserDao.resetLoginErrorsAndLockedAtByUsername(username);
	}

}
