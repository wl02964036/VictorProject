package com.rx.core.dao;

import java.util.List;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.rx.core.bean.TodoD;
import com.rx.core.bean.TodoM;

public interface TodoDao extends SqlObject {

	@SqlUpdate("INSERT INTO TodoM (groupId, name, displayOrder) " + "VALUES (:groupId, :name, "
			+ "(SELECT IIF(MAX(displayOrder) IS NULL, 1, MAX(displayOrder)+1) FROM TodoM))")
	boolean create(@BindBean TodoM bean);

	@SqlUpdate("INSERT INTO TodoD (groupId, todoId, name, value, createAt, createBy, updateAt, updateBy, displayOrder) "
			+ "VALUES (:groupId, :todoId, :name, :value, :createAt, :createBy, :updateAt, :updateBy, "
			+ "(SELECT IIF(MAX(displayOrder) IS NULL, 1, MAX(displayOrder)+1) FROM TodoD))")
	boolean create(@BindBean TodoD bean);

	@SqlUpdate("UPDATE TodoD SET name = :name, value = :value, updateAt = :updateAt, updateBy = :updateBy WHERE todoId = :todoId")
	boolean update(@BindBean TodoD bean);

	default boolean updateAll(final TodoD bean) {

		try (Handle handle = getHandle()) {

			StringBuffer sql = new StringBuffer();

			sql.append(" UPDATE TodoD SET ");

			if (bean.getName() != null) {
				sql.append(" name = :name, ");
			}

			if (bean.getValue() != null) {
				sql.append(" value = :value, ");
			}

			sql.append("  updateAt = :updateAt, ");
			sql.append("  updateBy = :updateBy ");
			sql.append(" WHERE groupId = :groupId ");

			Update update = handle.createUpdate(sql.toString());

			if (bean.getName() != null) {
				update.bind("name", bean.getName());
			}

			if (bean.getValue() != null) {
				update.bind("value", bean.getValue());
			}

			update.bind("updateAt", bean.getUpdateAt());
			update.bind("updateBy", bean.getUpdateBy());
			update.bind("groupId", bean.getGroupId());

			return update.execute() > 0;
		}
	}

	@SqlUpdate("DELETE FROM TodoD WHERE todoId = ?")
	boolean delete(String todoId);

	@SqlUpdate("DELETE FROM TodoD WHERE groupId = ?")
	boolean deleteAll(String groupId);

	@RegisterBeanMapper(TodoD.class)
	@SqlQuery("SELECT * FROM TodoD WHERE groupId = ? ORDER BY displayOrder")
	List<TodoD> queryTodoD(String groupId);

	@RegisterBeanMapper(TodoM.class)
	@SqlQuery("SELECT * FROM TodoM ORDER BY displayOrder")
	List<TodoM> queryTodoM();

}
