package com.grassminevn.bwaddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Deque;

public abstract class Countdown implements Runnable {
    private final int duration;
    private final Plugin plugin;
    private final Deque<Moment> moments;
    private Deque<Moment> runningMoments;
    private int nextMoment;
    private Moment currentMoment = Moment.EMPTY;
    private int secondsSinceStart;
    private int taskId = -1;

    protected Countdown(final Plugin plugin, final int duration, final Deque<Moment> moments) {
        this.plugin = plugin;
        this.duration = duration;
        this.moments = moments;

        runningMoments = moments;
        setNextMoment();
    }

    public int getNextMoment() {
        return nextMoment;
    }

    public Moment getCurrentMoment() {
        return currentMoment;
    }

    public final void run() {
        secondsSinceStart++;
        nextMoment--;
        if (secondsSinceStart < duration) {
            onTick();
            if (secondsSinceStart == currentMoment.time) {
                onMoment(currentMoment);
                setNextMoment();
            }
        } else {
            cancel();
            onEnd();
        }
    }

    public final synchronized void launch() {
        if (isRunning()) {
            throw new RuntimeException("Task " + this + " already scheduled!");
        }
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 20L);
        taskId = task.getTaskId();
    }

    private void setNextMoment() {
        if (!runningMoments.isEmpty()) {
            nextMoment = runningMoments.pollFirst().time - currentMoment.time;
            currentMoment = runningMoments.pop();
        }
        else {
            nextMoment = duration - currentMoment.time;
            currentMoment = Moment.MAX;
        }
    }

    public synchronized void cancel() {
        Bukkit.getScheduler().cancelTask(getTaskId());
        taskId = -1;
        secondsSinceStart = 0;

        runningMoments = moments;
        currentMoment = Moment.EMPTY;
        setNextMoment();
    }

    public boolean isRunning() {
        return (taskId != -1);
    }

    private int getTaskId() {
        if (!isRunning()) {
            throw new RuntimeException("Task " + this + " not scheduled yet");
        }
        return taskId;
    }

    public final String toString() {
        return getClass().getSimpleName() + "{" + duration + ", id=" + taskId + "}";
    }

    protected abstract void onTick();

    protected abstract void onEnd();

    protected abstract void onMoment(final Moment moment);

    public static class Moment {
        private static final Moment MAX = new Moment(Integer.MAX_VALUE, "");
        private static final Moment EMPTY = new Moment(0, "");
        private final int time;
        public String name;

        public Moment(final int time, final String name) {
            this.time = time;
            this.name = name;
        }
    }
}