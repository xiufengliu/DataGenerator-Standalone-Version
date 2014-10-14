package org.iss4e.datagen.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.iss4e.datagen.Matrix.Matrix;
import org.iss4e.datagen.common.Generator;
import org.iss4e.datagen.common.AbstractGenerator;
import org.iss4e.datagen.common.Arg;
import org.iss4e.datagen.common.Utils;
import org.iss4e.datagen.io.JdbcUtils;

public class ThreelineSmartMeterDataGenerator extends AbstractGenerator {
	static final String[] usage = new String[] { 
			"Usage:",
			"--startHouseholdID=n",
			"--nHousehold=1",
			"--startYear=1900",
			"--nYear=1",
            "--rowPerDatapoint=1",
            "--nHouseholdPerFile=-1",
			"--output=/tmp"
	};

	static final double[][] slopes = {
			{ (float) -0.0059, (float) 0.0155, (float) 0.2233 },
			{ (float) -0.0136, (float) -0.0049, (float) 0.0315 },
			{ (float) -0.0079, (float) 0.0112, (float) 0.1365 },
			{ (float) -0.0146, (float) 0.0034, (float) 0.0774 } };

	
	final Arg startHoseholdIDArg = new Arg(true, "startHouseholdID", "1");
	final Arg nHouseholdArg = new Arg(true, "nHousehold", "10");
	final Arg startYearArg = new Arg(true, "startYear", "1900");
	final Arg nYearArg = new Arg(true, "nYear", "1");
    final Arg rowPerDatapointArg = new Arg(true, "rowPerDatapoint", "1");
    final Arg nHouseholdPerFileArg = new Arg(true, "nHouseholdPerFile", "-1");
	final Arg outputDirArg = new Arg(true, "output", "/tmp");
	
	public ThreelineSmartMeterDataGenerator() {
		setUsage(usage);
		cmdLine.add(new Arg("help"));
		cmdLine.add(startHoseholdIDArg);
		cmdLine.add(nHouseholdArg);
		cmdLine.add(startYearArg);
		cmdLine.add(nYearArg);
        cmdLine.add(rowPerDatapointArg);
        cmdLine.add(nHouseholdPerFileArg);
		cmdLine.add(outputDirArg);
	}

    @Override
    public void generate(String[] params) {
        cmdLine.process(params);
        int rowPerDatapoint = Integer.parseInt(cmdLine.getArg(rowPerDatapointArg).getValue());
        if (rowPerDatapoint==1){
            generateRowPerPoint(params);
            //generateRowPerPoint_OneMeterPerFile(params);
        } else{
            generateRowPerHousehold(params);
        }
        String outputDir = cmdLine.getArg(outputDirArg).getValue();
       Utils.touch(String.format("%s/DONE", outputDir));
    }

    public void generateRowPerPoint_OneMeterPerFile(String[] params) {
        JdbcUtils dbUtils = new JdbcUtils();
        try {
            int startHouseholdID = Integer.parseInt(cmdLine.getArg(startHoseholdIDArg).getValue());
            int numOfhouseholds = Integer.parseInt(cmdLine.getArg(nHouseholdArg).getValue());
            int startYear = Integer.parseInt(cmdLine.getArg(startYearArg).getValue());
            int numOfDays = Integer.parseInt(cmdLine.getArg(nYearArg).getValue())*365;
            String outputDir = cmdLine.getArg(outputDirArg).getValue();

            Connection conn = dbUtils.openDBConn("essex");
            List<Matrix> weekdayClusters = getClustersSeed(conn, "");
            List<Matrix> weekendClusters = getClustersSeed(conn, "we");
            double[] wdRatios = getRatios(weekdayClusters);
            double[] wkRatios = getRatios(weekendClusters);
            Matrix temperatures = getTemperaturesSeed(conn);

            List<Integer> shuffledNums = Utils.getShuffledNumbers(numOfhouseholds);
            for (int i = 0; i < numOfhouseholds; ++i) {
                int hid = startHouseholdID + shuffledNums.get(i);
                BufferedWriter out = new BufferedWriter(new FileWriter(String.format("%s/%d.csv", outputDir, hid), true));
                for (int j = 0; j < numOfDays; ++j) {
                    double ratio = j * 1.0 / numOfDays;
                    double[] hourlyTemperatures = Utils
                            .getNormalDistributionArray(
                                    temperatures.getRow(j
                                            % temperatures.getRowDimension()),
                                    1.0);
                    double[] baseload;
                    double[] slope;
                    if (Utils.isWeekend(startYear, j + 1)) {
                        int idx = getIndex(wkRatios, ratio);
                        Matrix cluster = weekendClusters.get(idx);
                        baseload = cluster.getRow(Utils.getRandInt(cluster
                                .getRowDimension()));
                        slope = slopes[idx];
                    } else {
                        int idx = getIndex(wdRatios, ratio);
                        Matrix cluster = weekdayClusters.get(idx);
                        baseload = cluster.getRow(Utils.getRandInt(cluster
                                .getRowDimension()));
                        slope = slopes[idx];
                    }

                    double[] loadsByT = loadsByTemperature(hourlyTemperatures, slope);
                    for (int h = 0; h < baseload.length; ++h) {
                        double hourlyLoad = baseload[h] + loadsByT[h];
                        String hour = h<10?"0"+h:String.valueOf(h);
                        out.write(String.format("%d,%s %s:00:00,%f,%f\n", hid, Utils.getDate(startYear, j + 1), hour, hourlyLoad, hourlyTemperatures[h]));


                    }
                }
                out.close();
            }
        } catch (Exception e) {
            printUsage();
            e.printStackTrace();
        } finally {
            dbUtils.disconnect();
        }
    }


    public void generateRowPerPoint(String[] params) {
		JdbcUtils dbUtils = new JdbcUtils();
		try {
			int startHouseholdID = Integer.parseInt(cmdLine.getArg(startHoseholdIDArg).getValue());
			int numOfhouseholds = Integer.parseInt(cmdLine.getArg(nHouseholdArg).getValue());
			int startYear = Integer.parseInt(cmdLine.getArg(startYearArg).getValue());
			int numOfDays = Integer.parseInt(cmdLine.getArg(nYearArg).getValue())*365;
            int nHouseholdPerFile = Integer.parseInt(cmdLine.getArg(nHouseholdPerFileArg).getValue());
            if (nHouseholdPerFile==-1){
                nHouseholdPerFile = Integer.MAX_VALUE;
            }
			String outputDir = cmdLine.getArg(outputDirArg).getValue();
			
			Connection conn = dbUtils.openDBConn("essex");

			List<Matrix> weekdayClusters = getClustersSeed(conn, "");
			List<Matrix> weekendClusters = getClustersSeed(conn, "we");
			double[] wdRatios = getRatios(weekdayClusters);
			double[] wkRatios = getRatios(weekendClusters);
			Matrix temperatures = getTemperaturesSeed(conn);

			List<Integer> shuffledNums = Utils
					.getShuffledNumbers(numOfhouseholds);
            BufferedWriter out = null;
            for (int i = 0; i < numOfhouseholds; ++i) {
                int fileno = i/nHouseholdPerFile;
                if (i%nHouseholdPerFile==0) {
                    if(i>0){
                        out.close();
                    }
                    out = new BufferedWriter(new FileWriter(String.format("%s/data%d.csv", outputDir, fileno), true));
                }
				int hid = startHouseholdID + shuffledNums.get(i);
				for (int j = 0; j < numOfDays; ++j) {
					double ratio = j * 1.0 / numOfDays;
					double[] hourlyTemperatures = Utils
							.getNormalDistributionArray(
									temperatures.getRow(j
											% temperatures.getRowDimension()),
									1.0);
					double[] baseload;
					double[] slope;
					if (Utils.isWeekend(startYear, j + 1)) {
						int idx = getIndex(wkRatios, ratio);
						Matrix cluster = weekendClusters.get(idx);
						baseload = cluster.getRow(Utils.getRandInt(cluster
								.getRowDimension()));
						slope = slopes[idx];
					} else {
						int idx = getIndex(wdRatios, ratio);
						Matrix cluster = weekdayClusters.get(idx);
						baseload = cluster.getRow(Utils.getRandInt(cluster
								.getRowDimension()));
						slope = slopes[idx];
					}

					double[] loadsByT = loadsByTemperature(hourlyTemperatures,	slope);
					for (int h = 0; h < baseload.length; ++h) {
						double hourlyLoad = baseload[h] + loadsByT[h];
                        //String hour = h<10?"0"+h:String.valueOf(h);
						//out.write(String.format("%d,%s %s:00:00,%f,%f\n", hid, Utils.getDate(startYear, j + 1), hour, hourlyLoad, hourlyTemperatures[h]));
                        out.write(String.format("%d,%s,%d,%f,%f\n", hid, Utils.getDate(startYear, j + 1), h, hourlyLoad, hourlyTemperatures[h]));// For KDB
					}
				}
			}
            if (out!=null){
                out.close();
            }
		} catch (Exception e) {
			printUsage();
			e.printStackTrace();
		} finally {
			dbUtils.disconnect();
		}
	}

    //@Override
    public void generateRowPerHousehold(String[] params) {
        JdbcUtils dbUtils = new JdbcUtils();
        try {
            int startHouseholdID = Integer.parseInt(cmdLine.getArg(startHoseholdIDArg).getValue());
            int numOfhouseholds = Integer.parseInt(cmdLine.getArg(nHouseholdArg).getValue());
            int startYear = Integer.parseInt(cmdLine.getArg(startYearArg).getValue());
            int numOfDays = Integer.parseInt(cmdLine.getArg(nYearArg).getValue())*365;
            int nHouseholdPerFile = Integer.parseInt(cmdLine.getArg(nHouseholdPerFileArg).getValue());
            if (nHouseholdPerFile==-1){
                nHouseholdPerFile = Integer.MAX_VALUE;
            }
            String outputDir = cmdLine.getArg(outputDirArg).getValue();

            Connection conn = dbUtils.openDBConn("essex");

            List<Matrix> weekdayClusters = getClustersSeed(conn, "");
            List<Matrix> weekendClusters = getClustersSeed(conn, "we");
            double[] wdRatios = getRatios(weekdayClusters);
            double[] wkRatios = getRatios(weekendClusters);
            Matrix temperatures = getTemperaturesSeed(conn);

            List<Integer> shuffledNums = Utils.getShuffledNumbers(numOfhouseholds);
            StringBuffer tempBuf = new StringBuffer();
            StringBuffer readingBuf = new StringBuffer();
            BufferedWriter out = null;
            for (int i = 0; i < numOfhouseholds; ++i) {
                int fileno = i/nHouseholdPerFile;
                if (i%nHouseholdPerFile==0) {
                    if(i>0){
                        out.close();
                    }
                    out = new BufferedWriter(new FileWriter(String.format("%s/data%d.csv", outputDir, fileno), true));
                }
                tempBuf.setLength(0);;
                readingBuf.setLength(0);

                int hid = startHouseholdID + shuffledNums.get(i);

                for (int j = 0; j < numOfDays; ++j) {
                    double ratio = j * 1.0 / numOfDays;
                    double[] hourlyTemperatures = Utils
                            .getNormalDistributionArray(
                                    temperatures.getRow(j
                                            % temperatures.getRowDimension()),
                                    1.0);
                    double[] baseload;
                    double[] slope;
                    if (Utils.isWeekend(startYear, j + 1)) {
                        int idx = getIndex(wkRatios, ratio);
                        Matrix cluster = weekendClusters.get(idx);
                        baseload = cluster.getRow(Utils.getRandInt(cluster
                                .getRowDimension()));
                        slope = slopes[idx];
                    } else {
                        int idx = getIndex(wdRatios, ratio);
                        Matrix cluster = weekdayClusters.get(idx);
                        baseload = cluster.getRow(Utils.getRandInt(cluster
                                .getRowDimension()));
                        slope = slopes[idx];
                    }

                    double[] loadsByT = loadsByTemperature(hourlyTemperatures,	slope);
                    for (int h = 0; h < baseload.length; ++h) {
                        double hourlyLoad = baseload[h] + loadsByT[h];
                        String hour = h<10?"0"+h:String.valueOf(h);
                        tempBuf.append( hourlyTemperatures[h]).append(";");
                        readingBuf.append(hourlyLoad).append(";");
                    }
                }
                //Meterid, readings[]; temperatures[];
                String startTime = String.format("%s 01:00:00", Utils.getDate(startYear, 1));
                String tempStr = tempBuf.toString();
                String readingStr = readingBuf.toString();
                out.write(String.format("%d,%s,%s,%s\n", hid, startTime,readingStr.substring(0, readingStr.length() - 1) , tempStr.substring(0, tempStr.length() - 1)));
            }
            if (out!=null){
                out.close();
            }
        } catch (Exception e) {
            printUsage();
            e.printStackTrace();
        } finally {
            dbUtils.disconnect();
        }
    }



	private double[] loadsByTemperature(double[] temperatures, double[] slopes) {
		double[] loads = new double[temperatures.length];
		Random ran = new Random();
		double X0 = Utils.getMinValue(temperatures);
		double Y0 = (double) Math.abs(ran.nextGaussian() / 1.5);
		int X1 = ran.nextInt(6) + 10;
		int X2 = X1 + 4 + ran.nextInt(9);

		for (int i = 0; i < temperatures.length; ++i) {
			double X = temperatures[i];
			double Y = 0.0;
			if (X < X1) {
				Y = slopes[0] * (X - X0) + Y0; // Section1
			} else if (X < X2) {
				Y = slopes[1] * (X - X1) + (slopes[0] * (X1 - X0) + Y0); // Section2
			} else {
				Y = slopes[2] * (X - X2) + slopes[1] * (X2 - X1)
						+ (slopes[0] * (X1 - X0) + Y0); // Section3
			}
			loads[i] = Y > 0.2 ? 0.2 : Y; // The maximum load contributed by
											// temperature is not greater than
											// 0.2kW
		}
		return loads;
	}

	private int getIndex(double[] baseRatios, double ratio) {
		for (int i = 0; i < baseRatios.length; ++i) {
			if (ratio <= baseRatios[i]) {
				return i;
			}
		}
		return 0;
	}

	private double[] getRatios(List<Matrix> clusters) {
		int size = clusters.size();
		double[] ratios = new double[size];
		int totalNumOfRows = 0;
		for (Matrix matrix : clusters) {
			totalNumOfRows += matrix.getRowDimension();
		}
		for (int i = 0; i < clusters.size(); ++i) {
			double ratio = 1.0 * clusters.get(i).getRowDimension()
					/ totalNumOfRows;
			if (i == 0) {
				ratios[i] = ratio;
			} else if (i == clusters.size() - 1) {
				ratios[i] = 1.0;
			} else {
				ratios[i] = ratios[i - 1] + ratio;
			}
		}
		return ratios;
	}

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

	private Matrix getTemperaturesSeed(Connection conn) throws SQLException, ParseException {

		PreparedStatement pstmt = conn
				.prepareStatement("select count(1) from power_weather where readdate between ? and ?");
		pstmt.setDate(1, Utils.toSqlDate("2011-01-01"));
		pstmt.setDate(2, Utils.toSqlDate("2011-12-31"));
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
					.prepareStatement("select temperature from power_weather where readdate between ? and ?");
            pstmt.setDate(1, Utils.toSqlDate("2011-01-01"));
            pstmt.setDate(2, Utils.toSqlDate("2011-12-31"));
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

	public static void main(String[] args) {
		Generator generator = new ThreelineSmartMeterDataGenerator();
		generator.generate(args);
	}
}
