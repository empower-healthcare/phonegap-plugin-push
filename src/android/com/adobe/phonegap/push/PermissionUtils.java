package com.adobe.phonegap.push;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PermissionUtils {

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";

    public static boolean hasPermission(Context appContext, String appOpsServiceId) throws UnknownError {

        if (Build.VERSION.SDK_INT >= 24) {
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.areNotificationsEnabled();
        }

        ApplicationInfo appInfo = appContext.getApplicationInfo();

        String pkg = appContext.getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        Object appOps = appContext.getSystemService("appops");

        try {

            appOpsClass = Class.forName("android.app.AppOpsManager");

            Method checkOpNoThrowMethod = appOpsClass.getMethod(
                CHECK_OP_NO_THROW,
                Integer.TYPE,
                Integer.TYPE,
                String.class
            );

            Field opValue = appOpsClass.getDeclaredField(appOpsServiceId);

            int value = (int) opValue.getInt(Integer.class);
            Object result = checkOpNoThrowMethod.invoke(appOps, value, uid, pkg);

            return Integer.parseInt(result.toString()) == 0; // AppOpsManager.MODE_ALLOWED

        } catch (ClassNotFoundException e) {
            throw new UnknownError("class not found");
        } catch (NoSuchMethodException e) {
            throw new UnknownError("no such method");
        } catch (NoSuchFieldException e) {
            throw new UnknownError("no such field");
        } catch (InvocationTargetException e) {
            throw new UnknownError("invocation target");
        } catch (IllegalAccessException e) {
            throw new UnknownError("illegal access");
        }

    }

}
