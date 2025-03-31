package com.rx.core.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.rx.core.bean.SystemMenu;

public interface SystemMenuDao extends SqlObject {

    @RegisterBeanMapper(SystemMenu.class)
    @SqlQuery("SELECT * FROM SystemMenus WHERE level = ? ORDER BY weight ASC")
    List<SystemMenu> findByLevel(int level);

    @RegisterBeanMapper(SystemMenu.class)
    @SqlQuery("SELECT * FROM SystemMenus WHERE parent = ? ORDER BY weight ASC")
    List<SystemMenu> findByParent(String parentUUID);

    @SqlQuery("SELECT COUNT(*) FROM SystemMenus WHERE parent = ?")
    Long countChildrenByParent(String parentUUID);

    @RegisterBeanMapper(SystemMenu.class)
    @SqlQuery("SELECT * FROM SystemMenus WHERE uuid = ?")
    Optional<SystemMenu> findByUUID(String uuid);

    @RegisterBeanMapper(SystemMenu.class)
    @SqlQuery("SELECT * FROM SystemMenus WHERE path != '#' ORDER BY weight ASC")
    List<SystemMenu> findByPathIsNotFolder();

}
