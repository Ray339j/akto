package com.akto.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.akto.dao.AccountSettingsDao;
import com.akto.dao.ApiCollectionsDao;
import com.akto.dao.context.Context;
import com.akto.dto.AccountSettings;
import com.akto.dto.ApiCollection;
import com.akto.dto.Config;
import com.akto.log.LoggerMaker;
import com.akto.log.LoggerMaker.LogDb;
import com.mongodb.BasicDBObject;

public class RedactAlert {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private static final LoggerMaker loggerMaker = new LoggerMaker(RedactAlert.class, LogDb.RUNTIME);

    static final String regex = ".*\\*\\*\\*\\*.*";
    static final Pattern pattern = Pattern.compile(regex);

    private static final int CACHE_INTERVAL = 2 * 60;
    private static int lastFetched = 0;
    private static boolean isRedactOn = false;

    private static boolean checkRedact() {
        int now = Context.now();
        if (lastFetched + CACHE_INTERVAL > now) {
            return isRedactOn;
        }

        isRedactOn = false;
        AccountSettings accountSettings = AccountSettingsDao.instance.findOne(AccountSettingsDao.generateFilter());
        List<ApiCollection> all = ApiCollectionsDao.instance.findAll(new BasicDBObject());
        for (ApiCollection apiCollection : all) {
            if (apiCollection.getRedact()) {
                isRedactOn = true;
            }
        }
        if (accountSettings.isRedactPayload()) {
            isRedactOn = true;
        }
        lastFetched = now;
        return isRedactOn;
    }

    public static void sendToCyborgSlack(String message) {
        String slackCyborgWebhookUrl = null;
        try {
            Config.SlackAlertCyborgConfig slackCyborgWebhook = com.akto.onprem.Constants.getSlackAlertCyborgConfig();
            if (slackCyborgWebhook != null && slackCyborgWebhook.getSlackWebhookUrl() != null
                    && !slackCyborgWebhook.getSlackWebhookUrl().isEmpty()) {
                slackCyborgWebhookUrl = slackCyborgWebhook.getSlackWebhookUrl();
                LoggerMaker.sendToSlack(slackCyborgWebhookUrl, message);
            }
        } catch (Exception e) {
            loggerMaker.errorAndAddToDb(e, "Unable to send slack alert");
        }
    }


    private static void checkRedactedDataAndSendAlert(List<String> data,
            int apiCollectionId, String method, String url) {

        for (String d : data) {
            if (!pattern.matcher(d).matches()) {
                int accountId = Context.accountId.get();
                String message = String.format("Unredacted sample data coming for account %d for API: %d %s %s",
                        accountId, apiCollectionId, method, url);
                sendToCyborgSlack(message);
            }
        }
    }

    public static void submitSampleDataForChecking(List<String> data,
            int apiCollectionId, String method, String url) {
        int accountId = Context.accountId.get();

        if (!checkRedact()) {
            return;
        }
        executorService.submit(() -> {
            Context.accountId.set(accountId);
            try {
                checkRedactedDataAndSendAlert(data, apiCollectionId, method, url);
            } catch (Exception e) {
                loggerMaker.errorAndAddToDb(e, "Error in check redact and send alert" + e.getMessage());
            }
        });
    }

    public static void submitSensitiveSampleDataCall(int apiCollectionId) {
        int accountId = Context.accountId.get();

        if (!checkRedact()) {
            return;
        }
        executorService.submit(() -> {
            Context.accountId.set(accountId);
            String message = String.format(
                    "Unredacted sensitive sample data coming for account %d for API collection: %d",
                    accountId, apiCollectionId);
            sendToCyborgSlack(message);
        });
    }
}