package com.example.vov4ik.musicplayer;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseUpdater {
    private static AtomicBoolean canStartDbUpdate = new AtomicBoolean(true);
    private Context context;

    public DatabaseUpdater(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("DatabaseUpdater must be initialized with non-null context.");
        }

        this.context = context;
    }

    /**
     * Starts database update, if it is not running yet.
     *
     * @param onUpdateFinishedCallback Optional callback to execute after successful update.
     * @return True when update was started, false otherwise.
     */
    public boolean updateDatabase(final Runnable onUpdateFinishedCallback) {
        boolean canUpdateDb = canStartDbUpdate.compareAndSet(true, false);
        if (canUpdateDb) {
            performUpdate(onUpdateFinishedCallback);
        }

        return canUpdateDb;
    }

    private void performUpdate(final Runnable onUpdateFinishedCallback) {
        Runnable dbUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    new DbConnector().fillerForDb(context);
                    if (onUpdateFinishedCallback != null) {
                        onUpdateFinishedCallback.run();
                    }
                } finally {
                    canStartDbUpdate.set(true);
                }
            }
        };

        new Thread(dbUpdateRunnable, "Music Player Db Update Thread").start();
    }
}
