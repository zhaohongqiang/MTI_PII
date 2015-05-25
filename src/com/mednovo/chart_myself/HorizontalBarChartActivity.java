
package com.mednovo.chart_myself;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.mednovo.mti_pii.R;
import com.mednovo.chart_myself.notimportant.DemoBase;
import com.mednovo.tools.GeneralCommands;

import java.util.ArrayList;

public class HorizontalBarChartActivity extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    protected HorizontalBarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private TextView total_txt;
    private EditText total;
    private Button my_buttontest1;
    private TextView basal_set;
    private TextView basal_read;
    private EditText hour_value_txt[] = new EditText[24];
    private int R_id[] ={
            R.id.hour_value1,R.id.hour_value2,R.id.hour_value3,R.id.hour_value4,R.id.hour_value5,R.id.hour_value6,
            R.id.hour_value7,R.id.hour_value8,R.id.hour_value9,R.id.hour_value10,R.id.hour_value11,R.id.hour_value12,
            R.id.hour_value13,R.id.hour_value14,R.id.hour_value15,R.id.hour_value16,R.id.hour_value17,R.id.hour_value18,
            R.id.hour_value19,R.id.hour_value20,R.id.hour_value21,R.id.hour_value22,R.id.hour_value23,R.id.hour_value24
    };

    /*private int[] basal_rate_names = new int[]{
            R.id.hour_value1,R.id.hour_value2,R.id.hour_value3,R.id.hour_value4,R.id.hour_value5,R.id.hour_value6,
            R.id.hour_value7,R.id.hour_value8,R.id.hour_value9,R.id.hour_value10,R.id.hour_value11,R.id.hour_value12,
            R.id.hour_value13,R.id.hour_value14,R.id.hour_value15,R.id.hour_value16,R.id.hour_value17,R.id.hour_value18,
            R.id.hour_value19,R.id.hour_value20,R.id.hour_value21,R.id.hour_value22,R.id.hour_value23,R.id.hour_value24
    };*/
    private GridLayout basal_rate_set;

    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_horizontalbarchart);

        Intent data = getIntent();
        int set_Or_read = data.getExtras().getInt("set_Or_read");

        total = (EditText) findViewById(R.id.total);
        total_txt = (TextView) findViewById(R.id.total_txt);
        my_buttontest1 = (Button) findViewById(R.id.my_buttontest1);
        basal_set = (TextView) findViewById(R.id.basal_set);
        basal_read = (TextView) findViewById(R.id.basal_read);
        basal_rate_set = (GridLayout) findViewById(R.id.basal_rate_set);
        basal_rate_set.setVisibility(View.GONE);
        basal_read.setVisibility(View.GONE);
        basal_set.setVisibility(View.GONE);
        my_buttontest1.setVisibility(View.GONE);
        //total.setVisibility(View.GONE);
        //total_txt.setVisibility(View.GONE);

        if(set_Or_read == 4){//set
            basal_set.setVisibility(View.VISIBLE);
            //basal_rate_set.setVisibility(View.VISIBLE);
            //total.setVisibility(View.VISIBLE);
            //total_txt.setVisibility(View.VISIBLE);
            //my_buttontest1.setVisibility(View.VISIBLE);
        }else{//read
            basal_read.setVisibility(View.VISIBLE);
            basal_rate_set.setVisibility(View.GONE);
        }
        for(int i=0;i<24;i++){
            hour_value_txt[i] = (EditText) findViewById(R_id[i]);
            hour_value_txt[i].setText(GeneralCommands.BasalData_value[i]);
        }

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mChart = (HorizontalBarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setHighlightEnabled(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);

        // mChart.setDrawYLabels(false);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        //setData(24, 50);
        readData(24, 24);
        mChart.animateY(2500);

        // setting data
        mSeekBarY.setProgress(24);//50
        mSeekBarX.setProgress(24);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        // mChart.setDrawLegend(false);

        my_buttontest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.my_buttontest1:
                        //String hour_value = my_test1.getText().toString();
                        String[] hour_value_tmp = new String[24];
                        for(int i=0;i<24;i++){
                            hour_value_tmp[i] = hour_value_txt[i].getText().toString();
                        }
                        readData_test(24, hour_value_tmp);
                        mChart.animateY(2500);
                        /*Legend l = mChart.getLegend();
                        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
                        l.setFormSize(8f);
                        l.setXEntrySpace(4f);*/
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (DataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: {
                mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
                mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionToggleFilter: {

                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 25);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void readData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(hour_value[i % 24]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        float total_tmp = (float) 0.00;
        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
            //val = (float) (++i);
            val = Float.parseFloat(GeneralCommands.BasalData_value[i]);
            total_tmp += val*100;
            yVals1.add(new BarEntry(val, i));
        }
        total.setText(Float.toString(total_tmp/100));

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    private void readData_test(int count, String[] range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(hour_value[i % 24]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        float total_tmp = (float) 0.00;
        for (int i = 0; i < count; i++) {
            //float mult = (range + 1);
            //float val = (float) (Math.random() * mult);
            //val = (float) (++i);
            float val = Float.parseFloat(range[i]);
            total_tmp += val*100;
            yVals1.add(new BarEntry(val, i));
        }

        total.setText(Float.toString(total_tmp/100));

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(hour_value[i % 24]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, mChart.getData().getDataSetByIndex(dataSetIndex)
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());
    }

    public void onNothingSelected() {
    };
}
