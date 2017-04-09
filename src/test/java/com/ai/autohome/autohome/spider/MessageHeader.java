package com.ai.autohome.autohome.spider;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;

public class MessageHeader {

	
	public static void main(String[] args){
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
			response.addHeader("Set-Cookie", 
			    "c1=a; path=/; domain=localhost");
			response.addHeader("Set-Cookie", 
			    "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
			Header[] h1 = response.getAllHeaders();
			System.out.println(h1[0]);
			/*
			HeaderElementIterator it = new BasicHeaderElementIterator(
			    response.headerIterator("Set-Cookie"));

			while (it.hasNext()) {
			    HeaderElement elem = it.nextElement(); 
			    System.out.println(elem.getName() + " = " + elem.getValue());
			    NameValuePair[] params = elem.getParameters();
			    for (int i = 0; i < params.length; i++) {
			        System.out.println(" " + params[i]);
			    }
			}
			*/
	}
}
