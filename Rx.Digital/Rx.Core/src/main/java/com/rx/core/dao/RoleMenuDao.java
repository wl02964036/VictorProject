package com.rx.core.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.RoleMenu;

public interface RoleMenuDao extends SqlObject {

	@RegisterBeanMapper(RoleMenu.class)
	@SqlQuery("SELECT * FROM RolesMenus WHERE code = ?")
	List<RoleMenu> findByCode(String code);

	@SqlQuery("SELECT uuid FROM RolesMenus WHERE code = ?")
	List<String> findUUIDsByCode(String code);

	@RegisterBeanMapper(RoleMenu.class)
	@SqlQuery("SELECT * FROM RolesMenus WHERE uuid = ?")
	List<RoleMenu> findByUUID(String uuid);

	@SqlUpdate("INSERT INTO RolesMenus (code, uuid) VALUES (:code, :uuid)")
	boolean create(@BindBean RoleMenu roleMenu);

	@SqlUpdate("DELETE FROM RolesMenus WHERE code = ?")
	boolean destroy(String code);

}
