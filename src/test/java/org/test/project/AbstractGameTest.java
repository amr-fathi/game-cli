package org.test.project;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-springContext.xml")
public class AbstractGameTest {
	
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
//	@Before
//	public void startup() throws ScriptException, SQLException {
//		ScriptUtils.executeSqlScript(
//				jdbcTemplate.getDataSource().getConnection(),
//				new ClassPathResource("db/sql/test-create-db.sql"));
//		ScriptUtils.executeSqlScript(
//				jdbcTemplate.getDataSource().getConnection(),
//				new ClassPathResource("db/sql/test-insert-data.sql"));
//	}

}
