package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private static final int MAX_ROUNDS = 100;

    private Random random = new Random(1L);

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {
        if (teamA == null || teamB == null || teamASkill == null || teamBSkill == null) {
            throw new IllegalArgumentException("Teams and skills must not be null");
        }
        RaidResult result = new RaidResult();
        if (!teamA.isAlive() && !teamB.isAlive()) {
            result.setRounds(0);
            result.setWinner("Draw");
            result.addLine("Both teams are already dead. Raid cannot start.");
            return result;
        }

        if (!teamA.isAlive()) {
            result.setRounds(0);
            result.setWinner(teamB.getName());
            result.addLine(teamA.getName() + " is already defeated. Winner: " + teamB.getName());
            return result;
        }

        if (!teamB.isAlive()) {
            result.setRounds(0);
            result.setWinner(teamA.getName());
            result.addLine(teamB.getName() + " is already defeated. Winner: " + teamA.getName());
            return result;
        }

        int rounds = 0;

        while (teamA.isAlive() && teamB.isAlive() && rounds < MAX_ROUNDS) {
            rounds++;
            result.addLine("Round " + rounds);

            if (teamA.isAlive()) {
                int beforeHealth = teamB.getHealth();
                teamASkill.cast(teamB);
                int afterHealth = teamB.getHealth();
                int dealtDamage = Math.max(0, beforeHealth - afterHealth);

                result.addLine(teamA.getName() + " uses " + teamASkill.getSkillName()
                        + " (" + teamASkill.getEffectName() + ") on " + teamB.getName()
                        + " and deals " + dealtDamage + " damage.");
                result.addLine(teamB.getName() + ": HP = " + teamB.getHealth());
            }

            if (teamB.isAlive()) {
                int beforeHealth = teamA.getHealth();
                teamBSkill.cast(teamA);
                int afterHealth = teamA.getHealth();
                int dealtDamage = Math.max(0, beforeHealth - afterHealth);

                result.addLine(teamB.getName() + " uses " + teamBSkill.getSkillName()
                        + " (" + teamBSkill.getEffectName() + ") on " + teamA.getName()
                        + " and deals " + dealtDamage + " damage.");
                result.addLine(teamA.getName() + ": HP = " + teamA.getHealth());
            }
        }

        result.setRounds(rounds);
        if (teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner(teamA.getName());
            result.addLine("Winner: " + teamA.getName());
        } else if (teamB.isAlive() && !teamA.isAlive()) {
            result.setWinner(teamB.getName());
            result.addLine("Winner: " + teamB.getName());
        } else {
            result.setWinner("Draw");
            result.addLine("The raid ended in a draw.");
        }

        return result;
    }
}
