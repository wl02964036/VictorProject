package com.rx.webapi.service;

import java.util.List;
import java.util.Optional;

import org.jooq.lambda.tuple.Tuple2;

import com.rx.core.bean.SystemRole;
import com.rx.core.bean.SystemUser;
import com.rx.core.support.datatable.DataTableRequest;
import com.rx.core.table.request.RoleTableRequest;
import com.rx.webapi.model.RoleModel;
import com.rx.webapi.model.RoleQueryModel;

public interface SystemRoleService {

	public Tuple2<Long, List<RoleQueryModel>> query(final RoleTableRequest dataTableRequest) throws Exception;

	public Long countByCode(String code);

	public Optional<SystemRole> findRoleByCode(String code);

	public List<String> findUUIDsByCode(String code);

	public List<Tuple2<String, String>> findAllAssignableRoleTuples();

	public List<Tuple2<String, String>> findAllAssignableRoleTuplesExcludeAct();

	public List<Tuple2<String, String>> findAllRoleTuples();

	public void createRole(RoleModel modal, SystemUser loginUser) throws Exception;

	public void updateRole(SystemRole role, RoleModel modal, SystemUser loginUser) throws Exception;

	public void destroy(List<String> items) throws Exception;
}
