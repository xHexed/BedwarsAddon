package com.grassminevn.bwaddon.phase;

import com.grassminevn.bwaddon.Util;
import org.bukkit.plugin.Plugin;

import java.util.Deque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Countdown implements Runnable {
    private final int duration;
    private final Plugin plugin;
    private final Deque<Moment> moments;
    private Deque<Moment> runningMoments;
    private int nextMoment;
    private Moment currentMoment = Moment.EMPTY;
    private int secondsSinceStart;
    private ScheduledFuture<?> task;

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
        task = Util.ASYNC_SCHEDULER_EXECUTER.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    private void setNextMoment() {
        if (!runningMoments.isEmpty()) {
            nextMoment = runningMoments.peek().time - currentMoment.time;
            currentMoment = runningMoments.pop();
        }
        else {
            nextMoment = duration - currentMoment.time;
            currentMoment = Moment.MAX;
        }
    }

    public synchronized void cancel() {
        task.cancel(true);
        secondsSinceStart = 0;

        runningMoments = moments;
        currentMoment = Moment.EMPTY;
        setNextMoment();
    }

    public boolean isRunning() {
        return !task.isDone();
    }

    private ScheduledFuture getTask() {
        if (!isRunning()) {
            throw new RuntimeException("Task " + this + " not scheduled yet");
        }
        return task;
    }

    public final String toString() {
        return getClass().getSimpleName() + "{" + duration + ", task=" + task.toString() + "}";
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