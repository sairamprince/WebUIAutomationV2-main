package com.rgt.utils;

import java.util.ArrayList; 
import java.util.List;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

public class ExcelUtils 
{
	protected Fillo fillo;
	protected Connection connection;

	public ExcelUtils(String excelpath) {
		try { 
			fillo = new Fillo();
			connection = fillo.getConnection(excelpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<TestData> getTestSteps(String TC_ID){
		List<TestData> listTestCaseDetails = new ArrayList<TestData>();
		try {
			String selectQuery = "select * from TC_Steps where TC_ID='"+TC_ID+"' and ToBeExecuted='Y'";
			Recordset recordset = connection.executeQuery(selectQuery);

			while(recordset.next()) {
				TestData data = new TestData();
				data.setTC_ID(recordset.getField("TC_ID"));
				data.setSteps(recordset.getField("Steps"));
				data.setTestCase(recordset.getField("TestCase"));
				data.setTestSteps(recordset.getField("TestSteps"));
				data.setToBeExecuted(recordset.getField("ToBeExecuted"));
				data.setLocatorType(recordset.getField("LocatorType"));
				data.setLocatorValue(recordset.getField("LocatorValue"));
				data.setAction(recordset.getField("Action"));
				data.setInputValue(recordset.getField("InputValue"));
				listTestCaseDetails.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listTestCaseDetails;
	}
	public List<TCMaster> getTCMaster(){
		List<TCMaster> listTestCases = new ArrayList<TCMaster>();
		try {
			String selectQuery = "select * from TC_Master where ToBeExecuted='Y'";
			Recordset recordset = connection.executeQuery(selectQuery);

			while(recordset.next()) {

				TCMaster data = new TCMaster();
				data.setTC_ID(recordset.getField("TC_ID"));
				data.setTestCase(recordset.getField("TestCase"));
				data.setToBeExecuted(recordset.getField("ToBeExecuted"));
				listTestCases.add(data);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return listTestCases;
	}



}
