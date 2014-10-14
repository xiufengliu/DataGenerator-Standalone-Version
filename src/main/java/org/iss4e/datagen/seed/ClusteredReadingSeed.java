package org.iss4e.datagen.seed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.iss4e.datagen.Matrix.Matrix;

public class ClusteredReadingSeed implements Seed {
	
	private List<Matrix> getClustersSeed(Connection conn, String suffix)
			throws SQLException {
		List<Matrix> clusters = new ArrayList<Matrix>();
		for (int c = 1; c <= 4; c++) {
			PreparedStatement pstmt = conn
					.prepareStatement("select count(1) from class" + c + suffix);
			ResultSet rs = pstmt.executeQuery();
			int m = 0, n = 24;
			if (rs.next()) {
				m = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			if (m > 0) {
				Matrix matrix = new Matrix(m, n);
				pstmt = conn.prepareStatement("select * from class" + c
						+ suffix);
				rs = pstmt.executeQuery();
				int i = 0;
				while (rs.next()) {
					for (int j = 0; j < 24; j++) {
						matrix.set(i, j, rs.getDouble(j + 2));
					}
					++i;
				}
				clusters.add(matrix);
				rs.close();
				pstmt.close();
			}
		}
		return clusters;
	}
}
