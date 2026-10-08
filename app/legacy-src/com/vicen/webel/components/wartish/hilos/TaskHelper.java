package com.vicen.webel.components.wartish.hilos;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class TaskHelper {
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task) {
        execute(task, (P[]) null);
    }

    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task, P... params) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

}
