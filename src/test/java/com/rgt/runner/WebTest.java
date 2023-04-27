package com.rgt.runner;

import java.io.IOException;

import org.testng.annotations.Test;

import com.rgt.engine.TestDriver;



public class WebTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		TestDriver td= new TestDriver();
		td.startExecution();

	}

}
