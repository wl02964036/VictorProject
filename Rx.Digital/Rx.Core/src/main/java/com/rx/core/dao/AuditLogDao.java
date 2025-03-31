package com.rx.core.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.AuditLog;
import com.rx.core.support.datatable.Search;
import com.rx.core.table.request.AuditLogTableRequest;
import com.rx.core.util.StringUtil;

public interface AuditLogDao extends SqlObject {

	@SqlUpdate("INSERT INTO AuditLogs (username, actionedAt, ip, title, uuid, type, target, subject, mark) "
			+ "VALUES (:username, :actionedAt, :ip, :title, :uuid, :type, :target, :subject, :mark)")
	@GetGeneratedKeys("id")
	Long create(@BindBean AuditLog auditLog);

	default Long totalCount(final AuditLogTableRequest dataTableRequest) {
		Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}"); // 日期格式 2020-02-31
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		try (Handle handle = getHandle()) {
			Search search = dataTableRequest.getSearch();
			String queryUsername = dataTableRequest.getQueryUsername();
			LocalDate queryActionedAt = dataTableRequest.getQueryActionedAt();

			String sql = "SELECT COUNT(*) FROM AuditLogs WHERE 1 = 1";
			if (search != null && StringUtils.isNotBlank(search.getValue())) {
				if (datePattern.matcher(search.getValue()).matches()) {
					sql += " AND actionedAt BETWEEN :start AND :end";
				} else {
					sql += " AND username LIKE :searchValue";
				}
			}

			if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			if (queryActionedAt != null) {
				sql += " AND actionedAt BETWEEN :start AND :end";
			}

			Query query = handle.createQuery(sql);
			if (search != null && StringUtils.isNotBlank(search.getValue())) {
				if (datePattern.matcher(search.getValue()).matches()) {
					String dateStart = search.getValue() + " 00:00:00";
					String dateEnd = search.getValue() + " 23:59:59";
					query.bind("start", dateStart);
					query.bind("end", dateEnd);
				} else {
					query.bind("searchValue", "%" + search.getValue() + "%");
				}
			}

			if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			if (queryActionedAt != null) {
				String date = queryActionedAt.format(formatter);
				String dateStart = date + " 00:00:00";
				String dateEnd = date + " 23:59:59";
				query.bind("start", dateStart);
				query.bind("end", dateEnd);
			}

			return query.mapTo(Long.class).one();
		}
	}

	default List<AuditLog> queryByPage(final AuditLogTableRequest dataTableRequest) {
		Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}"); // 日期格式 2020-02-31
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		try (Handle handle = getHandle()) {

			// 將 ResultSet 轉換成 AuditLog 物件
			handle.registerRowMapper(BeanMapper.factory(AuditLog.class));

			String orderClosure = dataTableRequest.orderClosure();
			Search search = dataTableRequest.getSearch();
			String fetchClosure = dataTableRequest.fetchClosure();
			String queryUsername = dataTableRequest.getQueryUsername();
			LocalDate queryActionedAt = dataTableRequest.getQueryActionedAt();

			String sql = "SELECT * FROM AuditLogs WHERE 1 = 1";
			if (search != null && StringUtils.isNotBlank(search.getValue())) {
				if (datePattern.matcher(search.getValue()).matches()) {
					sql += " AND actionedAt BETWEEN :start AND :end";
				} else {
					sql += " AND username LIKE :searchValue";
				}
			}

			if (StringUtil.isNotBlank(queryUsername)) {
				sql += " AND username LIKE :queryUsername";
			}

			if (queryActionedAt != null) {
				sql += " AND actionedAt BETWEEN :start AND :end";
			}

			if (StringUtils.isNotBlank(orderClosure)) {
				sql += " ORDER BY " + orderClosure;
			} else {
				sql += " ORDER BY actionedAt DESC";
			}

			// 使用 SQL SERVER 2012 offset 0 rows fetch next ? rows only 語句，一定要接在 order by
			// 子句後面
			sql += " " + fetchClosure;

			Query query = handle.createQuery(sql);
			if (search != null && StringUtils.isNotBlank(search.getValue())) {
				if (datePattern.matcher(search.getValue()).matches()) {
					String dateStart = search.getValue() + " 00:00:00";
					String dateEnd = search.getValue() + " 23:59:59";
					query.bind("start", dateStart);
					query.bind("end", dateEnd);
				} else {
					query.bind("searchValue", "%" + search.getValue() + "%");
				}
			}

			if (StringUtil.isNotBlank(queryUsername)) {
				query.bind("queryUsername", "%" + queryUsername + "%");
			}

			if (queryActionedAt != null) {
				String date = queryActionedAt.format(formatter);
				String dateStart = date + " 00:00:00";
				String dateEnd = date + " 23:59:59";
				query.bind("start", dateStart);
				query.bind("end", dateEnd);
			}

			return query.mapTo(AuditLog.class).list();
		}
	}

}
