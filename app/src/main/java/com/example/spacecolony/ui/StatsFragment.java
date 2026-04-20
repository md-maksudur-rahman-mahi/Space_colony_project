package com.example.spacecolony.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.spacecolony.R;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateStats(view);
        setupChart(view);
    }

    private void populateStats(View view) {
        LinearLayout container = view.findViewById(R.id.ll_stats_list);
        container.removeAllViews();
        List<CrewMember> all = Storage.getInstance().getAllCrew();
        int totalMissions = 0, totalWins = 0;

        for (CrewMember m : all) {
            totalMissions += m.getStatistics().getMissionsPlayed();
            totalWins += m.getStatistics().getMissionsWon();

            TextView tv = new TextView(getContext());
            tv.setPadding(16, 12, 16, 12);
            tv.setTextSize(14);
            tv.setText(m.getName() + " (" + m.getSpecialization() + ") " +
                    "\n  Missions: " + m.getStatistics().getMissionsPlayed() +
                    " | Wins: " + m.getStatistics().getMissionsWon() +
                    " | Training: " + m.getStatistics().getTrainingSessions() +
                    " | XP: " + m.getExperiencePoints());
            container.addView(tv);

            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.LTGRAY);
            container.addView(divider);
        }
        ((TextView) view.findViewById(R.id.tv_total_missions)).setText("Total missions: " + totalMissions);
        ((TextView) view.findViewById(R.id.tv_total_wins)).setText("Total wins: " + totalWins);
    }

    private void setupChart(View view) {
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar_chart));
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        for (CrewMember m : Storage.getInstance().getAllCrew()) {
            data.add(new ValueDataEntry(m.getName(), m.getStatistics().getMissionsWon()));
        }
        Column column = cartesian.column(data);
        column.tooltip().titleFormat("{%X}").position(Position.CENTER_BOTTOM).anchor(Anchor.CENTER_BOTTOM);
        cartesian.animation(true);
        cartesian.title("Wins per Crew Member");
        cartesian.yScale().minimum(0d);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.xAxis(0).title("Crew");
        cartesian.yAxis(0).title("Wins");
        anyChartView.setChart(cartesian);
    }
}