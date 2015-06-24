package com.sendgrid;
/**
 * Simple checked SendGrid Exception.
 * 
 * Original code supplied with the Java SendGrid API.
 * 
 * @author mauriciofernandesdecastro
 */
public class SendGridException extends Exception {

	private static final long serialVersionUID = 1L;

	public SendGridException(Exception e) {
		super(e);
	}
}
