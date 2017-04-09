package dmpApp.appBasicInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class AppInfoMySQLInsert {
	private static Logger logger = Logger.getLogger(AppInfoMySQLInsert.class);

	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private int totalUpdateRows = 0;

	/**
	 * 获取MySQL连接
	 * @return
	 */
	public void getConn(){
		try {
			conn = DriverManager.getConnection("jdbc:mysql://10.1.3.4:3306/dmpap?" +
					"user=dmpap&password=e2sUvVQz");
		} catch (SQLException ex) {
			// handle any errors
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
	}

	/**
	 * 关闭 数据库连接
	 */
	public void closeConn(){
		try {
			this.conn.close();
			this.pstmt.close();
			logger.info( totalUpdateRows+" rows are updated." );
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	/**
	 * 向MySQL数据库插入爬取的app信息
	 * @param appInfo
	 * @return
	 */
	public int insertAppInfo(String appInfo){
		int rs = 0;
		String sql = "INSERT INTO dmpap.dim_data_app_info (package_id, app_name, version_name"
				+ ", rating, market_name, category_name, download_count, package_size, package_md5"
				+ ", download_url, update_date, os, os_version, developer, crawling_time, description ) "
				+ " VALUES(?, ?, ?, "
				+ "?, ?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?, ?, ?, ?)";

		String[]  appInfoList = appInfo.split("\t");
		if(appInfoList.length == 16 && appInfoList[1] != ""){
			String pkId = appInfoList[0];
			String appName = appInfoList[1];
			String versionName = appInfoList[2];

			String rating = appInfoList[3];
			String marketName = appInfoList[4];
			String categoryName = appInfoList[5];

			String downCount = appInfoList[6];
			String pkSize = appInfoList[7];
			String pkMd5 = appInfoList[8];

			String downUrl = appInfoList[9];
			String updateDate = appInfoList[10];
			String os = appInfoList[11];

			String osVersion = appInfoList[12];
			String developer = appInfoList[13];
			String crawlingTime = appInfoList[14];;
			String desc = appInfoList[15];

			try {
				if(!conn.equals(null)){
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, pkId);
					pstmt.setString(2, appName);
					pstmt.setString(3, versionName);

					pstmt.setString(4, rating);
					pstmt.setString(5, marketName);
					pstmt.setString(6, categoryName);

					pstmt.setString(7, downCount);
					pstmt.setString(8, pkSize);
					pstmt.setString(9, pkMd5);

					pstmt.setString(10, downUrl);
					pstmt.setString(11, updateDate);
					pstmt.setString(12, os);

					pstmt.setString(13, osVersion);
					pstmt.setString(14, developer);
					pstmt.setString(15, crawlingTime);

					pstmt.setString(16, desc);
					rs = pstmt.executeUpdate();
					totalUpdateRows += rs;
				}else{
					logger.error("请初始化数据库连接");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			logger.error("输入错误："+appInfo);
		}
		return rs;
	}


	public static void main(String[] args){
		String filePath = "E:\\work\\dmp_app\\applicationa_info\\mobile_application_info20170210141717";
		AppInfoMySQLInsert im = new AppInfoMySQLInsert();
		try{
			BufferedReader breader = new BufferedReader(new FileReader(filePath));
			String tmpLine = null;
			while((tmpLine = breader.readLine()) != null){
				im.insertAppInfo(tmpLine);
			}
			breader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
