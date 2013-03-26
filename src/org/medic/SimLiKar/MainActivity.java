package org.medic.SimLiKar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText mSearchEdit;
    Button mConfirm, mCreate, mLogin;
    ListView mPeopleList;
    SQLiteDatabase mDatabase;
    CursorAdapter peopleListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // База данных больных
        mDatabase = new MedDatabase(this).getWritableDatabase();

        mSearchEdit = (EditText) findViewById(R.id.search_edit);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        mConfirm.setOnClickListener(this);
        mCreate = (Button) findViewById(R.id.create_button);
        mCreate.setOnClickListener(this);
        mLogin = (Button) findViewById(R.id.login_button);
        mLogin.setOnClickListener(this);


        mPeopleList = (ListView) findViewById(R.id.clients_list);
        mPeopleList.setOnItemClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //String [] columns = MedDatabase.peopleCols.values().toArray(new String[MedDatabase.peopleCols.values().size()]);
        //Cursor cur = mDatabase.query(MedDatabase.peopleTable, columns, null, null, null, null, null);
        Cursor cur = mDatabase.rawQuery(MedDatabase.mainQuery, null);
        peopleListAdapter = new PeopleAdapter(this, cur);
        mPeopleList.setAdapter(peopleListAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.login_button:
                final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.logging_in), getString(R.string.wait_please));
                v.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pd.dismiss();
                        findViewById(R.id.login_layout).setVisibility(View.GONE);
                        findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                    }
                }, 2000);
                break;
            case R.id.confirm_button:
                Cursor cur = mDatabase.rawQuery(MedDatabase.mainQueryWithParams + " LIKE \"%" + mSearchEdit.getText().toString() + "%\"", null);
                CursorAdapter newPeopleListAdapter = new PeopleAdapter(this, cur);
                mPeopleList.setAdapter(newPeopleListAdapter);

                peopleListAdapter.getCursor().close();
                peopleListAdapter = newPeopleListAdapter;
                break;
            case R.id.create_button:
                startActivity(new Intent(this, ManCreateActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(this, ManCreateActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}
