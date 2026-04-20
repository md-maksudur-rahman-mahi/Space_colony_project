package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.spacecolony.R;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.Location;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStats(view);

        view.findViewById(R.id.btn_go_quarters).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_quarters));
        view.findViewById(R.id.btn_go_simulator).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_simulator));
        view.findViewById(R.id.btn_go_mission).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_mission));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStats(getView());
    }

    private void updateStats(View view) {
        if (view == null) return;
        Storage s = Storage.getInstance();
        ((TextView) view.findViewById(R.id.tv_quarters_count)).setText(String.valueOf(s.getCrewByLocation(Location.QUARTERS).size()));
        ((TextView) view.findViewById(R.id.tv_simulator_count)).setText(String.valueOf(s.getCrewByLocation(Location.SIMULATOR).size()));
        ((TextView) view.findViewById(R.id.tv_mission_count)).setText(String.valueOf(s.getCrewByLocation(Location.MISSION_CONTROL).size()));
        ((TextView) view.findViewById(R.id.tv_total_crew)).setText("Total crew: " + s.getAllCrew().size());
    }
}