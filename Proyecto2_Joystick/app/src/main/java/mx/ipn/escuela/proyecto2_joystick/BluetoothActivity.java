package mx.ipn.escuela.proyecto2_joystick;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    // --- 1. AÑADE AQUÍ LA DIRECCIÓN MAC DE TU CONTROL DE PS5 ---
    private static final String PS5_CONTROLLER_MAC = "PON_AQUI_LA_MAC_DE_TU_CONTROL";
    // ----------------------------------------------------------------

    private static final int REQUEST_PERMISSIONS = 1;

    private Button scanButton, viewFigureButton;
    private RecyclerView deviceRecyclerView;
    private TextView statusTextView;
    private ProgressBar progressBar;

    private BluetoothAdapter bluetoothAdapter;
    private DeviceAdapter deviceAdapter;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initializeViews();
        setupRecyclerView();
        setupListeners();

        if (hasPermissions()) {
            initializeBluetooth();
        } else {
            requestBluetoothPermissions();
        }
    }

    private void initializeViews() {
        scanButton = findViewById(R.id.scanButton);
        viewFigureButton = findViewById(R.id.viewFigureButton);
        deviceRecyclerView = findViewById(R.id.deviceRecyclerView);
        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(deviceList, this::handleDeviceSelection);
        deviceRecyclerView.setAdapter(deviceAdapter);
    }

    private void setupListeners() {
        scanButton.setOnClickListener(v -> startManualScan());
        viewFigureButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(BluetoothActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }

    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            updateStatus("Bluetooth no es soportado", false);
            scanButton.setEnabled(false);
            return;
        }

        registerReceiver(discoveryReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(bondStateReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(discoveryFinisher, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        attemptAutoConnect();
    }

    private void attemptAutoConnect() {
        if (PS5_CONTROLLER_MAC == null || PS5_CONTROLLER_MAC.equals("PON_AQUI_LA_MAC_DE_TU_CONTROL") || !BluetoothAdapter.checkBluetoothAddress(PS5_CONTROLLER_MAC)) {
            updateStatus("MAC no configurada. Busca un dispositivo manualmente.", false);
            populateWithPairedDevices();
            return;
        }

        updateStatus("Intentando conexión automática...", true);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(PS5_CONTROLLER_MAC);
        handleDeviceSelection(device);
    }

    private void handleDeviceSelection(BluetoothDevice device) {
        selectedDevice = device;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            updateStatus("¡Conectado a " + device.getName() + "!", false);
            viewFigureButton.setVisibility(View.VISIBLE);
            deviceAdapter.notifyDataSetChanged();
        } else {
            updateStatus("Vinculando con " + device.getName() + "...", true);
            viewFigureButton.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
            device.createBond();
        }
    }

    private void startManualScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) return;
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Por favor, active el Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();

        updateStatus("Buscando dispositivos...", true);
        viewFigureButton.setVisibility(View.GONE);
        deviceList.clear();
        populateWithPairedDevices();
        bluetoothAdapter.startDiscovery();
    }

    private void populateWithPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : pairedDevices) {
            if(!deviceList.contains(device)) {
                deviceList.add(device);
            }
        }
        deviceAdapter.notifyDataSetChanged();
    }

    private void updateStatus(String text, boolean isLoading) {
        statusTextView.setText(text);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !deviceList.contains(device)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
                    if (device.getName() != null && !device.getName().isEmpty()) {
                        deviceList.add(device);
                        deviceAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private final BroadcastReceiver discoveryFinisher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus("Búsqueda finalizada. Seleccione un dispositivo.", false);
        }
    };

    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device == null || selectedDevice == null || !device.getAddress().equals(selectedDevice.getAddress())) return;

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    updateStatus("¡Conexión exitosa! Listo para continuar.", false);
                    viewFigureButton.setVisibility(View.VISIBLE);
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    updateStatus("Vinculando con " + device.getName() + "...", true);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    updateStatus("Error de conexión. Intente de nuevo.", false);
                }
                deviceAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoveryReceiver);
        unregisterReceiver(bondStateReceiver);
        unregisterReceiver(discoveryFinisher);
        if (bluetoothAdapter != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
        }
    }

    private boolean hasPermissions() {
        return checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
               checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
               checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth();
            } else {
                 Toast.makeText(this, "Se requieren todos los permisos para usar Bluetooth", Toast.LENGTH_LONG).show();
                 finish();
            }
        }
    }

    interface OnDeviceSelectedListener {
        void onDeviceSelected(BluetoothDevice device);
    }

    class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
        private ArrayList<BluetoothDevice> devices;
        private OnDeviceSelectedListener listener;

        public DeviceAdapter(ArrayList<BluetoothDevice> devices, OnDeviceSelectedListener listener) {
            this.devices = devices;
            this.listener = listener;
        }

        @NonNull @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            holder.bind(devices.get(position), listener);
        }

        @Override public int getItemCount() { return devices.size(); }

        class DeviceViewHolder extends RecyclerView.ViewHolder {
            TextView deviceName, deviceStatus;
            ImageView connectionIndicator;

            public DeviceViewHolder(@NonNull View itemView) {
                super(itemView);
                deviceName = itemView.findViewById(R.id.deviceName);
                deviceStatus = itemView.findViewById(R.id.deviceStatus);
                connectionIndicator = itemView.findViewById(R.id.connectionIndicator);
            }

            public void bind(final BluetoothDevice device, final OnDeviceSelectedListener listener) {
                if (ActivityCompat.checkSelfPermission(itemView.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;
                deviceName.setText(device.getName());
                itemView.setOnClickListener(v -> listener.onDeviceSelected(device));

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    deviceStatus.setText("Conectado");
                    connectionIndicator.setImageResource(R.drawable.ic_indicator_connected);
                } else {
                    deviceStatus.setText("No Conectado");
                    connectionIndicator.setImageResource(R.drawable.ic_indicator_disconnected);
                }
            }
        }
    }
}