package com.espressif.provision;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import cloud.Cloud;
import espressif.Constants;
import espressif.WifiConfig;
import espressif.WifiScan;

public class Messenger {

    // Send Wi-Fi Scan command
    public static byte[] prepareWiFiScanMsg() {

        WifiScan.CmdScanStart configRequest = WifiScan.CmdScanStart.newBuilder()
                .setBlocking(true)
                .setPassive(false)
                .setGroupChannels(0)
                .setPeriodMs(120)
                .build();
        WifiScan.WiFiScanMsgType msgType = WifiScan.WiFiScanMsgType.TypeCmdScanStart;
        WifiScan.WiFiScanPayload payload = WifiScan.WiFiScanPayload.newBuilder()
                .setMsg(msgType)
                .setCmdScanStart(configRequest)
                .build();

        return payload.toByteArray();
    }

    public static byte[] prepareGetWiFiScanStatusMsg() {

        WifiScan.CmdScanStatus configRequest = WifiScan.CmdScanStatus.newBuilder()
                .build();
        WifiScan.WiFiScanMsgType msgType = WifiScan.WiFiScanMsgType.TypeCmdScanStatus;
        WifiScan.WiFiScanPayload payload = WifiScan.WiFiScanPayload.newBuilder()
                .setMsg(msgType)
                .setCmdScanStatus(configRequest)
                .build();
        return payload.toByteArray();
    }

    // Get Wi-Fi scan list
    public static byte[] prepareGetWiFiScanListMsg(int start, int count) {

        WifiScan.CmdScanResult configRequest = WifiScan.CmdScanResult.newBuilder()
                .setStartIndex(start)
                .setCount(count)
                .build();
        WifiScan.WiFiScanMsgType msgType = WifiScan.WiFiScanMsgType.TypeCmdScanResult;
        WifiScan.WiFiScanPayload payload = WifiScan.WiFiScanPayload.newBuilder()
                .setMsg(msgType)
                .setCmdScanResult(configRequest)
                .build();

        return payload.toByteArray();
    }

    // Send Wi-Fi Config
    public static byte[] prepareWiFiConfigMsg(String ssid, String passphrase) {

        WifiConfig.CmdSetConfig cmdSetConfig;

        if (passphrase != null) {

            cmdSetConfig = WifiConfig.CmdSetConfig
                    .newBuilder()
                    .setSsid(ByteString.copyFrom(ssid.getBytes()))
                    .setPassphrase(ByteString.copyFrom(passphrase.getBytes()))
                    .build();
        } else {
            cmdSetConfig = WifiConfig.CmdSetConfig
                    .newBuilder()
                    .setSsid(ByteString.copyFrom(ssid.getBytes()))
                    .build();
        }
        WifiConfig.WiFiConfigPayload wiFiConfigPayload = WifiConfig.WiFiConfigPayload
                .newBuilder()
                .setCmdSetConfig(cmdSetConfig)
                .setMsg(WifiConfig.WiFiConfigMsgType.TypeCmdSetConfig)
                .build();

        return wiFiConfigPayload.toByteArray();
    }

    // Apply Wi-Fi config
    public static byte[] prepareApplyWiFiConfigMsg() {

        WifiConfig.CmdApplyConfig cmdApplyConfig = WifiConfig.CmdApplyConfig
                .newBuilder()
                .build();
        WifiConfig.WiFiConfigPayload wiFiConfigPayload = WifiConfig.WiFiConfigPayload
                .newBuilder()
                .setCmdApplyConfig(cmdApplyConfig)
                .setMsg(WifiConfig.WiFiConfigMsgType.TypeCmdApplyConfig)
                .build();
        return wiFiConfigPayload.toByteArray();
    }

    // Get Wi-Fi Config status
    public static byte[] prepareGetWiFiConfigStatusMsg() {

        WifiConfig.CmdGetStatus cmdGetStatus = WifiConfig.CmdGetStatus
                .newBuilder()
                .build();
        WifiConfig.WiFiConfigPayload wiFiConfigPayload = WifiConfig.WiFiConfigPayload
                .newBuilder()
                .setCmdGetStatus(cmdGetStatus)
                .setMsg(WifiConfig.WiFiConfigMsgType.TypeCmdGetStatus)
                .build();
        return wiFiConfigPayload.toByteArray();
    }

    // Use device association
    public static byte[] prepareAssociateDeviceMsg(String userId, String secretKey) {

        Cloud.CmdGetSetDetails deviceSecretRequest = Cloud.CmdGetSetDetails.newBuilder()
                .setUserID(userId)
                .setSecretKey(secretKey)
                .build();
        Cloud.CloudConfigMsgType msgType = Cloud.CloudConfigMsgType.TypeCmdGetSetDetails;
        Cloud.CloudConfigPayload payload = Cloud.CloudConfigPayload.newBuilder()
                .setMsg(msgType)
                .setCmdGetSetDetails(deviceSecretRequest)
                .build();

        return payload.toByteArray();
    }

    public static Constants.Status processWiFiConfigResponse(byte[] responseData) {

        Constants.Status status = Constants.Status.InvalidSession;

        try {
            WifiConfig.WiFiConfigPayload wiFiConfigPayload = WifiConfig.WiFiConfigPayload.parseFrom(responseData);
            status = wiFiConfigPayload.getRespSetConfig().getStatus();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return status;
    }

//    public static Constants.Status processWiFiConfigResponse(byte[] responseData) {
//
//        Constants.Status status = Constants.Status.InvalidSession;
//
//        try {
//            WifiConfig.WiFiConfigPayload wiFiConfigPayload = WifiConfig.WiFiConfigPayload.parseFrom(responseData);
//            status = wiFiConfigPayload.getRespSetConfig().getStatus();
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//        return status;
//    }
}