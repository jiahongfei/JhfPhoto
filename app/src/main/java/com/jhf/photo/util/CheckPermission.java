package com.jhf.photo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by jiahongfei on 16/8/30.
 */
public class CheckPermission {

    /**
     * Determines if the context calling has the required permission
     *
     * @param context    - the IPC context
     * @param permission - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public static boolean hasPermission(Context context, String permission) {

        int res = context.checkCallingOrSelfPermission(permission);

        Log.v("", "permission: " + permission + " = \t\t" +
                (res == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));

        return res == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Determines if the context calling has the required permissions
     *
     * @param context     - the IPC context
     * @param permissions - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public static boolean hasPermissions(Context context, String... permissions) {

        boolean hasAllPermissions = true;

        for (String permission : permissions) {
            //return false instead of assigning, but with this you can log all permission values
            if (!hasPermission(context, permission)) {
                hasAllPermissions = false;
            }
        }

        return hasAllPermissions;

    }
}
