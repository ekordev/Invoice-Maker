package com.example.padmavatidiamondjewellers;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class CustomersCopy extends Activity {
	EditText name_ca,mobile_ca;
	SQLiteDatabase mydatabase;
	ListView l;
	DBHelper mydbhelper;
	String name[],mobile_no[],adv[];
	Cursor c;
	int count;
	double pending[];
	String name_person,phone_number;
	String name_edit,mobile_edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customers);
		Intent in=getIntent();
		
		ActionBar actionBar=this.getActionBar();
		actionBar.setIcon(R.drawable.pdj);
		actionBar.setTitle(" CUSTOMERS");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.menubar));
		
		l=(ListView)findViewById(R.id.customer_ca);
		update();
		OnItemClickListener o=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				DBHelper mydbhelper=new DBHelper(CustomersCopy.this, Database.PDJ_DATABASE_NAME, null, Database.PDJ_DATABASE_VERSION);
				mydatabase=mydbhelper.getWritableDatabase();
				String cols[]=new String[]{Database.C_NAME};
				Cursor c=mydatabase.query(Database.C_TABLE_NAME, cols, null, null, null, null, null);
				int co=c.getCount();
				c.moveToPosition(pos);
				Intent i=new Intent(CustomersCopy.this,CustomersHistory.class);
				i.putExtra("c_name", c.getString(0));
				startActivity(i);
				
			}
			
		};
		l.setOnItemClickListener(o);
	}
	 private void update() {
		// TODO Auto-generated method stub
		 	mydbhelper=new DBHelper(this, Database.PDJ_DATABASE_NAME, null, Database.PDJ_DATABASE_VERSION);
			mydatabase=mydbhelper.getWritableDatabase();
			Cursor c_count= mydatabase.rawQuery("select * from "+Database.C_TABLE_NAME,null);
			count=c_count.getCount();
			name=new String[count];
			//String address[]=new String[count];
			String mobile_no[]=new String[count];
			//String desc[]=new String[count];
			int i=0;
			adv=new String[count];
			String col1[]=new String[]{Database.C_NAME,
					Database.C_ADDRESS,Database.C_MOBILE_NO,Database.C_DESC,Database.C_ADV};
			Cursor c1=mydatabase.query(Database.C_TABLE_NAME, col1, null, null, null, null, null);
			for(c1.moveToFirst();!c1.isAfterLast();c1.moveToNext()){
				name[i]=c1.getString(0);
				///address[i]=c1.getString(1);
				mobile_no[i]=c1.getString(2);
				//desc[i]=c1.getString(3);
				adv[i]=c1.getString(4);
				i++;
			}
			String col[]=new String[]{Database.BI_BILL_NAME,
					Database.BI_AMT_PENDING};
			Cursor c=mydatabase.query(Database.BI_TABLE_NAME, col, null, null, null, null, null);
			int j=0;
			pending=new double[count];
			
			int k=i;
			i=0;
			while(i!=k){
				pending[i]=0;
				
			for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious()){
				if(name[i].equals(c.getString(0))){
					
					pending[i]+=Double.parseDouble(c.getString(1));
				}
			}
			i++;
			}
			
			l.setAdapter(new CustomersAdapter(CustomersCopy.this,name,mobile_no,pending,adv));
			registerForContextMenu(l);
	 }
	 public boolean onCreateOptionsMenu(Menu menu) {  
	         
	        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu  
	        return true;  
	    }
	@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Take appropriate action for each action item click
	        switch (item.getItemId()) {
	        case R.id.refresh:
	            // search action
	        	update();
	            return true;
	        case R.id.add_person:
	        	selectCustomer();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	public void editContact(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Edit Details") ; 
		LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.customers_add,null);
		mydbhelper=new DBHelper(this, Database.PDJ_DATABASE_NAME, null, Database.PDJ_DATABASE_VERSION);
		mydatabase=mydbhelper.getWritableDatabase();
		//Dialog f=(Dialog)arg0;
		name_ca=(EditText)view.findViewById(R.id.name_ca);
		//address_ca=(EditText)f.findViewById(R.id.address_ca);
		mobile_ca=(EditText)view.findViewById(R.id.mobile_ca);
		name_ca.setText(name_edit);
		mobile_ca.setText(mobile_edit);
	    alertDialogBuilder.setPositiveButton("Update",new DialogInterface.OnClickListener() {
			@Override
	        public void onClick(DialogInterface arg0, int arg1) {
				Dialog f=(Dialog)arg0;
				name_ca=(EditText)f.findViewById(R.id.name_ca);
				mobile_ca=(EditText)f.findViewById(R.id.mobile_ca);
				
				ContentValues cv=new ContentValues();
				cv.put(Database.C_NAME, name_ca.getText().toString());
				cv.put(Database.C_MOBILE_NO, mobile_ca.getText().toString());
				mydatabase.update(Database.C_TABLE_NAME, cv, Database.C_NAME+"= '"+name_edit+"'", null);
				update();
				
			}

			
	    });
	    alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
	        public void onClick(DialogInterface arg0, int arg1) {
				//gold_total.setText("0.0");
				
			}
	    });
	   
	    AlertDialog alertDialog = alertDialogBuilder.create();
	    alertDialog.setView(view);
	    alertDialog.show();
	}
	public void selectCustomer(){
		Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); 
		startActivityForResult(intentContact, 2);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)  
    {  
        super.onActivityResult(requestCode, resultCode, intent);  
        // check if the request code is same as what is passed  here it is 2  
        if(requestCode==2){  
        	getContactInfo(intent); 
        	mydbhelper=new DBHelper(this, Database.PDJ_DATABASE_NAME, null, Database.PDJ_DATABASE_VERSION);
    		mydatabase=mydbhelper.getWritableDatabase();
    		name_ca=(EditText)findViewById(R.id.name_ca);
			//address_ca=(EditText)findViewById(R.id.address_ca);
			mobile_ca=(EditText)findViewById(R.id.mobile_ca);
			//desc_ca=(EditText)findViewById(R.id.desc_ca);
			ContentValues cv=new ContentValues();
			cv.put(Database.C_NAME, name_person);
			cv.put(Database.C_ADDRESS, "");
			cv.put(Database.C_MOBILE_NO, phone_number);
			cv.put(Database.C_DESC, "");
			cv.put(Database.C_ADV, 0+"");

			mydatabase.insertOrThrow(Database.C_TABLE_NAME, null, cv);
			update();
        }  
    }
	public void getContactInfo(Intent intent) {
		// TODO Auto-generated method stub
		Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);      
		while (cursor.moveToNext()) 
		{           
		       String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		       name_person = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 

		       String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		       if ( hasPhone.equalsIgnoreCase("1"))
		           hasPhone = "true";
		       else
		           hasPhone = "false" ;

		       if (Boolean.parseBoolean(hasPhone)) 
		       {
		        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		        while (phones.moveToNext()) 
		        {
		          phone_number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		        }
		        phones.close();
		       }
		 }
		
	}
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)  
    {  
            super.onCreateContextMenu(menu, v, menuInfo); 
            menu.setHeaderTitle("Select The Action");    
            menu.add(0, v.getId(), 0, "Call");  
            menu.add(0, v.getId(), 0, "Message");   
            menu.add(0, v.getId(), 0, "Edit Contact"); 
    }   
    @Override    
    public boolean onContextItemSelected(MenuItem item){ 
    	String status="";
    	AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = info.position;/*what item was selected is ListView*/
        DBHelper mydbhelper=new DBHelper(this, Database.PDJ_DATABASE_NAME, null, Database.PDJ_DATABASE_VERSION);
		mydatabase=mydbhelper.getWritableDatabase();
		String cols[]=new String[]{Database.C_MOBILE_NO,Database.C_NAME};
		Cursor c=mydatabase.query(Database.C_TABLE_NAME, cols, null, null, null, null, null);
		int co=c.getCount();
		c.moveToPosition(id);
		
		if(item.getTitle()=="Call"){ 
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+c.getString(0)));
			startActivity(callIntent);
    	}else if(item.getTitle()=="Message"){  
        }else if(item.getTitle()=="Edit Contact"){
        	name_edit=c.getString(1);
        	mobile_edit=c.getString(0);
        	editContact();
        }else{
            return false;  
        }
    	return true;    
      }
	
}
