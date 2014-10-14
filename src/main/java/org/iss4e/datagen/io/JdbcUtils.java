package org.iss4e.datagen.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.iss4e.datagen.common.StackTracer;

public final class JdbcUtils {

	private Connection connection;

	public Connection openDBConn(String db) {
		try {
			if (connection == null || connection.isClosed()) {
				String driverName = "org.postgresql.Driver";
				String connectionString = String.format("jdbc:postgresql://gho1:5432/%s", db);
				String userName = "afancy";
				String password = "Abcd1234";

				Class.forName(driverName);
				connection = DriverManager.getConnection(connectionString,
						userName, password);
			}
		} catch (ClassNotFoundException ex) {
			StackTracer.printStackTrace(ex);
		} catch (SQLException ex) {
			StackTracer.printStackTrace(ex);
		}
		return connection;
	}

	public final void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception ex) {
			}
		}
	}
}
