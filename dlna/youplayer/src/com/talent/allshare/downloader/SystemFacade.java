
package com.talent.allshare.downloader;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;


interface SystemFacade {
    /**
     * @see System#currentTimeMillis()
     */
    public long currentTimeMillis();

    /**
     * Send a broadcast intent.
     */
    public void sendBroadcast(Intent intent);

    /**
     * Returns true if the specified UID owns the specified package name.
     */
    public boolean userOwnsPackage(int uid, String pckg) throws NameNotFoundException;

    /**
     * Post a system notification to the NotificationManager.
     */
    public void postNotification(long id, Notification notification);

    /**
     * Cancel a system notification.
     */
    public void cancelNotification(long id);

    /**
     * Cancel all system notifications.
     */
    public void cancelAllNotifications();

    /**
     * Start a thread.
     */
    public void startThread(Thread thread);
}
