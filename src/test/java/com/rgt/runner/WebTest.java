package com.rgt.runner;

import java.io.IOException;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lowagie.text.DocumentException;
import com.rgt.engine.TestDriver;


public class WebTest
{

	@Test
	public void runner() throws IOException, InterruptedException, DocumentException
	{
		TestDriver td= new TestDriver();
		td.startExecution();
	}
}

