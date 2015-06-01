package org.helianto.sendgrid.message.sender;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import org.helianto.core.domain.Identity;
import org.helianto.sendgrid.message.AbstractMessageAdapter;

/**
 * SendGrid web api message adapter.
 * 
 * @author mauriciofernandesdecastro
 */
public class SendGridMessageAdapter extends AbstractMessageAdapter<String> {
	
	/**
	 * Message constructor.
	 * 
	 * @param apiUser
	 * @param apiKey
	 */
	public SendGridMessageAdapter() {
		super();
	}
	
	@Override
	public String getMessage() {
		StringBuilder messageBuilder = new StringBuilder(super.getMessage());
		Set<Identity> validRecipients = getRecipients();
		try {
			for (Identity recipient: validRecipients) {
				messageBuilder.append("&to[]=").append(recipient.getPrincipal());
				messageBuilder.append("&toName[]=").append(URLEncoder.encode(recipient.getIdentityName().trim(), "UTF-8"));
			}
			messageBuilder.append("&subject=").append(URLEncoder.encode(getSubject(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unable to create mail subject.", e);
		}
		try {
			messageBuilder.append("&text=").append(URLEncoder.encode(getText(), "UTF-8"));
			if (getHtml()!=null && !getHtml().isEmpty()) {
				messageBuilder.append("&html=").append(URLEncoder.encode(getHtml(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unable to create mail content.", e);
		}
		messageBuilder.append("&from=").append(getFrom().getPrincipal());
		return messageBuilder.toString();
	}
	
}
