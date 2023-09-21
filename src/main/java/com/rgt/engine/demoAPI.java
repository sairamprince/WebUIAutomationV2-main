package com.rgt.engine;

public class demoAPI 
{
	
	 public static void main(String args[])
	 {
		 APIDriver apidriver = new APIDriver();
		String response =  apidriver.getRequest("https://jsonplaceholder.typicode.com/posts");
		System.out.println(response);
		apidriver.postRequest();
	 }

}
