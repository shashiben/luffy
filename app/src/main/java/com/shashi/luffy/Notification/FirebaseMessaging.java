package com.shashi.luffy.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shashi.luffy.Fragments.ChatScreenActivity;
import com.shashi.luffy.Fragments.CommentActivity;
import com.shashi.luffy.R;

import java.util.Random;

public class FirebaseMessaging extends  FirebaseMessagingService{
    private static final String ADMIN_CHANNEL_ID="admin_channel";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser=sp.getString("Current_USERID","None");

        String notificationType=remoteMessage.getData().get("notificationType");
        assert notificationType != null;
        if(notificationType.equals("PostNotification")){
           String sender=remoteMessage.getData().get("notificationType");
            String pId=remoteMessage.getData().get("pId");
            String pTitile=remoteMessage.getData().get("pTitle");
            String pDescription=remoteMessage.getData().get("pDescription");
            assert sender != null;
            if(!sender.equals(savedCurrentUser)){
                showPostNotifications(""+pId,""+pTitile,""+pDescription);
            }

        }else if(notificationType.equals("ChatNotification")){
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
            assert sent != null;
            if(fUser!=null && sent.equals(fUser.getUid())){
                if(!savedCurrentUser.equals(user)){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendOAndAboveNotification(remoteMessage);
                    }else{
                        sendNormalMessage(remoteMessage);
                    }
                }
            }
        }

    }

    private void showPostNotifications(String pId, String pTitle, String pDescription) {
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID=new Random().nextInt(3000);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            setupPostNotificationChannel(notificationManager);

        }

        Intent intent=new Intent(this, CommentActivity.class);
        intent.putExtra("postId",pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(), R.drawable.luffy);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this,""+ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.luffy)
                .setLargeIcon(largeIcon)
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);
        assert notificationManager != null;
        notificationManager.notify(notificationID,notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupPostNotificationChannel(NotificationManager notificationManager) {

        CharSequence channelName="New Notification";
        String channelDescription="Device to device post notification";
        NotificationChannel adminChannel= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(channelDescription);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            adminChannel.enableLights(true);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(adminChannel);
            }
        }



    }

    private void sendNormalMessage(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        assert user != null;
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatScreenActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert icon != null;
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true).setSound(defSoundUri)
                .setContentIntent(pIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;
        }
        assert notificationManager != null;
        notificationManager.notify(j,builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        assert user != null;
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatScreenActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification notification1=new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pIntent,defSoundUri,icon);
        int j=0;
        if(i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(tokenRefresh);
        assert user != null;
        ref.child(user.getUid()).setValue(token);
    }
}