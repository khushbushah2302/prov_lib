// Copyright 2018 Espressif Systems (Shanghai) PTE LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.espressif.provisioningap;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ContentLoadingProgressBar;

import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ProvisionLanding extends AppCompatActivity {

    private static final String TAG = ProvisionLanding.class.getSimpleName();

    private static final int WIFI_SETTINGS_ACTIVITY_REQUEST = 121;
    private static final String BASE_URL = "192.168.4.1:80";

    private TextView tvTitle, tvBack, tvCancel;
    private CardView btnConnect;
    private TextView txtConnectBtn;
    private ImageView arrowImage;
    private ContentLoadingProgressBar progressBar;

    private Button btnWiFiSettings;
    private TextView txtWiFiSettingBtn;
    private ImageView arrowImageWiFiSetting;
    private ContentLoadingProgressBar progressBarWiFiSetting;
    private ESPProvisionManager provisionLib;
    private int securityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provision_landing);
        securityType = getIntent().getIntExtra("security_type", 0);
        provisionLib = ESPProvisionManager.getInstance(getApplicationContext());
        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case WIFI_SETTINGS_ACTIVITY_REQUEST:
                // TODO
                break;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        switch (requestCode) {
//
//            case Provision.REQUEST_PERMISSIONS_CODE: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                }
//            }
//            break;
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Prov Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_CONNECTED:

                Log.e(TAG, "Device Connected Event Received");
                ArrayList<String> deviceCaps = provisionLib.getEspDevice().getDeviceCapabilities();

                btnConnect.setEnabled(true);
                btnConnect.setAlpha(1f);
                txtConnectBtn.setText(R.string.btn_connect);
                progressBar.setVisibility(View.GONE);
                arrowImage.setVisibility(View.VISIBLE);

                if (deviceCaps != null && !deviceCaps.contains("no_pop") && securityType == 1) {

                    goToPopActivity();

                } else if (deviceCaps != null && deviceCaps.contains("wifi_scan")) {

                    goToWifiScanListActivity();

                } else {

                    goToProvisionActivity();
                }
                break;

            case ESPConstants.EVENT_DEVICE_CONNECTION_FAILED:

                btnConnect.setEnabled(true);
                btnConnect.setAlpha(1f);
                txtConnectBtn.setText(R.string.btn_connect);
                progressBar.setVisibility(View.GONE);
                arrowImage.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.error_device_connect_failed, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    View.OnClickListener btnConnectClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            connectDevice();
        }
    };

    View.OnClickListener btnWiFiSettingsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_SETTINGS_ACTIVITY_REQUEST);
        }
    };

    private void connectDevice() {

        btnConnect.setEnabled(false);
        btnConnect.setAlpha(0.5f);
        txtConnectBtn.setText(R.string.btn_connecting);
        progressBar.setVisibility(View.VISIBLE);
        arrowImage.setVisibility(View.GONE);
        provisionLib.getEspDevice().connectWiFiDevice();
    }

    private View.OnClickListener cancelButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            finish();
        }
    };

    private void initViews() {

        tvTitle = findViewById(R.id.main_toolbar_title);
        tvBack = findViewById(R.id.btn_back);
        tvCancel = findViewById(R.id.btn_cancel);

        tvTitle.setText(R.string.title_activity_connect_device);
        tvBack.setVisibility(View.GONE);
        tvCancel.setVisibility(View.VISIBLE);

        tvCancel.setOnClickListener(cancelButtonClickListener);

        btnConnect = findViewById(R.id.btn_connect);
        txtConnectBtn = btnConnect.findViewById(R.id.text_btn);
        arrowImage = btnConnect.findViewById(R.id.iv_arrow);
        progressBar = btnConnect.findViewById(R.id.progress_indicator);

        btnWiFiSettings = findViewById(R.id.btn_wifi_settings);
        txtWiFiSettingBtn = btnWiFiSettings.findViewById(R.id.text_btn);
        arrowImageWiFiSetting = btnWiFiSettings.findViewById(R.id.iv_arrow);
        progressBarWiFiSetting = btnWiFiSettings.findViewById(R.id.progress_indicator);
        arrowImageWiFiSetting.setVisibility(View.GONE);
        progressBarWiFiSetting.setVisibility(View.GONE);

        txtConnectBtn.setText(R.string.btn_connect);
        txtWiFiSettingBtn.setText(R.string.btn_wifi_settings);
        btnConnect.setOnClickListener(btnConnectClickListener);
        btnWiFiSettings.setOnClickListener(btnWiFiSettingsClickListener);
    }

    private void goToPopActivity() {

        finish();
        Intent popIntent = new Intent(getApplicationContext(), ProofOfPossessionActivity.class);
        popIntent.putExtras(getIntent());
        startActivity(popIntent);
    }

    private void goToWifiScanListActivity() {

        finish();
        Intent wifiListIntent = new Intent(getApplicationContext(), WiFiScanActivity.class);
        wifiListIntent.putExtras(getIntent());
        wifiListIntent.putExtra(AppConstants.KEY_PROOF_OF_POSSESSION, "");
        startActivity(wifiListIntent);
    }

    private void goToProvisionActivity() {

        finish();
        Intent provisionIntent = new Intent(getApplicationContext(), ProvisionActivity.class);
        provisionIntent.putExtras(getIntent());
        provisionIntent.putExtra(AppConstants.KEY_PROOF_OF_POSSESSION, "");
        startActivity(provisionIntent);
    }
}
