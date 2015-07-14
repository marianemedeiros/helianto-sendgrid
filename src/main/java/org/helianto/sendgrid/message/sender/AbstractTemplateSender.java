package org.helianto.sendgrid.message.sender;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.helianto.core.domain.Identity;
import org.helianto.sendgrid.message.SendGridMessageAdapter;
import org.helianto.sendgrid.message.sender.SendGridSender.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * Abstract class to SendGrid e-mail senders.
 * 
 * Subclasses have to implement only the relevant details to send rich e-email using 
 * SendGrid API (see API docs for help). The subclass constructor must
 * supply immutable sender data and SendGrid template name. The latter must 
 * correspond to a property defined in the sendGrid.properties file and point to
 * an active template id under the SendGrid user account.
 * 
 * @author mauriciofernandesdecastro
 */
public abstract class AbstractTemplateSender {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateSender.class);
	
	/**
	 * Endpoint of the helianto API.
	 */
	protected String apiUrlProperty = "helianto.api.url";
	
	/**
	 * String to prefix all template names in the /META-INF/sendGrid.properties file.
	 */
	protected String templatePrefix = "helianto.sendgrid.template.";
	
	/**
	 * Uri path to static version of the template.
	 */
	protected String staticPath = "/static/template/";
	
	private final String senderEmail;
	
	private final String senderName;
	
	private final String templateName;
	
	private String confirmationUri;
	
	@Inject
	private Environment env;
	
	@Inject
	private SendGridSender sendGridSender;
	
	/**
	 * Constructor.
	 * 
	 * @param identity
	 * @param templateName
	 */
	public AbstractTemplateSender(Identity identity, String templateName) {
		super();
		this.senderEmail = identity.getPrincipal();
		this.senderName = identity.getIdentityFirstName().trim()+" "+identity.getIdentityLastName();
		this.templateName = templateName;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param senderEmail
	 * @param senderName
	 * @param templateName
	 */
	public AbstractTemplateSender(String senderEmail, String senderName, String templateName) {
		super();
		this.senderEmail = senderEmail;
		this.senderName = senderName;
		this.templateName = templateName;
	}
	
	/**
	 * Do send e-mail.
	 * 
	 * @param recipient
	 * @param subject
	 * @param params
	 */
	public final boolean send(Identity recipient, String subject, String... params) {
		return send(recipient.getPrincipal(), recipient.getIdentityFirstName(), recipient.getIdentityLastName()
				,subject, params);
	}
	
	/**
	 * Do send e-mail.
	 * 
	 * @param recipientEmail
	 * @param recipientFirstName
	 * @param recipientLastName
	 * @param subject
	 * @param params
	 */
	public final boolean send(String recipientEmail, String recipientFirstName, String recipientLastName
			, String subject, String... params) {
		
		logger.debug("Sender {}<{}>", senderName, senderEmail);
		
		SendGridMessageAdapter sendGridEmail = new SendGridMessageAdapter(); 

		sendGridEmail.setSubject(subject);
		sendGridEmail.setHtml(getBody());

		sendGridEmail.addTo(recipientEmail);
		sendGridEmail.addToName(recipientFirstName.trim()+" "+recipientLastName);
		sendGridEmail.setFrom(senderEmail);
		sendGridEmail.setFromName(senderName);
		sendGridEmail.setText(subject);
		
		String templateId = getTemplateId();
      	try {
          	if (templateId!=null) {
          		sendGridEmail.addSubstitution("${recipientEmail}", new String[] { new String(Base64.encodeBase64(recipientEmail.getBytes())) } );
          		sendGridEmail.addSubstitution("${recipientFirstName}", new String[] { new String(Base64.encodeBase64(recipientFirstName.getBytes())) } );
          		sendGridEmail.addSubstitution("${recipientLastName}", new String[] { new String(Base64.encodeBase64(recipientLastName.getBytes())) } );
          		for (String key: getDefaultSubstitutions(params).keySet()) {
              		sendGridEmail.addSubstitution(key, new String[] { new String(Base64.encodeBase64(getDefaultSubstitutions().get(key).getBytes())) } );
          		}
          		sendGridEmail.getSMTPAPI().addFilter("templates", "enabled", 1);
          		sendGridEmail.addFilter("templates", "template_id", templateId);
          	}
			Response response = sendGridSender.send(sendGridEmail);
			int responseCode = response.getCode();
			if (responseCode!=200) {
				logger.warn("E-mail failed ({}) with message: {} ", responseCode, response.getMessage());
				return false;
			}
		} catch (Exception e) {
			logger.debug("Unable to send: {} ", e.getMessage());
			return false;
		}
    	
      	logger.debug("Sent e-mail with subject {} and template {}.", subject, templateId);
      	return true;
	}
	
	/**
	 * Api url.
	 */
	protected final String getApiUrl() {
		return env.getProperty(apiUrlProperty);
	}
	
	/**
	 * Template name prepended with prefix.
	 */
	protected final String getTemplateId() {
		return env.getProperty(templatePrefix+templateName, "invalid-template");
	}
	
	/**
	 * Body.
	 */
	protected String getBody(String... params) {
		return "<p></p>";
	}
	
	/**
	 * Create a default map of substitutions to be supplied to the template. Override only
	 * if you need to supply a different default map, otherwise use {@link #getSubstitutions()}.
	 * 
	 * Defaults include ${confirmationUri}, if not empty, and ${senderEmail}. Keep the ${} format in your
	 * substitutions if you want the static representation to be processed with a similar model.
	 * 
	 * @param params
	 */
	protected Map<String, String> getDefaultSubstitutions(String... params) {
		Map<String, String> substitutions = new HashMap<>();
		String internalConfirmationUri = getConfirmationUri(params);
		if (internalConfirmationUri!=null && !internalConfirmationUri.isEmpty()) {
			substitutions.put("${confirmationUri}", getConfirmationUriEncoded(internalConfirmationUri));
		}
		substitutions.put("${senderEmail}", senderEmail);
		Map<String, String> paramMap = getParams(params);
		for (String param: paramMap.keySet()) {
			try {
				substitutions.put("${"+param+"}"
						, URLEncoder.encode(paramMap.get(param), StandardCharsets.UTF_8.name()));
			} catch (UnsupportedEncodingException e) {
				logger.warn("Unable to encode param {} = {}",param, getParams().get(param));
			}			
		}
		return substitutions;
	}
	
	/**
	 * Override to add parameters as extra substitutions.
	 */
	protected Map<String, String> getParams(String... params) {
		return new HashMap<String, String>();
	}
	
	/**
	 * Read parameters as a query.
	 */
	protected final String getParamsAsQuery(String... params) {
		StringBuilder query = new StringBuilder();
		for (String param: getParams(params).keySet()) {
			try {
				query.append(param).append("=")
					.append(URLEncoder.encode(getParams().get(param), StandardCharsets.UTF_8.name()));
			} catch (UnsupportedEncodingException e) {
				logger.warn("Unable to encode param {} = {}",param, getParams().get(param));
			}			
		}
		return query.toString();
	}
	
	/**
	 * Override to change confirmation URI, if any.
	 */
	protected String getConfirmationUri(String... params) {
		return confirmationUri;
	}
	
	/**
	 * Confirmation URI encoded, if any.
	 */
	protected final static String getConfirmationUriEncoded(String confirmationUri) {
		try {
			return URLEncoder.encode(confirmationUri, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unable to encode confirmation uri.");
		}
	}
	
}
