package com.example.spacecolony.model;

public class Soldier extends CrewMember {
    public Soldier(int id, String name) {
        super(id, name, 9, 0, 16);
    }

    @Override
    public int act(Threat threat, MissionType missionType) {
        int skill = getEffectiveSkill();
        if (missionType == MissionType.COMBAT) skill += 3;
        return Math.max(0, skill - threat.getResilience());
    }

    @Override
    public String getSpecialization() { return "Soldier"; }
}