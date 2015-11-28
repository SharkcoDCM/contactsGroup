package com.example.mohammad.contactsgroup;



import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

    @Override
    public long getSelectedItemId() {
        return super.getSelectedItemId();
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    ListView lv;
    Cursor cursor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.btn_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstIntent = new Intent(getApplicationContext(), group.class);
                startActivity(firstIntent);

            }
        });
                cursor1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                startManagingCursor(cursor1);
                String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                int[] to = {R.id.txt_name, R.id.txt_number};
                SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, R.layout.text, cursor1, from, to);
                setListAdapter(listadapter);
                lv = getListView();
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            }
        }
