package com.example.spacecolony.model;

public class Threat {
    private String name;
    private int skill;
    private int resilience;
    private int maxEnergy;
    private int currentEnergy;

    public Threat(String name, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

    public int act() { return skill; }

    public int defend(int incomingDamage) {
        int dealt = Math.max(0, incomingDamage - resilience);
        currentEnergy = Math.max(0, currentEnergy - dealt);
        return dealt;
    }

    public boolean isAlive() { return currentEnergy > 0; }
    public String getName() { return name; }
    public int getCurrentEnergy() { return currentEnergy; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
}