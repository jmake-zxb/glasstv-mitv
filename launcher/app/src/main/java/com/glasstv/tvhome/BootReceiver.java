package com.glasstv.tvhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机后把 GlassTV 拉到前台，防止原生桌面先占住。
 *
 * 2026-10-25：桌面进程改为 :home 后，MIUI 的 STR 待机不会再 force-stop 它，
 * 不再需要无障碍守护；这里只保留一个开机兜底。
 * 从 Receiver 拉 activity 在 Android 10+ 需要 SYSTEM_ALERT_WINDOW（装的时候 appops 给过）。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        try {
            context.startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception ignored) {}
    }
}
