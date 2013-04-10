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
    Button mSave, mHideCover;
    EditText mLastName;
    EditText mFirstName;
    EditText mMiddleName;
    Spinner mGender;
    DatePicker mBirthDate;
    EditText mInsurGroup;
    EditText mInsurNum;
    EditText mWorkAddr;

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
        mHideCover = (Button) findViewById(R.id.cover_hide_button);
        mHideCover.setOnClickListener(this);
        mLastName = (EditText) findViewById(R.id.last_name);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mMiddleName = (EditText) findViewById(R.id.middle_name);
        mGender = (Spinner) findViewById(R.id.gender_spinner);
        mGender.setOnItemSelectedListener(mGenderListener);
        mBirthDate = (DatePicker) findViewById(R.id.birth_date);
        mWorkAddr = (EditText) findViewById(R.id.work_address);
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
        else
            mHideCover.performClick();

    }

    private void prepareUI(Cursor dataCursor) throws ParseException
    {
        mLastName.setText(dataCursor.getString(1));
        mFirstName.setText(dataCursor.getString(2));
        mMiddleName.setText(dataCursor.getString(3));
        mGender.setSelection(dataCursor.getInt(4));
        mGenderListener.selectedId = dataCursor.getInt(4);

        // 5 - извлекаются далее
        mWorkAddr.setText(dataCursor.getString(6));
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(frm.parse(dataCursor.getString(7)));
        mBirthDate.updateDate(birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH), birthDate.get(Calendar.DAY_OF_MONTH));

        mInsurGroup.setText(dataCursor.getString(8));
        mInsurNum.setText(dataCursor.getString(9));

        // 5
        EditText city = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.city_text);
        EditText street = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.street);
        EditText street_num = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.street_num_text);
        EditText flat = (EditText) findViewById(R.id.live_address_layout).findViewById(R.id.flat_num);
        city.setText(dataCursor.getString(20));
        street.setText(dataCursor.getString(21));
        street_num.setText(dataCursor.getString(22));
        flat.setText(Integer.toString(dataCursor.getInt(23)));

        // 6

        LinearLayout signals = (LinearLayout) mDataLayout.findViewWithTag("1");
        deserializeTexts(dataCursor.getString(10), signals);
        LinearLayout diagnosis = (LinearLayout) mDataLayout.findViewWithTag("2");
        deserializeTexts(dataCursor.getString(11), diagnosis);
        LinearLayout vaccination_info = (LinearLayout) mDataLayout.findViewWithTag("3");
        deserializeTexts(dataCursor.getString(12), vaccination_info);
        LinearLayout prof_survey = (LinearLayout) mDataLayout.findViewWithTag("4");
        deserializeTexts(dataCursor.getString(13), prof_survey);
        LinearLayout unwork_data = (LinearLayout) mDataLayout.findViewWithTag("5");
        deserializeTexts(dataCursor.getString(14), unwork_data);
        LinearLayout hospital_info = (LinearLayout) mDataLayout.findViewWithTag("6");
        deserializeTexts(dataCursor.getString(15), hospital_info);
        LinearLayout patient_diary = (LinearLayout) mDataLayout.findViewWithTag("7");
        deserializeTexts(dataCursor.getString(16), patient_diary);
        LinearLayout yearly_epic = (LinearLayout) mDataLayout.findViewWithTag("8");
        deserializeTexts(dataCursor.getString(17), yearly_epic);
        LinearLayout nextyear_plan = (LinearLayout) mDataLayout.findViewWithTag("9");
        deserializeTexts(dataCursor.getString(18), nextyear_plan);

        // Обложка
        ((TextView)findViewById(R.id.fio_cover_text)).setText(mLastName.getText() + " " + mFirstName.getText() + " " + mMiddleName.getText());
        ((TextView)findViewById(R.id.gender_cover_text)).setText(getResources().getStringArray(R.array.genders)[mGenderListener.selectedId]);
        ((TextView)findViewById(R.id.birth_cover_text)).setText(frm.format(birthDate.getTime()));
        ((TextView)findViewById(R.id.live_cover_text)).setText(getString(R.string.city) + dataCursor.getString(20) + " " + getString(R.string.street_hint) + " " + dataCursor.getString(21) + " " + dataCursor.getString(22) + " " + getString(R.string.flat_hint) + dataCursor.getString(23));
        ((TextView)findViewById(R.id.work_cover_text)).setText(mWorkAddr.getText());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cover_hide_button:
            {
                findViewById(R.id.cover_layout).setVisibility(View.GONE);
                findViewById(R.id.patient_data).setVisibility(View.VISIBLE);
            }
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

                if(mID != -1)
                    CV.put(MedDatabase.peopleCols.get("ID"), mID);

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

                if(!mWorkAddr.getText().toString().isEmpty())
                    CV.put(MedDatabase.peopleCols.get("workAddr"), mWorkAddr.getText().toString());

                // Адрес проживания
                String[] columns = new String[]{MedDatabase.addressCols.get("ID")};
                String selection = MedDatabase.addressCols.get("city") + "=? AND " + MedDatabase.addressCols.get("street") + "=? AND " + MedDatabase.addressCols.get("street_num") + "=? AND " + MedDatabase.addressCols.get("flat") + "=?";
                String[] projection = new String[]
                {
                    ((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.city_text)).getText().toString(),
                    ((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.street)).getText().toString(),
                    ((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.street_num_text)).getText().toString(),
                    ((EditText)findViewById(R.id.live_address_layout).findViewById(R.id.flat_num)).getText().toString()
                };

                Cursor cur = mDB.query(MedDatabase.addressTable, columns, selection, projection, null, null, null);
                if(cur.moveToFirst()) // есть такой адрес
                    CV.put(MedDatabase.peopleCols.get("liveAddrID"), cur.getInt(0));
                else
                {
                    ContentValues addrCV = new ContentValues();
                    addrCV.put(MedDatabase.addressCols.get("city"), projection[0]);
                    addrCV.put(MedDatabase.addressCols.get("street"), projection[1]);
                    addrCV.put(MedDatabase.addressCols.get("street_num"), projection[2]);
                    addrCV.put(MedDatabase.addressCols.get("flat"), projection[3]);
                    long row = mDB.insert(MedDatabase.addressTable, null, addrCV);

                    CV.put(MedDatabase.peopleCols.get("liveAddrID"), row);
                }
                cur.close();

                LinearLayout signals = (LinearLayout) mDataLayout.findViewWithTag("1");
                CV.put(MedDatabase.peopleCols.get("signalMarks"), serializeTexts(signals));
                LinearLayout diagnosis = (LinearLayout) mDataLayout.findViewWithTag("2");
                CV.put(MedDatabase.peopleCols.get("finalDiagnosisSheet"), serializeTexts(diagnosis));
                LinearLayout vaccination_info = (LinearLayout) mDataLayout.findViewWithTag("3");
                CV.put(MedDatabase.peopleCols.get("vaccinationInfo"), serializeTexts(vaccination_info));
                LinearLayout prof_survey = (LinearLayout) mDataLayout.findViewWithTag("4");
                CV.put(MedDatabase.peopleCols.get("profSurvey"), serializeTexts(prof_survey));
                LinearLayout unwork_data = (LinearLayout) mDataLayout.findViewWithTag("5");
                CV.put(MedDatabase.peopleCols.get("illDates"), serializeTexts(unwork_data));
                LinearLayout hospital_info = (LinearLayout) mDataLayout.findViewWithTag("6");
                CV.put(MedDatabase.peopleCols.get("hospitalInfo"), serializeTexts(hospital_info));
                LinearLayout patient_diary = (LinearLayout) mDataLayout.findViewWithTag("7");
                CV.put(MedDatabase.peopleCols.get("illDiary"), serializeTexts(patient_diary));
                LinearLayout yearly_epic = (LinearLayout) mDataLayout.findViewWithTag("8");
                CV.put(MedDatabase.peopleCols.get("yearlyEpicrisis"), serializeTexts(yearly_epic));
                LinearLayout nextyear_plan = (LinearLayout) mDataLayout.findViewWithTag("9");
                CV.put(MedDatabase.peopleCols.get("nextYearPlan"), serializeTexts(nextyear_plan));

                if(mID == -1) // создаем
                {
                    long row = mDB.insert(MedDatabase.peopleTable, null, CV);
                    if(row < 0)
                        Toast.makeText(this, R.string.error_already_exists, Toast.LENGTH_SHORT).show();
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.success_create, Toast.LENGTH_SHORT).show();
                        mDB.close();
                        finish();
                    }
                }
                else  // сохраняем
                {
                    long row = mDB.replace(MedDatabase.peopleTable, null, CV);
                    if(row < 0)
                        Toast.makeText(this, R.string.error_cannot_save, Toast.LENGTH_SHORT).show();
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.success_save, Toast.LENGTH_SHORT).show();
                        mDB.close();
                        finish();
                    }
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
        if(strings.length == 0)
            return; // пустой текст

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