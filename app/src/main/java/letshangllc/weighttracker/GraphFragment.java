package letshangllc.weighttracker;


import android.app.FragmentManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    private WeightDBHelper weightDBHelper;
    private ArrayList<Weight> weights;
    private LineChart chart;

    private AdsHelper adsHelper;

    /* todo remove after removed and update after update */



    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        weightDBHelper =  new WeightDBHelper(this.getContext());

        loadData();

        chart = (LineChart) view.findViewById(R.id.chart);

        /*Set callback to update graph when a new data point has been added or deleted */
        WeightListFragment weightListFragment = (WeightListFragment) getFragmentManager().getFragments().get(0);

        weightListFragment.setCallback(new WeightListFragment.UpdateGraphListener() {
            @Override
            public void onDialogPositiveClick(Weight newWeight) {
                updateGraph(newWeight);
            }
        });

        this.setData();

        adsHelper = new AdsHelper(view, getResources().getString(R.string.admob_graph_id),this.getActivity());
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

    private void loadData(){
        weights = new ArrayList<>();

        SQLiteDatabase db = weightDBHelper.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        String sql  = "SELECT * FROM " + WeightTableContract.TABLE_NAME+ " ";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            //Log.i("SETSANDREPS", "Weight: " + c.getString(0) + " Reps: " + c.getString(1) + " sets: " + c.getInt(2));

            weights.add(new Weight(c.getInt(0), c.getDouble(1), c.getString(2)));
            c.moveToNext();
        }
        db.close();
    }

    public void updateGraph(Weight weight){
        Log.e("ADD ENTRY", "ADDED ENTRY");
        LineData data =chart.getLineData();

        ILineDataSet set = data.getDataSetByIndex(0);

        // add a new x-value first
        data.addXValue(weight.date + "");

        // choose a random dataSet
        data.addEntry(new Entry((float)weight.weight, weight.id), 0);

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh
    }

    private void setData(){
        //weights = MockedData.mockedWeightsReverse;
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < weights.size(); i++) {
            xVals.add(weights.get(i).date);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < weights.size(); i++) {

            yVals.add(new Entry((float)weights.get(i).weight, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Weight");

        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);



        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets



        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);


        // set data
        //chart.getAxisLeft().setAxisMinValue((float) 100.0);

        chart.getAxisLeft().setStartAtZero(false);
        chart.getAxisRight().setStartAtZero(false);

        chart.getLegend().setEnabled(false);

        //chart.getAxisLeft().setAxisMaxValue((float) getMaxWeight() +15);
        chart.setData(data);

    }

}
