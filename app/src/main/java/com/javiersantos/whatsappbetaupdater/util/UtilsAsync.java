package com.javiersantos.whatsappbetaupdater.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.javiersantos.whatsappbetaupdater.Config;
import com.javiersantos.whatsappbetaupdater.R;
import com.javiersantos.whatsappbetaupdater.WhatsAppBetaUpdaterApplication;
import com.javiersantos.whatsappbetaupdater.activity.MainActivity;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UtilsAsync {

    public static class LatestWhatsAppVersion extends AsyncTask<Void, Void, String> {
        private TextView latestVersion, toolbarSubtitle;
        private FloatingActionButton fab;
        private ProgressWheel progressWheel;
        private Context context;
        private AppPreferences appPreferences;

        public LatestWhatsAppVersion(Context context, TextView latestVersion, TextView toolbarSubtitle, FloatingActionButton fab, ProgressWheel progressWheel) {
            this.latestVersion = latestVersion;
            this.toolbarSubtitle = toolbarSubtitle;
            this.fab = fab;
            this.progressWheel = progressWheel;
            this.context = context;
            this.appPreferences = WhatsAppBetaUpdaterApplication.getAppPreferences();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UtilsUI.showFAB(fab, false);
            latestVersion.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (UtilsNetwork.isNetworkAvailable(context)) {
                return getLatestWhatsAppVersion();
            } else {
                return "0.0.0.0";
            }
        }

        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);
            latestVersion.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.GONE);

            if (!version.equals("0.0.0.0")) {
                latestVersion.setText(version);
                if (UtilsWhatsApp.isWhatsAppInstalled(context) && UtilsWhatsApp.isUpdateAvailable(UtilsWhatsApp.getInstalledWhatsAppVersion(context), version)) {
                    UtilsUI.showFAB(fab, true);
                    toolbarSubtitle.setText(String.format(context.getResources().getString(R.string.update_available), version));
                    if (appPreferences.getAutoDownload()) {
                        new UtilsAsync.DownloadFile(context, UtilsEnum.DownloadType.WHATSAPP_APK, version).execute();
                    }
                } else if(!UtilsWhatsApp.isWhatsAppInstalled(context)){
                    UtilsUI.showFAB(fab, true);
                    toolbarSubtitle.setText(String.format(context.getResources().getString(R.string.update_not_installed), version));
                } else {
                    UtilsUI.showFAB(fab, false);
                    toolbarSubtitle.setText(context.getResources().getString(R.string.update_not_available));
                }
            } else {
                latestVersion.setText(context.getResources().getString(R.string.whatsapp_not_available));
                toolbarSubtitle.setText(context.getResources().getString(R.string.update_not_connection));
            }

        }
    }

    public static class NotifyWhatsAppVersion extends AsyncTask<Void, Void, String> {
        private Context context;
        private AppPreferences appPreferences;
        private Intent intent;

        public NotifyWhatsAppVersion(Context context, Intent intent) {
            this.context = context;
            this.appPreferences = WhatsAppBetaUpdaterApplication.getAppPreferences();
            this.intent = intent;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (UtilsNetwork.isNetworkAvailable(context)) {
                return getLatestWhatsAppVersion();
            } else {
                return "0.0.0.0";
            }
        }

        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);

            if (UtilsWhatsApp.isWhatsAppInstalled(context) && UtilsWhatsApp.isUpdateAvailable(UtilsWhatsApp.getInstalledWhatsAppVersion(context), version)) {
                String title = String.format(context.getResources().getString(R.string.notification), version);
                String message = String.format(context.getResources().getString(R.string.notification_description), context.getResources().getString(R.string.app_name));
                intent.putExtra("title", title);
                intent.putExtra("message", message);

                Intent notIntent = new Intent(context, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                NotificationManagerCompat manager = NotificationManagerCompat.from(context);

                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(message);
                Integer resId = R.mipmap.ic_launcher;

                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), resId));

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(style)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .extend(wearableExtender);

                // Check if "Silent Notification Tone" is selected
                if (!appPreferences.getSoundNotification().toString().equals("null")) {
                    builder.setSound(appPreferences.getSoundNotification());
                }

                Notification notification = builder.build();
                manager.notify(0, notification);
            }

        }
    }

    public static class LatestAppVersion extends AsyncTask<Void, Void, String> {
        private Context context;

        public LatestAppVersion(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return getLatestAppVersion();
        }

        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);

            if (UtilsWhatsApp.isUpdateAvailable(UtilsApp.getAppVersionName(context), version)) {
                UtilsDialog.showUpdateAvailableDialog(context, version);
            }
        }
    }

    public static class DownloadFile extends AsyncTask<Void, Integer, Integer> {
        private Context context;
        private MaterialDialog dialog;
        private UtilsEnum.DownloadType downloadType;
        private String version, path, filename, downloadUrl;

        public DownloadFile(Context context, UtilsEnum.DownloadType downloadType, String version) {
            this.context = context;
            this.version = version;
            this.downloadType = downloadType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

            // Configure cancel button and show progress dialog
            MaterialDialog.Builder builder = UtilsDialog.showDownloadingDialog(context, downloadType, version);
            builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    cancel(true);
                }
            });
            dialog = builder.show();

            // Configure type of download: WhatsApp update or Beta Updater update
            switch (downloadType) {
                case WHATSAPP_APK:
                    filename = "Signal_" + version + ".apk";
                    downloadUrl = Config.WHATSAPP_APK;
                    break;
                case UPDATE:
                    filename = context.getPackageName() + "_" + version + ".apk";
                    downloadUrl = Config.GITHUB_APK + "v" + version + "/" + context.getPackageName() + ".apk";
                    break;
            }

            // Create download directory if doesn't exist
            File file = new File(path);
            if (!file.exists()) { file.mkdir(); }

        }

        @Override
        protected Integer doInBackground(Void... voids) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            Integer lengthOfFile = 0;

            try {
                URL url = new URL(downloadUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // Getting file lenght
                lengthOfFile = connection.getContentLength();
                // Read file
                input = connection.getInputStream();
                // Where to write file
                output = new FileOutputStream(new File(path, filename));

                byte data[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    // Close input if download has been cancelled
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // Updating download progress
                    if (lengthOfFile > 0) {
                        publishProgress((int) ((total * 100) / lengthOfFile));
                    }
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (output != null) { output.close(); }
                    if (input != null) { input.close(); }
                } catch (IOException ignored) {}

                if (connection != null) {
                    connection.disconnect();
                }
            }

            return lengthOfFile;
        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Integer file_length) {
            UtilsHelper.dismissDialog(dialog);
            File file = new File(path, filename);
            if (file_length != null && file.length() == file_length) {
                // File download: OK
                context.startActivity(UtilsIntent.getOpenAPKIntent(file));
                switch (downloadType) {
                    case WHATSAPP_APK:
                        UtilsDialog.showSaveAPKDialog(context, file, version);
                        break;
                    case UPDATE:
                        break;
                }
            } else {
                // File download: FAILED
                onCancelled();
                UtilsDialog.showSnackbar(context, context.getResources().getString(R.string.snackbar_failed));
            }
        }

        @Override
        protected void onCancelled() {
            // Delete uncompleted file
            File file = new File(path, filename);
            if (file.exists()) { file.delete(); }
        }

    }

    public static String getLatestWhatsAppVersion() {
        String source = "";

        try {
            URL url = new URL(Config.WHATSAPP_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder str = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                if (line.contains(Config.PATTERN_LATEST_VERSION)) {
                    str.append(line);
                }
            }

            in.close();

            source = str.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] split = source.split(Config.PATTERN_LATEST_VERSION);
        String urlWithVersion = split[1].split("<")[0].trim();

        return urlWithVersion;
    }

    public static String getLatestAppVersion() {
        String res = "0.0.0.0";
        String source = "";

        try {
            URL url = new URL(Config.GITHUB_TAGS);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                str.append(line);
            }

            in.close();

            source = str.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] split = source.split(">");
        int i = 0;
        while (i < split.length) {
            if (split[i].startsWith("v")) {
                split = split[i].split("(v)|(<)");
                res = split[1].trim();
                break;
            }
            i++;
        }

        return res;
    }

}
