package com.ai.autohome.fileOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;;

public class CreateHiveFormatFiles {

	Logger logger = Logger.getLogger(getClass());
	
//	private HashSet<String> idSet = new HashSet<>();
	private  String outputFilePath ;
	private  String arOutputFilePath ;
	private  String flag;
	private  ArrayList<String> inputFielPath = new ArrayList<>();
	private int sum = 0;
	
	private static String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	private static String dayID = new SimpleDateFormat("yyyyMMdd").format(new Date());
	private static String state = "1";
	
	
	public static void main(String[] args){
		CreateHiveFormatFiles chf = new CreateHiveFormatFiles(args);
		
		System.out.println(chf.runAnalysis());
	}
	
	public CreateHiveFormatFiles(String[] args){
		this.flag = args[0];
		this.outputFilePath = args[1];
		
		if(args[0].equals("js")){
			this.arOutputFilePath=args[2];
			for(int i=3; i<args.length; i++){
				this.inputFielPath.add(args[i]);
			}
		}else{
			for(int i=2; i<args.length; i++){
				this.inputFielPath.add(args[i]);
			}
		}
	}
	
	
	public int runAnalysis(){
		try{
			if(flag.equals("ac")){
				sum = accountFileAnalysis(inputFielPath);
			}else if(flag.equals("ar")){
				sum = articleFileAnalysis(inputFielPath);
			}else if(flag.equals("js")){
				jsonFileAnalysis(inputFielPath);
			}
		}catch(IOException e){
			logger.error(e);
		}
		return sum;
	}
	
	/**
	 * 处理公众账号
	 * @param fp 公众账号文件名称数组
	 * @return
	 * @throws IOException
	 */
	public int accountFileAnalysis(ArrayList<String> fp) throws IOException{
		HashSet<String> accountIdSet = new HashSet<>();
		FileWriter fileWriter  = new FileWriter(outputFilePath);
		BufferedWriter bfWriter = new BufferedWriter(fileWriter);
		BufferedReader bfReader = null;
		try{
			for(String filePath: fp){
				bfReader = new BufferedReader(new FileReader(filePath));
				String tmpLine ;
				
				while((tmpLine=bfReader.readLine()) != null){
					try{
						String[] st = tmpLine.split("\t");
						if(st.length == 4){
							if(accountIdSet.add(st[0])){
								String ac=st[0]+"\t"+st[1]+"\t"+st[2]+state+"\t"+st[3]+"\t"+currentDate+"\t"+dayID;
								bfWriter.write(ac+"\n");
								accountIdSet.add(st[0]);
								sum++;
								logger.info("One new account is add: "+fp+tmpLine);
							}else{
								logger.info("Duplicate account is skiped: "+fp+tmpLine);
							}
						}else{
							logger.error("Bad input line from: "+fp+tmpLine);
						}
					}catch(Exception e){
						logger.error(fp+tmpLine);
						logger.error(e);
					}
				}
			}
		}finally{
			try{
				bfReader.close();
				bfWriter.flush();
				bfWriter.close();
			}catch(IOException ie){
				logger.error(ie);
			}
		}
		
		return sum;
	}
	
	
	/**
	 * 读取文章内容文件并处理
	 * @param fp 待处理输入文件名称数组
	 * @return
	 * @throws IOException
	 */
	public int articleFileAnalysis(ArrayList<String> fp) throws IOException{
		HashSet<String> articleIdSet = new HashSet<>();
		FileWriter fileWriter  = new FileWriter(outputFilePath);
		BufferedWriter bfWriter = new BufferedWriter(fileWriter);
		BufferedReader bfReader = null;
		try{
			for(String filePath: fp){
				bfReader = new BufferedReader(new FileReader(filePath));
				String tmpLine ;
				String url;
				
				while((tmpLine=bfReader.readLine()) != null){
					try{
						String[] st = tmpLine.split("\t");
						if(st.length == 4){
							url=st[3]; 
							String parms = st[3].split("s\\?")[1];
							
							String biz = "";
							String mid = "";
							String idx = "";
							String sn = "";
							
							for(String p: parms.split("&")){
								if(p.split("=")[0].contains("__biz")){
									biz = p.split("=")[1];
								}else if(p.split("=")[0].contains("mid")){
									mid = p.split("=")[1];
								}else if(p.split("=")[0].contains("idx")){
									idx = p.split("=")[1];
								}else if(p.split("=")[0].contains("sn")){
									sn = p.split("=")[1];
								}
							}
							
							String arId = mid+"_"+idx+"_"+sn;
							
							if(articleIdSet.add(arId)){
								String ar=arId+"\t"+biz+"\t"+st[1]+"\t"+st[3]+"\t"+state+"\t"+currentDate+"\t"+st[2]+"\t"+dayID;
								bfWriter.write(ar+"\n");
								sum++;
								logger.info("One new article is add: "+fp+url);
							}else{
								logger.info("Duplicate article is skiped: "+fp+url);
							}
						}else{
							logger.error("Bad input line from: "+fp+tmpLine);
						}
					}catch(Exception e){
						logger.error(fp+tmpLine);
						logger.error(e);
					}
					
				}
			}
		}finally{
			
			try{
				bfWriter.flush();
				bfWriter.close();
				
				bfReader.close();
			}catch(IOException ie){
				logger.error(ie);
			}

		}

		return sum;
	}
	
	public void jsonFileAnalysis(ArrayList<String> fp) throws IOException{
		
		BufferedReader bfReader;
		BufferedWriter acBfWriter;
		BufferedWriter arBfWriter;
	
		acBfWriter = new BufferedWriter(new FileWriter(outputFilePath));
		arBfWriter = new BufferedWriter(new FileWriter(arOutputFilePath));
			
		try{
			for(String filePath : fp){
				bfReader = new BufferedReader(new FileReader(filePath));
				String tmpLine;
				
				String url="";
				String acId="";
				String acName="";
				String wxName="";
				String function="";
				String title="";
				String content="";
				String sn="";
				try{
					while((tmpLine=bfReader.readLine()) != null){
						try{
							JSONObject jsObt = JSONObject.fromObject(tmpLine);
							url=jsObt.getString("网址URL");
							acId=jsObt.getString("公众号ID");
							acName=jsObt.getString("公众号名称");
							wxName=jsObt.getString("微信号");
							function=jsObt.getString("功能介绍");
							title=jsObt.getString("文章标题");
							content=jsObt.getString("文章内容");
							
							sn=url.split("__biz=")[1].split("&")[0];
							
							String account = acId+"\t"+acName+"\t"+wxName+"\t"+function;
							acBfWriter.write(account+"\n");
							
							String article = sn+"\t"+title+"\t"+content+"\t"+url;
							arBfWriter.write(article+"\n");
						}catch(Exception e){
							logger.error(e);
						}
					}
				}finally{
					try{
						bfReader.close();
					}catch(IOException ie){
						logger.error(ie);
					}
				}
				
			}
		}finally{
			try{
				acBfWriter.flush();
				acBfWriter.close();
				arBfWriter.flush();
				arBfWriter.close();
			}catch(IOException ie){
				logger.error(ie);
			}
		}
		
	}
}
