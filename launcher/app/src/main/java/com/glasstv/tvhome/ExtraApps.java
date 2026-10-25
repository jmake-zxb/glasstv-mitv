package com.glasstv.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Apps the firmware keeps out of the LAUNCHER and LEANBACK queries.
 *
 * MiTV apps such as the app store, the game centre, Mi Home and the media player declare only
 * ACTION_MAIN, with no category at all, so no standard launcher can enumerate them. config.xml lists
 * their components explicitly (extra_apps) and this class turns each one into the same ResolveInfo
 * the normal query returns, so labels, icons, focus and ordering all reuse the existing tile code.
 */
final class ExtraApps {
    private static final String TAG = "TvHome.ExtraApps";

    private ExtraApps() {
    }

    /** One ResolveInfo per configured component that is installed and exported. */
    static List<ResolveInfo> resolve(Context context) {
        List<ResolveInfo> out = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        for (String spec : context.getResources().getStringArray(R.array.extra_apps)) {
            String trimmed = spec == null ? "" : spec.trim();
            ComponentName cn = ComponentName.unflattenFromString(trimmed);
            if (cn == null) {
                Log.w(TAG, "ignoring bad extra_apps entry: " + trimmed);
                continue;
            }
            try {
                ActivityInfo info = pm.getActivityInfo(cn, 0);
                if (!info.exported) {
                    Log.w(TAG, "not exported: " + cn.flattenToShortString());
                    continue;
                }
                ResolveInfo ri = new ResolveInfo();
                ri.activityInfo = info;
                out.add(ri);
            } catch (PackageManager.NameNotFoundException missing) {
                Log.i(TAG, "not installed: " + cn.flattenToShortString());
            }
        }
        return out;
    }

    /** The configured components, flattened, to tell an extra app from an ordinary tile. */
    static Set<String> components(Context context) {
        Set<String> out = new HashSet<>();
        for (String spec : context.getResources().getStringArray(R.array.extra_apps)) {
            String trimmed = spec == null ? "" : spec.trim();
            ComponentName cn = ComponentName.unflattenFromString(trimmed);
            if (cn != null) out.add(cn.flattenToString());
        }
        return out;
    }
}
