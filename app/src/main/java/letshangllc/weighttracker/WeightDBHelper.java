package letshangllc.weighttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cvburnha on 2/12/2016.
 */
public class WeightDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "CompleteListDB";


    private static final String TABLE_WEIGHTS =
            "CREATE TABLE " + WeightTableContract.TABLE_NAME + " ("
                    + WeightTableContract.COLUMN_ID + " integer primary key AUTOINCREMENT, "
                    + WeightTableContract.COLUMN_WEIGHT + " DECIMAL, "
                    + WeightTableContract.COLUMN_DATE + " TEXT"
                    + ")";


    public WeightDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_WEIGHTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
