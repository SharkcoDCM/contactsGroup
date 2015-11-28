package com.example.mohammad.contactsgroup;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


//    @Override
//    public long getSelectedItemId() {
//        return super.getSelectedItemId();
//    }
//
//    @Override
//    public int getSelectedItemPosition() {
//        return super.getSelectedItemPosition();
//    }
//
//    ListView lv;
//    Cursor cursor1;
public class group extends Activity {
    ListView listView = null;
    EditText editText = null;
    Cursor cursor = null;
    Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        context = this;
        listView =(ListView) findViewById(R.id.mylist);
        editText = (EditText) findViewById(R.id.txt_gp);

        try {
            // Create an array of Strings, for List
            ArrayAdapter<Model> adapter = new InteractiveArrayAdapter(this,getModel());

            // Assign adapter to ListView
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(getApplicationContext(),
                            "Click ListItem Number " + position, Toast.LENGTH_LONG)
                            .show();
                }
            });

        } catch(Exception e) {
            Log.d("**** Exception: ",e.getMessage());
        }
    }

    private List<Model> getModel() {
        List<Model> list = new ArrayList<Model>();

        try {
            ContentResolver cr = getContentResolver();
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, null, null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex  (ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String s = name + "\n" + number;
                    list.add(get(s));
                    s = null;
                } while (cursor.moveToNext());
            }
        } catch(Exception e){
            Log.d("Error inContacts Read:",""+e.getMessage());
        }

        return list;
    }

    private Model get(String s) {
        return new Model(s);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addgroup:
                String s = "";
                for (int i = 0; i < InteractiveArrayAdapter.list.size(); i++) {
                    if (InteractiveArrayAdapter.list.get(i).isSelected()) {
                        s = s + i + " ";
                    }
                }
                String s1 = null;
                s1 = editText.getText().toString();

                // Check the edittext is empty or not
                if (s1.equals("")) {
                    Toast.makeText(group.this, "Please Enter Any Text", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Check the Group is available or not
                Cursor groupCursor = null;
                String[] GROUP_PROJECTION = new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
                groupCursor = this.managedQuery(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, ContactsContract.Groups.TITLE + "=?", new String[]{s1}, ContactsContract.Groups.TITLE + " ASC");
                Log.d("*** Here Counts: ", "** " + groupCursor.getCount());

                if (groupCursor.getCount() > 0) {
                    Toast.makeText(group.this, "Group is already available", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(group.this, "Not available", Toast.LENGTH_SHORT).show();

                    //Here we create a new Group
                    try {
                        ContentValues groupValues = null;
                        ContentResolver cr = this.getContentResolver();
                        groupValues = new ContentValues();
                        groupValues.put(ContactsContract.Groups.TITLE, s1);
                        cr.insert(ContactsContract.Groups.CONTENT_URI, groupValues);
                        Log.d("GroupCreationFinished:", "###### Success");
                    } catch (Exception e) {
                        Log.d("########### Exception :", "" + e.getMessage());
                    }
                    Toast.makeText(group.this, "Created Successfully",
                            Toast.LENGTH_SHORT).show();
                }

                groupCursor.close();
                groupCursor = null;


                Log.d("Contacts add to Groups.", "** Fine");

                String groupID = null;
                Cursor getGroupID_Cursor = null;
                getGroupID_Cursor = this.managedQuery(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, ContactsContract.Groups.TITLE + "=?", new String[]{s1}, null);
                Log.d("Now Empty Cursor size:", "** " + getGroupID_Cursor.getCount());
                getGroupID_Cursor.moveToFirst();
                groupID = (getGroupID_Cursor.getString(getGroupID_Cursor.getColumnIndex("_id")));
                Log.d(" **** Group ID is: ", "** " + groupID);

                getGroupID_Cursor.close();
                getGroupID_Cursor = null;


                for (int i = 0; i < InteractiveArrayAdapter.list.size(); i++) {
                    if (InteractiveArrayAdapter.list.get(i).isSelected()) {
                        cursor.moveToPosition(i);
                        String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                        long contact = Long.parseLong(contactID);
                        long group = Long.parseLong(groupID);

                        addToGroup(contact, group);

                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        Log.d(" **** Contact Added: ", "* :" + name);
                        Toast.makeText(group.this, name + " Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                }


                break;
        }
    }


        public Uri addToGroup(long personId, long groupId) {

        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
                personId);
        values.put(
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                groupId);
        values
                .put(
                        ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

        return this.context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        }

}

//        final Button button = (Button) findViewById(R.id.btn_addgroup);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                EditText editText = (EditText) findViewById(R.id.txt_gp);
//                Spinner spinner = (Spinner) findViewById(R.id.spinner);
//                final ArrayList<String> list = new ArrayList<String>();
//
////                createGroup(editText.getText().toString());
//
//
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,list);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(dataAdapter);
//
//
//
//                String ET = editText.getText().toString();
//                if (!ET.equals("")) {
//                    list.add(ET);
//                    dataAdapter.notifyDataSetChanged();
//                }
//
//            }
//        });

//        cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        startManagingCursor(cursor1);
//        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
//        int[] to = {R.id.txt_name, R.id.txt_number};
//        SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, R.layout.text2, cursor1, from, to);
//        setListAdapter(listadapter);
//        lv = getListView();
//        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


//    }
//    private void createGroup(String string) {
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//        ops.add(ContentProviderOperation
//                .newInsert(ContactsContract.Groups.CONTENT_URI)
//                .withValue(ContactsContract.Groups.TITLE,string ).build());
//        try {
//
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//
//        } catch (Exception e) {
//            Log.e("Error", e.toString());
//        }
//
//    }
//}
