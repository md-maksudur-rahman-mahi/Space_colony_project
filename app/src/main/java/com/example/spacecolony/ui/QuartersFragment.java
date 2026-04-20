package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spacecolony.R;
import com.example.spacecolony.adapter.CrewAdapter;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Location;
import java.util.List;

public class QuartersFragment extends Fragment {
    private CrewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quarters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rv_crew);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CrewAdapter(true, null);
        rv.setAdapter(adapter);
        refreshList();

        view.findViewById(R.id.btn_recruit).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_quarters_to_recruit));
        view.findViewById(R.id.btn_to_simulator).setOnClickListener(v -> moveTo(Location.SIMULATOR));
        view.findViewById(R.id.btn_to_mission).setOnClickListener(v -> moveTo(Location.MISSION_CONTROL));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<CrewMember> crew = Storage.getInstance().getCrewByLocation(Location.QUARTERS);
        adapter.setData(crew);
    }

    private void moveTo(Location loc) {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members first!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember m : selected) m.setLocation(loc);
        Toast.makeText(getContext(), selected.size() + " crew moved.", Toast.LENGTH_SHORT).show();
        refreshList();
    }
}