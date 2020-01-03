package com.lifeofcoding.shiro.dao.impl;

import com.lifeofcoding.shiro.dao.PermissionDao;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PermissionDaoImpl implements PermissionDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addPermissions(String roleName, Set<String> permissions) {
        String addPermissionSql = "INSERT IGNORE INTO shiro_web_roles_permissions (role,permission) VALUES (?,?)";
        //去掉空数据
        permissions.remove("");
        //后面StatementSetter需要用index遍历集合，所以转为List
        ArrayList<String> tempPermissions = new ArrayList<>(permissions);
        //批量添加数据
        jdbcTemplate.batchUpdate(addPermissionSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, roleName);
                ps.setString(2, tempPermissions.get(i));
            }

            @Override
            public int getBatchSize() {
                return tempPermissions.size();
            }
        });
    }

    @Override
    public Set<String> getPermissionsByRole(String role) {
        String queryPermissionSql = "SELECT permission FROM shiro_web_roles_permissions WHERE role = ?";
        List<String> permissions = jdbcTemplate.query(queryPermissionSql, new String[]{role}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("permission");
            }
        });
        if (CollectionUtils.isEmpty(permissions)){
            return null;
        }
        return new HashSet<>(permissions);
    }

    @Override
    public void deletePermissionsByRole(String role) {
        String deletePermissionsByRoleSql = "DELETE FROM shiro_web_roles_permissions WHERE role = ?";
        jdbcTemplate.update(deletePermissionsByRoleSql,role);
    }

    @Override
    public void deletePermission(String permission) {
        String deletePermissionSql = "DELETE FROM shiro_web_roles_permissions WHERE permission = ?";
        jdbcTemplate.update(deletePermissionSql,permission);
    }

    @Override
    public void deleteRolePermission(String role, String permission) {
        String deleteRolePermissionSql = "DELETE FROM shiro_web_roles_permissions WHERE role = ? AND permission = ?";
        jdbcTemplate.update(deleteRolePermissionSql,new Object[]{role,permission});
    }
}
