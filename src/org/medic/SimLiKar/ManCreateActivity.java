package org.medic.SimLiKar;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ManCreateActivity extends Activity implements View.OnClickListener
{
    private final int MAX_STEP = 1;

    Integer mCurrentStep = 0;
    LinearLayout mDataLayout;

    long mID = -1;
    Cursor dataCursor;
    SQLiteDatabase mDB;

    Button mNext;
    Button mBack;
    Button mSave;
    EditText mLastName;
    EditText mFirstName;
    EditText mMiddleName;
    Spinner mGender;
    DatePicker mBirthDate;
    EditText mInsurGroup;
    EditText mInsurNum;

    SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private class GenderListener implements AdapterView.OnItemSelectedListener
    {
        public int selectedId = 0;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selectedId = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            selectedId = 0;
        }
    }
    GenderListener mGenderListener = new GenderListener();


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.man_edit_activity);

        mDataLayout = (LinearLayout) findViewById(R.id.data_layout);
        mDB = new MedDatabase(this).getWritableDatabase();

        mNext = (Button) findViewById(R.id.next_button);
        mNext.setOnClickListener(this);
        mBack = (Button) findViewById(R.id.back_button);
        mBack.setOnClickListener(this);
        mSave = (Button) findViewById(R.id.save_button);
        mSave.setOnClickListener(this);
        mLastName = (EditText) findViewById(R.id.last_name);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mMiddleName = (EditText) findViewById(R.id.middle_name);
        mGender = (Spinner) findViewById(R.id.gender_spinner);
        mGender.setOnItemSelectedListener(mGenderListener);
        mBirthDate = (DatePicker) findViewById(R.id.birth_date);
        mInsurGroup = (EditText) findViewById(R.id.insurance_group);
        mInsurNum = (EditText) findViewById(R.id.insurance_num);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("ID"))
        {
            mID = getIntent().getLongExtra("ID", -1);
            dataCursor = mDB.rawQuery(MedDatabase.mainQuery + " WHERE p._id=?", new String[]{Long.toString(mID)});
            dataCursor.moveToFirst();
            try
            {
                prepareUI(dataCursor);
            }
            catch (Exception ignored) {}

            dataCursor.close();
        }

    }

    private void prepareUI(Cursor dataCursor) throws ParseException
    {
        mLastName.setText(dataCursor.getString(1));
        mFirstName.setText(dataCursor.getString(2));
        mMiddleName.setText(dataCursor.getString(3));
        mGender.setSelection(dataCursor.getInt(4));
        // 5 и 6 - извлекаются далее
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(frm.parse(dataCursor.getString(7)));
        mBirthDate.updateDate(birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH), birthDate.get(Calendar.DAY_OF_MONTH));

        mInsurGroup.setText(dataCursor.getString(8));
        mInsurNum.setText(dataCursor.getString(9));

        // 5
        EditText street = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.street);
        EditText flat = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.flat_num);
        street.setText(dataCursor.getString(19));
        flat.setText(Integer.toString(dataCursor.getInt(20)));

        // 6
        street = (EditText) findViewById(R.id.work_address_layout).findViewById(R.id.street);
        flat = (EditText) findViewById(R.id.work_address_layout).findViewById(R.id.flat_num);
        if(!dataCursor.isNull(21))
        {
            street.setText(dataCursor.getString(22));
            flat.setText(Integer.toString(dataCursor.getInt(23)));
        }

        LinearLayout signals = (LinearLayout) mDataLayout.findViewWithTag("1");
        deserializeTexts(dataCursor.getString(10), signals);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_button:
            {
                LinearLayout thisStep = (LinearLayout) mDataLayout.findViewWithTag(mCurrentStep.toString());
                View prevStep = mDataLayout.findViewWithTag(Integer.toString(mCurrentStep - 1));
                if(prevStep == null)
                    return;

                mCurrentStep--;
                thisStep.setVisibility(View.GONE);
                prevStep.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.next_button:
            {
                LinearLayout thisStep = (LinearLayout) mDataLayout.findViewWithTag(mCurrentStep.toString());
                View nextStep = mDataLayout.findViewWithTag(Integer.toString(mCurrentStep + 1));
                if(nextStep == null)
                    return;

                mCurrentStep++;
                thisStep.setVisibility(View.GONE);
                nextStep.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.save_button:
                ContentValues CV = new ContentValues();

                if(!mFirstName.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("firstName"), mFirstName.getText().toString());

                if(!mMiddleName.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("middleName"), mMiddleName.getText().toString());

                if(!mLastName.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("lastName"), mLastName.getText().toString());

                Calendar birthDate = Calendar.getInstance();
                birthDate.set(mBirthDate.getYear(), mBirthDate.getMonth(), mBirthDate.getDayOfMonth());
                CV.put(MedDatabase.peopleCols.get("birthDate"), frm.format(birthDate.getTime()));

                CV.put(MedDatabase.peopleCols.get("gender"), mGenderListener.selectedId);

                if(!mInsurGroup.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("insuranceGroup"), mInsurGroup.getText().toString());

                if(!mInsurNum.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("insuranceNumber"), mInsurNum.getText().toString());

                // Адрес проживания
                String[] columns = new String[]{MedDatabase.addressCols.get("ID")};
                String selection = MedDatabase.addressCols.get("street") + "=? AND " + MedDatabase.addressCols.get("flat") + "=?";
                String[] projection = new String[]{((EditText)findViewById(R.id.work_address_layout).findViewById(R.id.street)).getText().toString(), ((EditText)findViewById(R.id.work_address_layout).findViewById(R.id.flat_num)).getText().toString()};
                Cursor cur = mDB.query(MedDatabase.addressTable, columns, selection, projection, null, null, null);
                if(cur.moveToFirst()) // есть такой адрес
                    CV.put(MedDatabase.peopleCols.get("liveAddrID"), cur.getInt(0));
                else
                {
                    ContentValues addrCV = new ContentValues();
                    addrCV.put(MedDatabase.addressCols.get("street"), projection[0]);
                    addrCV.put(MedDatabase.addressCols.get("flat"), projection[1]);
                    long row = mDB.insert(MedDatabase.addressTable, null, addrCV);

                    CV.put(MedDatabase.peopleCols.get("liveAddrID"), row);
                }
                cur.close();

                // Адрес работы
                projection = new String[]{((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.street)).getText().toString(), ((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.flat_num)).getText().toString()};
                cur = mDB.query(MedDatabase.addressTable, columns, selection, projection, null, null, null);
                if(cur.moveToFirst()) // есть такой адрес
                    CV.put(MedDatabase.peopleCols.get("workAddrID"), cur.getInt(0));
                else
                {
                    ContentValues addrCV = new ContentValues();
                    addrCV.put(MedDatabase.addressCols.get("street"), projection[0]);
                    addrCV.put(MedDatabase.addressCols.get("flat"), projection[1]);
                    long row = mDB.insert(MedDatabase.addressTable, null, addrCV);

                    CV.put(MedDatabase.peopleCols.get("workAddrID"), row);
                }
                cur.close();

                LinearLayout signals = (LinearLayout) mDataLayout.findViewWithTag("1");
                CV.put(MedDatabase.peopleCols.get("signalMarks"), serializeTexts(signals));


                long row = mDB.insert(MedDatabase.peopleTable, null, CV);
                if(row < 0)
                    Toast.makeText(this, R.string.error_already_exists, Toast.LENGTH_SHORT).show();
                else
                {
                    mDB.close();
                    finish();
                }

                break;
        }
    }

    public String serializeTexts(LinearLayout currentStep)
    {
        String result = "";
        for(int j = 0; j < currentStep.getChildCount(); j++)
        {
            View edit = currentStep.getChildAt(j);
            if(edit instanceof EditText)
            {
                result += ((EditText) edit).getText().toString();
                if(j + 1 < currentStep.getChildCount())
                    result += ">>delimiter<<";
            }
        }
        return result;
    }

    public void deserializeTexts(String input, LinearLayout output)
    {
        String[] strings = input.split(">>delimiter<<");

        int pairIndex = 0;
        for(int j = 0; j < output.getChildCount(); j++)
        {
            View edit = output.getChildAt(j);
            if(edit instanceof EditText)
            {
                ((EditText) edit).setText(strings[pairIndex]);

                if(++pairIndex == strings.length)
                    break;
            }
        }
    }
}