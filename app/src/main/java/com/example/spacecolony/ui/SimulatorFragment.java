package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spacecolony.R;
import com.example.spacecolony.adapter.CrewAdapter;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Location;
import java.util.List;

public class SimulatorFragment extends Fragment {
    private CrewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simulator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rv_simulator_crew);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CrewAdapter(true, null);
        rv.setAdapter(adapter);
        refreshList();

        view.findViewById(R.id.btn_train).setOnClickListener(v -> trainSelected());
        view.findViewById(R.id.btn_to_quarters).setOnClickListener(v -> sendToQuarters());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        adapter.setData(Storage.getInstance().getCrewByLocation(Location.SIMULATOR));
    }

    private void trainSelected() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Select crew to train!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember m : selected) {
            m.addExperience(1);
            m.getStatistics().incrementTraining();
        }
        Toast.makeText(getContext(), selected.size() + " crew trained! +1 XP each.", Toast.LENGTH_SHORT).show();
        refreshList();
    }

    private void sendToQuarters() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Select crew to move!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember m : selected) {
            m.recoverEnergy();
            m.setLocation(Location.QUARTERS);
        }
        Toast.makeText(getContext(), "Crew sent to Quarters. Energy restored!", Toast.LENGTH_SHORT).show();
        refreshList();
    }
}