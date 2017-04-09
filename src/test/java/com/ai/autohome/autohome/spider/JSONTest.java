package com.ai.autohome.autohome.spider;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class JSONTest {

	public static void main(String[] args){
		JSONObject jsobj = JSONObject.fromObject("{'name':'xiazdong','age':20}");
		JSONObject subJsObj = JSONObject.fromObject("{'book':{'title':'book2','price':'$22'}}");
		/*
		jsobj.put("book", subJsObj);
		
		System.out.println(jsobj.names());
		
		System.out.println(subJsObj.toString());
		
		System.out.println(subJsObj.names());
		
		System.out.println(jsobj.toString());
		*/
//		System.out.println(subJsObj.toString());
//		System.out.println(JSONUtils.stripQuotes(subJsObj.toString()));
		System.out.println(JSONUtils.stripQuotes("{'name':'xiazdong','age':20}"));
		
		HashMap<String, ArrayList<String>> map_js = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> bs= new ArrayList<String>();
		bs.add("book1");
		bs.add("book2");
		
		map_js.put("Books", bs);
		
		JSONObject map_jsobj = JSONObject.fromObject(map_js);
//		System.out.println(map_jsobj.toString());
//		
//		System.err.println(map_jsobj.getJSONArray("Books").isArray());
		
	}
}
