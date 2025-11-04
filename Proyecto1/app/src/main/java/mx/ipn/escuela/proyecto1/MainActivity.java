package mx.ipn.escuela.proyecto1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.bluetooth.*;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.graphics.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice selectedDevice;
    BluetoothSocket socket;
    InputStream inputStream;

    Button connectButton;
    ListView deviceListView;
    TextView bluetoothStatus, dataStatusText, sensorDataText;
    View connectionIndicator, dataIndicator;

    ArrayAdapter<String> deviceListAdapter;
    Set<BluetoothDevice> pairedDevices;

    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Inicializar vistas
        connectButton = findViewById(R.id.connectButton);
        deviceListView = findViewById(R.id.deviceListView);
        bluetoothStatus = findViewById(R.id.bluetoothStatus);
        dataStatusText = findViewById(R.id.dataStatusText);
        sensorDataText = findViewById(R.id.sensorDataText);
        connectionIndicator = findViewById(R.id.connectionIndicator);
        dataIndicator = findViewById(R.id.dataIndicator);

        // Configurar colores iniciales de los indicadores
        connectionIndicator.setBackgroundColor(Color.parseColor("#E53E3E")); // Rojo - desconectado
        dataIndicator.setBackgroundColor(Color.parseColor("#E53E3E")); // Rojo - sin datos

        checkBluetoothStatus();

        connectButton.setOnClickListener(v -> showPairedDevices());
    }

    private void checkBluetoothStatus() {
        if (bluetoothAdapter == null) {
            bluetoothStatus.setText("Bluetooth no soportado");
            bluetoothStatus.setTextColor(Color.parseColor("#DC2626")); // Rojo
            connectionIndicator.setBackgroundColor(Color.parseColor("#DC2626"));
        } else if (!bluetoothAdapter.isEnabled()) {
            bluetoothStatus.setText("Bluetooth apagado");
            bluetoothStatus.setTextColor(Color.parseColor("#DC2626")); // Rojo
            connectionIndicator.setBackgroundColor(Color.parseColor("#DC2626"));
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            bluetoothStatus.setText("Bluetooth encendido");
            bluetoothStatus.setTextColor(Color.parseColor("#059669")); // Verde
            connectionIndicator.setBackgroundColor(Color.parseColor("#059669"));
        }
    }

    private void showPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            deviceListAdapter.add("No hay dispositivos emparejados");
        }

        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String info = ((TextView) view).getText().toString();
            if (!info.equals("No hay dispositivos emparejados")) {
                String address = info.substring(info.length() - 17);
                selectedDevice = bluetoothAdapter.getRemoteDevice(address);
                connectToDevice();
            }
        });
    }

    private void connectToDevice() {
        new Thread(() -> {
            try {
                socket = selectedDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                inputStream = socket.getInputStream();
                runOnUiThread(() -> {
                    bluetoothStatus.setText("Conectado a: " + selectedDevice.getName());
                    bluetoothStatus.setTextColor(Color.parseColor("#059669")); // Verde
                    connectionIndicator.setBackgroundColor(Color.parseColor("#059669"));
                    dataStatusText.setText("Recibiendo datos...");
                    dataStatusText.setTextColor(Color.parseColor("#1F2937"));
                });
                isConnected = true;
                listenForData();
            } catch (IOException e) {
                runOnUiThread(() -> {
                    bluetoothStatus.setText("Error al conectar");
                    bluetoothStatus.setTextColor(Color.parseColor("#DC2626")); // Rojo
                    connectionIndicator.setBackgroundColor(Color.parseColor("#DC2626"));
                    dataStatusText.setText("Error de conexión");
                    dataStatusText.setTextColor(Color.parseColor("#DC2626"));

                    // Mostrar mensaje de error
                    Toast.makeText(MainActivity.this,
                            "No se pudo conectar al dispositivo",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void listenForData() {
        byte[] buffer = new byte[256];
        int bytes;

        while (isConnected) {
            try {
                bytes = inputStream.read(buffer);
                String message = new String(buffer, 0, bytes).trim();
                runOnUiThread(() -> {
                    sensorDataText.setText(message);
                    dataIndicator.setBackgroundColor(Color.parseColor("#059669")); // Verde
                    dataStatusText.setText("Datos recibidos");
                    dataStatusText.setTextColor(Color.parseColor("#059669"));

                    // Opcional: Cambiar color del texto según el valor del sensor
                    updateSensorDisplay(message);
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    dataIndicator.setBackgroundColor(Color.parseColor("#DC2626")); // Rojo
                    dataStatusText.setText("Sin datos...");
                    dataStatusText.setTextColor(Color.parseColor("#DC2626"));
                });
                break;
            }
        }
    }

    private void updateSensorDisplay(String sensorValue) {
        try {
            double value = Double.parseDouble(sensorValue);
            // Puedes personalizar colores según el valor del sensor
            if (value > 100) {
                sensorDataText.setTextColor(Color.parseColor("#DC2626")); // Rojo para valores altos
            } else if (value > 50) {
                sensorDataText.setTextColor(Color.parseColor("#D97706")); // Naranja para valores medios
            } else {
                sensorDataText.setTextColor(Color.parseColor("#059669")); // Verde para valores bajos
            }
        } catch (NumberFormatException e) {
            // Si no es un número, mantener color por defecto
            sensorDataText.setTextColor(Color.parseColor("#1F2937")); // Gris oscuro
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConnected = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            checkBluetoothStatus();
        }
    }
}