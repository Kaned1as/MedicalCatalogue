package org.medic.SimLiKar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements View.OnClickListener
{
    EditText mSearchEdit;
    Button mConfirm, mCreate, mLogin;
    ListView mAddressList, mPeopleList;
    SQLiteDatabase mDatabase;
    CursorAdapter addressAdapter, peopleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // База данных больных
        mDatabase = new MedDatabase(this).getReadableDatabase();

        mSearchEdit = (EditText) findViewById(R.id.search_edit);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        mConfirm.setOnClickListener(this);
        mCreate = (Button) findViewById(R.id.create_button);
        mCreate.setOnClickListener(this);
        mLogin = (Button) findViewById(R.id.login_button);
        mLogin.setOnClickListener(this);

        mAddressList = (ListView) findViewById(R.id.address_list);
        mPeopleList = (ListView) findViewById(R.id.patient_list);
        mPeopleList.setOnItemClickListener(new manClickListener());
        mAddressList.setOnItemClickListener(new addressClickListener());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //String [] columns = MedDatabase.peopleCols.values().toArray(new String[MedDatabase.peopleCols.values().size()]);
        //Cursor cur = mDatabase.query(MedDatabase.peopleTable, columns, null, null, null, null, null);
        Cursor cur = mDatabase.rawQuery(MedDatabase.addressQuery, null);
        addressAdapter = new AddressAdapter(this, cur);
        mAddressList.setAdapter(addressAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.login_button:
                EditText login = (EditText) findViewById(R.id.login_text);
                EditText pass = (EditText) findViewById(R.id.password_text);

                if(login.getText().toString().equals("User") && pass.getText().toString().equals("User"))
                {
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
                }
                else
                    new AlertDialog.Builder(this).setMessage(R.string.login_error).setPositiveButton(android.R.string.ok, null).show();

                break;
            case R.id.confirm_button:
                String filtered = mSearchEdit.getText().toString();
                filtered = filtered.replace(".", "").replace("ул", "").replace("кв", "").replace("г", "");  // избавляемся от ненужного

                String searchTerms[] = filtered.split(" "); // делим запрос на части
                String query = MedDatabase.addressQuery;
                for(int i = 0; i < searchTerms.length; ++i)
                {
                    if(searchTerms[i].equals(""))
                        continue;

                    if(query.endsWith(MedDatabase.addressQuery))
                        query += " WHERE "; // если добавляем в первый раз
                    else
                        query += " AND ";   // если добавляем дополнительное условие

                    query += MedDatabase.addressCols.get("city") + " || " + MedDatabase.addressCols.get("street") + " || " + MedDatabase.addressCols.get("street_num") + " || " + MedDatabase.addressCols.get("flat") + " LIKE " + "\'%" + searchTerms[i] + "%\'";
                }

                Cursor cur = mDatabase.rawQuery(query, null);
                CursorAdapter newAddressAdapter = new AddressAdapter(this, cur);
                mAddressList.setAdapter(newAddressAdapter);

                addressAdapter.getCursor().close();
                addressAdapter = newAddressAdapter;
                break;
            case R.id.create_button:
                startActivity(new Intent(this, ManCreateActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if(findViewById(R.id.search_layout).getVisibility() == View.VISIBLE)
        {
            findViewById(R.id.search_layout).setVisibility(View.GONE);
            findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    class addressClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Cursor parentCur = addressAdapter.getCursor();
            parentCur.moveToPosition(position);

            Cursor cur = mDatabase.rawQuery(MedDatabase.mainQuery + " WHERE la." + MedDatabase.addressCols.get("ID") + "=?", new String[]{parentCur.getString(0)});
            CursorAdapter newPeopleListAdapter = new PeopleAdapter(MainActivity.this, cur);

            if(peopleAdapter != null)
                peopleAdapter.getCursor().close();

            peopleAdapter = newPeopleListAdapter;
            mPeopleList.setAdapter(peopleAdapter);

            findViewById(R.id.main_layout).setVisibility(View.GONE);
            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
        }
    }

    class manClickListener implements  AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            final ProgressDialog pd = ProgressDialog.show(view.getContext(), getString(R.string.connecting_to_card), getString(R.string.wait_please));
            final long objectID = id;

            view.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    pd.dismiss();
                    Intent intent = new Intent(MainActivity.this, ManCreateActivity.class);
                    intent.putExtra("ID", objectID);
                    startActivity(intent);
                }
            }, 2000);
        }
    }
}
