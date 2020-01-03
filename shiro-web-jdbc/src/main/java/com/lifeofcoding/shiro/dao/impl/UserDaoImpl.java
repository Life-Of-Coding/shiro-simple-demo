package com.lifeofcoding.shiro.dao.impl;

import com.lifeofcoding.shiro.dao.UserDao;
import com.lifeofcoding.shiro.pojo.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserDaoImpl implements UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getPasswordByUserName(String userName) {
        String queryPasswordSql = "SELECT password FROM shiro_web_users WHERE username = ?";
        List<String> passwords = jdbcTemplate.query(queryPasswordSql, new String[]{userName}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("password");
            }
        });
        if(CollectionUtils.isEmpty(passwords)){
            return null;
        }
        return passwords.get(0);
    }

    @Override
    public Set<String> getRolesByUserName(String userName) {
        String queryRoleSql = "SELECT role FROM shiro_web_user_roles WHERE username = ?";
        List<String> roles = jdbcTemplate.query(queryRoleSql, new String[]{userName}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("role");
            }
        });
        if (CollectionUtils.isEmpty(roles)){
            return null;
        }
        return new HashSet<>(roles);
    }

    @Override
    public String getSaltByUserName(String userName) {
        String querySaltSql = "SELECT salt FROM shiro_web_users WHERE username = ?";
        List<String> salts = jdbcTemplate.query(querySaltSql,new String[]{userName},new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("salt");
            }
        });
        if (CollectionUtils.isEmpty(salts)){
            return null;
        }
        return salts.get(0);
    }

    @Override
    public void addUser(User user) throws Exception{
        if (user == null){
            return;
        }
        String addUserSql = "INSERT INTO shiro_web_users (username,password,salt) VALUES (?,?,?)";
        jdbcTemplate.update(addUserSql,new Object[]{user.getUsername(),user.getPassword(),user.getSalt()});
    }

    @Override
    public void deleteUser(String userName) {
        String deleteUserSql = "DELETE FROM shiro_web_users WHERE username = ?";
        jdbcTemplate.update(deleteUserSql,userName);
    }


}
