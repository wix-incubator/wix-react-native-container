
 package com.wixoneapp.notifications.posting;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.wix.container.R;
import com.wixoneapp.notifications.NotificationIntentAdapter;
import com.wixoneapp.notifications.PushNotificationProps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class NotificationSummaryStrategy {

    protected static class CategorySummary {
        int count = 0;
        PushNotificationProps dominantNotification;
    }

    private static final Map<String, String> sCategoryMapping = new TreeMap<>();
    static {
        sCategoryMapping.put("category 1", "Category 1");
        sCategoryMapping.put("category 2", "Category 2");
    }

    public Map<Integer, Notification> getNotificationsMap(Context context, List<PushNotificationProps> notifications) {
        if (notifications.size() == 0) {
            return null;
        }

        if (notifications.size() == 1) {
            return singleNotificationContent(context, notifications.get(0));
        }
        return multipleNotificationContent(context, notifications);
    }

    protected Map<Integer, Notification> singleNotificationContent(Context context, PushNotificationProps notificationProps) {
        final Notification.BigTextStyle style = new Notification.BigTextStyle().bigText(notificationProps.getBody()).setSummaryText(getCategory(notificationProps));
        final PendingIntent intent = NotificationIntentAdapter.getCTAIntent(context, notificationProps);
        final Notification notification = buildNotification(context, notificationProps.getTitle(), notificationProps.getBody(), style, intent);

        final int notificationId = convertMetaSiteToId(notificationProps.getMetaSiteId());

        final HashMap<Integer, Notification> notificationsMap = new HashMap<>();
        notificationsMap.put(notificationId, notification);
        return notificationsMap;
    }

    protected Map<Integer, Notification> multipleNotificationContent(Context context, List<PushNotificationProps> rawNotifications) {
        final Map<String, List<PushNotificationProps>> byMetaSiteRaw = groupByMetaSites(rawNotifications);
        final Map<String, Notification> byMetaSiteConcrete = aggregatePerMetaSiteNotifications(context, byMetaSiteRaw);
        final Map<Integer, Notification> byIdConcrete = getConcreteNotificationsById(byMetaSiteConcrete);
        return byIdConcrete;
    }

    protected Map<String, List<PushNotificationProps>> groupByMetaSites(List<PushNotificationProps> rawNotifications) {
        final Map<String, List<PushNotificationProps>> dictionary = new HashMap<>();

        for (PushNotificationProps notification : rawNotifications) {
            final String metaSiteId = notification.getMetaSiteId();
            if (metaSiteId != null) {
                List<PushNotificationProps> siteNotifications = dictionary.get(metaSiteId);
                if (siteNotifications == null) {
                    siteNotifications = new LinkedList<>();
                    dictionary.put(metaSiteId, siteNotifications);
                }
                siteNotifications.add(notification);
            }
        }
        return dictionary;
    }

    protected Map<String, Notification> aggregatePerMetaSiteNotifications(Context context, Map<String, List<PushNotificationProps>> rawNotificationsByMetaSite) {
        final Map<String, Notification> dictionary = new HashMap<>();
        for (Map.Entry<String, List<PushNotificationProps>> entry : rawNotificationsByMetaSite.entrySet()) {
            final String metaSiteId = entry.getKey();
            final List<PushNotificationProps> rawMetaSiteNotifications = entry.getValue();

            final Notification metaSiteNotification = aggregateMetaSiteNotifications(context, rawMetaSiteNotifications);
            dictionary.put(metaSiteId, metaSiteNotification);
        }
        return dictionary;
    }

    protected Map<Integer, Notification> getConcreteNotificationsById(Map<String, Notification> notificationsByMetaSite) {
        final Map<Integer, Notification> notificationsById = new HashMap<>();
        for (Map.Entry<String, Notification> entry : notificationsByMetaSite.entrySet()) {
            final String metaSiteId = entry.getKey();
            final Notification notification = entry.getValue();

            final int id = convertMetaSiteToId(metaSiteId);
            notificationsById.put(id, notification);
        }
        return notificationsById;
    }

    protected Notification aggregateMetaSiteNotifications(Context context, List<PushNotificationProps> rawMetaSiteNotifications) {
        final Map<String, CategorySummary> byCategorySummaries = summarizeByCategories(rawMetaSiteNotifications);
        final int totalCount = rawMetaSiteNotifications.size();

        final Notification.InboxStyle styleBuilder = new Notification.InboxStyle();
        final StringBuilder bodyBuilder = new StringBuilder(64);
        final Set<Map.Entry<String, CategorySummary>> entries = byCategorySummaries.entrySet();
        int i = 0;
        for (Map.Entry<String, CategorySummary> entry : entries) {
            final String category = entry.getKey();
            final CategorySummary summary = entry.getValue();

            styleBuilder.addLine(getNotificationLine(category, summary));

            bodyBuilder.append(category);
            if (i != entries.size() - 1) {
                bodyBuilder.append(", ");
            }
            i++;
        }

        PendingIntent ctaPendingIntent;
        if (byCategorySummaries.size() > 1) {
            ctaPendingIntent = NotificationIntentAdapter.getMultiCategoriesCTAIntent(context, rawMetaSiteNotifications.get(0).getMetaSiteId());
        } else {
            ctaPendingIntent = NotificationIntentAdapter.getCTAIntent(context, rawMetaSiteNotifications.get(rawMetaSiteNotifications.size() - 1));
        }

        final String title = context.getResources().getString(R.string.notificationsAggrTitle, totalCount);
        styleBuilder.setBigContentTitle(title);
        styleBuilder.setSummaryText(context.getResources().getString(R.string.notificationsAggrSubtext));

        Notification notification = buildNotification(context, title, bodyBuilder.toString(), styleBuilder, ctaPendingIntent);
        notification.defaults |= Notification.DEFAULT_ALL;
        return notification;
    }

    protected Map<String, CategorySummary> summarizeByCategories(List<PushNotificationProps> rawMetaSiteNotifications) {
        final Map<String, CategorySummary> categorySummaries = new HashMap<>();

        for (PushNotificationProps rawPushNotification : rawMetaSiteNotifications) {
            final String category = getCategory(rawPushNotification);
            CategorySummary summary = categorySummaries.get(category);
            if (summary == null) {
                summary = new CategorySummary();
                categorySummaries.put(category, summary);
            }
            summary.count++;
            summary.dominantNotification = rawPushNotification;
        }
        return categorySummaries;
    }

    protected Notification buildNotification(Context context, String title, String body, Notification.Style style, PendingIntent intent) {
        final Resources res = context.getResources();
        final Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(title != null ? title : res.getString(R.string.notificationsFallbackTitle))
                .setContentText(body)
                .setStyle(style)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder
//                    .setSmallIcon(R.drawable.notification)
                    .setColor(res.getColor(R.color.notificationBkg));
        }

        Notification notification = notificationBuilder.build();
        notification.defaults |= Notification.DEFAULT_ALL;
        return notification;
    }

    protected int convertMetaSiteToId(String metaSiteId) {
        return (int) UUID.fromString(metaSiteId).getMostSignificantBits();
    }

    protected String getCategory(PushNotificationProps notificationProps) {
        final String rawCategory = notificationProps.getCategory().split("[/.]")[0];
        final String category = sCategoryMapping.get(rawCategory);
        return (category != null ? category : rawCategory);
    }

    protected CharSequence getNotificationLine(String category, CategorySummary summary) {
        final SpannableStringBuilder notifLineBuilder = new SpannableStringBuilder();

        // First the prefix so we could measure it out.
        notifLineBuilder.append(category).append(" (").append(String.valueOf(summary.count)).append(") ");
        final int prefixLength = notifLineBuilder.length();

        // Now the content.
        notifLineBuilder.append(summary.dominantNotification.getBody());

        // Bold-en the prefix region only.
        notifLineBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, prefixLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return notifLineBuilder;
    }
}
