package org.balanceline4j.example.benchmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	// example for Oracle database
	public static Connection createConnection() throws SQLException {
		String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
		String connectionString = "jdbc:oracle:thin:@localhost:1521:xe";
		String login = "TESTS";
		String password = "TESTS";

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException ex) {
			System.out.println("JDBC driver not found on classpath.");
		}

		return DriverManager.getConnection(connectionString, login, password);
	}
	
}
