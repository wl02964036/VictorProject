package com.rx.web.configuration;

import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rx.core.dao.AuditLogDao;
import com.rx.core.dao.LoginLogDao;
import com.rx.core.dao.RoleMenuDao;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemMenuDao;
import com.rx.core.dao.SystemRoleDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JdbiDaoConfiguration {

	@Bean
	public SystemUnitDao systemUnitDao(Jdbi jdbi) {
		return jdbi.onDemand(SystemUnitDao.class);
	}

	@Bean
	public SystemUserDao systemUserDao(Jdbi jdbi) {
		return jdbi.onDemand(SystemUserDao.class);
	}

	@Bean
	public SystemRoleDao systemRoleDao(Jdbi jdbi) {
		return jdbi.onDemand(SystemRoleDao.class);
	}

	@Bean
	public SystemMenuDao systemMenuDao(Jdbi jdbi) {
		return jdbi.onDemand(SystemMenuDao.class);
	}

	@Bean
	public RoleUserDao roleUserDao(Jdbi jdbi) {
		return jdbi.onDemand(RoleUserDao.class);
	}

	@Bean
	public RoleMenuDao roleMenuDao(Jdbi jdbi) {
		return jdbi.onDemand(RoleMenuDao.class);
	}

	@Bean
	public LoginLogDao loginLogDao(Jdbi jdbi) {
		return jdbi.onDemand(LoginLogDao.class);
	}

	@Bean
	public AuditLogDao auditLogDao(Jdbi jdbi) {
		return jdbi.onDemand(AuditLogDao.class);
	}
}
