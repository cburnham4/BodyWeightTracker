package letshangllc.weighttracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by cvburnha on 2/14/2016.
 */
public class UpdateWeightDialog extends DialogFragment {


    private double weight;

    public interface UpdateListener {
        public void onDialogPositiveClick(double newValue);
    }


    public void setCallback(UpdateListener mListener) {
        this.mListener = mListener;
    }

    UpdateListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_updateweight, null);


        Button btn_enterWeight =(Button) view.findViewById(R.id.enter_weight);

        Button subWeight = (Button) view.findViewById(R.id.subWeight);

        Button addWeight = (Button) view.findViewById(R.id.addWeight);

        final EditText weightCount = (EditText) view.findViewById(R.id.Weight);
        weightCount.setText(this.weight + "");

        final DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

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

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        double weight = Double.parseDouble(weightCount.getText().toString());

                /* format double before inserting into DB */
                        weightCount.setText(df.format(weight));
                        weight = Double.parseDouble(weightCount.getText().toString());

                        mListener.onDialogPositiveClick(weight);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UpdateWeightDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}