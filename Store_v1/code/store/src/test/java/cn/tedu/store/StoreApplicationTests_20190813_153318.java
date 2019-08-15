package cn.tedu.store;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreApplicationTests {
	
	@Autowired
	DataSource ds;

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void getConnection() throws SQLException {
		Connection conn=ds.getConnection();
		System.err.println(conn);
	}
	

}
