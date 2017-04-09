package com.ai.autohome.hivejdbc;

import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import java.sql.DriverManager;
 
public class HiveJdbcClient {
  private static String driverName = "org.apache.hive.jdbc.HiveDriver";
  
  private static Logger logger = Logger.getLogger(HiveJdbcClient.class);
  
//  private GregorianCalendar calender = new GregorianCalendar();
		 
  /*static{
	  GregorianCalendar calender = new GregorianCalendar();
	  calender.add(Calendar.DAY_OF_MONTH, -1);
	  String dtYesterdday = new SimpleDateFormat("yyyyMMdd").format(calender.getTime());
  }*/
  
  private static String hiveInsertSqlP1="INSERT OVERWRITE TABLE yuanjk.wexin_official_account_url_overwrite_daily_tmp "
  		+ "SELECT regexp_replace(value, 'amp\\\\;', '') value, value_type_id, day_id  FROM dmp_online.dmp_uc_tags_m_bh  "
  		+ "WHERE value_type_id='30' AND value RLIKE 'http://mp\\\\.weixin\\\\.qq\\\\.com/s\\\\?__biz.*?sn=.*' AND day_id=";
  
  private static String hiveCleanSql="INSERT OVERWRITE TABLE yuanjk.wexin_official_account_url_overwrite_daily "
  		+ "SELECT concat_ws('_', parse_url(d.value, 'QUERY', 'mid'), parse_url(d.value, 'QUERY', 'idx'), parse_url(d.value, 'QUERY', 'sn')) ar_id, b.id, d.value "
  		+ "FROM yuanjk.wexin_official_account_url_overwrite_daily d LEFT OUTER JOIN dmp_console.dim_data_pubnum_article_base b "
  		+ "ON concat_ws('_', parse_url(d.value, 'QUERY', 'mid'), parse_url(d.value, 'QUERY', 'idx'), parse_url(d.value, 'QUERY', 'sn'))=b.id "
  		+ "WHERE b.id IS NULL";
  
  private static String hiveExportSql="SELECT d.ar_id, d.value FROM yuanjk.wexin_official_account_url_overwrite_daily d";

//  private static String rootWorkDirectory = "/data1/user/yuanjk/weixin_automatic_execution_daily";
  private static String rootWorkDirectory = "E:\\Documents\\temp\\hive_export";

  
//  private static String dtNow = new SimpleDateFormat("yyyyMMdd").format(new Date());
  
  /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
	GregorianCalendar calender = new GregorianCalendar();
    calender.add(Calendar.DAY_OF_MONTH, -1);
    String dtYesterdday = new SimpleDateFormat("yyyyMMdd").format(calender.getTime());
    System.out.println(dtYesterdday);
    
    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      logger.error(e);
      System.exit(1);
    }
    //replace "hive" here with the name of the user the queries should run as
    Connection con = DriverManager.getConnection("jdbc:hive2://117.121.97.2:10000/yuanjk", "yuanjk", "7REYo1jF");
    Statement stmt = con.createStatement();
    
    //insert available urls to yuanjk.wexin_official_account_url_overwrite_daily_tmp table
    String hiveInsertSql=hiveInsertSqlP1+dtYesterdday+" LIMIT 40";
    System.out.println("Running: " + hiveInsertSql);
    stmt.execute(hiveInsertSql);
    
    //filter out crawled urls
//    System.out.println("Running: " + hiveCleanSql);
//    stmt.execute(hiveCleanSql);
    /*
    //export available urls to local file
    System.out.println("Running: " + hiveExportSql);
    ResultSet res = stmt.executeQuery(hiveExportSql);
    try{
    	FileWriter fileWriter = new FileWriter(rootWorkDirectory);
    	BufferedWriter writer = new BufferedWriter(fileWriter);
    	try{
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
                writer.write(res.getString(1) + "\t" + res.getString(2));
              }
    	}finally{
    		writer.flush();
    		writer.close();
    	}
    }catch(IOException e){
    	logger.error(e);
    }
    */
//    String tableName = "yuanjk.wexin_official_account_url_overwrite_daily";
//    stmt.execute("drop table if exists " + tableName);
//    stmt.execute("create table " + tableName + " (key int, value string)");
    

    // show tables
//    String sql = "show tables '" + tableName + "'";
    
    /*
    System.out.println("Running: " + sql);
    ResultSet res = stmt.executeQuery(sql);
    if (res.next()) {
      System.out.println(res.getString(1));
    }
    */
    /*
       // describe table
    sql = "describe " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1) + "\t" + res.getString(2));
    }
    */
    /*
    // load data into table
    // NOTE: filepath has to be local to the hive server
    // NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
    String filepath = "/tmp/a.txt";
    sql = "load data local inpath '" + filepath + "' into table " + tableName;
    System.out.println("Running: " + sql);
    stmt.execute(sql);
 
    // select * query
    sql = "select * from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
    }
 
    // regular hive query
    sql = "select count(1) from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1));
    }
    */
  }
}