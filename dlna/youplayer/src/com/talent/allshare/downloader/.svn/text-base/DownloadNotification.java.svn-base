/*
 * Copyright (C) 2008 The Android Open Source Project
 * Copyright (C) 2012, Code Aurora Forum. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talent.allshare.downloader;

import java.util.HashMap;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.youplayer.player.R;

/**
 * This class handles the updating of the Notification Manager for the
 * cases where there is an ongoing download. Once the download is complete
 * (be it successful or unsuccessful) it is no longer the responsibility
 * of this component to show the download in the notification manager.
 *
 */
public class DownloadNotification {

    Context mContext;
    HashMap <String, NotificationItem> mNotifications;
    private SystemFacade mSystemFacade;

    static final String LOGTAG = "DownloadNotification";
    static class NotificationItem {
        int mId;  // This first db _id for the download for the app
        long mTotalCurrent = 0;
        long mTotalTotal = 0;
        int mTitleCount = 0;
        String mPackageName;  // App package name
        String mDescription;
        String[] mTitles = new String[2]; // download titles.
        String mPausedText = null;
        boolean mUnsuccessful = false;
        int speed=0;
        /*
         * Add a second download to this notification item.
         */
        void addItem(String title, long currentBytes, long totalBytes) {
            mTotalCurrent += currentBytes;
            if (totalBytes <= 0 || mTotalTotal == -1) {
                mTotalTotal = -1;
            } else {
                mTotalTotal += totalBytes;
            }
            if (mTitleCount < 2) {
                mTitles[mTitleCount] = title;
            }
            mTitleCount++;
        }
    }


    /**
     * Constructor
     * @param ctx The context to use to obtain access to the
     *            Notification Service
     */
    public DownloadNotification(Context ctx, SystemFacade systemFacade) {
        mContext = ctx;
        mSystemFacade = systemFacade;
        mNotifications = new HashMap<String, NotificationItem>();
    }

    /*
     * Update the notification ui.
     */
  /*  public void updateNotification(Collection<DownloadInfo> downloads) {
        updateActiveNotification(downloads);
        updateCompletedNotification(downloads);
    }*/

    public void updateActiveNotification(DownloadInfo download , boolean cancleDownload) {
        // Collate the notifications
    	Log.i("app", "updateActiveNotification : id :  " + download.getId());
        mNotifications.clear();
            if (!isActiveAndVisible(download)) {
                return;
            }
            String packageName = download.getFilePath();
            long max = download.getFileSize();
            long progress = download.getCompeleteSize();
            String[] path = download.getFilePath().split("/");
            String title = null;
            if (path != null) {
            	 title = path[path.length - 1];
			}
            if (title == null || title.length() == 0) {
            	title = mContext.getResources().getString(
            			R.string.download_unknown_title);
            }

            NotificationItem item;
            if (mNotifications.containsKey(packageName)) {
                item = mNotifications.get(packageName);
                item.addItem(title, progress, max);
		  item.speed=download.getSpeed();
            } else {
                item = new NotificationItem();
                item.mPackageName = packageName;
                item.addItem(title, progress, max);
		  item.speed=download.getSpeed();
                mNotifications.put(packageName, item);
            }
            // Check whether status of downloading is unsuccessful
            if (isUnsuccessful(download)) {
                item.mUnsuccessful = true;
            }

        // Add the notifications
        for (NotificationItem notificationitem : mNotifications.values()) {
            // Build the notification object
           // final Notification.Builder builder = new Notification.Builder(mContext);
            Notification n = new Notification();
			
            boolean hasPausedText = (notificationitem.mPausedText != null);
            int iconResource = android.R.drawable.stat_sys_download_done;
            if (hasPausedText) {
                iconResource = android.R.drawable.stat_sys_warning;
            }
            //builder.setSmallIcon(iconResource);
	     n.icon = iconResource;
	     n.when=System.currentTimeMillis();
	     n.flags |= Notification.FLAG_ONGOING_EVENT;
            //builder.setOngoing(true);



       // Build the RemoteView object
            RemoteViews expandedView = new RemoteViews(
                    "com.youplayer.player",
                    R.layout.status_bar_ongoing_event_progress_bar);
            
               // expandedView.setTextViewText(R.id.description,              "Description");
        
            //expandedView.setTextViewText(R.id.title, " "+mName+"  "+mId);

           // expandedView.setTextViewText(R.id.progress_text,
             //       "progress text");
            expandedView.setImageViewResource(R.id.appIcon, iconResource);
            n.contentView = expandedView;
			
            boolean hasContentText = false;
            StringBuilder notificationtitle = new StringBuilder(notificationitem.mTitles[0]);
            if (notificationitem.mTitleCount > 1) {
                notificationtitle.append(", ");
                notificationtitle.append(notificationitem.mTitles[1]);
                if (notificationitem.mTitleCount > 2) {
                    notificationtitle.append(String.format(" and %d more",
                            new Object[] { Integer.valueOf(notificationitem.mTitleCount - 2) }));
                }
            } else if (!TextUtils.isEmpty(notificationitem.mDescription)) {
                //builder.setContentText(notificationitem.mDescription);
		  expandedView.setTextViewText(R.id.description,notificationitem.mDescription);
                hasContentText = true;
            }
           // builder.setContentTitle(notificationtitle);

            expandedView.setTextViewText(R.id.title, notificationtitle);

	     expandedView.setTextViewText(R.id.description," "+notificationitem.speed+"KB/S");
		 
            if (hasPausedText) {
                //builder.setContentText(notificationitem.mPausedText);
		  expandedView.setTextViewText(R.id.description,notificationitem.mPausedText);

            } else {
                // Indicate user to handle if status of downloading is
                // unsuccessful
                if (notificationitem.mUnsuccessful) {
                  //  builder.setContentText(mContext
                  //          .getString(R.string.notification_download_failed));
			expandedView.setTextViewText(R.id.description,mContext
                            .getString(R.string.notification_download_failed));

                }

		  expandedView.setViewVisibility(R.id.progress_bar, View.VISIBLE);
                expandedView.setProgressBar(R.id.progress_bar,
			(int) notificationitem.mTotalTotal, (int) notificationitem.mTotalCurrent, notificationitem.mTotalTotal == -1);
            }
               // builder.setProgress(
              //          (int) notificationitem.mTotalTotal, (int) notificationitem.mTotalCurrent, notificationitem.mTotalTotal == -1);
              /*  if (hasContentText) {
                    builder.setContentInfo(
                            buildPercentageLabel(mContext, notificationitem.mTotalTotal, notificationitem.mTotalCurrent));
                }*/

		 Intent intent ;
	   
            	intent = new Intent("action.com.youplayer.player");
           
          

            n.contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
			

            if (progress >= max || cancleDownload) {
				mSystemFacade.cancelNotification(notificationitem.mId);
			} else {
				//mSystemFacade.postNotification(notificationitem.mId, builder.getNotification());
				mSystemFacade.postNotification(notificationitem.mId,n);
			}

        }
    }

    public void updateCompletedNotification(DownloadInfo download, boolean cancleDownload) {
            if (!isCompleteAndVisible(download)) {
                return;
            }
            mNotifications.clear();
            Log.i("app", "updateCompletedNotification : id :  " + download.getId());
            String[] path = download.getFilePath().split("/");
            String title = null;
            if (path != null) {
            	 title = path[path.length - 1];
			}
            if (title == null || title.length() == 0) {
            	title = mContext.getResources().getString(
            			R.string.download_unknown_title);
            }
            notificationForCompletedDownload(download.getId(), title ,cancleDownload);
    }
    
    void notificationForCompletedDownload(int id, String title, boolean cancleDownload) {
        // Add the notifications

		Notification n = new Notification();
		n.icon = android.R.drawable.stat_sys_download_done;
     //   Notification.Builder builder = new Notification.Builder(mContext);
       // builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        if (title == null || title.length() == 0) {
            title = mContext.getResources().getString(
                    R.string.download_unknown_title);
        }
        String caption;
        if (cancleDownload) {
            caption = mContext.getResources()
                    .getString(R.string.notification_download_failed);
        } else {
            caption = mContext.getResources()
                    .getString(R.string.notification_download_complete);
        }
        /*
        intent.setClassName("com.android.providers.downloads",
                DownloadReceiver.class.getName());
        intent.setData(contentUri);*/


 // Build the RemoteView object
            RemoteViews expandedView = new RemoteViews(
                    "com.youplayer.player",
                    R.layout.status_bar_ongoing_event_progress_bar);
            
               // expandedView.setTextViewText(R.id.description,              "Description");
        
            //expandedView.setTextViewText(R.id.title, " "+mName+"  "+mId);

                expandedView.setViewVisibility(R.id.progress_bar, View.GONE);

  //          expandedView.setTextViewText(R.id.progress_text,
  //                  "progress text");
            expandedView.setImageViewResource(R.id.appIcon, android.R.drawable.stat_sys_download_done);
            n.contentView = expandedView;

           expandedView.setTextViewText(R.id.description,caption);
	    expandedView.setTextViewText(R.id.title, title);
       // builder.setContentTitle(title);
        //builder.setContentText(caption);
        /*builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));

        intent = new Intent(Constants.ACTION_HIDE);
        intent.setClassName("com.android.providers.downloads",
                DownloadReceiver.class.getName());
        intent.setData(contentUri);
        builder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));*/

	  Intent intent ;
	   
            	intent = new Intent("action.com.youplayer.player");
           
          

            n.contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        mSystemFacade.cancelNotification(id);
        mSystemFacade.postNotification(id, n);
    }

    private boolean isActiveAndVisible(DownloadInfo download) {
        return true;
    }
    
    private boolean isUnsuccessful(DownloadInfo download) {
        return false;
    }
    
    private boolean isCompleteAndVisible(DownloadInfo download) {
        return download.getFileSize() >= download.getCompeleteSize();
    }

    private static String buildPercentageLabel(
            Context context, long totalBytes, long currentBytes) {
        if (totalBytes <= 0) {
            return null;
        } else {
            final int percent = (int) (100 * currentBytes / totalBytes);
            return String.format("%d %", percent);
        }
    }
}
