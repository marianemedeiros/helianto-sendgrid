package org.helianto.sendgrid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.iservport.message.sender.SendGridWebMailSender;

/**
 * Sendgrid code based configuration.
 * 
 * @author mauriciofernandesdecastro
 */
@Configuration
@PropertySource("classpath:/META-INF/sendgrid.properties")
public class SendGridConfig {

	private final static String SEND_GRID_API_ENDPOINT = "https://sendgrid.com/api/mail.send.xml";
	
	@Autowired
	private Environment env;
	
	@Bean(name="mailSender")
	public SendGridWebMailSender sendGridWebMailSender() {
		SendGridWebMailSender ms = new SendGridWebMailSender();
		ms.setTargetURL(env.getProperty("helianto.sendGrid.apiEndPoint", SEND_GRID_API_ENDPOINT));
		ms.setApiUser(env.getProperty("iservport.sendgrid.user"));
		ms.setApiKey(env.getProperty("iservport.sendgrid.password"));
		return ms;
	}
	
}
