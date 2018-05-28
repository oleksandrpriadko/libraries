package com.android.oleksandrpriadko.widget_provider;

import android.app.ActivityManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class BaseWidgetProvider extends AppWidgetProvider {

    public String getTag() {
        return this.getClass().getSimpleName();
    }

    //region Service
    protected boolean runService(Context context, Class<?> serviceClass) {
        if (!isServiceRunning(context, serviceClass)) {
            Intent intent = new Intent(context, serviceClass);
            context.startService(intent);
        }
        return isServiceRunning(context, serviceClass);
    }

    protected boolean runService(Context context, Intent intent) {
        Class<?> serviceClass;
        try {
            serviceClass = Class.forName(intent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (!isServiceRunning(context, serviceClass)) {
            context.startService(intent);
        }
        return isServiceRunning(context, serviceClass);
    }

    protected boolean stopService(Context context, Class<?> serviceClass) {
        return !isServiceRunning(context, serviceClass)
            || context.stopService(new Intent(context, serviceClass));
    }

    protected boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean isServiceRunning = false;
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }
        return isServiceRunning;
    }
    //endregion
}
