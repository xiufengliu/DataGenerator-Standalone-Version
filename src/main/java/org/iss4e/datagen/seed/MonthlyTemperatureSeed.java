package org.iss4e.datagen.seed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.iss4e.datagen.Matrix.Matrix;

public class MonthlyTemperatureSeed implements Seed {
	
	
	private Matrix getTemperaturesSeed(Connection conn) throws SQLException {
		PreparedStatement pstmt = conn
				.prepareStatement("select count(1) from weather where date between ? and ?");
		pstmt.setString(1, "2011-01-01");
		pstmt.setString(2, "2011-12-31");
		ResultSet rs = pstmt.executeQuery();
		int m = 0;
		if (rs.next()) {
			m = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		if (m > 0) {
			int numOfDays = m / 24;
			Matrix matrix = new Matrix(numOfDays, 24);
			pstmt = conn
					.prepareStatement("select temperature from weather where date between ? and ?");
			pstmt.setString(1, "2011-01-01");
			pstmt.setString(2, "2011-12-31");
			rs = pstmt.executeQuery();
			for (int i = 0; i < numOfDays; ++i) {
				for (int j = 0; j < 24; j++) {
					if (rs.next())
						matrix.set(i, j, rs.getDouble(1));
				}
			}
			rs.close();
			pstmt.close();
			return matrix;
		}
		return null;
	}

}
