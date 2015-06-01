package org.helianto.sendgrid.config;

import org.helianto.sendgrid.message.sender.SendGridWebMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

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
		ms.setApiUser(env.getProperty("sendgrid.user"));
		ms.setApiKey(env.getProperty("sendgrid.password"));
		return ms;
	}
	
}
