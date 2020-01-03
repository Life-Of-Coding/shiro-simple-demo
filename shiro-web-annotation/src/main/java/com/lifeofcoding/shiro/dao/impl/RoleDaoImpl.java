package com.lifeofcoding.shiro.dao.impl;



import com.lifeofcoding.shiro.dao.RoleDao;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

@Component
public class RoleDaoImpl implements RoleDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addRole(String username, Set<String> roles) {
        //去掉空数据
        roles.remove("");
        String addRoleSql = "INSERT IGNORE INTO shiro_web_user_roles (username,role) VALUES (?,?)";
        //StatementSetter用index遍历集合，转为List
        ArrayList<String> tempRoles = new ArrayList<>(roles);
        //批量添加数据
        jdbcTemplate.batchUpdate(addRoleSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, username);
                ps.setString(2, tempRoles.get(i));
            }

            @Override
            public int getBatchSize() {
                return tempRoles.size();
            }
        });
    }

    @Override
    public void deleteRolesByUsername(String userName) {
        String deleteRoleByUsernameSql = "DELETE FROM shiro_web_user_roles WHERE username = ?";
        jdbcTemplate.update(deleteRoleByUsernameSql,userName);
    }

    @Override
    public void deleteRole(String role) {
        String deleteRoleSql = "DELETE FROM shiro_web_user_roles WHERE role = ?";
        jdbcTemplate.update(deleteRoleSql,role);
    }

    @Override
    public void deleteUserRole(String userName, String role) {
        String deleteUserRoleSql = "DELETE FROM shiro_web_user_roles WHERE username = ? AND role = ?";
        jdbcTemplate.update(deleteUserRoleSql,new Object[]{userName,role});
    }
}
