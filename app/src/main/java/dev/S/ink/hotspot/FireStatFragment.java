package dev.S.ink.hotspot;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FireStatFragment extends Fragment {

    Context context;

    TextView stat_title;
    TextView stat_total;
    BarChart bar_chart;
    Button sel_year_btn;

    int selected_year;
    int[] fire_spot_json;
    int year_json;

    public FireStatFragment() {
        // Required empty public constructor
    }
    public static Fragment newInstance() {
        FireStatFragment f = new FireStatFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_firestat, container, false);

        stat_title = rootview.findViewById(R.id.stat_title);
        //stat_title.setVisibility(View.INVISIBLE);

        stat_total = rootview.findViewById(R.id.stat_total);

        bar_chart = rootview.findViewById(R.id.bar_chart);
        bar_chart.setVisibility(View.INVISIBLE);

        sel_year_btn = rootview.findViewById(R.id.select_year_btn);
        sel_year_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickerDialog();
            }
        });

        // Inflate the layout for this fragment
        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    private int getThisYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private void setYear(int y){
        stat_title.setText(String.format("%s\t%s", getString(R.string.stat_title), y));
        stat_title.setTextSize(30);
    }

    private void CallJsonFireStat() {
        String url = "http://tatam.esy.es/api.php?key=stat";
        Log.d("firestatink", "CallJsonFireStat: ");
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        fire_spot_json = new int[13];
                        Log.i("firestatink", "onResponse: "+response);
                        try {
                            JSONObject job = new JSONObject(response);
                            JSONArray arr = job.getJSONArray("posts");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                String num = Integer.toString(i+1);
                                if(o.has(num)){
                                    JSONObject datares = o.getJSONObject(Integer.toString(i + 1));
                                    fire_spot_json[i] = Integer.parseInt((String) datares.get("num"));
                                }else if(o.has("year")){
                                    year_json = Integer.parseInt((String)o.get("year"));
                                }
                                Log.i("firestatink", "onResponse: noooooo");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.i("firestatink", "onErrorResponse: ");
                        // error
                    }
                }
        );
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(postRequest);
    }
    private int[] getData(){
        CallJsonFireStat();
        setTotalSpot(fire_spot_json);
        return fire_spot_json;
    }
    private void setTotalSpot(int[] spot){
        int sum = 0;
        for(int i = 0;i<spot.length;i++){
            sum+=spot[i];
        }
        stat_total.setText(String.format("%s\t%s %s",getString(R.string.total),sum,"จุด"));
    }

    private void setBarChartData(int[] raw_data){

        List<BarEntry> bar_entries = new ArrayList<>();
        for(int i = 0;i<raw_data.length;i++) {
                // turn your data into Entry objects
            bar_entries.add(new BarEntry(i, raw_data[i]));
        }

        BarDataSet bar_dataSet = new BarDataSet(bar_entries,"จำนวนไฟป่าที่เกิดขึ้น");
        bar_dataSet.setColors(Color.parseColor("#90cc00"));
        bar_dataSet.setValueTextColor(Color.parseColor("#b4004e"));
        bar_dataSet.setValueTextSize(10);

        BarData bar_data = new BarData(bar_dataSet);
        bar_chart.setData(bar_data);

        //Y-Axis
        YAxis right = bar_chart.getAxisRight();
        right.setEnabled(false);
        YAxis left = bar_chart.getAxisLeft();
        left.setEnabled(false);
        left.setAxisMinimum(0f);
        //left.setValueFormatter(new MyYAxisValueFormatter());

        //X-Axis
        String[] x_values = new String[]{"ม.ค.","ก.พ.","มี.ค.","เม.ย.","พ.ค.","มิ.ย.","ก.ค.","ส.ค.","ก.ย.","ต.ค.","พ.ย.","ธ.ค."};
        XAxis xAxis = bar_chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(x_values));
        xAxis.setLabelCount(12);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //xAxis.setLabelRotationAngle(-90);

    }
    private class MyYAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {

            // format values to 1 decimal digit
            mFormat = new DecimalFormat("###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mFormat.format(value);
        }

    }
    private class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }


    }
    private void setBarChartStyle(){
        bar_chart.setDrawGridBackground(false);//no grid
        bar_chart.setFitBars(true);
        bar_chart.setDrawBorders(false);//no border
        bar_chart.getDescription().setText("");
        bar_chart.animateY(1000);//animation
    }

    private void showPickerDialog(){
        RelativeLayout linearLayout = new RelativeLayout(context);
        final NumberPicker aNumberPicker = new NumberPicker(context);
        aNumberPicker.setClickable(false);
        aNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        aNumberPicker.setMaxValue(getThisYear());
        aNumberPicker.setMinValue(2017);
        selected_year = 2017;
        aNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selected_year = i1;
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("เลือกปี");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setYear(selected_year);
                                Toast.makeText(context,""+selected_year,Toast.LENGTH_SHORT).show();
                                setBarChartData(getData());
                                bar_chart.setVisibility(View.VISIBLE);
                                setBarChartStyle();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
