package com.sunway.averychoke.studywifidirect3.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;

/**
 * Created by AveryChoke on 14/4/2017.
 */

public class PermissionUtil {
    public enum Permission {
        FINE_LOCATION, COARSE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, READ_CONTACT;

        public String[] getManifestString() {
            switch (this) {
                case FINE_LOCATION:
                    return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                case COARSE_LOCATION:
                    return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
                case CAMERA:
                    return new String[]{Manifest.permission.CAMERA};
                case WRITE_EXTERNAL_STORAGE:
                    return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                case READ_EXTERNAL_STORAGE:
                    return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                case READ_CONTACT:
                    return new String[]{Manifest.permission.READ_CONTACTS};
                default:
                    return new String[]{};
            }
        }

        public int getRequestCode() {
            switch (this) {
                case FINE_LOCATION:
                    return 100;
                case COARSE_LOCATION:
                    return 101;
                case CAMERA:
                    return 102;
                case WRITE_EXTERNAL_STORAGE:
                    return 103;
                case READ_EXTERNAL_STORAGE:
                    return 104;
                case READ_CONTACT:
                    return 105;
                default:
                    return -1;
            }
        }
    }

    public enum PermissionStatus {
        ALLOW, DENY, NEVER_ASK_AGAIN
    }

    public static boolean isPermissionGranted(Fragment fragment, Permission permissionRequestType) {
        for (String permission : permissionRequestType.getManifestString()) {
            if (PermissionChecker.checkSelfPermission(fragment.getContext(), permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean isPermissionGranted(Activity activity, Permission permissionRequestType) {
        for (String permission : permissionRequestType.getManifestString()) {
            if (PermissionChecker.checkSelfPermission(activity, permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void requestPermission(Fragment fragment, Permission permissionRequestType) {
        fragment.requestPermissions(permissionRequestType.getManifestString(), permissionRequestType.getRequestCode());
    }

    public static void requestPermission(Activity activity, Permission permissionRequestType) {
        ActivityCompat.requestPermissions(activity, permissionRequestType.getManifestString(), permissionRequestType.getRequestCode());
    }

    public static <T> PermissionStatus isRequestGranted(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Fragment fragment, Permission permissionRequestType) {
        if (requestCode == permissionRequestType.getRequestCode()) {
            for (int i = 0; i < permissions.length; i++) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        if (fragment.shouldShowRequestPermissionRationale(permissions[0])) {
                            return PermissionStatus.DENY;
                        } else {
                            return PermissionStatus.NEVER_ASK_AGAIN;
                        }
                    }
                }
            }
            return PermissionStatus.ALLOW;
        }
        return PermissionStatus.DENY;
    }

    public static <T> PermissionStatus isRequestGranted(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Activity activity, Permission permissionRequestType) {
        if (requestCode == permissionRequestType.getRequestCode()) {
            for (int i = 0; i < permissions.length; i++) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                            return PermissionStatus.DENY;
                        } else {
                            return PermissionStatus.NEVER_ASK_AGAIN;
                        }
                    }
                }
            }

            return PermissionStatus.ALLOW;
        }
        return PermissionStatus.DENY;
    }

    public static Intent getSettingIntent (@NonNull Context context) {
        Uri appPackageUri = Uri.fromParts("package", context.getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, appPackageUri);
        return intent;
    }
}
