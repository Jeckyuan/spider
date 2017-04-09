package com.ai.autohome.autohome.spider;

import java.io.BufferedWriter;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import weibo.HttpClientWeibo;

public class Test {

	public static void main(String[] args) throws IOException{
//		String wb_url="http://weibo.com/622007913";
//		String wb_url="http://weibo.com/622007913?is_hot=1";
		
		String[] urls={"http://weibo.com/﻿1000179950/info", 
				"http://weibo.com/1000183485/info", 
				"http://weibo.com/1000192902/info"};
		Random ra =  new Random();
		
		for (int i=0; i<30; i++){
			System.out.println(ra.nextInt(3));
		}

		
	}
	
}
