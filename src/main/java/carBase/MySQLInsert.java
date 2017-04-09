package carBase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MySQLInsert {
	private static Logger logger = Logger.getLogger(MySQLInsert.class);

	private Connection conn = null;
	private int totalUpdateRows = 0;
	// private String dbConn =
	// "jdbc:mysql://10.1.3.4:3306/dmpap?user=dmpap&password=e2sUvVQz";
	private String dbConn = "jdbc:mysql://localhost:3306/test?user=yuanjk&password=password";

	/**
	 * 获取MySQL连接
	 *
	 * @return
	 */
	public void getConn() {
		try {
			logger.info(dbConn);
			// conn =
			// DriverManager.getConnection("jdbc:mysql://10.1.3.4:3306/dmpap?user=dmpap&password=e2sUvVQz");
			conn = DriverManager.getConnection(dbConn);
			if (conn != null) {
				System.out.println("数据库已连接");
			} else {
				System.out.println("数据未已连接");
			}

		} catch (SQLException ex) {
			// handle any errors
			logger.error("数据库连接失败");
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
	}

	/**
	 * 关闭 数据库连接
	 */
	public void closeConn() {
		try {
			this.conn.close();
			logger.info(totalUpdateRows + " rows are updated.");
		} catch (SQLException e) {
			logger.error("数据库关闭失败");
			logger.error(e);
		}
	}

	/**
	 * 向MySQL数据库插入爬取的车系信息
	 *
	 * @param csInfo
	 * @return
	 */
	public int insertCarSeriesInfo(String tableName, String csInfo) {
		int rs = 0;
		String sql = "INSERT INTO " + tableName + " (web_site, id, name, url, current_location, price, score ) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?)";

		String[] csInfoList = csInfo.split("\t", -1);
		if (csInfoList.length == 8 && csInfoList[1] != "") {
			String webSite = csInfoList[0];
			String id = csInfoList[1];
			String name = csInfoList[2];

			String url = csInfoList[4];
			String currentLocation = csInfoList[3];
			String price = csInfoList[5];
			// String price = csInfoList[6];

			String score = csInfoList[7];

			int count = queryCarSeriesInfo(webSite, id);
			if (count == 0) {
				try {
					if (conn != null) {
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, webSite);
						pstmt.setString(2, id);
						pstmt.setString(3, name);

						pstmt.setString(4, url);
						pstmt.setString(5, currentLocation);
						pstmt.setString(6, price);

						pstmt.setString(7, score);

						rs = pstmt.executeUpdate();
						totalUpdateRows += rs;
						pstmt.close();
					} else {
						logger.error("请初始化数据库连接");
					}
				} catch (Exception e) {
					logger.error("数据插入失败：" + csInfo);
					logger.error(e);
				}
			} else {
				logger.info("车系信息已存在：" + webSite + "," + id);
			}

		} else {
			logger.error("MySQL Insert输入错误：" + csInfo);
		}
		return rs;
	}

	/**
	 * 向MySQL数据库插入爬取的车型信息
	 *
	 * @param carInfo
	 * @return
	 */
	public int insertCarInfo(String tableName, String carInfo) {
		int rs = 0;
		String sql = "INSERT INTO " + tableName
				+ " (web_site, cs_id, car_id, name, company, price, car_type, machine, transmission_type, car_structure) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String[] csInfoList = carInfo.split("\t", -1);
		if (csInfoList.length == 10 && csInfoList[1] != "") {
			String webSite = csInfoList[0];
			String csId = csInfoList[1];
			String carId = csInfoList[2];

			String naem = csInfoList[3];
			String company = csInfoList[4];
			String pricce = csInfoList[5];

			String carType = csInfoList[6];
			String machine = csInfoList[7];
			String transmissionType = csInfoList[8];

			String carStructure = csInfoList[9];

			int count = queryCarInfo(webSite, csId, carId);
			if (count == 0) {
				try {
					if (conn != null) {
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, webSite);
						pstmt.setString(2, csId);
						pstmt.setString(3, carId);

						pstmt.setString(4, naem);
						pstmt.setString(5, company);
						pstmt.setString(6, pricce);

						pstmt.setString(7, carType);
						pstmt.setString(8, machine);
						pstmt.setString(9, transmissionType);

						pstmt.setString(10, carStructure);

						rs = pstmt.executeUpdate();
						totalUpdateRows += rs;
						pstmt.close();
					} else {
						logger.error("请初始化数据库连接");
					}
				} catch (Exception e) {
					logger.error("数据插入失败：" + carInfo);
					logger.error(e);
				}
			} else {
				logger.info("车型信息已存在：" + webSite + "," + csId + "," + carId);
			}

		} else {
			logger.error("MySQL Insert输入错误：" + carInfo);
		}
		return rs;
	}

	/**
	 * 查询车系信息是否存在
	 *
	 * @param webSite
	 * @param csId
	 * @return
	 */
	public int queryCarSeriesInfo(String webSite, String csId) {
		int count = 0;
		String sql = "SELECT web_site, id FROM dim_car_series_info " + "WHERE web_site=? AND id=?";
		try {
			if (conn != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, webSite);
				pstmt.setString(2, csId);

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					count++;
				}
				pstmt.close();
			} else {
				logger.error("请初始化数据库连接");
			}
		} catch (Exception e) {
			logger.error("数据查询失败：" + webSite + "," + csId);
			logger.error(e);
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 查询车型信息是否存在
	 *
	 * @param webSite
	 * @param csId
	 * @param carId
	 * @return
	 */
	public int queryCarInfo(String webSite, String csId, String carId) {
		int count = 0;
		String sql = "SELECT web_site, cs_id, car_id FROM dim_car_details_info "
				+ "WHERE web_site=? AND cs_id=? AND car_id=?";
		try {
			if (conn != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, webSite);
				pstmt.setString(2, csId);
				pstmt.setString(3, carId);

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					count++;
				}
				pstmt.close();
			} else {
				logger.error("请初始化数据库连接");
			}
		} catch (Exception e) {
			logger.error("数据查询失败：" + webSite + "," + csId + "," + carId);
			logger.error(e);
		}
		return count;
	}

	public static void main(String[] args) {
		String[] csInfoFileList = { "E:\\work\\汽车之家\\tmp\\autohome\\autohome_csinfo.txt",
				"E:\\work\\汽车之家\\tmp\\bitauto\\bitauto_csinfo.txt", "E:\\work\\汽车之家\\tmp\\xcar\\csinfo.txt" };
		String[] carInfoFileList = { "E:\\work\\汽车之家\\tmp\\autohome\\autohome_carinfo.txt",
				"E:\\work\\汽车之家\\tmp\\xcar\\carinfo.txt", "E:\\work\\汽车之家\\tmp\\bitauto\\bitauto_carinfo.txt" };
		MySQLInsert im = new MySQLInsert();
		im.getConn();
		if (im.conn != null) {
			// 插入车系信息
			for (String filePath : csInfoFileList) {
				try {
					BufferedReader breader = new BufferedReader(new FileReader(filePath));
					String tmpLine = null;
					while ((tmpLine = breader.readLine()) != null) {
						im.insertCarSeriesInfo("dim_car_series_info", tmpLine);
					}
					breader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 插入车型信息
			for (String filePath : carInfoFileList) {
				try {
					BufferedReader breader = new BufferedReader(new FileReader(filePath));
					String tmpLine = null;
					while ((tmpLine = breader.readLine()) != null) {
						im.insertCarInfo("dim_car_details_info", tmpLine);
					}
					breader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.error("请建立数据库链接");
		}
		if (im.conn != null) {
			im.closeConn();
		}

	}

}
