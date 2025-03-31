package com.rx.core.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.SystemUnit;

public interface SystemUnitDao extends SqlObject {

    @SqlUpdate("INSERT INTO SystemUnits (code, displayName, fax, email, tel, " +
            "parent, path, weight, createdAt, createdBy, updatedAt, updatedBy) VALUES " +
            "(:code, :displayName, :fax, :email, :tel, " +
            ":parent, :path, :weight, :createdAt, :createdBy, :updatedAt, :updatedBy)")
    boolean create(@BindBean SystemUnit unit);

    @RegisterBeanMapper(SystemUnit.class)
    @SqlQuery("SELECT * FROM SystemUnits WHERE code = ?")
    Optional<SystemUnit> findByCode(String code);

    @SqlQuery("SELECT COUNT(*) FROM SystemUnits WHERE code = ?")
    Long countByCode(String code);

    @RegisterBeanMapper(SystemUnit.class)
    @SqlQuery("SELECT * FROM SystemUnits WHERE parent = ? ORDER BY weight ASC")
    List<SystemUnit> findAllByParent(String parentCode);

    @RegisterBeanMapper(SystemUnit.class)
    @SqlQuery("SELECT * FROM SystemUnits WHERE path LIKE ? ORDER BY weight ASC")
    List<SystemUnit> findChildrenByPathLike(String codeLike);

    @SqlQuery("SELECT COUNT(*) FROM SystemUnits WHERE parent = ?")
    Long countChildrenByParent(String parentCode);

    @SqlUpdate("UPDATE SystemUnits SET displayName = :displayName, fax = :fax, email = :email, tel = :tel, " +
               "weight = :weight, updatedAt = :updatedAt, updatedBy = :updatedBy WHERE code = :code")
    boolean update(@BindBean SystemUnit unit);

    @SqlUpdate("DELETE FROM SystemUnits WHERE code = ?")
    boolean delete(String code);

}
