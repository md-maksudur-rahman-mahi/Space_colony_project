package com.example.spacecolony.model;

public class Pilot extends CrewMember {
    public Pilot(int id, String name) {
        super(id, name, 5, 4, 20);
    }

    @Override
    public int act(Threat threat, MissionType missionType) {
        int skill = getEffectiveSkill();
        if (missionType == MissionType.NAVIGATION) skill += 3;
        return Math.max(0, skill - threat.getResilience());
    }

    @Override
    public String getSpecialization() { return "Pilot"; }
}