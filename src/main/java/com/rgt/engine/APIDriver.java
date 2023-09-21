package com.rgt.engine;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIDriver 
{
	static StringBuilder responseContent = new StringBuilder();
	
	public static String getRequest(String URL)
	{
		try {
			// Define the API endpoint URL
			String apiUrl = "https://jsonplaceholder.typicode.com/posts";
			// Create a URL object
			URL url = new URL(apiUrl);
			// Open a connection to the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Set the request method (GET)
			connection.setRequestMethod("GET");
			// Set request headers
			//connection.setRequestProperty("Authorization", "Bearer YourAccessToken");
			connection.setRequestProperty("Content-Type", "application/json");
			// Add other headers as needed
			// Get the response code
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			// Read and print the response content
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			//			StringBuilder responseContent = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}
			reader.close();
			// Close the connection
			connection.disconnect();
			// Perform validations on the response as needed

			// Example: Parse JSON response and validate specific data
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseContent.toString();
	}

	public static void postRequest()
	{
		try {

			// Define the API endpoint URL
			String apiUrl = "https://jsonplaceholder.typicode.com/posts";
			// Create a URL object
			URL url = new URL(apiUrl);
			// Open a connection to the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Set the request method to POST
			connection.setRequestMethod("POST");
			// Set request headers
			//connection.setRequestProperty("Authorization", "Bearer YourAccessToken");
			connection.setRequestProperty("Content-Type", "application/json");
			// Add other headers as needed
			// Enable input and output streams for writing the request body
			connection.setDoOutput(true);
			// Define the JSON payload
			String jsonPayload = "{\r\n"
					+ "    \"userId\": 1000,\r\n"
					+ "    \"it\": 1,\r\n"
					+ "    \"title\": \"API Testing\",\r\n"
					+ "    \"body\": \"because he also accepts\\nundertakes the consequences of refusal and when\\nhe criticizes the troubles so that the whole\\nof our things are but they are the matter will happen to the architect\"\r\n"
					+ "  }";
			// Write the JSON payload to the request body
			try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
				dataOutputStream.writeBytes(jsonPayload);
				dataOutputStream.flush();
			}
			// Get the response code
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			// Read and print the response content
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder responseContent = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}
			reader.close();
			// Print the response content
			System.out.println("Response Content:");
			System.out.println(responseContent.toString());
			// Close the connection
			connection.disconnect();
			// Perform validations on the response as needed
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
