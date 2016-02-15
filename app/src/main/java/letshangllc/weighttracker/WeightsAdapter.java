package letshangllc.weighttracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cvburnha on 10/30/2015.
 */
public class WeightsAdapter extends ArrayAdapter<Weight> {

    private static class ViewHolder {
        TextView weight;
        TextView date;
    }

    public WeightsAdapter(Context context, ArrayList<Weight> sets) {
        super(context, R.layout.item_row, sets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Weight weight  = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row, parent, false);
            viewHolder.weight = (TextView) convertView.findViewById(R.id.tv_itemweight);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tv_itemdate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object

        viewHolder.weight.setText(weight.weight+"");
        viewHolder.date.setText(weight.date+"");

        // Return the completed view to render on screen
        return convertView;
    }

}
