package com.example.hardeep.kp_encrypt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsReader extends BroadcastReceiver {

    Context context;
    String messageBody = "";
    String fromNo = "";
    String contactName = "";
    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBody = smsMessage.getMessageBody();
                fromNo = smsMessage.getDisplayOriginatingAddress();
            }
            if(messageBody.startsWith("!!encrypt") || messageBody.startsWith("!encrypt")) {
                //Resolving the contact name from the contacts.
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(fromNo));
                Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);
                try {
                    c.moveToFirst();
                    contactName = c.getString(0);
                } catch (Exception e) {
                    contactName = fromNo;
                } finally { c.close(); }
                askPermission();
            }
        }
    }

    public void askPermission() {
        Intent intent = new Intent(context, Main.class);
        intent.putExtra("message", messageBody);
        intent.putExtra("noti", true);
        PendingIntent decryptOpen = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Encrypted Message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(decryptOpen)
                .setContentText("Encrypted message from: " + contactName + " Would you like to decrypt it?")
                .setAutoCancel(true)
                .build();
        notification.defaults |= Notification.DEFAULT_ALL;

        NotificationManager manager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);

        manager.notify(1,notification);
    }
}
