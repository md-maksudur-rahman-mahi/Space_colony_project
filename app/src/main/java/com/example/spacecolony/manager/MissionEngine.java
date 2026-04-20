package com.example.spacecolony.manager;

import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Location;
import com.example.spacecolony.model.MissionType;
import com.example.spacecolony.model.Threat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MissionEngine {
    private List<CrewMember> squad;
    private Threat currentThreat;
    private boolean isMissionActive;
    private static int missionCount = 0;
    private static final Random random = new Random();
    private MissionType missionType;
    private static final String[] THREAT_NAMES = {
            "Asteroid Storm", "Alien Invasion", "Solar Flare",
            "Fuel Leak", "Hull Breach", "Fire in Kitchen",
            "Broken Heating", "Rogue AI", "Meteor Shower"
    };

    public MissionEngine(List<CrewMember> squad) {
        this.squad = new ArrayList<>(squad);
        this.isMissionActive = false;
    }

    public void startMission(MissionType type) {
        missionCount++;
        this.missionType = type;
        int skill = 4 + missionCount;
        int resilience = 1 + (missionCount / 2);
        int energy = 15 + (missionCount * 3);
        String name = THREAT_NAMES[missionCount % THREAT_NAMES.length];
        this.currentThreat = new Threat(name, skill, resilience, energy);
        this.isMissionActive = true;
    }

    public String executeRound(Map<Integer, String> actions, MissionType type) {
        if (!isMissionActive) return "Mission already finished.";
        StringBuilder log = new StringBuilder();

        for (CrewMember member : squad) {
            if (!member.isAlive()) continue;

            String action = actions.getOrDefault(member.getId(), "ATTACK");
            int damageDealt;

            if (action.equals("ATTACK")) {
                int baseDmg = member.act(currentThreat, type);
                int randomFactor = random.nextInt(3);
                damageDealt = baseDmg + randomFactor;
                currentThreat.defend(damageDealt);
                log.append(member.getName()).append(" attacks! Deals ").append(damageDealt).append(" dmg.\n");
            } else if (action.equals("SPECIAL")) {
                member.setEnergy(member.getCurrentEnergy() - 5);
                damageDealt = member.getEffectiveSkill() * 2;
                currentThreat.defend(damageDealt);
                log.append(member.getName()).append(" uses SPECIAL! Deals ").append(damageDealt).append(" dmg.\n");
            } else {
                log.append(member.getName()).append(" defends! (damage halved this turn)\n");
                damageDealt = 0;
            }

            log.append("Threat HP: ").append(currentThreat.getCurrentEnergy())
                    .append("/").append(currentThreat.getMaxEnergy()).append("\n");

            if (!currentThreat.isAlive()) {
                log.append(">> VICTORY! ").append(currentThreat.getName()).append(" neutralized!\n");
                handleVictory(log);
                return log.toString();
            }

            int threatDmg = currentThreat.act();
            if (action.equals("DEFEND")) threatDmg = Math.max(0, threatDmg / 2);
            member.defend(threatDmg);
            log.append("Threat retaliates! ").append(member.getName())
                    .append(" takes ").append(threatDmg).append(" dmg. HP: ")
                    .append(member.getCurrentEnergy()).append("\n");

            if (!member.isAlive()) {
                log.append(">> ").append(member.getName()).append(" has fallen!\n");
            }
        }

        boolean allDead = squad.stream().noneMatch(CrewMember::isAlive);
        if (allDead) {
            log.append(">> DEFEAT. All crew members lost.\n");
            handleDefeat();
        }

        return log.toString();
    }

    private void handleVictory(StringBuilder log) {
        isMissionActive = false;
        for (CrewMember m : squad) {
            m.getStatistics().incrementMissions();
            if (m.isAlive()) {
                m.getStatistics().incrementWins();
                int xp = 1 + random.nextInt(2);
                m.addExperience(xp);
                log.append(m.getName()).append(" gains ").append(xp).append(" XP!\n");
                m.setLocation(Location.MISSION_CONTROL);
            } else {
                Storage.getInstance().removeCrew(m.getId());
            }
        }
    }

    private void handleDefeat() {
        isMissionActive = false;
        for (CrewMember m : squad) {
            m.getStatistics().incrementMissions();
            Storage.getInstance().removeCrew(m.getId());
        }
    }

    public boolean isMissionActive() { return isMissionActive; }
    public Threat getThreat() { return currentThreat; }
    public List<CrewMember> getSquad() { return squad; }
}