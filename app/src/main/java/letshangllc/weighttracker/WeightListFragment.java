package letshangllc.weighttracker;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeightListFragment extends Fragment {

    private WeightDBHelper weightDBHelper;
    private ArrayList<Weight> weights;
    private ListView lv_weights;
    private WeightsAdapter weightsAdapter;

    private String PREFS_NAME;
    SharedPreferences prefs;

    private AdsHelper adsHelper;

    public interface UpdateGraphListener {
        public void onDialogPositiveClick(Weight newWeight);
    }
    /*todo store recent weight */

    UpdateGraphListener mListener;

    public void setCallback(UpdateGraphListener mListener) {
        this.mListener = mListener;
    }

    public WeightListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_list, container, false);

        /* Create a db helper to access the internal db */
        PREFS_NAME = getResources().getString(R.string.Prefs_name);
        prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        float lastWeight = prefs.getFloat("lastWeight", (float) 0.0);

        weightDBHelper =  new WeightDBHelper(this.getContext());

        loadData();

        weightsAdapter = new WeightsAdapter(this.getContext(), weights);
        //weightsAdapter = new WeightsAdapter(this.getContext(), MockedData.mockedWeights);
        lv_weights = (ListView) view.findViewById(R.id.lv_weights);
        lv_weights.setAdapter(weightsAdapter);

        Button btn_enterWeight =(Button) view.findViewById(R.id.enter_weight);

        Button subWeight = (Button) view.findViewById(R.id.subWeight);

        Button addWeight = (Button) view.findViewById(R.id.addWeight);

        final EditText weightCount = (EditText) view.findViewById(R.id.Weight);
        weightCount.setText(lastWeight+"");

        final DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        btn_enterWeight.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                double weight = Double.parseDouble(weightCount.getText().toString());

                /* format double before inserting into DB */
                weightCount.setText(df.format(weight));
                weight = Double.parseDouble(weightCount.getText().toString());
                if(isDailyDouble()){
                    Toast.makeText(getContext(), "Only one weight can be recorded a day", Toast.LENGTH_SHORT).show();
                }else{
                    insertWeightIntoDB(weight);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("lastWeight", (float) weight);
                    editor.commit();

                    SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
                    Date date = new Date();
                    String formattedDate = dateFormat.format(date);
                    mListener.onDialogPositiveClick(new Weight(getLastId(), weight, formattedDate));
                }

            }
        });
        addWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString()) +.1;
                weightCount.setText(df.format(weight));
            }
        });
        subWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString()) - .1;

                if (weight > 0) {
                    weightCount.setText(df.format(weight));
                }

            }
        });
        registerForContextMenu(lv_weights);

        adsHelper = new AdsHelper(view, getResources().getString(R.string.admob_weightlist_id),this.getActivity());
        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);

        return view;
    }

    private boolean isDailyDouble(){

        SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        SQLiteDatabase db = weightDBHelper.getReadableDatabase();
        String sql  = "SELECT * FROM " + WeightTableContract.TABLE_NAME +" WHERE " +
                WeightTableContract.COLUMN_DATE +" = '" + formattedDate +"' ";

        Cursor c = db.rawQuery(sql, null);
        Log.e("DAILY DOUBLE ", "count: " +c.getCount());
        if(c.getCount()>0 ){
            db.close();
            return true;
        }

        db.close();
        return false;
    }

    private void insertWeightIntoDB(double weight){
        SQLiteDatabase writableDB = this.weightDBHelper.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        //PUT SET INTO SETS
        ContentValues values = new ContentValues();
        values.put(WeightTableContract.COLUMN_WEIGHT, weight);
        values.put(WeightTableContract.COLUMN_DATE, formattedDate);

        writableDB.insert(WeightTableContract.TABLE_NAME, null, values);
        writableDB.close();

        weights.add(0, new Weight(getLastId(), weight, formattedDate));
        weightsAdapter.notifyDataSetChanged();
    }

    private void loadData(){
        weights = new ArrayList<>();

        SQLiteDatabase db = weightDBHelper.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        String sql  = "SELECT * FROM " + WeightTableContract.TABLE_NAME +" ORDER BY " +
                WeightTableContract.COLUMN_ID +" DESC";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            //Log.i("SETSANDREPS", "Weight: " + c.getString(0) + " Reps: " + c.getString(1) + " sets: " + c.getInt(2));

            weights.add(new Weight(c.getInt(0), c.getDouble(1), c.getString(2)));
            c.moveToNext();
        }
        db.close();
    }

    private int getLastId(){
        SQLiteDatabase db = weightDBHelper.getReadableDatabase();
        String sql = "SELECT Max( "+WeightTableContract.COLUMN_ID +" ) FROM "+WeightTableContract.TABLE_NAME+ " ";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_weightlist, menu);

    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.deleteWeight:
                //Toast.makeText(this, "Deleted: " + adapter.getItem(info.position), Toast.LENGTH_SHORT).show();
                deleteFromDatabase(weightsAdapter.getItem(info.position));
                break;
            case R.id.updateWeight:
                displayDialog(weightsAdapter.getItem(info.position));
                break;
        }
        return true;
    }
    private void displayDialog(Weight weight){
        final UpdateWeightDialog updateWeightDialog = new UpdateWeightDialog();
        final Weight w = weight;

        /* Set the name for the dialog to use */
        updateWeightDialog.setWeight(weight.weight);

        /* Set the callback for when the user presses Finish */
        updateWeightDialog.setCallback(new UpdateWeightDialog.UpdateListener() {
            @Override
            public void onDialogPositiveClick(double newValue) {
                updateWeight(w, newValue);
            }
        });

        updateWeightDialog.show(getFragmentManager(), "EditWeight");
    }

    private void updateWeight(Weight weight, double newValue){

        SQLiteDatabase db = weightDBHelper.getWritableDatabase();
        /*Update the day in the Database */
        ContentValues newValues = new ContentValues();
        newValues.put(WeightTableContract.COLUMN_WEIGHT, newValue);
        db.update(WeightTableContract.TABLE_NAME, newValues, WeightTableContract.COLUMN_ID + " = " + weight.id, null);
        /*Update the day on the listview */
        weight.weight = newValue;
        weightsAdapter.notifyDataSetChanged();

        db.close();
    }

    private void deleteFromDatabase(Weight weight){
        SQLiteDatabase db = weightDBHelper.getWritableDatabase();
        int id = weight.id;
        db.delete(WeightTableContract.TABLE_NAME, WeightTableContract.COLUMN_ID + " = " + id, null);

        weights.remove(weight);

        weightsAdapter.notifyDataSetChanged();
        db.close();
    }

}
