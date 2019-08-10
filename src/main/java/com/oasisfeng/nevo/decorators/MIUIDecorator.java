package com.oasisfeng.nevo.decorators;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Log;

import de.robv.android.xposed.XposedHelpers;

import com.oasisfeng.nevo.xposed.R;
import com.oasisfeng.nevo.decorators.media.ColorUtil;

import top.trumeet.common.cache.IconCache;

public class MIUIDecorator extends BigTextDecorator {

	private static final String NOTIFICATION_ICON = "mipush_notification";
	private static final String NOTIFICATION_SMALL_ICON = "mipush_small_notification";
	private static final String TAG = "MIUIDecorator";

	private static void setSmallIcon(Notification n, Icon icon) {
		XposedHelpers.setObjectField(n, "mSmallIcon", icon);
	}

	@Override
	public void apply(StatusBarNotification evolving) {
		super.apply(evolving);
		final Notification n = evolving.getNotification();
		Icon defIcon;
		{
			final Context context = getPackageContext();
			defIcon = Icon.createWithResource(context, R.drawable.mipush_small_notification);
		}
		Log.d(TAG, "begin modifying ");
		String packageName = null;
		try {
			packageName = evolving.getPackageName();
			if ("com.xiaomi.xmsf".equals(packageName))
				packageName = n.extras.getString("target_package", null);
		} catch (final RuntimeException ignored) {} // Fall-through
		if (packageName == null) {
			Log.e(TAG, "packageName is null");
			return;
		}
		n.extras.putBoolean("miui.isGrayscaleIcon", true);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			// do nothing
		} else {
			final IconCache cache = IconCache.getInstance();
			final Context context = getPackageContext(packageName);
			int iconId;
			if ((iconId  = context.getResources().getIdentifier(NOTIFICATION_SMALL_ICON, "drawable", packageName)) != 0) { // has small icon
				Log.d("inspect", "iconId0 " + iconId);
				setSmallIcon(n, Icon.createWithResource(packageName, iconId));
			} else if ((iconId = context.getResources().getIdentifier(NOTIFICATION_ICON, "drawable", packageName)) != 0) { // has icon
				Log.d("inspect", "iconId1 " + iconId);
				setSmallIcon(n, Icon.createWithResource(packageName, iconId));
			} else { // does not have icon
				Log.d("inspect", "iconId2 " + iconId);
				Icon iconCache = cache.getIconCache(context, packageName, (ctx, b) -> Icon.createWithBitmap(b));
				if (iconCache != null) {
					setSmallIcon(n, iconCache);
				} else {
					setSmallIcon(n, defIcon);
				}
			}
			n.color = cache.getAppColor(context, packageName, (ctx, b) -> ColorUtil.getColor(b)[0]);
		}
		Log.d(TAG, "end modifying");
	}
}
