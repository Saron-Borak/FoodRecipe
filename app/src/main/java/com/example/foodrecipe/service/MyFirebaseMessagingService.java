package com.example.foodrecipe.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.foodrecipe.MainActivity;
import com.example.foodrecipe.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service to handle Firebase Cloud Messaging (FCM) messages
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "recipe_notifications";
    private static final String CHANNEL_NAME = "Recipe Notifications";
    private static final String CHANNEL_DESC = "Notifications for recipe updates and suggestions";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        
        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            
            // Send notification
            sendNotification(
                    remoteMessage.getNotification().getTitle(), 
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getData());
        }
    }
    
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        
        // TODO: Send token to your app server for targeted notifications
    }
    
    /**
     * Create and show a notification with received FCM message
     *
     * @param title Title of the notification
     * @param messageBody Body of the notification
     * @param data Additional data from the FCM message
     */
    private void sendNotification(String title, String messageBody, 
                                 java.util.Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        
        // If we have recipe data, we can navigate to that specific recipe
        if (data != null && data.containsKey("recipeId")) {
            intent.putExtra("recipeId", data.get("recipeId"));
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        // Create notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
        
        // Show notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
