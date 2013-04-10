package org.medic.SimLiKar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedHashMap;
import java.util.Map;


public class MedDatabase extends SQLiteOpenHelper
{
    static final String dbName="medDB";
    static final String peopleTable="people";
    static final Map<String, String> peopleCols = new LinkedHashMap<String, String>() // такая реализация не прерывает порядок очередности вставки
    {{
            put("ID", "_id");                            // 0
            put("lastName", "LAST_NAME");                   // 1
            put("firstName", "FIRST_NAME");                 // 2
            put("middleName", "MIDDLE_NAME");               // 3
            put("gender", "GENDER");                        // 4
            put("liveAddrID", "LIVE_ADDRESS_ID");           // 5
            put("workAddr", "WORK_PLACE");                  // 6
            put("birthDate", "BIRTH_DATE");                 // 7
            put("insuranceGroup", "INSURANCE_GROUP");       // 8
            put("insuranceNumber", "INSUR_NUMBER");         // 9
            put("signalMarks", "SIGNAL_MARKS");             // 10
            put("finalDiagnosisSheet", "FINAL_DIAG_SHEET"); // 11
            put("vaccinationInfo", "VACCINATION_INFO");     // 12
            put("profSurvey", "PROF_SURVEY");               // 13
            put("illDates", "ILL_DATES");                   // 14
            put("hospitalInfo", "HOSPITAL_INFO");           // 15
            put("illDiary", "ILL_DIARY");                   // 16
            put("yearlyEpicrisis", "YEARLY_EPIC");          // 17
            put("nextYearPlan", "NEXT_PLAN");               // 18
    }};

    static final String addressTable="address";
    static final Map<String, String> addressCols = new LinkedHashMap<String, String>()
    {{
        put("ID", "_id");                                  // 19
        put("city", "CITY_NAME");                          // 20
        put("street", "STREET_NAME");                      // 21
        put("street_num", "STREET_NUM");                   // 22
        put("flat", "FLAT");                               // 23
    }};

    static final String addressQuery = "SELECT * FROM " + addressTable;

    static final String mainQuery = "SELECT * FROM " + peopleTable + " AS p" +
            " INNER JOIN " + addressTable +" AS la ON p." + peopleCols.get("liveAddrID") + "=la." + addressCols.get("ID");

    public MedDatabase(Context context)
    {
        super(context, dbName, null, 33);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String[] peopleKeys = peopleCols.values().toArray(new String[peopleCols.values().size()]);
        String[] addressKeys = addressCols.values().toArray(new String[addressCols.values().size()]);
        sqLiteDatabase.execSQL("CREATE TABLE " + peopleTable + " ("+
                peopleKeys[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                peopleKeys[1] + " TEXT DEFAULT '', " +
                peopleKeys[2] + " TEXT DEFAULT '', " +
                peopleKeys[3] + " TEXT DEFAULT '', " +
                peopleKeys[4] + " INTEGER DEFAULT 0, " +
                peopleKeys[5] + " INTEGER DEFAULT -1, " +
                peopleKeys[6] + " TEXT DEFAULT '', " +
                peopleKeys[7] + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                peopleKeys[8] + " TEXT DEFAULT '', " +
                peopleKeys[9] + " TEXT DEFAULT '', " +
                peopleKeys[10] + " TEXT DEFAULT '', " +
                peopleKeys[11] + " TEXT DEFAULT '', " +
                peopleKeys[12] + " TEXT DEFAULT '', " +
                peopleKeys[13] + " TEXT DEFAULT '', " +
                peopleKeys[14] + " TEXT DEFAULT '', " +
                peopleKeys[15] + " TEXT DEFAULT '', " +
                peopleKeys[16] + " TEXT DEFAULT '', " +
                peopleKeys[17] + " TEXT DEFAULT '', " +
                peopleKeys[18] + " TEXT DEFAULT ''" +
                ")");

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX " + "EVERY_MAN_FOR_HIMSELF ON " + peopleTable + "(" +
                peopleCols.get("lastName") + ", " +
                peopleCols.get("firstName") + ", " +
                peopleCols.get("middleName") + ", " +
                peopleCols.get("liveAddrID") +
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + addressTable + " (" +
                addressKeys[0] +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                addressKeys[1] +" TEXT, " +
                addressKeys[2] +" TEXT, " +
                addressKeys[3] +" INTEGER, " +
                addressKeys[4] +" INTEGER" +
                ")");

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX " + "ADDRESS_UNIQUITY ON " + addressTable + "(" +
                addressCols.get("city") + ", " +
                addressCols.get("street") + ", " +
                addressCols.get("street_num") + ", " +
                addressCols.get("flat") +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
    {
    }
}
