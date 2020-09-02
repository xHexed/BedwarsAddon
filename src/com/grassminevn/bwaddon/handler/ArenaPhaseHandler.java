package com.grassminevn.bwaddon.handler;

import com.grassminevn.bwaddon.BedwarsAddon;
import com.grassminevn.bwaddon.Countdown;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Arrays;

public class ArenaPhaseHandler {
    private static final Countdown counter = new Countdown(BedwarsAddon.getInstance(), 60, new ArrayDeque<>(Arrays.asList(
            new Countdown.Moment(10, "Kc1"),
            new Countdown.Moment(20, "Kc2"),
            new Countdown.Moment(30, "Kc3"),
            new Countdown.Moment(40, "Kc4")))) {
        @Override
        protected void onTick() { }

        @Override
        protected void onEnd() {
            Bukkit.broadcastMessage("ended");
        }

        @Override
        protected void onMoment(final Moment moment) { }

    };

    public static Countdown getCounter() {
        return counter;
    }

    public static void startArena() {
        counter.launch();
    }

    public static void endArena() {
        if (counter.isRunning())
            counter.cancel();
    }
}
