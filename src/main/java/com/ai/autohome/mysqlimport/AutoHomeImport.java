package com.ai.autohome.mysqlimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class AutoHomeImport {

	public static void main(String[] args) throws FileNotFoundException{
		  
		AutoHomeImport im=new AutoHomeImport();
		String inputFilePath="E:\\auto_home_car_info.txt";
		String outputFilePath="E:\\mysql_insert_sqls.txt";
		
		im.getSqlString(inputFilePath, outputFilePath);
		  
	}  
	
	/**
	 * 
	 * @param filePath 
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ArrayList<String> getSqlString(String filePath, String outFilePath) throws FileNotFoundException{
		
//		FileOutputStream out=new FileOutputStream("E:\\mysql_insert_sqls.txt");
		FileOutputStream out=new FileOutputStream(outFilePath);
        PrintStream p=new PrintStream(out);
		
		File file = new File(filePath);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int i=0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	if(!tempString.equals("")){
            		//get sql string
            		i++;
//            		System.out.println("line"+i+":-->"+AutoHomeImport.sqlConstructor(tempString));
//            		System.out.println("line"+i+":-->"+AutoHomeImport.getCarDetailsInsertSql("000000",tempString));
//            		System.out.println("line"+i+":-->"+AutoHomeImport.sqlConstructor(tempString));
            		AutoHomeImport.mysqlInsert(AutoHomeImport.sqlConstructor(tempString), p);
            	}

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            p.close();
        }
		
		return new ArrayList<String>();
	}
	
	
	/**
	 * 
	 * @param 一个车系的全部信息，JSON格式
	 * @return inster sqls [车系信息，车型1信息，车型2信息，...]
	 */
	public static ArrayList<String> sqlConstructor(String st){
		
		ArrayList<String> sqls=new ArrayList<String>();
		
		JSONObject cx_jb = JSONObject.fromObject(st);
		
		String id=cx_jb.getString("车系ID").trim();
		String name=cx_jb.getString("车系名称").trim();
		String cx_url=cx_jb.getString("车系URL").trim();
		String loc=cx_jb.getString("当前位置").trim();
		String price=cx_jb.getString("新车指导价").trim();
		String score=cx_jb.getString("评分").trim();
		
		String cs_url;
		if(cx_jb.getString("参数配置URL").equals("")){
			cs_url="无参数配置信息";
		}else{
			cs_url=cx_jb.getString("参数配置URL").trim();
		}
		
		String cspz=cx_jb.optString("参数配置", null);
		
		//dmp_category.dim_car_series_info
		String cx_sql = "insert into " + "dmp_category.dim_car_series_info "
				+ "values("
				+ "'" + id + "', "
				+ "'" + name + "', "
				+ "'" + cx_url + "', "
				+ "'" + loc + "', "
				+ "'" + price + "', "
				+ "'" + score + "') ";
//				+ "'" + cs_url + "') ";
//		sqls.add(cx_sql);
		
		//dmp_category.dim_car_details_info
		ArrayList<String> carDetailInsertSql = AutoHomeImport.getCarDetailsInsertSql(id, cspz);
		sqls.addAll(carDetailInsertSql);
		
		return sqls;
	}
	
	
	/**
	 * 
	 * @param CSID 车系ID
	 * @param st 所有车型的参数配置
	 * @return
	 */
	public static ArrayList<String> getCarDetailsInsertSql(String CSID,String st){
		JSONObject job=JSONObject.fromObject(st);
		
		ArrayList<String> insertCarDetailsSqlList = new ArrayList<String>();
		
//		String js=job.optString("参数配置", null);
		
		String js=st;
		
		if(js != null){
			JSONObject allCarTypes=JSONObject.fromObject(js);
			//get all car ids 
			JSONArray carIds = allCarTypes.names();
			for(int i=0; i<carIds.size() ;i++){
				String carSerieId = CSID;
				String carId=carIds.getString(i);
//				lt.add(allCarTypes.getJSONObject(carId).getString("车型名称"));
				String carName = allCarTypes.getJSONObject(carId).optString("车型名称", null);
				String carPrice = allCarTypes.getJSONObject(carId).optString("厂商指导价(元)", null);
				String carCompany = allCarTypes.getJSONObject(carId).optString("厂商", null);
				String carType = allCarTypes.getJSONObject(carId).optString("级别", null);
				String carMachine = allCarTypes.getJSONObject(carId).optString("发动机", null);
				String carStructure = allCarTypes.getJSONObject(carId).optString("车身结构", null);
				String carFuel = allCarTypes.getJSONObject(carId).optString("工信部综合油耗(L/100km)", null);
				String carIntake = allCarTypes.getJSONObject(carId).optString("进气形式", null);
				String carTrans = allCarTypes.getJSONObject(carId).optString("变速箱类型", null);
				
				String carDetailInsertSql="insert into " + "dmp_category.dim_car_details_info "
						+ "values("
						+ "'" + carSerieId + "', "
						+ "'" + carId + "', "
						+ "'" + carName + "', "
						+ "'" + carPrice + "', "
						+ "'" + carCompany + "', "
						+ "'" + carType + "', "
						+ "'" + carMachine + "', "
						+ "'" + carStructure + "', "
						+ "'" + carFuel + "', "
						+ "'" + carIntake + "', "
						+ "'" + carTrans + "') ";;
				
				insertCarDetailsSqlList.add(carDetailInsertSql);
				
			}
		}
		
		return insertCarDetailsSqlList;
	}
	
	public static void mysqlInsert(ArrayList<String> insertSqls, PrintStream fp){
		//驱动程序名//不固定，根据驱动
		  String driver = "com.mysql.jdbc.Driver";
		  // URL指向要访问的数据库名******
		  String url = "jdbc:mysql://10.1.3.4:3306/dmp_category";
		  // MySQL配置时的用户名
		  String user = "dmp_category";
		  // Java连接MySQL配置时的密码******
		  String password = "jK7Acgrg";
		  
		  try {
			  // 加载驱动程序
			  Class.forName(driver);
			  
			  // 连续数据库
			  Connection conn = DriverManager.getConnection(url, user, password);
			  if(!conn.isClosed())
			   System.out.println("Succeeded connecting to the Database!");
			  
			  // statement用来执行SQL语句
			  Statement statement = conn.createStatement();
	
			  // 要执行的SQL语句id和content是表review中的项。
			  ArrayList<String> sqlArr = new ArrayList<String>();
			  sqlArr=insertSqls;
//			  String sql = "select * from dmp_category.delete_1108 limit 3";
//			  sqlArr.add(sql);
			  
			  /*String sql = insertSql;
			  ResultSet rs = statement.executeQuery(sql);  
			  
			  //输出id值和content值
			  while(rs.next()) {
				  System.out.println(rs.getInt(1) + "\t" + rs.getString(3)); 
			  } 
			  rs.close(); */
			  
			  for(String sql:sqlArr){
				  statement.executeUpdate(sql);
				  System.out.println(sql);
				  fp.println(sql);
			  }
			  
			  conn.close();  
			  } catch(ClassNotFoundException e) {  
			    System.out.println("Sorry,can`t find the Driver!");  
			   		e.printStackTrace();  
			  	} catch(SQLException e) {
			  		e.printStackTrace();
			    } catch(Exception e){
			    	e.printStackTrace();
			    }
	}
	
}
