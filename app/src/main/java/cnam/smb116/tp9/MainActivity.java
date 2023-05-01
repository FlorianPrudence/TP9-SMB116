package cnam.smb116.tp9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private WifiManager wifiManager;
    private WifiListAdapter wifiListAdapter;
    IntentFilter filterScan;
    private BroadcastReceiver connectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        wifiListAdapter = new WifiListAdapter(this, new ArrayList<>());
        listView.setAdapter(wifiListAdapter);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        filterScan = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        List<ScanResult> scanResults = wifiManager.getScanResults();
                        if(scanResults.size() == 0)
                            Toast.makeText(MainActivity.this, "Aucun point d'accès trouvé, veuillez activer votre localisation ou vous déplacer près d'un point d'accès Wi-Fi", Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(MainActivity.this, scanResults.size() + " point(s) d'accès Wi-Fi trouvé(s)", Toast.LENGTH_SHORT).show();
                            wifiListAdapter.clear();
                            wifiListAdapter.addAll(scanResults);
                            wifiListAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        };

        // Vérifier si les permissions nécessaires sont accordées
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectionReceiver, filterScan);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionReceiver);
    }

    private void scanWifiNetworks() {
        if(wifiManager.isWifiEnabled()) {
            wifiManager.startScan();
        }
    }

    private void checkPermissions() {
        List<String> permissionsList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(android.Manifest.permission.CHANGE_WIFI_STATE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(android.Manifest.permission.ACCESS_WIFI_STATE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(android.Manifest.permission.ACCESS_NETWORK_STATE);

        if (!permissionsList.isEmpty())
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        else
            scanWifiNetworks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            // Si toutes les permissions sont accordées, on peut lancer le scan
            if (allPermissionsGranted) {
                Toast.makeText(this, "Permissions accordées", Toast.LENGTH_SHORT).show();
                scanWifiNetworks();
            } else {
                // Autrement on affiche un toast pour l'indiquer à l'utilisateur
                Toast.makeText(this, "Permissions non accordées", Toast.LENGTH_SHORT).show();
            }
        }
    }
}