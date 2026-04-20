package com.example.spacecolony.model;

public class Engineer extends CrewMember {
    public Engineer(int id, String name) {
        super(id, name, 6, 3, 19);
    }

    @Override
    public int act(Threat threat, MissionType missionType) {
        int skill = getEffectiveSkill();
        if (missionType == MissionType.REPAIR) skill += 3;
        return Math.max(0, skill - threat.getResilience());
    }

    @Override
    public String getSpecialization() { return "Engineer"; }
}