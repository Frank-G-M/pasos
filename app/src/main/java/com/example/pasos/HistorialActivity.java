package com.example.pasos;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private FirebaseHelper firebaseHelper;
    private BarChart barChartSteps;
    private TextView[] stepTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        firebaseHelper = new FirebaseHelper();
        initializeViews();
        loadWeeklyData();
    }

    private void initializeViews() {
        barChartSteps = findViewById(R.id.barChartSteps);

        stepTextViews = new TextView[]{
                findViewById(R.id.tvStepsMon),
                findViewById(R.id.tvStepsTue),
                findViewById(R.id.tvStepsWed),
                findViewById(R.id.tvStepsThu),
                findViewById(R.id.tvStepsFri),
                findViewById(R.id.tvStepsSat),
                findViewById(R.id.tvStepsSun)
        };
    }

    private void loadWeeklyData() {
        firebaseHelper.getWeeklySteps(new FirebaseHelper.WeeklyDataCallback() {
            @Override
            public void onCallback(int[] weeklySteps) {
                runOnUiThread(() -> {
                    updateWeeklyDisplay(weeklySteps);
                    setupBarChart(weeklySteps);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(HistorialActivity.this, "Error cargando historial", Toast.LENGTH_SHORT).show();
                    int[] emptyData = new int[7];
                    updateWeeklyDisplay(emptyData);
                    setupBarChart(emptyData);
                });
            }
        });
    }

    private void updateWeeklyDisplay(int[] weeklySteps) {
        for (int i = 0; i < 7; i++) {
            stepTextViews[i].setText(String.valueOf(weeklySteps[i]));
        }
    }

    private void setupBarChart(int[] weeklySteps) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, weeklySteps[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Pasos por día");
        dataSet.setColor(Color.parseColor("#FF5722"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        XAxis xAxis = barChartSteps.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = barChartSteps.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#80FFFFFF"));

        YAxis rightAxis = barChartSteps.getAxisRight();
        rightAxis.setEnabled(false);

        barChartSteps.setData(barData);
        barChartSteps.getDescription().setEnabled(false);
        barChartSteps.getLegend().setEnabled(false);
        barChartSteps.setFitBars(true);
        barChartSteps.animateY(1000);
        barChartSteps.invalidate();
    }
}