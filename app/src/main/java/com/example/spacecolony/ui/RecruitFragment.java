package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.spacecolony.R;
import com.example.spacecolony.manager.Storage;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;

public class RecruitFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etName = view.findViewById(R.id.et_name);
        RadioGroup rgSpec = view.findViewById(R.id.rg_specialization);

        view.findViewById(R.id.btn_recruit).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Enter a name!", Toast.LENGTH_SHORT).show();
                return;
            }
            int checkedId = rgSpec.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(getContext(), "Select a specialization!", Toast.LENGTH_SHORT).show();
                return;
            }

            Storage storage = Storage.getInstance();
            int id = storage.getNextId();
            CrewMember newMember;

            if (checkedId == R.id.rb_pilot) newMember = new Pilot(id, name);
            else if (checkedId == R.id.rb_engineer) newMember = new Engineer(id, name);
            else if (checkedId == R.id.rb_medic) newMember = new Medic(id, name);
            else if (checkedId == R.id.rb_scientist) newMember = new Scientist(id, name);
            else newMember = new Soldier(id, name);

            storage.addCrew(newMember);
            Toast.makeText(getContext(), name + " recruited!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }
}