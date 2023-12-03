package ru.malw.weatherinformer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class WeatherNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Имя и описание канала уведомлений
            CharSequence channelName = "Weather Channel";
            String channelDescription = "Weather Updates";

            // Важность канала уведомлений
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Создание канала уведомлений с указанными атрибутами
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            channel.setDescription(channelDescription);

            // Регистрация канала уведомлений в системе
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Получение данных о погоде из Intent
        double currentTemperature = intent.getDoubleExtra("currentTemperature", 0.0);
        String currentDescription = intent.getStringExtra("currentDescription");
        double futureTemperature = intent.getDoubleExtra("futureTemperature", 0.0);
        String futureDescription = intent.getStringExtra("futureDescription");

        // Создание уведомления с соответствующими данными о погоде
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.i01)
                .setContentTitle("Погода сейчас и через 3 часа")
                .setContentText("Сейчас: " + currentTemperature + "°, " + currentDescription +
                        "\nЧерез 3 часа: " + futureTemperature + "°, " + futureDescription)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Отображение уведомления
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
