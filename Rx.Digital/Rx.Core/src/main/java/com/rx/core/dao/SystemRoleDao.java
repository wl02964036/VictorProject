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

import com.rx.core.bean.SystemRole;
import com.rx.core.support.datatable.DataTableRequest;
import com.rx.core.support.datatable.Search;
import com.rx.core.table.request.RoleTableRequest;
import com.rx.core.util.StringUtil;

public interface SystemRoleDao extends SqlObject {

	@RegisterBeanMapper(SystemRole.class)
	@SqlQuery("SELECT * FROM SystemRoles ORDER BY createdAt ASC")
	List<SystemRole> findAll();

	@RegisterBeanMapper(SystemRole.class)
	@SqlQuery("SELECT * FROM SystemRoles WHERE assignable = 1 ORDER BY createdAt ASC")
	List<SystemRole> findAllAssignable();

	@RegisterBeanMapper(SystemRole.class)
	@SqlQuery("SELECT * FROM SystemRoles WHERE assignable = 1 AND code !='ROLE_ACTIVITY' AND code !='ROLE_ACTIVITYMANAGER' ORDER BY createdAt ASC")
	List<SystemRole> findAllAssignableExcludeAct();

	@RegisterBeanMapper(SystemRole.class)
	@SqlQuery("SELECT * FROM SystemRoles WHERE code = ?")
	Optional<SystemRole> findByCode(String code);

	@SqlQuery("SELECT COUNT(*) FROM SystemRoles WHERE code = ?")
	Long countByCode(String code);

	default Long totalCount(final RoleTableRequest dataTableRequest) {
		try (Handle handle = getHandle()) {
			Search search = dataTableRequest.getSearch();
			String queryTitle = dataTableRequest.getQueryTitle();

			String sql = "SELECT COUNT(*) FROM SystemRoles WHERE 1 = 1";
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND title LIKE :searchValue";
			}else if (StringUtil.isNotBlank(queryTitle)) {
				sql += " AND title LIKE :queryTitle";
			}

			Query query = handle.createQuery(sql);
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				query.bind("searchValue", "%" + search.getValue() + "%");
			}else if (StringUtil.isNotBlank(queryTitle)) {
				query.bind("queryTitle", "%" + queryTitle + "%");
			}

			return query.mapTo(Long.class).one();
		}
	}

	default List<SystemRole> queryByPage(final RoleTableRequest dataTableRequest) {
		try (Handle handle = getHandle()) {

			// 將 ResultSet 轉換成 SystemRole 物件
			handle.registerRowMapper(BeanMapper.factory(SystemRole.class));

			String orderClosure = dataTableRequest.orderClosure();
			Search search = dataTableRequest.getSearch();
			String fetchClosure = dataTableRequest.fetchClosure();
			String queryTitle = dataTableRequest.getQueryTitle();

			String sql = "SELECT * FROM SystemRoles WHERE 1 = 1";
			if (search != null && StringUtil.isNotBlank(search.getValue())) {
				sql += " AND title LIKE :searchValue";
			}else if (StringUtil.isNotBlank(queryTitle)) {
				sql += " AND title LIKE :queryTitle";
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
			}else if (StringUtil.isNotBlank(queryTitle)) {
				query.bind("queryTitle", "%" + queryTitle + "%");
			}

			return query.mapTo(SystemRole.class).list();
		}
	}

	@SqlUpdate("INSERT INTO SystemRoles (code, title, description, assignable, createdAt, createdBy, updatedAt, updatedBy) "
			+ "VALUES (:code, :title, :description, :assignable, :createdAt, :createdBy, :updatedAt, :updatedBy)")
	boolean create(@BindBean SystemRole role);

	@SqlUpdate("UPDATE SystemRoles SET title = :title, description = :description, assignable = :assignable, "
			+ "updatedAt = :updatedAt, updatedBy = :updatedBy WHERE code = :code")
	boolean update(@BindBean SystemRole role);

	@SqlUpdate("DELETE FROM SystemRoles WHERE code = ?")
	boolean destroy(String code);

}
