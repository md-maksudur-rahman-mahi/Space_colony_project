package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spacecolony.R;
import com.example.spacecolony.adapter.CrewAdapter;
import com.example.spacecolony.manager.MissionEngine;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Location;
import com.example.spacecolony.model.MissionType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionControlFragment extends Fragment {
    private CrewAdapter adapter;
    private MissionEngine missionEngine;
    private TextView tvMissionLog, tvThreatInfo;
    private ProgressBar pbThreatHp;
    private ScrollView scrollLog;
    private View missionPanel, selectionPanel;
    private View rootView;
    private LinearLayout crewHpBars;
    private MissionType selectedMissionType = MissionType.COMBAT;
    private String currentAction = "ATTACK";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mission_control, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMissionLog = view.findViewById(R.id.tv_mission_log);
        tvThreatInfo = view.findViewById(R.id.tv_threat_info);
        pbThreatHp = view.findViewById(R.id.pb_threat_hp);
        scrollLog = view.findViewById(R.id.scroll_log);
        missionPanel = view.findViewById(R.id.mission_panel);
        selectionPanel = view.findViewById(R.id.selection_panel);
        crewHpBars = view.findViewById(R.id.crew_hp_bars);

        Spinner spinner = view.findViewById(R.id.spinner_mission_type);
        String[] types = {"COMBAT", "REPAIR", "NAVIGATION", "MEDICAL", "RESEARCH"};
        spinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, types));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedMissionType = MissionType.valueOf(types[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        RecyclerView rv = view.findViewById(R.id.rv_mission_crew);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CrewAdapter(true, null);
        rv.setAdapter(adapter);
        refreshList();

        view.findViewById(R.id.btn_launch_mission).setOnClickListener(v -> launchMission());
        view.findViewById(R.id.btn_return_to_quarters).setOnClickListener(v -> returnSelectedToQuarters());
        view.findViewById(R.id.btn_attack).setOnClickListener(v -> { currentAction = "ATTACK"; executeRound(); });
        view.findViewById(R.id.btn_defend).setOnClickListener(v -> { currentAction = "DEFEND"; executeRound(); });
        view.findViewById(R.id.btn_special).setOnClickListener(v -> { currentAction = "SPECIAL"; executeRound(); });
        view.findViewById(R.id.btn_home_from_mission).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.homeFragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        adapter.setData(Storage.getInstance().getCrewByLocation(Location.MISSION_CONTROL));
    }

    private void launchMission() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.size() < 2) {
            Toast.makeText(getContext(), "Select at least 2 crew members!", Toast.LENGTH_SHORT).show();
            return;
        }
        missionEngine = new MissionEngine(selected);
        missionEngine.startMission(selectedMissionType);
        selectionPanel.setVisibility(View.GONE);
        missionPanel.setVisibility(View.VISIBLE);
        tvMissionLog.setText("=== MISSION STARTED ===\n");
        setActionButtons(true);
        rootView.findViewById(R.id.btn_end_mission).setVisibility(View.GONE);
        updateThreatUI();
        updateCrewHpBars();
    }

    private void executeRound() {
        if (missionEngine == null || !missionEngine.isMissionActive()) return;
        Map<Integer, String> actions = new HashMap<>();
        for (CrewMember m : missionEngine.getSquad()) actions.put(m.getId(), currentAction);

        String log = missionEngine.executeRound(actions, selectedMissionType);
        tvMissionLog.append(log + "\n");
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
        updateThreatUI();
        updateCrewHpBars();

        if (!missionEngine.isMissionActive()) {
            setActionButtons(false);
            rootView.findViewById(R.id.btn_end_mission).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.btn_end_mission).setOnClickListener(v -> resetToSelection());
        }
    }

    private void updateThreatUI() {
        if (missionEngine == null || missionEngine.getThreat() == null) return;
        com.example.spacecolony.model.Threat threat = missionEngine.getThreat();
        tvThreatInfo.setText(threat.getName() + " HP: " + threat.getCurrentEnergy() + "/" + threat.getMaxEnergy() + " SKL: " + threat.getSkill() + " RES: " + threat.getResilience());
        pbThreatHp.setMax(threat.getMaxEnergy());
        pbThreatHp.setProgress(threat.getCurrentEnergy());
    }

    private void setActionButtons(boolean enabled) {
        rootView.findViewById(R.id.btn_attack).setEnabled(enabled);
        rootView.findViewById(R.id.btn_defend).setEnabled(enabled);
        rootView.findViewById(R.id.btn_special).setEnabled(enabled);
    }

    private void returnSelectedToQuarters() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members first!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember m : selected) {
            m.recoverEnergy();
            m.setLocation(Location.QUARTERS);
        }
        Toast.makeText(getContext(), selected.size() + " crew sent to Quarters. Energy restored!", Toast.LENGTH_SHORT).show();
        refreshList();
    }

    private void updateCrewHpBars() {
        if (crewHpBars == null || missionEngine == null) return;
        crewHpBars.removeAllViews();
        for (CrewMember m : missionEngine.getSquad()) {
            // Label
            TextView label = new TextView(getContext());
            label.setText(m.getName() + " (" + m.getSpecialization() + ")  HP: "
                    + m.getCurrentEnergy() + "/" + m.getMaxEnergy()
                    + (m.isAlive() ? "" : "  ☠ FALLEN"));
            label.setTextSize(12f);
            crewHpBars.addView(label);
            // Bar
            ProgressBar pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(m.getMaxEnergy());
            pb.setProgress(m.getCurrentEnergy());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 18);
            params.bottomMargin = 6;
            pb.setLayoutParams(params);
            // Color by specialization
            int color;
            android.content.res.Resources res = requireContext().getResources();
            switch (m.getSpecialization()) {
                case "Pilot":     color = res.getColor(R.color.color_pilot, null); break;
                case "Engineer":  color = res.getColor(R.color.color_engineer, null); break;
                case "Medic":     color = res.getColor(R.color.color_medic, null); break;
                case "Scientist": color = res.getColor(R.color.color_scientist, null); break;
                case "Soldier":   color = res.getColor(R.color.color_soldier, null); break;
                default:          color = 0xFF888888; break;
            }
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(color));
            crewHpBars.addView(pb);
        }
    }

    private void resetToSelection() {
        missionEngine = null;
        missionPanel.setVisibility(View.GONE);
        selectionPanel.setVisibility(View.VISIBLE);
        setActionButtons(true);
        rootView.findViewById(R.id.btn_end_mission).setVisibility(View.GONE);
        refreshList();
    }
}