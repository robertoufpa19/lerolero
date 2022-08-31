package robertorodrigues.curso.academicos.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.ComentariosActivity;
import robertorodrigues.curso.academicos.activity.ConversasActivity;
import robertorodrigues.curso.academicos.activity.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage notificacao) {

        if (notificacao.getNotification() != null) {

            String titulo = notificacao.getNotification().getTitle();
            String corpo = notificacao.getNotification().getBody();

            if(corpo.equals("Nova Mensagem de")){
                enviarNotificacao(titulo, corpo);
            }

            if(corpo.equals("Novo Comentario ")){
                enviarNotificacaoComentarios(titulo, corpo);
            }

        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void enviarNotificacao(String titulo, String corpo) {

        //Configurações para a notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getBaseContext(), ConversasActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setSmallIcon(R.drawable.logosemnomeacademicos)
                .setSound(uriSom)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000,1000})
                .setContentIntent(pendingIntent);

        //Recuperar notificação
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channel.setSound(uriSom, audioAttributes);
            channel.setVibrationPattern(new long[] {1000,1000});
            notificationManager.createNotificationChannel(channel);

        }

        //Enviar notificação
        notificationManager.notify(0, notificacao.build());

    }

    private void enviarNotificacaoComentarios(String titulo, String corpo) {

        //Configurações para a notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getBaseContext(), ComentariosActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setSmallIcon(R.drawable.logosemnomeacademicos)
                .setSound(uriSom)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000,1000})
                .setContentIntent(pendingIntent);

        //Recuperar notificação
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channel.setSound(uriSom, audioAttributes);
            channel.setVibrationPattern(new long[] {1000,1000});
            notificationManager.createNotificationChannel(channel);

        }

        //Enviar notificação
        notificationManager.notify(0, notificacao.build());

    }

}
