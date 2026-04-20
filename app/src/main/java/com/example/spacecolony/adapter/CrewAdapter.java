package com.example.spacecolony.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;
import java.util.ArrayList;
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {
    public interface OnCrewClickListener {
        void onCrewClick(CrewMember member);
    }

    private List<CrewMember> crewList = new ArrayList<>();
    private List<Integer> selectedIds = new ArrayList<>();
    private boolean showCheckboxes = false;
    private OnCrewClickListener listener;

    public CrewAdapter(boolean showCheckboxes, OnCrewClickListener listener) {
        this.showCheckboxes = showCheckboxes;
        this.listener = listener;
    }

    public void setData(List<CrewMember> data) {
        this.crewList = data;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() { return selectedIds; }

    public List<CrewMember> getSelectedCrew() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember m : crewList) {
            if (selectedIds.contains(m.getId())) selected.add(m);
        }
        return selected;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember member = crewList.get(position);

        holder.tvName.setText(member.getName());
        holder.tvSpec.setText(member.getSpecialization() + " | XP: " + member.getExperiencePoints());
        holder.tvStats.setText("SKL: " + member.getEffectiveSkill() +
                " | RES: " + member.getResilience() +
                " | HP: " + member.getCurrentEnergy() + "/" + member.getMaxEnergy());

        holder.progressEnergy.setMax(member.getMaxEnergy());
        holder.progressEnergy.setProgress(member.getCurrentEnergy());

        // Set icon emoji and color per specialization
        if (holder.tvCrewIcon != null) {
            String emoji;
            int color;
            android.content.res.Resources res = holder.itemView.getContext().getResources();
            switch (member.getSpecialization()) {
                case "Pilot":
                    emoji = "✈️"; color = res.getColor(com.example.spacecolony.R.color.color_pilot, null); break;
                case "Engineer":
                    emoji = "🔧"; color = res.getColor(com.example.spacecolony.R.color.color_engineer, null); break;
                case "Medic":
                    emoji = "⚕️"; color = res.getColor(com.example.spacecolony.R.color.color_medic, null); break;
                case "Scientist":
                    emoji = "🔬"; color = res.getColor(com.example.spacecolony.R.color.color_scientist, null); break;
                case "Soldier":
                    emoji = "⚔️"; color = res.getColor(com.example.spacecolony.R.color.color_soldier, null); break;
                default:
                    emoji = "👤"; color = 0xFF888888; break;
            }
            holder.tvCrewIcon.setText(emoji);
            holder.tvCrewIcon.setBackgroundColor(color);
        }

        holder.cbSelect.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedIds.contains(member.getId()));
        holder.cbSelect.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                if (!selectedIds.contains(member.getId())) selectedIds.add(member.getId());
            } else {
                selectedIds.remove((Integer) member.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCrewClick(member);
        });
    }

    @Override
    public int getItemCount() { return crewList.size(); }

    public static class CrewViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpec, tvStats, tvCrewIcon;
        ProgressBar progressEnergy;
        CheckBox cbSelect;

        public CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_crew_name);
            tvSpec = itemView.findViewById(R.id.tv_crew_spec);
            tvStats = itemView.findViewById(R.id.tv_crew_stats);
            progressEnergy = itemView.findViewById(R.id.progress_energy);
            cbSelect = itemView.findViewById(R.id.cb_select);
            tvCrewIcon = itemView.findViewById(R.id.tv_crew_icon);
        }
    }
}