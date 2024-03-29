package com.main.personalfinances.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.AlarmManagerCompat;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.main.personalfinances.notification.NotificationReceiver;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Represents a reminder for future payment. It is related to a budget.
 * Has category, description and due date.
 */
@Entity(tableName = "future_payments")
public class FuturePayment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int budgetId;
    private final FuturePaymentCategory category;
    private final String description;
    private final LocalDateTime dueDate;


    public FuturePayment(int budgetId, FuturePaymentCategory category, String description,
                         LocalDateTime dueDate) {
        this.budgetId = budgetId;
        this.category = category;
        this.description = description;
        this.dueDate = dueDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public String getDescription() {
        return description;
    }

    public FuturePaymentCategory getCategory() {
        return category;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void scheduleNotification(Context context) {
        if (dueDate != null && dueDate.isAfter(LocalDateTime.now())) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            notificationIntent.putExtra("future_payment_description", description);

            long triggerMillis = LocalDateTimeToMillis(dueDate);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, id, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
        }
    }

    /**
     * LocalDateTime doesn't use a specific timezone. We need to get zone first,
     * then use instant to make it UTC (Coordinated Universal Time),
     * and then turn it to milliseconds
     *
     * @param localDateTime date to get milliseconds from
     * @return Unix timestamp in milliseconds
     */
    public static long LocalDateTimeToMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
