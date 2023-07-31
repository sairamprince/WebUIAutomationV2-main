package com.rgt.utils;

public class TCMaster 
{
	private String TC_ID;
	private String ToBeExecuted;
	private String Priority;
	private String TestCase;


	public String getTC_ID() {
		return TC_ID;
	}
	public void setTC_ID(String tc_ID){
		TC_ID = tc_ID;
	}


	public String getToBeExecuted() {
		return ToBeExecuted;
	}
	public void setToBeExecuted(String toBeExecuted) {
		ToBeExecuted = toBeExecuted;
	}

	public String getTestCase() {
		return TestCase;
	}

	public void setTestCase(String testCase) {
		TestCase = testCase;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

}
