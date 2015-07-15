package org.helianto.sendgrid.message.sender;

import org.helianto.sendgrid.message.SendGridMessageAdapter;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ContentType;

import com.sendgrid.SendGridException;

/**
 * Uses Apache HttpClient to send e-mails via SendGrid API.
 * 
 * Original code supplied with the Java SendGrid API.
 * 
 * @author mauriciofernandesdecastro
 */
public class SendGridSender {
	
  private static final String VERSION           = "1.2.0";
  private static final String USER_AGENT        = "sendgrid/" + VERSION + ";java";

  private static final String PARAM_TO          = "to[%d]";
  private static final String PARAM_TONAME      = "toname[%d]";
  private static final String PARAM_CC          = "cc[%d]";
  private static final String PARAM_FROM        = "from";
  private static final String PARAM_FROMNAME    = "fromname";
  private static final String PARAM_REPLYTO     = "replyto";
  private static final String PARAM_BCC         = "bcc[%d]";
  private static final String PARAM_SUBJECT     = "subject";
  private static final String PARAM_HTML        = "html";
  private static final String PARAM_TEXT        = "text";
  private static final String PARAM_FILES       = "files[%s]";
  private static final String PARAM_XSMTPAPI    = "x-smtpapi";
  private static final String PARAM_HEADERS     = "headers";

  private String username;
  private String password;
  private String url;
  private String port;
  private String endpoint;
  private CloseableHttpClient client;

  public SendGridSender(String username, String password) {
    this.username = username;
    this.password = password;
    this.url = "https://api.sendgrid.com";
    this.endpoint = "/api/mail.send.json";
    this.client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
  }

  public SendGridSender setUrl(String url) {
    this.url = url;
    return this;
  }

  public SendGridSender setEndpoint(String endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  public String getVersion() {
    return VERSION;
  }

  public SendGridSender setClient(CloseableHttpClient client) {
    this.client = client;
    return this;
  }

  public HttpEntity buildBody(SendGridMessageAdapter email) {
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    builder.addTextBody("api_user", this.username);
    builder.addTextBody("api_key", this.password);

    String[] tos = email.getTos();
    String[] tonames = email.getToNames();
    String[] ccs = email.getCcs();
    String[] bccs = email.getBccs();

    for (int i = 0, len = tos.length; i < len; i++)
      builder.addTextBody(String.format(PARAM_TO, i), tos[i]);
    for (int i = 0, len = tonames.length; i < len; i++)
      builder.addTextBody(String.format(PARAM_TONAME, i), tonames[i], ContentType.create("text/plain", "UTF-8"));
    for (int i = 0, len = ccs.length; i < len; i++)
      builder.addTextBody(String.format(PARAM_CC, i), ccs[i]);
    for (int i = 0, len = bccs.length; i < len; i++)
      builder.addTextBody(String.format(PARAM_BCC, i), bccs[i]);
    // Files
    if (email.getAttachments().size() > 0) {
      Iterator it = email.getAttachments().entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        builder.addBinaryBody(String.format(PARAM_FILES, entry.getKey()), (InputStream) entry.getValue());
      }
    }

    if (email.getHeaders().size() > 0)
      builder.addTextBody(PARAM_HEADERS, new JSONObject(email.getHeaders()).toString());

    if (email.getFrom() != null && !email.getFrom().isEmpty())
      builder.addTextBody(PARAM_FROM, email.getFrom());

    if (email.getFromName() != null && !email.getFromName().isEmpty())
      builder.addTextBody(PARAM_FROMNAME, email.getFromName(), ContentType.create("text/plain", "UTF-8"));

    if (email.getReplyTo() != null && !email.getReplyTo().isEmpty())
      builder.addTextBody(PARAM_REPLYTO, email.getReplyTo());

    if (email.getSubject() != null && !email.getSubject().isEmpty())
      builder.addTextBody(PARAM_SUBJECT, email.getSubject(), ContentType.create("text/plain", "UTF-8"));

    if (email.getHtml() != null && !email.getHtml().isEmpty())
      builder.addTextBody(PARAM_HTML, email.getHtml(), ContentType.create("text/plain", "UTF-8"));

    if (email.getText() != null && !email.getText().isEmpty())
      builder.addTextBody(PARAM_TEXT, email.getText(), ContentType.create("text/plain", "UTF-8"));

    if (!email.getSMTPAPI().jsonString().equals("{}"))
      builder.addTextBody(PARAM_XSMTPAPI, email.getSMTPAPI().jsonString());
    
    System.err.println("Texto"+email.getText());
    return builder.build();
  }

  public SendGridSender.Response send(SendGridMessageAdapter email) throws SendGridException {
    HttpPost httppost = new HttpPost(this.url + this.endpoint);
    httppost.setEntity(this.buildBody(email));
    try {
      HttpResponse res = this.client.execute(httppost);
      return new SendGridSender.Response(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity()));
    } catch (IOException e) {
      return new SendGridSender.Response(500, "Problem connecting to SendGrid");
    }

  }

  public static class Response {
    private int code;
    private boolean success;
    private String message;

    public Response(int code, String msg) {
      this.code = code;
      this.success = code == 200;
      this.message = msg;
    }

    public int getCode() {
      return this.code;
    }

    public boolean getStatus() {
      return this.success;
    }

    public String getMessage() {
      return this.message;
    }
  }
}
