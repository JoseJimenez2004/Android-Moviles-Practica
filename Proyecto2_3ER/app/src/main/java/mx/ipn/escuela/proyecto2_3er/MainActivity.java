package mx.ipn.escuela.proyecto2_3er;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 100;

    CheckBox chkContacts, chkCallLog, chkSMS, chkLocation;
    Button btnCapture, btnEmail;
    SwitchMaterial selectAllSwitch;
    TextView txtLocation;
    ProgressBar progressBar;

    FusedLocationProviderClient fusedLocationClient;
    double lastLat = 0.0;
    double lastLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Views
        chkContacts = findViewById(R.id.chkContacts);
        chkCallLog = findViewById(R.id.chkCallLog);
        chkSMS = findViewById(R.id.chkSMS);
        chkLocation = findViewById(R.id.chkLocation);
        btnCapture = findViewById(R.id.btnCapture);
        btnEmail = findViewById(R.id.btnEmail);
        selectAllSwitch = findViewById(R.id.selectAllSwitch);
        txtLocation = findViewById(R.id.txtLocation);
        progressBar = findViewById(R.id.progressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermissions();

        // Set Listeners
        selectAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chkContacts.setChecked(isChecked);
            chkCallLog.setChecked(isChecked);
            chkSMS.setChecked(isChecked);
            chkLocation.setChecked(isChecked);
        });

        btnCapture.setOnClickListener(v -> startForensicExtraction());
        btnEmail.setOnClickListener(v -> sendForensicByEmail());
    }

    private void startForensicExtraction() {
        if (!chkContacts.isChecked() && !chkCallLog.isChecked() && !chkSMS.isChecked() && !chkLocation.isChecked()) {
            Toast.makeText(this, "Selecciona al menos una opción", Toast.LENGTH_LONG).show();
            return;
        }

        setInProgress(true);

        new Thread(() -> {
            if (chkContacts.isChecked()) extractContacts();
            if (chkCallLog.isChecked()) extractCallLog();
            if (chkSMS.isChecked()) extractSMS();

            runOnUiThread(() -> {
                setInProgress(false);
                if (chkContacts.isChecked() || chkCallLog.isChecked() || chkSMS.isChecked()) {
                    showExtractionCompletedDialog();
                }
            });
        }).start();

        if (chkLocation.isChecked()) {
            getLocationGPS();
        }
    }

    private void showExtractionCompletedDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Extracción Completada")
                .setMessage("La evidencia ha sido guardada en la carpeta 'Forensic'.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(R.drawable.ic_capture)
                .show();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        }
    }

    private File getForensicDir() {
        File dir = new File(getExternalFilesDir(null), "Forensic");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void extractContacts() {
        File file = new File(getForensicDir(), "Contacts.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Nombre,Telefono\n");
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    writer.append(name).append(",").append(number).append("\n");
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractCallLog() {
        File file = new File(getForensicDir(), "CallLog.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Numero,Tipo,Fecha,Duracion(s)\n");
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int typeCode = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    String type = (typeCode == CallLog.Calls.INCOMING_TYPE) ? "Entrante" : (typeCode == CallLog.Calls.OUTGOING_TYPE) ? "Saliente" : (typeCode == CallLog.Calls.MISSED_TYPE) ? "Perdida" : "Otro";
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    writer.append(number).append(",").append(type).append(",").append(date).append(",").append(duration).append("\n");
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractSMS() {
        File file = new File(getForensicDir(), "SMS.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Direccion,Tipo,Fecha,Mensaje\n");
            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, "date DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                    String type = (typeCode == 1) ? "Entrante" : (typeCode == 2) ? "Saliente" : "Otro";
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date"))));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).replace("\n", " ").replace(",", " ");
                    writer.append(address).append(",").append(type).append(",").append(date).append(",").append(body).append("\n");
                }
                cursor.close();
            }
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Restricción de Android: No se puede acceder a los SMS.", Toast.LENGTH_LONG).show());
        }
    }

    private void getLocationGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                lastLat = location.getLatitude();
                lastLon = location.getLongitude();
                txtLocation.setText(String.format(Locale.getDefault(), "Latitud: %.6f\nLongitud: %.6f", lastLat, lastLon));
                saveLocationToFile(lastLat, lastLon);
            }
        });
    }

    private void saveLocationToFile(double lat, double lon) {
        File file = new File(getForensicDir(), "Location.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Ubicacion GPS del dispositivo\n");
            writer.append("Latitud: ").append(String.valueOf(lat)).append("\n");
            writer.append("Longitud: ").append(String.valueOf(lon)).append("\n");
            writer.append("Fecha: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendForensicByEmail() {
        setInProgress(true);
        new Thread(() -> {
            final File zipFile = zipForensicFolder();
            runOnUiThread(() -> {
                setInProgress(false);
                if (zipFile == null || !zipFile.exists()) {
                    Toast.makeText(MainActivity.this, "No hay evidencia para enviar o error al comprimir", Toast.LENGTH_LONG).show();
                    return;
                }
                Uri uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", zipFile);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/zip");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Evidencia forense - Proyecto");
                intent.putExtra(Intent.EXTRA_TEXT, "Se adjunta la evidencia forense extraída del dispositivo.");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Enviar evidencia por correo"));
            });
        }).start();
    }

    private File zipForensicFolder() {
        File forensicDir = getForensicDir();
        File zipFile = new File(getExternalFilesDir(null), "Forensic.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] files = forensicDir.listFiles();
            if (files == null) return null;
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
            return zipFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnCapture.setEnabled(false);
            btnEmail.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnCapture.setEnabled(true);
            btnEmail.setEnabled(true);
        }
    }
}