package org.helianto.sendgrid.message;

/**
 * Sender properties holder.
 * 
 * @author mauriciofernandesdecastro
 */
public final class SenderProperties {
	
	private final String senderEmail;
	
	private final String senderName;
	
	/**
	 * Full constructor.
	 * 
	 * @param senderEmail
	 * @param senderName
	 */
	public SenderProperties(String senderEmail, String senderName) {
		super();
		this.senderEmail = senderEmail;
		this.senderName = senderName;
	}
	
	public String getSenderEmail() {
		return senderEmail;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
}
