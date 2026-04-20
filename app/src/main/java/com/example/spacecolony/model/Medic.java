package com.example.spacecolony.model;

public class Medic extends CrewMember {
    public Medic(int id, String name) {
        super(id, name, 7, 2, 18);
    }

    @Override
    public int act(Threat threat, MissionType missionType) {
        int skill = getEffectiveSkill();
        if (missionType == MissionType.MEDICAL) skill += 3;
        return Math.max(0, skill - threat.getResilience());
    }

    @Override
    public String getSpecialization() { return "Medic"; }
}