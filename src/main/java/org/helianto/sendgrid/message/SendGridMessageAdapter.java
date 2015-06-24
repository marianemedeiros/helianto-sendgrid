package org.helianto.sendgrid.message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sendgrid.smtpapi.SMTPAPI;

/**
 * An adapter to SendGrid messages. Original code supplied with the Java SendGrid API.
 * 
 * @author mauriciofernandesdecastro
 */
public class SendGridMessageAdapter {
	
    private SMTPAPI smtpapi;
    
    private String from;
    
    private ArrayList<String> to;
    
    private ArrayList<String> toname;
    
    private ArrayList<String> cc;
    
    private ArrayList<String> bcc;
    
    private String fromname;
    
    private String replyto;
    
    private String subject;
    
    private String text;
    
    private String html;
    
    private Map<String, InputStream> attachments;
    
    private Map<String, String> headers;

    /**
     * Constructor.
     */
    public SendGridMessageAdapter () {
      this.smtpapi = new SMTPAPI();
      this.to = new ArrayList<String>();
      this.toname = new ArrayList<String>();
      this.cc = new ArrayList<String>();
      this.bcc = new ArrayList<String>();
      this.attachments = new HashMap<String, InputStream>();
      this.headers = new HashMap<String, String>();
    }

    public SendGridMessageAdapter addTo(String to) {
      this.smtpapi.addTo(to);
      this.to.add(to);
      return this;
    }

    public SendGridMessageAdapter addTo(String[] tos) {
      this.smtpapi.addTos(tos);
      this.to.addAll(Arrays.asList(tos));
      return this;
    }

    public SendGridMessageAdapter addTo(String to, String name) {
      this.addTo(to);
      return this.addToName(name);
    }

    public SendGridMessageAdapter setTo(String[] tos) {
      this.smtpapi.setTos(tos);
      this.to = new ArrayList<String>(Arrays.asList(tos));
      return this;
    }

    public String[] getTos() {
      return this.to.toArray(new String[this.to.size()]);
    }

    public SendGridMessageAdapter addToName(String toname) {
      this.toname.add(toname);
      return this;
    }

    public SendGridMessageAdapter addToName(String[] tonames) {
      this.toname.addAll(Arrays.asList(tonames));
      return this;
    }

    public SendGridMessageAdapter setToName(String[] tonames) {
      this.toname = new ArrayList<String>(Arrays.asList(tonames));
      return this;
    }

    public String[] getToNames() {
      return this.toname.toArray(new String[this.toname.size()]);
    }

    public SendGridMessageAdapter addCc(String cc) {
      this.cc.add(cc);
      return this;
    }

    public SendGridMessageAdapter addCc(String[] ccs) {
      this.cc.addAll(Arrays.asList(ccs));
      return this;
    }

    public SendGridMessageAdapter setCc(String[] ccs) {
      this.cc = new ArrayList<String>(Arrays.asList(ccs));
      return this;
    }

    public String[] getCcs() {
      return this.cc.toArray(new String[this.cc.size()]);
    }

    public SendGridMessageAdapter setFrom(String from) {
      this.from = from;
      return this;
    }

    public String getFrom() {
      return this.from;
    }

    public SendGridMessageAdapter setFromName(String fromname) {
      this.fromname = fromname;
      return this;
    }

    public String getFromName() {
      return this.fromname;
    }

    public SendGridMessageAdapter setReplyTo(String replyto) {
      this.replyto = replyto;
      return this;
    }

    public String getReplyTo() {
      return this.replyto;
    }

    public SendGridMessageAdapter addBcc(String bcc) {
      this.bcc.add(bcc);
      return this;
    }

    public SendGridMessageAdapter addBcc(String[] bccs) {
      this.bcc.addAll(Arrays.asList(bccs));
      return this;
    }

    public SendGridMessageAdapter setBcc(String[] bccs) {
      this.bcc = new ArrayList<String>(Arrays.asList(bccs));
      return this;
    }

    public String[] getBccs() {
      return this.bcc.toArray(new String[this.bcc.size()]);
    }

    public SendGridMessageAdapter setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public String getSubject() {
      return this.subject;
    }

    public SendGridMessageAdapter setText(String text) {
      this.text = text;
      return this;
    }

    public String getText() {
      return this.text;
    }

    public SendGridMessageAdapter setHtml(String html) {
      this.html = html;
      return this;
    }

    public String getHtml() {
      return this.html;
    }

    public SendGridMessageAdapter dropSMTPAPITos() {
      JSONObject oldHeader = new JSONObject(this.smtpapi.jsonString());
      oldHeader.remove("to");
      this.smtpapi = new SMTPAPI(oldHeader);
      return this;
    }

    public SendGridMessageAdapter addSubstitution(String key, String[] val) {
      this.smtpapi.addSubstitutions(key, val);
      return this;
    }

    public JSONObject getSubstitutions() {
      return this.smtpapi.getSubstitutions();
    }

    public SendGridMessageAdapter addUniqueArg(String key, String val) {
      this.smtpapi.addUniqueArg(key, val);
      return this;
    }

    public JSONObject getUniqueArgs() {
      return this.smtpapi.getUniqueArgs();
    }

    public SendGridMessageAdapter addCategory(String category) {
      this.smtpapi.addCategory(category);
      return this;
    }

    public String[] getCategories() {
      return this.smtpapi.getCategories();
    }

    public SendGridMessageAdapter addSection(String key, String val) {
      this.smtpapi.addSection(key, val);
      return this;
    }

    public JSONObject getSections() {
      return this.smtpapi.getSections();
    }

    public SendGridMessageAdapter addFilter(String filter_name, String parameter_name, String parameter_value) {
      this.smtpapi.addFilter(filter_name, parameter_name, parameter_value);
      return this;
    }

    public JSONObject getFilters() {
      return this.smtpapi.getFilters();
    }

    public SendGridMessageAdapter addAttachment(String name, File file) throws IOException, FileNotFoundException {
      return this.addAttachment(name, new FileInputStream(file));
    }

    public SendGridMessageAdapter addAttachment(String name, String file) throws IOException {
      return this.addAttachment(name, new ByteArrayInputStream(file.getBytes()));
    }

    public SendGridMessageAdapter addAttachment(String name, InputStream file) throws IOException {
      this.attachments.put(name, file);
      return this;
    }

    public Map getAttachments() {
      return this.attachments;
    }

    public SendGridMessageAdapter addHeader(String key, String val) {
      this.headers.put(key, val);
      return this;
    }

    public Map getHeaders() {
      return this.headers;
    }

    public SMTPAPI getSMTPAPI() {
      return this.smtpapi;
    }
    
  }