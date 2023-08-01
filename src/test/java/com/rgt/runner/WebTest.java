package com.rgt.runner;

import java.io.IOException;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;
import com.rgt.engine.TestDriver;


public class WebTest
{
	@Parameters("tc_master")
	@Test
	//public void runner(String tc_master) throws IOException, InterruptedException, DocumentException
	public void runner() throws IOException, InterruptedException, DocumentException
	{
		TestDriver td= new TestDriver();
		//td.startExecution(tc_master);
		td.startExecution();
	}
}