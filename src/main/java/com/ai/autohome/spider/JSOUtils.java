package com.ai.autohome.spider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kopitubruk.util.json.JSONParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;;

public class JSOUtils {

	
	public static void main(String[] args) throws Exception{
		String url="http://car.autohome.com.cn/config/series/3170.html";
		
		JsoupUtil jsp = new JsoupUtil();
		ArrayList<String> a=jsp.getCarSeriesSetting(url);
		
		System.out.println(JSOUtils.getJSText(a));
		
	}
	
//	public static String getJSText(ArrayList<String> lt){
	public static LinkedHashMap<String, Map<String, String>> getJSText(ArrayList<String> lt){
		//所有参数
		LinkedHashMap<String, Map<String, String>> parm = new LinkedHashMap<String, Map<String, String>>();
		
		LinkedHashMap<String, String> mp= new LinkedHashMap<String, String>();
		
		String s_config=lt.get(0).trim();
		String cfg=s_config.substring(0, s_config.length()-1);
		JSONObject jb_cfg = JSONObject.fromObject(cfg);
		//result
		String result=jb_cfg.getString("result");
		JSONObject jb_result=JSONObject.fromObject(result);
		//paramtypeitems
		String paramtypeitems=jb_result.getString("paramtypeitems");
		JSONArray ja_paramtypeitems = new JSONArray().fromObject(paramtypeitems);
		//基本参数
		String jbcs=ja_paramtypeitems.get(0).toString();
		String jbcs_paramitems=JSONObject.fromObject(jbcs).getString("paramitems");
		JSONArray ja_jbcs_paramitems = new JSONArray().fromObject(jbcs_paramitems);
		//基本参数-车型名称
		String cxmc=ja_jbcs_paramitems.get(0).toString();
		String cxmc_valueitems=JSONObject.fromObject(cxmc).get("valueitems").toString();
		JSONArray ja_cxmc_valueitmes = new JSONArray().fromObject(cxmc_valueitems);
		//基本参数-车型名称-所有车型ID
		for(int i=0; i<ja_cxmc_valueitmes.size(); i++ ){
			String s1=ja_cxmc_valueitmes.get(i).toString();
			JSONObject carID =JSONObject.fromObject(s1);
			parm.put(carID.get("specid").toString(), new LinkedHashMap<String, String>());
		}
		//configures
		for(int f1=0 ; f1<ja_paramtypeitems.size(); f1++){
			String jbcs_a=ja_paramtypeitems.get(f1).toString();
			String jbcs_paramitems_a=JSONObject.fromObject(jbcs_a).getString("paramitems");
			JSONArray ja_jbcs_paramitems_a = new JSONArray().fromObject(jbcs_paramitems_a);
			for(int f2=0; f2<ja_jbcs_paramitems_a.size(); f2++){
				String cs=ja_jbcs_paramitems_a.get(f2).toString();
				//参数名称
				String cs_name=JSONObject.fromObject(cs).get("name").toString();
				String cs_valueitems=JSONObject.fromObject(cs).get("valueitems").toString();
				
				JSONArray ja_cs_valueitems = new JSONArray().fromObject(cs_valueitems);
				for(int i=0; i<ja_cs_valueitems.size(); i++ ){
					String s1=ja_cs_valueitems.get(i).toString();
					JSONObject kv =JSONObject.fromObject(s1);
					String carId=kv.get("specid").toString();
					String value=kv.get("value").toString();
					
					mp= (LinkedHashMap<String, String>)parm.get(carId);

					mp.put(cs_name, value);
					parm.put(carId, mp);
				}
			}
		}
		//options
		String s_option = lt.get(1).trim();
		String opt = s_option.substring(0, s_option.length()-1);
		JSONObject jb_opt = JSONObject.fromObject(opt);
		String p_result=jb_opt.getString("result");
		
		JSONObject p_jb_result=JSONObject.fromObject(p_result);
		String p_configtypeitems=p_jb_result.getString("configtypeitems");
		JSONArray p_ja_configtypeitems = new JSONArray().fromObject(p_configtypeitems);
		
		for(int f1=0 ; f1<p_ja_configtypeitems.size(); f1++){
			String aqzb_a=p_ja_configtypeitems.get(f1).toString();
			String aqzb_configitems_a=JSONObject.fromObject(aqzb_a).getString("configitems");
			JSONArray ja_configitems_a = new JSONArray().fromObject(aqzb_configitems_a);
			for(int f2=0; f2<ja_configitems_a.size(); f2++){
				String cslb=ja_configitems_a.get(f2).toString();
				//参数名称
				String csmc=JSONObject.fromObject(cslb).get("name").toString();
				String cslb_v=JSONObject.fromObject(cslb).get("valueitems").toString();
				
				JSONArray ja_cslb_v = new JSONArray().fromObject(cslb_v);
				for(int i=0; i<ja_cslb_v.size(); i++ ){
					String s1=ja_cslb_v.get(i).toString();
					JSONObject kv =JSONObject.fromObject(s1);
					String carId=kv.get("specid").toString();
					String value=kv.get("value").toString();
					
					mp= (LinkedHashMap<String, String>)parm.get(carId);

					mp.put(csmc, value);
					parm.put(carId, mp);
				}
			}
		}
		
		//对内层Map进行JSON格式转换
		LinkedHashMap<String, String> jt=new LinkedHashMap<String, String>();
		
		Iterator iter = parm.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			String key=(String)entry.getKey();
			LinkedHashMap<String, String> val= (LinkedHashMap<String, String>)entry.getValue();
			jt.put(key, JSONObject.fromObject(val).toString());
		}
		
//		return JSONObject.fromObject(jt).toString();
		return parm;
	}
}
