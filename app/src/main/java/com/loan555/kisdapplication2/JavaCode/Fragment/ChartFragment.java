package com.loan555.kisdapplication2.JavaCode.Fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loan555.kisdapplication2.JavaCode.ActivityKid;
import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.NumAccess;
import com.loan555.kisdapplication2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChartFragment extends Fragment {
    private static final String TAG = "KA.ChartFragment";
    private final int FILTER24HOUR = 0;
    private final int FILTER168HOUR = 1;
    private final int FILTER30DAY = 2;
    private final int FILTER90DAY = 3;
    private final int FILTERACCESS = 0;
    private final int FILTERBLOCK = 1;

    String idkid;
    PieChart pieChart;
    LineChart lineChart;
    String[] options2 = {"Kết nối", "Đã chặn"};
    TextView nav1;
    TextView nav2;
    TextView nav3;
    TextView nav4;
    Spinner spiner2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    public ChartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        lineChart = view.findViewById(R.id.lineChart);
        ActivityKid activity = (ActivityKid) getActivity();
        idkid = activity.getMyData();

        spiner2 = view.findViewById(R.id.spinner2);
        nav1 = view.findViewById(R.id.nav1);
        nav2 = view.findViewById(R.id.nav2);
        nav3 = view.findViewById(R.id.nav3);
        nav4 = view.findViewById(R.id.nav4);
        initNavListener();
        initSpiner2();
        loadPieChart(idkid);
        loadLineChartByHour(idkid,FILTERACCESS,FILTER24HOUR);

//        Cursor datablockurlchart2 = dh.getblockUrlChar(idkid,1);
//        if(datablockurlchart2!=null){
//            if (datablockurlchart2.getCount() == 0) {
//                ArrayList<Entry> values2 = new ArrayList<>();
//                for (int i = 0; i < 5; i++) {
//                    values2.add(new Entry(i, 0));
//                }
//                LineDataSet d2 = new LineDataSet(values2, "Da Chan");
//                d2.setLineWidth(2.5f);
//                d2.setCircleRadius(4.5f);
//                d2.setHighLightColor(Color.rgb(244, 117, 117));
//                d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//                d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//                d2.setDrawValues(false);
//                sets.add(d2);
//            }
//            else{
//                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                ArrayList<String> date = new ArrayList<String>();
//                LocalDateTime now = LocalDateTime.now();
//                String strDate = dtf.format(now);
//                Date datecur = new Date();
//                try {
//                    datecur = sdf.parse(strDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(datecur);
//                calendar.add(Calendar.DATE, -4);
//                Date yesterday = calendar.getTime();
//                date.add(sdf.format(yesterday));
//                for(int i=0; i<4; i++){
//                    calendar.add(Calendar.DATE, 1);
//                    Date yesterday2 = calendar.getTime();
//                    date.add(sdf.format(yesterday2));
//                }
//                ArrayList<NumAccess> listNumAccess = new ArrayList<>();
//                ArrayList<Entry> values2 = new ArrayList<>();
//                int soluong = datablockurlchart2.getColumnIndex("soluong");
//                int time = datablockurlchart2.getColumnIndex("timeStr");
//                while (datablockurlchart2.moveToNext()) {
//                    String timeStr = datablockurlchart2.getString(time);
//                    int sl = Integer.parseInt(datablockurlchart2.getString(soluong));
//                    for(int i=0; i< 5; i++){
//                        if(timeStr.equals(date.get(i))){
//                            NumAccess numAccess = new NumAccess();
//                            numAccess.setTimeStr(timeStr);
//                            numAccess.setSl(sl);
//                            numAccess.setIndex(i);
//                            listNumAccess.add(numAccess);
//                        }
//                    }
//                }
//                ArrayList<Integer> arrayListInt = new ArrayList<Integer>();
//                for(int i=0; i<5; i++){
//                    arrayListInt.add(i);
//                }
//                for(int i=0; i<listNumAccess.size();i++){
//                    arrayListInt.remove(new Integer(listNumAccess.get(i).getIndex()));
//                }
//                for(int i=0; i<arrayListInt.size(); i++){
//                    NumAccess numAccess = new NumAccess();
//                    numAccess.setTimeStr("");
//                    numAccess.setIndex(i);
//                    numAccess.setSl(0);
//                    listNumAccess.add(arrayListInt.get(i),numAccess);
//
//                }
//                for(int i=0;i<listNumAccess.size();i++){
//                    values2.add(new Entry(i,listNumAccess.get(i).getSl()));
//                }
//                LineDataSet d2 = new LineDataSet(values2, "Da Chan");
//                d2.setLineWidth(2.5f);
//                d2.setCircleRadius(4.5f);
//                d2.setHighLightColor(Color.rgb(244, 117, 117));
//                d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//                d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//                d2.setDrawValues(false);
//                sets.add(d2);
//            }
//        }

        return view;
    }

    private void resetChart() {

        lineChart.clear();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    public class ClaimsXAxisValueFormatterByHour extends ValueFormatter {

        List<String> hoursList;

        public ClaimsXAxisValueFormatterByHour(List<String> arrayOfHours) {
            this.hoursList = arrayOfHours;
        }
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Integer position = Math.round(value);
            SimpleDateFormat sdf = new SimpleDateFormat("HH");

            if (value > 1 && value < 2) {
                position = 0;
            } else if (value > 2 && value < 3) {
                position = 1;
            } else if (value > 3 && value < 4) {
                position = 2;
            } else if (value > 4 && value <= 5) {
                position = 3;
            }
            if (position < hoursList.size())
                return sdf.format(new Date((getDateInMilliSeconds(hoursList.get(position), "yyyy-MM-dd-HH"))));
            return "";
        }
    }
    public class ClaimsXAxisValueFormatter extends ValueFormatter {

        List<String> datesList;

        public ClaimsXAxisValueFormatter(List<String> arrayOfDates) {
            this.datesList = arrayOfDates;
        }
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Integer position = Math.round(value);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

            if (value > 1 && value < 2) {
                position = 0;
            } else if (value > 2 && value < 3) {
                position = 1;
            } else if (value > 3 && value < 4) {
                position = 2;
            } else if (value > 4 && value <= 5) {
                position = 3;
            }
            if (position < datesList.size())
                return sdf.format(new Date((getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd"))));
            return "";
        }
    }
    public static long getDateInMilliSeconds(String givenDateString, String format) {
        String DATE_TIME_FORMAT = format;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
    public class ClaimsYAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return value + " ";
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadLineChartByDay(String idkid, int typeOfHistory, int typeOfFilter){
        DatabaseHelper dh = DatabaseHelper.getInstance(getActivity());
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        ArrayList<String> date = new ArrayList<String>();
        Cursor datablockurlchart = null;
        if(typeOfHistory==FILTERACCESS){
            if(typeOfFilter==FILTER30DAY){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERACCESS,FILTER30DAY);
            }
            else if(typeOfFilter==FILTER90DAY){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERACCESS,FILTER90DAY);
            }
        }
        else if(typeOfHistory==FILTERBLOCK){
            if(typeOfFilter==FILTER30DAY){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERBLOCK,FILTER30DAY);
            }
            else if(typeOfFilter==FILTER90DAY){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERBLOCK,FILTER90DAY);
            }
        }
            if (datablockurlchart!= null && datablockurlchart.getCount() != 0 ) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                if(typeOfFilter==FILTER30DAY){
                    now = now.minusDays(30);
                    for(int i=0; i<30; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusDays(1);
                        date.add(strDate_i);
                    }
                }
                else if(typeOfFilter==FILTER90DAY){
                    now = now.minusDays(90);
                    for(int i=0; i<90; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusDays(1);
                        date.add(strDate_i);
                    }
                }
                int soluong = datablockurlchart.getColumnIndex("soluong");
                int time = datablockurlchart.getColumnIndex("timeStr");
                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<NumAccess> listNumAccess = new ArrayList<>();
                if(typeOfFilter==FILTER30DAY) {
                    for (int i = 0; i < 30; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                else if(typeOfFilter==FILTER90DAY){
                    for (int i = 0; i < 90; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                while (datablockurlchart.moveToNext()) {
                    String timeStr = datablockurlchart.getString(time);
                    int sl = Integer.parseInt(datablockurlchart.getString(soluong));
                    int index_i = date.indexOf(timeStr);
                    Log.d("index_i",String.valueOf(index_i));
                    if(index_i!=-1){
                        listNumAccess.get(index_i).setSl(sl);
                    }
                }
                for(int i=0;i<listNumAccess.size();i++){
                    values1.add(new Entry(i,listNumAccess.get(i).getSl()));
                }
                LineDataSet d1 = new LineDataSet(values1, "Truy Cap");
                d1.setLineWidth(1.5f);

                if(typeOfHistory==FILTERACCESS){
                    d1.setColor(Color.GREEN);
                    d1.setHighLightColor(Color.GREEN);
//                    d1.setFillColor(Color.GREEN);
                }
                else if(typeOfHistory==FILTERBLOCK){
                    d1.setColor(Color.RED);
                    d1.setHighLightColor(Color.RED);
//                    d1.setFillColor(Color.RED);
                }
                d1.setDrawValues(false);
//                d1.setDrawFilled(true);
                //set the transparency
//                d1.setFillAlpha(80);
                d1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                d1.setCubicIntensity(0.2f);
                d1.setDrawCircles(false);
                sets.add(d1);
            }
            else if(datablockurlchart==null || datablockurlchart.getCount()==0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();

                if(typeOfFilter==FILTER30DAY){
                    now = now.minusDays(30);
                    for(int i=0; i<30; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusDays(1);
                        date.add(strDate_i);
                    }
                }
                else if(typeOfFilter==FILTER90DAY){
                    now = now.minusDays(90);
                    for(int i=0; i<90; i++){
                        String strDate_i = dtf.format(now);
                        now = now.minusDays(1);
                        date.add(strDate_i);
                    }
                }
                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<NumAccess> listNumAccess = new ArrayList<>();
                if(typeOfFilter==FILTER30DAY) {
                    for (int i = 0; i < 30; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                else if(typeOfFilter==FILTER90DAY){
                    for (int i = 0; i < 90; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                for(int i=0;i<listNumAccess.size();i++){
                    values1.add(new Entry(i,listNumAccess.get(i).getSl()));
                }
                LineDataSet d1 = new LineDataSet(values1, "Truy Cap");
                d1.setLineWidth(1.5f);

                if(typeOfHistory==FILTERACCESS){
                    d1.setColor(Color.GREEN);
                    d1.setHighLightColor(Color.GREEN);
//                    d1.setFillColor(Color.GREEN);
                }
                else if(typeOfHistory==FILTERBLOCK){
                    d1.setColor(Color.RED);
                    d1.setHighLightColor(Color.RED);
//                    d1.setFillColor(Color.RED);
                }
                d1.setDrawValues(false);
//                d1.setDrawFilled(true);
                //set the transparency
//                d1.setFillAlpha(80);
                d1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                d1.setCubicIntensity(0.2f);
                d1.setDrawCircles(false);
                sets.add(d1);
            }
            XAxis xAxis = lineChart.getXAxis();
            YAxis leftAxis = lineChart.getAxisLeft();
            YAxis rightAxis = lineChart.getAxisRight();
            XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
            xAxis.setPosition(position);
            rightAxis.setEnabled(false);

            Description description = new Description();

            description.setTextSize(15f);
            xAxis.setAxisMinimum(0f);
            leftAxis.setAxisMinimum(0f);

            xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(date));
            leftAxis.setValueFormatter(new ClaimsYAxisValueFormatter());
            LineData data = new LineData(sets);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.setDrawGridBackground(false);
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
//            lineChart.setBackgroundColor(Color.GRAY);
            lineChart.setData(data);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadLineChartByHour(String idkid, int typeOfHistory, int typeOfFilter){
        DatabaseHelper dh = DatabaseHelper.getInstance(getActivity());
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        ArrayList<String> date = new ArrayList<String>();
        Cursor datablockurlchart = null;
        if(typeOfHistory==FILTERACCESS){
            if(typeOfFilter==FILTER24HOUR){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERACCESS,FILTER24HOUR);
            }
            else if(typeOfFilter==FILTER168HOUR){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERACCESS,FILTER168HOUR);
            }
        }
        else if(typeOfHistory==FILTERBLOCK){
            if(typeOfFilter==FILTER24HOUR){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERBLOCK,FILTER24HOUR);
            }
            else if(typeOfFilter==FILTER168HOUR){
                datablockurlchart = dh.getblockUrlChart(idkid,FILTERBLOCK,FILTER168HOUR);
            }
        }
            if (datablockurlchart!= null && datablockurlchart.getCount() != 0) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
                LocalDateTime now = LocalDateTime.now();
                if(typeOfFilter==FILTER24HOUR){
                    now = now.minusHours(24);
                    for(int i=0; i<24; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusHours(1);
                        date.add(strDate_i);
                    }
                }
                else if(typeOfFilter==FILTER168HOUR){
                    now = now.minusHours(168);
                    for(int i=0; i<168; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusHours(1);
                        date.add(strDate_i);
                    }
                }
                int soluong = datablockurlchart.getColumnIndex("soluong");
                int timeLong = datablockurlchart.getColumnIndex("timeLong");
                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<NumAccess> listNumAccess = new ArrayList<>();
                if(typeOfFilter==FILTER24HOUR) {
                    for (int i = 0; i < 24; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                else if(typeOfFilter==FILTER168HOUR){
                    for (int i = 0; i < 168; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
                while (datablockurlchart.moveToNext()) {
                    Long time = Long.parseLong(datablockurlchart.getString(timeLong));
                    Log.d("TimeLong",datablockurlchart.getString(timeLong));
                    String timeLongStr = sdf.format(new Date(time));
                    int sl = Integer.parseInt(datablockurlchart.getString(soluong));
                    int index_i = date.indexOf(timeLongStr);
                    Log.d("index_i",String.valueOf(index_i));
                    if(index_i!=-1){
                        listNumAccess.get(index_i).setSl(sl);
                    }
                }
                for(int i=0;i<listNumAccess.size();i++){
                    values1.add(new Entry(i,listNumAccess.get(i).getSl()));
                }
                LineDataSet d1 = new LineDataSet(values1, "Truy Cap");
                d1.setLineWidth(1.5f);

                //to fill the below of smooth line in graph
                if(typeOfHistory==FILTERACCESS){
                    d1.setColor(Color.GREEN);
                    d1.setHighLightColor(Color.GREEN);
//                    d1.setFillColor(Color.GREEN);
                }
                else if(typeOfHistory==FILTERBLOCK){
                    d1.setColor(Color.RED);
                    d1.setHighLightColor(Color.RED);
//                    d1.setFillColor(Color.RED);
                }
                d1.setDrawValues(false);
//                d1.setDrawFilled(true);
                //set the transparency
//                d1.setFillAlpha(80);
                d1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                d1.setCubicIntensity(0.2f);
                d1.setDrawCircles(false);
                sets.add(d1);
            }
            else if(datablockurlchart==null || datablockurlchart.getCount() == 0){
                Log.d("data null","data hour null");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
                LocalDateTime now = LocalDateTime.now();

                if(typeOfFilter==FILTER24HOUR){
                    now = now.minusHours(24);
                    for(int i=0; i<24; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusHours(1);
                        date.add(strDate_i);
                    }
                }
                else if(typeOfFilter==FILTER168HOUR){
                    now = now.minusHours(168);
                    for(int i=0; i<168; i++){
                        String strDate_i = dtf.format(now);
                        now = now.plusHours(1);
                        date.add(strDate_i);
                    }
                }
                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<NumAccess> listNumAccess = new ArrayList<>();
                if(typeOfFilter==FILTER24HOUR) {
                    for (int i = 0; i < 24; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                else if(typeOfFilter==FILTER168HOUR){
                    for (int i = 0; i < 168; i++) {
                        NumAccess numAccess = new NumAccess();
                        numAccess.setTimeStr(date.get(i));
                        numAccess.setSl(0);
                        listNumAccess.add(numAccess);
                    }
                }
                for(int i=0;i<listNumAccess.size();i++){
                    values1.add(new Entry(i,listNumAccess.get(listNumAccess.size()-i-1).getSl()));
                }
                LineDataSet d1 = new LineDataSet(values1, "Truy Cap");
                d1.setLineWidth(1.5f);

                if(typeOfHistory==FILTERACCESS){
                    d1.setColor(Color.GREEN);
                    d1.setHighLightColor(Color.GREEN);
//                    d1.setFillColor(Color.GREEN);
                }
                else if(typeOfHistory==FILTERBLOCK){
                    d1.setColor(Color.RED);
                    d1.setHighLightColor(Color.RED);
//                    d1.setFillColor(Color.RED);
                }
                d1.setDrawValues(false);
//                d1.setDrawFilled(true);
                //set the transparency
//                d1.setFillAlpha(80);
                d1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                d1.setCubicIntensity(0.2f);
                d1.setDrawCircles(false);
                sets.add(d1);
            }
            XAxis xAxis = lineChart.getXAxis();
            YAxis leftAxis = lineChart.getAxisLeft();
            YAxis rightAxis = lineChart.getAxisRight();
            XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
            xAxis.setPosition(position);
            rightAxis.setEnabled(false);

            Description description = new Description();

            description.setTextSize(15f);
            xAxis.setAxisMinimum(0f);
            leftAxis.setAxisMinimum(0f);

            xAxis.setValueFormatter(new ClaimsXAxisValueFormatterByHour(date));
            leftAxis.setValueFormatter(new ClaimsYAxisValueFormatter());

            LineData data = new LineData(sets);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.setDrawGridBackground(false);
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
//            lineChart.setBackgroundColor(Color.GRAY);
            lineChart.setData(data);

    }
    public void loadPieChart(String idkid){
        DatabaseHelper dh = DatabaseHelper.getInstance(getActivity());
        ArrayList<PieEntry> apps = new ArrayList<>();
        Cursor dataappchart = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dataappchart = dh.getAppChart(idkid);
        }
        Cursor tsoluong = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            tsoluong = dh.getAppChart(idkid);
        }
        if(dataappchart!=null) {
            if (dataappchart.getCount() == 0) {
                Log.d("Lỗi", "Không có dữ liệu của getAppChart");
                apps.add(new PieEntry(0, "Không có dữ liệu"));
                PieDataSet pieDataSet = new PieDataSet(apps, "");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("Ứng dụng");
                pieChart.animate();
            } else {
                int nameapp = dataappchart.getColumnIndex("nameapp");
                int soluong = dataappchart.getColumnIndex("soluong");
                int tongsoluong = 0;
                //
                while (tsoluong.moveToNext()) {
                    if (!tsoluong.getString(nameapp).equals("root")&&!tsoluong.getString(nameapp).equals("-1")) {
                        tongsoluong += Integer.parseInt(tsoluong.getString(soluong));
                    }
                }
                Log.d("tổng số lượng",String.valueOf(tongsoluong));
                while (dataappchart.moveToNext()) {
                    if (!dataappchart.getString(nameapp).equals("root")&&!dataappchart.getString(nameapp).equals("-1")) {
                        String tenapp = dataappchart.getString(nameapp);
                        float phantram = Math.round(Integer.parseInt(dataappchart.getString(soluong))*100/tongsoluong * 10) / 10;
                        Log.d("phan tram",String.valueOf(phantram));
                        apps.add(new PieEntry(phantram, tenapp));
                    }
                }
                PieDataSet pieDataSet = new PieDataSet(apps, "");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);
                //pieChart.setDrawSliceText(false);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("Ứng dụng");
                pieChart.getData().setDrawValues(false);
                pieChart.setDrawEntryLabels(false);
                pieChart.animate();
            }
        }

        pieChart.invalidate();
    }
    private void initSpiner2() {
        ArrayAdapter<String> adapter2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, options2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner2.setAdapter(adapter2);
        spiner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: Code here
                resetChart();
                if(spiner2.getSelectedItem().toString().contains("Kết nối")){

                    if(nav1.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByHour(idkid,FILTERACCESS,FILTER24HOUR);
                        }
                    }
                    else if(nav2.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByHour(idkid,FILTERACCESS,FILTER168HOUR);
                        }
                    }
                    else if(nav3.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByDay(idkid,FILTERACCESS,FILTER30DAY);
                        }
                    }
                    else if(nav4.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByDay(idkid,FILTERACCESS,FILTER90DAY);
                        }
                    }
                }
                else{
                    if(nav1.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByHour(idkid,FILTERBLOCK,FILTER24HOUR);
                        }
                    }
                    else if(nav2.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByHour(idkid,FILTERBLOCK,FILTER168HOUR);
                        }
                    }
                    else if(nav3.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByDay(idkid,FILTERBLOCK,FILTER30DAY);
                        }
                    }
                    else if(nav4.isSelected()==true){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            loadLineChartByDay(idkid,FILTERBLOCK,FILTER90DAY);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO: Code here
                resetChart();
                if(nav1.isSelected()==true){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERACCESS,FILTER24HOUR);
                    }
                }
                else if(nav2.isSelected()==true){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERACCESS,FILTER168HOUR);
                    }
                }
                else if(nav3.isSelected()==true){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERACCESS,FILTER30DAY);
                    }
                }
                else if(nav4.isSelected()==true){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERACCESS,FILTER90DAY);
                    }
                }

            }

        });
    }

    private void initNavListener() {
        nav1.setSelected(true);
        nav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(1);
            }
        });
        nav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(2);
            }
        });
        nav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(3);
            }
        });
        nav4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemSelected(4);
            }
        });
    }

    private void setItemSelected(Integer p) {
        nav1.setSelected(false);
        nav2.setSelected(false);
        nav3.setSelected(false);
        nav4.setSelected(false);
        switch (p) {
            case 2: {
                nav2.setSelected(true);
                //TODO: nav4 selected
                resetChart();
                if(spiner2.getSelectedItem().toString().equals("Đã chặn")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERBLOCK,FILTER168HOUR);
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERACCESS,FILTER168HOUR);
                    }
                }
                break;
            }
            case 3: {
                nav3.setSelected(true);
                //TODO: nav3 selected
                resetChart();
                if(spiner2.getSelectedItem().toString().equals("Đã chặn")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERBLOCK,FILTER30DAY);
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERACCESS,FILTER30DAY);
                    }
                }
                break;
            }
            case 4: {
                nav4.setSelected(true);
                //TODO: nav2 selected
                resetChart();
                if(spiner2.getSelectedItem().toString().equals("Đã chặn")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERBLOCK,FILTER90DAY);
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByDay(idkid,FILTERACCESS,FILTER90DAY);
                    }
                }
                break;
            }
            default: {
                nav1.setSelected(true);
                resetChart();
                if(spiner2.getSelectedItem().toString().equals("Đã chặn")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERBLOCK,FILTER24HOUR);
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadLineChartByHour(idkid,FILTERACCESS,FILTER24HOUR);
                    }
                }
                //TODO: nav1 selected
            }
        }
    }
}
