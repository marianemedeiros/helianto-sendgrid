package org.helianto.sendgrid.message;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.helianto.core.MessageAdapter;
import org.helianto.core.domain.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Message adapter base class.
 * 
 * @author mauriciofernandesdecastro
 */
public abstract class AbstractMessageAdapter<T> 
	implements MessageAdapter<T> {
	
	private T message;

	private Identity from;
	private Set<Identity> to = new HashSet<Identity>();
	private Set<Identity> cc  = new HashSet<Identity>();
	private Set<Identity> bcc = new HashSet<Identity>();
	private String replyTo;
	private String subject;
	private String text;
	private String html;
	private Date sentDate;
	private Set<Resource> attachments;
	
	
	public Identity getFrom() {
		return from;
	}
	public MessageAdapter<T> setFrom(Identity from) {
		this.from = from;
		return this;
	}
	
	public Set<Identity> getTo() {
		return to;
	}
	public MessageAdapter<T> setTo(Set<Identity> to) {
		this.to = to;
		return this;
	}
	
	public MessageAdapter<T> setTo(Identity to) {
		this.to.add(to);
		return this;
	}
	
	/**
	 * Get only valid recipients.
	 */
	protected Set<Identity> getRecipients() {
		if (!(getTo()!=null && getTo().size()>0)) {
			throw new IllegalArgumentException("Unable to send mail, no recipients.");
		}
		Set<Identity> recipients = new HashSet<Identity>();
		int addressableRecipientsCount = 0;
		for (Identity recipient: getTo()) {
			if (recipient.isAddressable()) {
				recipients.add(recipient);
				addressableRecipientsCount++;
			}
		}
		logger.info("Found {} valid recipients.", addressableRecipientsCount);
		if (addressableRecipientsCount==0) {
			throw new IllegalArgumentException("Unable to send mail, no valid recipients.");
		}
		return recipients;
	}
	
	public Set<Identity> getCc() {
		return cc;
	}
	public MessageAdapter<T> setCc(Set<Identity> cc) {
		this.cc = cc;
		return this;
	}
	public MessageAdapter<T> setBcc(Set<Identity> bcc) {
		this.bcc = bcc;
		return this;
	}
	public Set<Identity> getBcc() {
		return bcc;
	}
	public String getReplyTo() {
		return replyTo;
	}
	public MessageAdapter<T> setReplyTo(String replyTo) {
		this.replyTo = replyTo;
		return this;
	}
	
	public String getSubject() {
		return subject;
	}
	public MessageAdapter<T> setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public String getText() {
		return text;
	}
	public MessageAdapter<T> setText(String text) {
		this.text = text;
		return this;
	}
	
	public String getHtml() {
		return html;
	}
	public MessageAdapter<T> setHtml(String html) {
		this.html = html;
		return this;
	}
	
	public Date getSentDate() {
		return sentDate;
	}
	public MessageAdapter<T> setSentDate(Date sentDate) {
		this.sentDate = sentDate;
		return this;
	}
	
	public T getMessage() {
		return message;
	}
	@SuppressWarnings("unchecked")
	public void setMessage(Object message) {
		this.message = (T) message;
	}
	
	public Set<Resource> getAttachments() {
		return attachments;
	}
	public void setAttachments(Set<Resource> attachments) {
		this.attachments = attachments;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractMessageAdapter.class);

}
