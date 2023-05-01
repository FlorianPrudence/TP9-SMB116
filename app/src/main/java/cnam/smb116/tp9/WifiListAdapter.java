package cnam.smb116.tp9;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WifiListAdapter extends ArrayAdapter<ScanResult> {

    private final Context mContext;
    private final List<ScanResult> mWifiList;

    public WifiListAdapter(Context context, List<ScanResult> wifiList) {
        super(context, 0, wifiList);
        mContext = context;
        mWifiList = wifiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_point_wifi, parent, false);
        }

        ScanResult wifi = mWifiList.get(position);

        TextView ssidTextView = listItem.findViewById(R.id.ssidTextView);
        TextView frequencyTextView = listItem.findViewById(R.id.frequencyTextView);
        TextView levelTextView = listItem.findViewById(R.id.levelTextView);

        ssidTextView.setText(wifi.SSID);
        frequencyTextView.setText(wifi.frequency + " Mhz");
        levelTextView.setText(wifi.level + " dBm");

        return listItem;
    }
}

