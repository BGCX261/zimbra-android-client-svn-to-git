package org.tpaagp.ZimbraClient;
import android.content.ContentResolver;
import org.tpaagp.ZimbraClient.ZimbraConnect;
import 	android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.AlarmManager;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import 	android.os.SystemClock;
import 	android.app.PendingIntent;
import org.apache.http.util.EntityUtils;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.StatusUpdates;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;


import android.content.ContentResolver;

import android.R.integer;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;
import 	android.provider.ContactsContract.*;
/**
 * @author sam
 * 
 */
public class ContactsSyncAdapterService extends Service {
	private static final String TAG = "ContactsSyncAdapterService";
	private static SyncAdapterImpl sSyncAdapter = null;
	private static ContentResolver mContentResolver = null;


	public ContactsSyncAdapterService() {
		super();
	}

	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		private Context mContext;

		public SyncAdapterImpl(Context context) {
			super(context, true);
			mContext = context;
		}

		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			try {
				ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
			} catch (OperationCanceledException e) {
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {

		
		
		
		
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}

	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null)
			sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}

	private static void addContact(Account account, ContactData Cont) {
		Log.i(TAG, "Adding contact: " + Cont.getUserId());
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
		
		builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
		builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
		builder.withValue(RawContacts.SYNC1, Cont.getUserId());
		operationList.add(builder.build());
		
		builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
		builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
		builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
		if(Cont.getLastName()!=null){
		builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, Cont.getFristName()+" "+Cont.getLastName());
		}
		else builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, Cont.getFristName());
		//builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, cognoms);
		operationList.add(builder.build());

//		builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
	//	builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
		//builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
		//builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, cognoms);
		//operationList.add(builder.build());
	
		builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
		builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
		builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.org.tpaagp.ZimbraClient.profile");
		builder.withValue(ContactsContract.Data.DATA1, Cont.getUserId());
		/*builder.withValue(ContactsContract.Data.DATA2, "SyncProviderDemo Profile");
		builder.withValue(ContactsContract.Data.DATA3, "View profile");*/
		
		operationList.add(builder.build());

		builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
		builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
		builder.withValue(ContactsContract.Data.MIMETYPE,
		ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Cont.getCellPhone());
		builder.withValue(ContactsContract.CommonDataKinds.Phone.DATA3, Cont.getHomePhone());
		builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM );
		operationList.add(builder.build());

		builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
		builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
		builder.withValue(ContactsContract.Data.MIMETYPE,
		ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
		builder.withValue(ContactsContract.CommonDataKinds.Email.DATA, Cont.getEmail());
		
		operationList.add(builder.build());
		
		
		try {
			mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
	
    private static void addContact(Context context, String accountName,
            User user) {
    	 final BatchOperation batchOperation =
	         new BatchOperation(context, mContentResolver);
            // Put the data in the contacts provider
            final ContactOperations contactOp =
                ContactOperations.createNewContact(context, user.getUserId(),
                    accountName, batchOperation);
            contactOp.addName(user.getFirstName(), user.getLastName()).addEmail(
                user.getEmail()).addPhone(user.getCellPhone(), Phone.TYPE_MOBILE)
                .addPhone(user.getHomePhone(), Phone.TYPE_OTHER).addProfileAction(
                    user.getUserId());
        }
*/
	private static void DeleteContact(String userID, long rawContactId){
	
		
		Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId);
		mContentResolver.delete(ContactsContract.Contacts.CONTENT_URI, RawContacts.SYNC1+"=?", new String[] {userID});
	}
	
	private static void updateContactStatus(ArrayList<ContentProviderOperation> operationList, long rawContactId, String status) {
		Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId);
		Uri entityUri = Uri.withAppendedPath(rawContactUri, Entity.CONTENT_DIRECTORY);
		Cursor c = mContentResolver.query(entityUri, new String[] { RawContacts.SOURCE_ID, Entity.DATA_ID, Entity.MIMETYPE, Entity.DATA1 }, null, null, null);
		try {
			while (c.moveToNext()) {
				if (!c.isNull(1)) {
					String mimeType = c.getString(2);

					if (mimeType.equals("vnd.android.cursor.item/vnd.org.tpaagp.ZimbraClient.profile")) {
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.StatusUpdates.CONTENT_URI);
						builder.withValue(ContactsContract.StatusUpdates.DATA_ID, c.getLong(1));
						builder.withValue(ContactsContract.StatusUpdates.STATUS, status);
						builder.withValue(ContactsContract.StatusUpdates.STATUS_RES_PACKAGE, "org.tpaagp.ZimbraClient");
						builder.withValue(ContactsContract.StatusUpdates.STATUS_LABEL, R.string.app_name);
						builder.withValue(ContactsContract.StatusUpdates.STATUS_ICON, R.drawable.icon);
						builder.withValue(ContactsContract.StatusUpdates.STATUS_TIMESTAMP, System.currentTimeMillis());
						operationList.add(builder.build());
						builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
						builder.withSelection(BaseColumns._ID + " = '" + c.getLong(1) + "'", null);
						builder.withValue(ContactsContract.Data.DATA3, status);
						operationList.add(builder.build());
					/*	builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
						builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
						builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "345");
						
						
						
						operationList.add(builder.build());*/
					}
				}
			}
		} finally {
			c.close();
		}
	}

	private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
			throws OperationCanceledException {
		
	//	provider.addPeriodicSync(account, "ZimbraClient",extras, 40);
		Intent widgetUpdate = new Intent();

		// make this pending intent unique

		  
		PendingIntent newPending = PendingIntent.getBroadcast(
		    context, 0, widgetUpdate, 
		    PendingIntent.FLAG_UPDATE_CURRENT);
		// now schedule it
		AlarmManager alarms = (AlarmManager) context.
		   getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, 
		   SystemClock.elapsedRealtime(), 40 * 1000, 
		   newPending);

		
		
		HashMap<String, Long> localContacts = new HashMap<String, Long>();
		HashMap<Long, String> localContactsBac = new HashMap<Long, String>();
		mContentResolver = context.getContentResolver();
		Log.i(TAG, "performSync: " + account.toString());
		Log.i(TAG, "Contingut del Bundle: " +  extras.getString("server"));
		// Load the local contacts
		Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
				RawContacts.ACCOUNT_TYPE, account.type).build();
		
		Cursor c1 = mContentResolver.query(rawContactUri, new String[] { BaseColumns._ID, RawContacts.SYNC1 }, null, null, null);
		while (c1.moveToNext()) {
			localContacts.put(c1.getString(1), c1.getLong(0));
			localContactsBac.put(c1.getLong(0),c1.getString(1));
		}
	
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		try {
			

			AccountManager am = AccountManager.get(context);
		Boolean ssl;
		if(am.getUserData(account, "useSSL")=="true") ssl=true;
		else ssl=false;
	        ZimbraConnect test= new ZimbraConnect(am.getUserData(account, "server").toString(), ssl, account.name.toString(), am.getPassword(account).toString());	
			test.Autentificate();
			
			String Contactes=test.SyncContacts();
			Log.i(TAG, "capturacontactes: " + Contactes);
			
			Log.i(TAG, "contactes: 1 " );
			KXmlParser parser2 = new KXmlParser();  
			Log.i(TAG, "contactes: 2 " );
		    InputStreamReader isr2 = new InputStreamReader(new ByteArrayInputStream(Contactes.getBytes("UTF-8")));  
		    Log.i(TAG, "contactes: 3 " );
		    parser2.setInput(isr2);  
		    Log.i(TAG, "contactes: 4 " );
		    String result2 = "";  
		    String name2 = ""; 
			Boolean seguentcontacte;
			String nom="";
			Integer mdtemp=0;
			Integer contactsnumber=0;
		//	ContactData Cont= new ContactData(null,null,null,null,null,null,null,null);
			//seguentcontacte=true;
			   while((parser2.next() != XmlPullParser.END_DOCUMENT))  
    	    {  
        	     if (parser2.getEventType() == XmlPullParser.START_TAG)  
        	           {  
        	    	 Log.i(TAG, "contactes: 5 " );
        	               name2 = parser2.getName();  
        	               if (name2.equals("cn"))  
        	                  {  
        	            	   Log.i(TAG,"HOLA "+ localContacts.size());	
        	            	   ++contactsnumber;
        	            	   if(Integer.parseInt(am.getUserData(account, "md"))>=Integer.parseInt(parser2.getAttributeValue(parser2.getNamespace() , "md")))parser2.next();
        	            	   else{
        	            		   if(Integer.parseInt(parser2.getAttributeValue(parser2.getNamespace() , "md"))>mdtemp)mdtemp=Integer.parseInt(parser2.getAttributeValue(parser2.getNamespace() , "md"));
        	            		
        	            	   ContactData Cont= new ContactData(parser2.getAttributeValue(parser2.getNamespace() , "id"),null,null,null,null,null,null,null);
        	            	  seguentcontacte=true;
        	            	//   Log.i(TAG, "contactes: CN trobat ");
        	                      
        	                    Log.i(TAG, "contactes: CN trobat " );
        	                   	
        	        		    while(seguentcontacte.booleanValue() && (parser2.next() != XmlPullParser.END_DOCUMENT))  
        	     	    	    {  
        	        		    	 Log.i(TAG, "contactes: CN trobat2 " );
        	        		    	if ((parser2.getEventType() == XmlPullParser.END_TAG)&& parser2.getName().equals("cn")){
        	        		    		if( localContacts.get(Cont.getUserId())==null){
        	        		    	                                                                                       seguentcontacte=false;  
        	        		    	                    
        	        		    	                     addContact(account,Cont);
        	        		    	                     Log.i(TAG,"HOLA "+ localContacts.size());
        	        		    		}
        	        		    		else{
        	        		    			//UPDATE
        	        		    		}
        	        		    	}
        	     				  if (parser2.getEventType() == XmlPullParser.START_TAG)  
        	        	           {  
        	     					 Log.i(TAG, "contactes: CN3 trobat " + parser2.getAttributeCount() + "   --" +parser2.toString() +"---"+parser2.getName()+".,.,"+parser2.getText());
        	     					 // if(parser2.getName().equals("cn"))seguentcontacte=false;
        	     					   if(parser2.getAttributeCount()>0){
        	     						   name2 = parser2.getAttributeValue(parser2.getNamespace(),"n").toString();  
        	     					   
        	     					   Log.i(TAG, "contactes: TAGS N trobat "+ name2);
        	         	               if (name2.equals("firstName"))Cont.setFirstName(parser2.nextText());
        	         	               else if (name2.equals("lastName"))Cont.setLastName(parser2.nextText());
        	         	              else if (name2.equals("mobilePhone"))Cont.setCellPhone(parser2.nextText());
        	         	             else if (name2.equals("email"))Cont.setEmail(parser2.nextText());
        	
        	         	               
        	         	              Log.i(TAG, "contactes: nom "+ nom);
        	        	           }
        	        	           }
        	     	    	    }
        	     	    	    }
        	                    
        	    	    	   } 
        	                      
        	                }  
        	           }
        	    if(mdtemp!=0)am.setUserData(account, "md", mdtemp.toString());
        	    if(localContacts.size()>contactsnumber){
        	    	Log.i(TAG,"HOLA: 10 "+ contactsnumber);
        	    	parser2 = new KXmlParser();  
        			//Log.i(TAG, "contactes: 2 " );
        		    isr2 = new InputStreamReader(new ByteArrayInputStream(Contactes.getBytes("UTF-8")));  
        		    //Log.i(TAG, "contactes: 3 " );
        		    parser2.setInput(isr2);  
        		   // HashMap<String, Long> localContactsBac = localContacts;
        	    	
        	    	  while((parser2.next() != XmlPullParser.END_DOCUMENT))  
        	    	    {  
        	        	     if (parser2.getEventType() == XmlPullParser.START_TAG)  
        	        	           {  
        	        	    	 if (name2.equals("cn"))  
        	        	                  {  
        	        	    		 	
        	        	            	   ContactData Cont= new ContactData(parser2.getAttributeValue(parser2.getNamespace() , "id"),null,null,null,null,null,null,null);
        	        	            	  seguentcontacte=true;
        	        	            	//   Log.i(TAG, "contactes: CN trobat ");
        	        	            	  if(localContactsBac.get(parser2.getAttributeValue(parser2.getNamespace() , "id"))!=null)localContactsBac.remove(parser2.getAttributeValue(parser2.getNamespace() , "id"));
        	        	                  }
        	        	                }  
        	        	           }
        	    	
        	    Collection<String> toDelete=localContactsBac.values();
        	    for (Iterator<String> iter = toDelete.iterator(); iter.hasNext(); ) {
        	    	//Log.i(TAG,"HOLA: 1 " + iter.getClass());
        	    	DeleteContact(iter.toString(),localContacts.get(iter.next()));
        	    }

        	    	
        	    }
    
 // User contacte= new User("nom1",nom,"lastnm","123","345","567","email@email.com",false,12);
			// If we don't have any contacts, create one. Otherwise, set a
			// status message
			if (localContacts.get("fulano") == null) {
		//		addContact(context,account.name, contacte);
			//	addContact(account,nom, "cognoms", "fulano","123","adsf@fads.com");
				
			} else {
				// User contacte2= new User("nom2","firstnam2","lastnm2","1232","3452","5672","email2@email2.com",false,12);
	//			updateContactStatus(operationList, localContacts.get("fulano"), "Cadena d'estat");
		//		updateContact(localContacts.get("fulano").toString(), "77894");
				
			}
			if (operationList.size() > 0)
				mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    private interface DataQuery {
        public static final String[] PROJECTION =
            new String[] {Data._ID, Data.MIMETYPE, Data.DATA1, Data.DATA2,
                Data.DATA3,};

        public static final int COLUMN_ID = 0;
        public static final int COLUMN_MIMETYPE = 1;
        public static final int COLUMN_DATA1 = 2;
        public static final int COLUMN_DATA2 = 3;
        public static final int COLUMN_DATA3 = 4;
        public static final int COLUMN_PHONE_NUMBER = COLUMN_DATA1;
        public static final int COLUMN_PHONE_TYPE = COLUMN_DATA2;
        public static final int COLUMN_EMAIL_ADDRESS = COLUMN_DATA1;
        public static final int COLUMN_EMAIL_TYPE = COLUMN_DATA2;
        public static final int COLUMN_GIVEN_NAME = COLUMN_DATA2;
        public static final int COLUMN_FAMILY_NAME = COLUMN_DATA3;

        public static final String SELECTION = Data.RAW_CONTACT_ID + "=?";
    }

}
