package com.android.oleksandrpriadko.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;

public class PermissionManager {

    private static final int REQUEST_CODE = 0xa;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    private static final int DENIED = PackageManager.PERMISSION_DENIED;

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    private PermissionManager() {

    }

    public void request(final Activity activity,
                        String... permissions) throws Exception {
        if (!areAllInManifest(activity, permissions)) {
            throw new Exception("Add requested permission to AndroidManifest.xml");
        }
        permissions = onlyDenied(activity, permissions);
        if (needRationale(activity, permissions)) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle(activity.getString(R.string.permissions_title));
            String message = activity.getString(R.string.rationale_all);
            alertDialog.setMessage(message);
            final String[] finalPerm = permissions;
            int type = AlertDialog.BUTTON_POSITIVE;
            String text = activity.getString(android.R.string.ok);
            alertDialog.setButton(type, text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    ActivityCompat.requestPermissions(activity, finalPerm, REQUEST_CODE);
                }
            });
            type = AlertDialog.BUTTON_NEGATIVE;
            text = activity.getString(android.R.string.cancel);
            alertDialog.setButton(type, text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }

    public boolean areAllGranted(Context context) {
        return areAllGranted(context, retrieveFromManifest(context));
    }

    public boolean areAllGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean isGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void request(final Activity activity) {
        try {
            request(activity, retrieveFromManifest(activity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] retrieveFromManifest(Context context) {
        try {
            return context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return new String[]{};
        }
    }

    public List<Pair<String, Integer>> check(int requestCode,
                                             @NonNull String[] permissions,
                                             @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return new ArrayList<>();
        }
        List<Pair<String, Integer>> results = new ArrayList<>(grantResults.length);
        for (int i = 0; i < grantResults.length; i++) {
            results.add(Pair.create(permissions[i], grantResults[i]));
        }
        return results;
    }

    private String[] onlyDenied(Context context, String[] permissions) {
        List<String> result = new ArrayList<>();
        String[] resultArr = new String[]{};
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == DENIED) {
                result.add(permission);
            }
        }
        resultArr = result.toArray(resultArr);
        return resultArr;
    }

    public boolean isAllSuccessful(List<Pair<String, Integer>> results) {
        for (Pair<String, Integer> result : results) {
            if (result.second == DENIED) {
                return false;
            }
        }
        return true;
    }

    private boolean areAllInManifest(Context context, String... permissions) {
        String[] fromManifest = retrieveFromManifest(context);
        boolean inManifest = true;
        if (fromManifest != null) {
            for (String permission : permissions) {
                boolean isIn = false;
                for (String s : fromManifest) {
                    if (permission.equals(s)) {
                        isIn = true;
                    }
                }
                if (!isIn) {
                    inManifest = false;
                    break;
                }

            }
        }
        return inManifest;
    }

    private boolean needRationale(Activity activity, String... permissions) {
        boolean need = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                need = true;
            }
        }
        return need;
    }
}
