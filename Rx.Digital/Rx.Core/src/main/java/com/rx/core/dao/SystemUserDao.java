package com.rx.core.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.SystemUser;
import com.rx.core.support.datatable.Search;
import com.rx.core.table.request.PermissionTableRequest;
import com.rx.core.util.StringUtil;

public interface SystemUserDao extends SqlObject {

	@RegisterBeanMapper(SystemUser.class)
	@SqlQuery("SELECT * FROM SystemUsers WHERE username = ?")
	Optional<SystemUser> findByUserName(String username);

	@SqlQuery("SELECT COUNT(*) FROM SystemUsers WHERE unitCode = ?")
	Long countAllByUnitCode(String unitCode);

	@SqlQuery("SELECT COUNT(*) FROM SystemUsers WHERE username = ?")
	Long countAllByUsername(String username);

	@RegisterBeanMapper(SystemUser.class)
	@SqlQuery("SELECT * FROM SystemUsers WHERE unitCode = ? ORDER BY username ASC")
	List<SystemUser> findAllByUnitCode(String unitCode);

	@RegisterBeanMapper(SystemUser.class)
	@SqlQuery("SELECT * FROM SystemUsers ORDER BY username ASC")
	List<SystemUser> findAll();

	@SqlQuery("SELECT loginErrors FROM SystemUsers WHERE username = ?")
	Optional<Integer> findLoginErrorsByUsername(String username);

	@SqlUpdate("UPDATE SystemUsers SET loginErrors = ? WHERE username = ?")
	boolean setLoginErrorsByUsername(int loginErrors, String username);

	@SqlUpdate("UPDATE SystemUsers SET lockedAt = CONVERT(DATETIME, SYSDATETIMEOFFSET() AT TIME ZONE 'Taipei Standard Time') WHERE username = ?")
	boolean setLockedAtByUsername(String username);

	@SqlUpdate("UPDATE SystemUsers SET loginErrors = 0, lockedAt = NULL WHERE username = ?")
	boolean resetLoginErrorsAndLockedAtByUsername(String username);

	@SqlUpdate("INSERT INTO SystemUsers (username, password, displayName, sex, email, "
			+ "tel, enabled, expired, locked, unitCode, pwdUpdateAt, createdAt, createdBy, "
			+ "updatedAt, updatedBy) VALUES " + "(:username, :password, :displayName, :sex, :email, "
			+ ":tel, :enabled, :expired, :locked, :unitCode, :pwdUpdateAt, :createdAt, :createdBy, "
			+ ":updatedAt, :updatedBy)")
	boolean create(@BindBean SystemUser user);

	@SqlUpdate("UPDATE SystemUsers SET displayName = :displayName, sex = :sex, email = :email, tel = :tel, "
			+ "enabled = :enabled, expired = :expired, locked = :locked, updatedBy = :updatedBy, "
			+ "updatedAt = :updatedAt WHERE username = :username")
	boolean update(@BindBean SystemUser user);

	@SqlUpdate("DELETE FROM SystemUsers WHERE username = ?")
	boolean delete(String username);

	@SqlUpdate("UPDATE SystemUsers SET password = :password, pwdUpdateAt = :pwdUpdateAt, "
			+ "updatedAt = :updatedAt, updatedBy = :updatedBy WHERE username = :username")
	boolean updatePassword(@BindBean SystemUser user);

	@SqlUpdate("UPDATE SystemUsers SET updatedAt = :updatedAt, updatedBy = :updatedBy WHERE username = :username")
	boolean updateChanged(@BindBean SystemUser user);

	@SqlUpdate("UPDATE SystemUsers SET unitCode = ? WHERE username = ?")
	boolean updateUnitCode(String code, String username);

	default Long totalCount(final PermissionTableRequest dataTableRequest) {
		try (Handle handle = getHandle()) {
			Search search = dataTableRequest.getSearch();
			String queryUsername = dataTableRequest.getQueryUsername();

			String sql = "SELECT COUNT(*) FROM SystemUsers WHERE 1 = 1";
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND username LIKE :searchValue";
			} else if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			Query query = handle.createQuery(sql);
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				query.bind("searchValue", "%" + search.getValue() + "%");
			} else if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			return query.mapTo(Long.class).one();
		}
	}

	default List<SystemUser> queryByPage(final PermissionTableRequest dataTableRequest) {
		try (Handle handle = getHandle()) {

			// 將 ResultSet 轉換成 SystemUser 物件
			handle.registerRowMapper(BeanMapper.factory(SystemUser.class));

			String orderClosure = dataTableRequest.orderClosure();
			Search search = dataTableRequest.getSearch();
			String fetchClosure = dataTableRequest.fetchClosure();
			String queryUsername = dataTableRequest.getQueryUsername();

			String sql = "SELECT * FROM SystemUsers WHERE 1 = 1";
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND username LIKE :searchValue";
			} else if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			if (StringUtil.isNotBlank(orderClosure)) {
				sql += " ORDER BY " + orderClosure;
			} else {
				sql += " ORDER BY updatedAt DESC";
			}

			// 使用 SQL SERVER 2012 offset 0 rows fetch next ? rows only 語句，一定要接在 order by
			// 子句後面
			sql += " " + fetchClosure;

			Query query = handle.createQuery(sql);
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				query.bind("searchValue", "%" + search.getValue() + "%");
			} else if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			return query.mapTo(SystemUser.class).list();
		}
	}

	default Long totalIncludeUnitsOnlyCount(final PermissionTableRequest dataTableRequest, List<String> unitCodes) {
		try (Handle handle = getHandle()) {
			Search search = dataTableRequest.getSearch();
			String queryUsername = dataTableRequest.getQueryUsername();

			String sql = "SELECT COUNT(*) FROM SystemUsers WHERE 1 = 1";
			if (unitCodes.size() > 0) {
				sql += " AND unitCode IN (<listOfUnitCode>)";
			}

			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND username LIKE :searchValue";
			} else if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			Query query = handle.createQuery(sql);
			if (unitCodes.size() > 0) {
				query.bindList("listOfUnitCode", unitCodes);
			}

			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				query.bind("searchValue", "%" + search.getValue() + "%");
			} else if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			return query.mapTo(Long.class).one();
		}
	}

	default List<SystemUser> queryIncludeUnitsOnlyByPage(final PermissionTableRequest dataTableRequest,
			List<String> unitCodes) {
		try (Handle handle = getHandle()) {

			// 將 ResultSet 轉換成 SystemUser 物件
			handle.registerRowMapper(BeanMapper.factory(SystemUser.class));

			String orderClosure = dataTableRequest.orderClosure();
			Search search = dataTableRequest.getSearch();
			String fetchClosure = dataTableRequest.fetchClosure();
			String queryUsername = dataTableRequest.getQueryUsername();

			String sql = "SELECT * FROM SystemUsers WHERE 1 = 1";
			if (unitCodes.size() > 0) {
				sql += " AND unitCode IN (<listOfUnitCode>)";
			}

			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND username LIKE :searchValue";
			} else if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			if (StringUtil.isNotBlank(orderClosure)) {
				sql += " ORDER BY " + orderClosure;
			} else {
				sql += " ORDER BY updatedAt DESC";
			}

			// 使用 SQL SERVER 2012 offset 0 rows fetch next ? rows only 語句，一定要接在 order by
			// 子句後面
			sql += " " + fetchClosure;

			Query query = handle.createQuery(sql);
			if (unitCodes.size() > 0) {
				query.bindList("listOfUnitCode", unitCodes);
			}

			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				query.bind("searchValue", "%" + search.getValue() + "%");
			} else if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			return query.mapTo(SystemUser.class).list();
		}
	}

}
