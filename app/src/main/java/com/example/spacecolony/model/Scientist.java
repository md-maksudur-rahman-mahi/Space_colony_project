package com.example.spacecolony.model;

public class Scientist extends CrewMember {
    public Scientist(int id, String name) {
        super(id, name, 8, 1, 17);
    }

    @Override
    public int act(Threat threat, MissionType missionType) {
        int skill = getEffectiveSkill();
        if (missionType == MissionType.RESEARCH) skill += 3;
        return Math.max(0, skill - threat.getResilience());
    }

    @Override
    public String getSpecialization() { return "Scientist"; }
}