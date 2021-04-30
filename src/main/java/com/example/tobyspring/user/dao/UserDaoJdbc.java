package com.example.tobyspring.user.dao;

import com.example.tobyspring.user.domain.User;
import com.mysql.cj.exceptions.MysqlErrorNumbers;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    };

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    /*
    public void add(User user) throws DuplicateUserIdException {
        try {
            this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
        }
        catch (SQLException e) {
            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY)
                throw new DuplicateUserIdException(e);  // 예외 전환
            else
                throw new RuntimeException(e);          // 예외 포장
        }
    }
    */

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", this.userMapper, id);
    }

    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        Integer value = this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        if (value == null) return 0;
        return value;
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }
}