package com.sap.smp.client.android.federation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.sap.smp.client.android.federation.Federation;
import com.sap.smp.client.supportability.ClientLogLevel;
import com.sap.smp.client.supportability.ClientLogger;
import com.sap.smp.client.supportability.Supportability;
import com.sybase.persistence.DataVaultException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class FederationClient {

    /* loaded from: classes.dex */
    public static class CheckDeviceForClientsReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ClientLogger clientLogger = Supportability.getInstance().getClientLogger(context, BuildConfig.APPLICATION_ID);
            boolean logDebugEnabled = ClientLogLevel.DEBUG.isEnabled(clientLogger.getLogLevel());
            String callerPackageName = intent.getStringExtra(".callerPackageName");
            String currentPackageName = context.getPackageName();
            Bundle bundle = getResultExtras(true);
            if (callerPackageName != null && !callerPackageName.equals(currentPackageName)) {
                Federation federation = Federation.getInstance();
                federation.init(context);
                try {
                    if (federation.isDataVaultExists()) {
                        ArrayList<String> packageNames = bundle.getStringArrayList(".applicationPackageNames");
                        if (packageNames == null) {
                            packageNames = new ArrayList<>();
                        }
                        packageNames.add(currentPackageName);
                        bundle.putStringArrayList(".applicationPackageNames", packageNames);
                        if (logDebugEnabled) {
                            clientLogger.logDebug("Client with package name " + currentPackageName + " is registered.");
                        }
                    }
                } catch (DataVaultException e) {
                    bundle.putSerializable(".errorObject", new FederationErrorObject(e));
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class GetCertificateReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ClientLogger clientLogger = Supportability.getInstance().getClientLogger(context, BuildConfig.APPLICATION_ID);
            boolean logDebugEnabled = ClientLogLevel.DEBUG.isEnabled(clientLogger.getLogLevel());
            String calledPackageName = intent.getStringExtra(".calledPackageName");
            if (calledPackageName != null && calledPackageName.equals(context.getPackageName())) {
                if (logDebugEnabled) {
                    clientLogger.logDebug("Getting certificate from client with package name: " + calledPackageName);
                }
                String dataVaultPin = intent.getStringExtra(".datavaultPin");
                if (dataVaultPin != null) {
                    Federation federation = Federation.getInstance();
                    federation.init(context);
                    Bundle bundle = getResultExtras(true);
                    try {
                        federation.isDataVaultPasswordCorrect(dataVaultPin);
                        Certificate certificate = federation.getCertificateFromDataVault(dataVaultPin);
                        if (certificate != null && certificate.keyStore != null && certificate.password != null) {
                            byte[] keyStoreByteArray = Federation.Util.serialize(certificate.keyStore, certificate.password.toCharArray());
                            bundle.putByteArray(".keyStoreByteArray", keyStoreByteArray);
                            bundle.putString(".keyStorePassword", certificate.password);
                            if (logDebugEnabled) {
                                clientLogger.logDebug("Certificate successfully aquired from client with package name: " + calledPackageName);
                            }
                        } else {
                            bundle.putSerializable(".errorObject", new FederationErrorObject(context.getString(R.string.error_could_not_retrieve_certificate)));
                        }
                    } catch (DataVaultException e) {
                        bundle.putSerializable(".errorObject", new FederationErrorObject(e));
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class UpdateCertificateReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ClientLogger clientLogger = Supportability.getInstance().getClientLogger(context, BuildConfig.APPLICATION_ID);
            boolean logDebugEnabled = ClientLogLevel.DEBUG.isEnabled(clientLogger.getLogLevel());
            byte[] keyStoreByteArray = intent.getByteArrayExtra(".keyStoreByteArray");
            String keyStorePassword = intent.getStringExtra(".keyStorePassword");
            String datavaultPass = intent.getStringExtra(".datavaultPin");
            String callerPackageName = intent.getStringExtra(".callerPackageName");
            ArrayList<String> registeredClients = intent.getStringArrayListExtra(".registeredClientsList");
            String currentPackageName = context.getPackageName();
            if (registeredClients != null) {
                Iterator<String> it = registeredClients.iterator();
                while (it.hasNext()) {
                    String registeredClient = it.next();
                    if (registeredClient != null && registeredClient.equals(currentPackageName) && keyStoreByteArray != null && keyStorePassword != null && callerPackageName != null && !callerPackageName.equals(currentPackageName)) {
                        Federation federation = Federation.getInstance();
                        federation.init(context);
                        try {
                            federation.isDataVaultPasswordCorrect(datavaultPass);
                            if (logDebugEnabled) {
                                clientLogger.logDebug("Updating certificate in private datavault of client with package name: " + currentPackageName);
                            }
                            KeyStore keyStore = Federation.Util.deserialize(keyStoreByteArray, keyStorePassword.toCharArray());
                            federation.storeCertificateInDataVault(datavaultPass, new Certificate(keyStore, keyStorePassword));
                            if (logDebugEnabled) {
                                clientLogger.logDebug("Certificate successfully updated in client with package name: " + currentPackageName);
                                return;
                            }
                            return;
                        } catch (DataVaultException e) {
                            Bundle bundle = getResultExtras(true);
                            bundle.putSerializable(".errorObject", e);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteCertificateReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ClientLogger clientLogger = Supportability.getInstance().getClientLogger(context, BuildConfig.APPLICATION_ID);
            boolean logDebugEnabled = ClientLogLevel.DEBUG.isEnabled(clientLogger.getLogLevel());
            String callerPackageName = intent.getStringExtra(".callerPackageName");
            String currentPackageName = context.getPackageName();
            if (callerPackageName != null && !callerPackageName.equals(currentPackageName)) {
                Federation federation = Federation.getInstance();
                federation.init(context);
                federation.deleteDataVault();
                if (logDebugEnabled) {
                    clientLogger.logDebug("Certificate deleted from client with package name; " + currentPackageName);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class CheckClientDataVaultPasswordReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ClientLogger clientLogger = Supportability.getInstance().getClientLogger(context, BuildConfig.APPLICATION_ID);
            ClientLogLevel.DEBUG.isEnabled(clientLogger.getLogLevel());
            String callerPackageName = intent.getStringExtra(".callerPackageName");
            String dataVaultPass = intent.getStringExtra(".datavaultPin");
            String currentPackageName = context.getPackageName();
            if (callerPackageName != null && !callerPackageName.equals(currentPackageName)) {
                Federation federation = Federation.getInstance();
                federation.init(context);
                Bundle bundle = getResultExtras(true);
                try {
                    federation.isDataVaultPasswordCorrect(dataVaultPass);
                } catch (DataVaultException e) {
                    bundle.putSerializable(".errorObject", new FederationErrorObject(e));
                }
            }
        }
    }
}
