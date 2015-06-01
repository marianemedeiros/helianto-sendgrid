package org.helianto.sendgrid.message.sender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.helianto.core.MessageAdapter;
import org.helianto.core.MessageSender;

/**
 * SendGrid Web API message sender.
 * 
 * @author mauriciofernandesdecastro
 */
public class SendGridWebMailSender implements MessageSender {
	
	private String targetURL;
	private String apiUser;
	private String apiKey;
	
	/**
	 * Target url.
	 */
	public String getTargetURL() {
		return targetURL;
	}
	public void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}
	
	public void prepareMessage(MessageAdapter<?> messageAdapter) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("api_user=").append(getApiUser());
		messageBuilder.append("&api_key=").append(getApiKey());
		messageAdapter.setMessage(messageBuilder.toString());
	}
	
	public String sendMessage(MessageAdapter<?> messageAdapter) {
		String message = (String) messageAdapter.getMessage();
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	    	URL url = new URL(getTargetURL());
	    	connection = (HttpURLConnection)url.openConnection();
	    	connection.setRequestMethod("POST");
	    	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    	connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(message.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");  
	      connection.setUseCaches(false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	      wr.writeBytes(message);
	      wr.flush ();
	      wr.close ();

	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();
	      return response.toString();
	    } catch (Exception e) {
	      e.printStackTrace();
	      return null;

	    } finally {
	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	}
	
	public String getApiUser() {
		return apiUser;
	}
	public void setApiUser(String apiUser) {
		this.apiUser = apiUser;
	}
	
	private String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
		

}
