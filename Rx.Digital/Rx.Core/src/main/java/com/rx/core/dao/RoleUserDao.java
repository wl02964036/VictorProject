package com.rx.core.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.RoleUser;

public interface RoleUserDao extends SqlObject {

	@RegisterBeanMapper(RoleUser.class)
	@SqlQuery("SELECT * FROM RolesUsers WHERE username = ?")
	List<RoleUser> findByUserName(String username);

	@RegisterBeanMapper(RoleUser.class)
	@SqlQuery("SELECT * FROM RolesUsers WHERE username = ? AND code = ?")
	Optional<RoleUser> findByUserNameAndCode(String username, String code);

	@SqlUpdate("INSERT INTO RolesUsers (code, username) VALUES (:code, :username)")
	boolean create(@BindBean RoleUser roleUser);

	@SqlUpdate("DELETE FROM RolesUsers WHERE username = ?")
	boolean destroy(String username);

	@SqlUpdate("DELETE FROM RolesUsers WHERE username = ? AND code = ?")
	boolean destroyByUsernameAndCode(String username, String code);

	@SqlUpdate("DELETE FROM RolesUsers WHERE code = ?")
	boolean destroyByCode(String code);

	@SqlQuery("SELECT COUNT(*) FROM RolesUsers WHERE username = ? AND code = ?")
	Long countByUserNameAndCode(String username, String code);

}