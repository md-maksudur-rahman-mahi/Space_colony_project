package com.example.spacecolony.model;

public abstract class CrewMember {
    protected int id;
    protected String name;
    protected int baseSkill;
    protected int resilience;
    protected int maxEnergy;
    protected int currentEnergy;
    protected int experiencePoints = 0;
    protected Location location = Location.QUARTERS;
    protected Statistics statistics = new Statistics();

    public CrewMember(int id, String name, int skill, int resilience, int maxEnergy) {
        this.id = id;
        this.name = name;
        this.baseSkill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

    public abstract int act(Threat threat, MissionType missionType);
    public abstract String getSpecialization();

    public int defend(int incomingDamage) {
        int reduced = incomingDamage - resilience;
        int actualDamage = Math.max(0, reduced);
        currentEnergy = Math.max(0, currentEnergy - actualDamage);
        return actualDamage;
    }

    public int getEffectiveSkill()  { return baseSkill + experiencePoints; }
    public int getBaseSkill()       { return baseSkill; }
    public int getResilience()      { return resilience; }
    public int getId()              { return id; }
    public String getName()         { return name; }
    public int getCurrentEnergy()   { return currentEnergy; }
    public void setEnergy(int energy) { this.currentEnergy = Math.min(maxEnergy, Math.max(0, energy)); }
    public int getMaxEnergy()       { return maxEnergy; }
    public int getExperiencePoints(){ return experiencePoints; }
    public Location getLocation()   { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Statistics getStatistics(){ return statistics; }
    public void addExperience(int xp){ this.experiencePoints += xp; }
    public void recoverEnergy()     { currentEnergy = maxEnergy; }
    public boolean isAlive()        { return currentEnergy > 0; }
}