package com.example.spacecolony.manager;

import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    private static Storage instance;
    private final Map<Integer, CrewMember> crewMap = new HashMap<>();
    private int nextId = 1;

    private Storage() {}

    public static synchronized Storage getInstance() {
        if (instance == null) instance = new Storage();
        return instance;
    }

    public int getNextId() { return nextId++; }
    public void setNextId(int id) { this.nextId = id; }

    public void addCrew(CrewMember member) { crewMap.put(member.getId(), member); }
    public void removeCrew(int id) { crewMap.remove(id); }
    public CrewMember getCrew(int id) { return crewMap.get(id); }
    public Map<Integer, CrewMember> getMap() { return crewMap; }

    public List<CrewMember> getCrewByLocation(Location loc) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember c : crewMap.values()) {
            if (c.getLocation() == loc) result.add(c);
        }
        return result;
    }

    public List<CrewMember> getAllCrew() { return new ArrayList<>(crewMap.values()); }
}