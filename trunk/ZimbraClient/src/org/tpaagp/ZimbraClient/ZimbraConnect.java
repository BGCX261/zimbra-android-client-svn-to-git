package org.tpaagp.ZimbraClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;





import android.R.bool;
import android.R.string;
import android.util.Xml;
import android.widget.TextView;

public class ZimbraConnect {
    private final String hostname;
    private final Boolean ssl;
    private final String accountName;
    private final String accountPassword;
    private String authToken;
    
    
    public ZimbraConnect(String hostname, Boolean ssl, String accountName, String accountPassword){
    	this.hostname=hostname;
    	this.ssl=ssl;
    	this.accountName=accountName;
    	this.accountPassword=accountPassword;
    	this.authToken="";
   }
	public Boolean Autentificate(){
		ByteArrayOutputStream os=new ByteArrayOutputStream();
        XmlSerializer serializer = Xml.newSerializer();
        String result =null;  
        try {
 			serializer.setOutput(os, "UTF-8");
			serializer.setPrefix("soap", "http://www.w3.org/2003/05/soap-envelope"); 
			serializer.startTag(null, "soap:Envelope");
			serializer.startTag(null,"soap:Header");
			serializer.startTag(null,"context");
			serializer.attribute(null, "xmlns", "urn:zimbra");
			serializer.startTag(null,"nosession");
			serializer.endTag(null, "nosession");
			serializer.startTag(null, "userAgent");
			serializer.attribute(null, "name", "ZimbrAndroid");
			serializer.endTag(null, "userAgent");
			serializer.endTag(null, "context");
			serializer.endTag(null, "soap:Header");
			serializer.startTag(null, "soap:Body");
			serializer.startTag(null, "AuthRequest");
			serializer.attribute(null, "xmlns","urn:zimbraAccount");
			serializer.startTag(null, "account");
			serializer.attribute(null, "by", "name");
			serializer.text(this.accountName);
			serializer.endTag(null,"account");
			serializer.startTag(null,"password");
			serializer.text(this.accountPassword);
			serializer.endTag(null,"password");
			serializer.endTag(null,"AuthRequest");
			serializer.endTag(null,"soap:Body");
			serializer.endTag(null, "soap:Envelope");
			serializer.endDocument();
			serializer.flush();
			HttpPost httppost;
			if(this.ssl)httppost = new HttpPost("https://"+this.hostname+"/service/soap/AuthRequest");
			else httppost = new HttpPost("http://"+this.hostname+"/service/soap/AuthRequest");
			HttpClient httpclient = new DefaultHttpClient(); 
			StringEntity se = new StringEntity(os.toString(),HTTP.UTF_8);
			se.setContentType("text/xml");
			httppost.setHeader("Content-Type","application/soap+xml;charset=UTF-8");
			httppost.setEntity(se);
			BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
			HttpEntity entity = httpResponse.getEntity();
			String responseText = EntityUtils.toString(entity);
			KXmlParser parser = new KXmlParser();  
			InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(responseText.getBytes("UTF-8")));  
		    parser.setInput(isr);  
		    String name = ""; 
		    while(parser.next() != XmlPullParser.END_DOCUMENT){  
		        	     if (parser.getEventType() == XmlPullParser.START_TAG){  
		        	               name = parser.getName();  
		        	               if (name.equals("authToken")){  
		        	                      result = parser.nextText();  
		        	                      break;
		        	               }  
		        	     }
		    }
		    this.authToken=result;

		    if(this.authToken!="") return false;
		    else return true;
		    	
		    
		    
        }catch (Exception e) {
        	return false;
		//mtv.setText("error occurred while creating xml file");
	} 
        
		
		
	}
	public String SyncContacts(){
		
		//testing
		
		//
		HttpClient httpclient2 = new DefaultHttpClient(); 
		ByteArrayOutputStream os2=new ByteArrayOutputStream();
		XmlSerializer serializer2 = Xml.newSerializer();
		    try {
		    	serializer2.setOutput(os2, "UTF-8");
				serializer2.setPrefix("soap", "http://www.w3.org/2003/05/soap-envelope"); 
				serializer2.startTag(null, "soap:Envelope");
				serializer2.startTag(null,"soap:Header");
				serializer2.startTag(null,"context");
				serializer2.attribute(null, "xmlns", "urn:zimbra");
				serializer2.startTag(null,"authToken");
				serializer2.text(this.authToken);
				serializer2.endTag(null,"authToken");
				serializer2.startTag(null,"nosession");
				serializer2.endTag(null, "nosession");
				serializer2.startTag(null, "userAgent");
				serializer2.attribute(null, "name", "ZimbrAndroid");
				serializer2.endTag(null, "userAgent");
				serializer2.endTag(null, "context");
				serializer2.endTag(null, "soap:Header");
				serializer2.startTag(null, "soap:Body");
				serializer2.startTag(null, "GetContactsRequest");
				serializer2.attribute(null, "sync","1");
				serializer2.attribute(null, "xmlns","urn:zimbraMail");
				serializer2.endTag(null,"GetContactsRequest");
				serializer2.endTag(null,"soap:Body");
				serializer2.endTag(null, "soap:Envelope");
				serializer2.endDocument();
				serializer2.flush();
				HttpPost httppost2;
				if(this.ssl)httppost2 = new HttpPost("https://"+this.hostname+"/service/soap/GetContactsRequest");
				else httppost2 = new HttpPost("http://"+this.hostname+"/service/soap/GetContactsRequest");
				StringEntity se2 = new StringEntity(os2.toString(),HTTP.UTF_8);
				se2.setContentType("text/xml");
				httppost2.setHeader("Content-Type","application/soap+xml;charset=UTF-8");
				httppost2.setEntity(se2);
				BasicHttpResponse httpResponse2 = (BasicHttpResponse) httpclient2.execute(httppost2);
				HttpEntity entity2 = httpResponse2.getEntity();
				String responseText2 = EntityUtils.toString(entity2);
				KXmlParser parser2 = new KXmlParser();  
			    InputStreamReader isr2 = new InputStreamReader(new ByteArrayInputStream(responseText2.getBytes("UTF-8")));  
			    parser2.setInput(isr2);  
			    String result2 = "";  
			    String name2 = ""; 
			   /* while(parser2.next() != XmlPullParser.END_DOCUMENT)  
			    	    {  
			        	     if (parser2.getEventType() == XmlPullParser.START_TAG)  
			        	           {  
			        	               name2 = parser2.getName();  
			        	               if (name2.equals("authToken"))  
			        	                  {  
			        	                      result2 = parser2.nextText();  
			        	                      break;
			        	                }  
			        	           }
			        	    }
	        
			    */
			  
			    return responseText2;
			} catch (Exception e) {
				return "";
			//	tv.setText("error occurred while creating xml file");
			}    
		
		
		
		
		
		
		
	}
	
}
