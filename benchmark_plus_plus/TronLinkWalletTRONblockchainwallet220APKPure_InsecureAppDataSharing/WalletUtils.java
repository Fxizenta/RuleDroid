package org.tron.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tron.tron_base.frame.utils.AppContextUtil;
import com.tron.tron_base.frame.utils.RxBus;
import com.tron.wallet.TronApplicaion;
import com.tron.wallet.config.Event;
import com.tron.wallet.customview.qr.utils.BarcodeEncoder;
import com.tron.wallet.customview.qr.utils.CodeUtils;
import com.tron.wallet.net.JsonFormat;
import com.tron.wallet.utils.Sha256Hash;
import com.tronlink.wallet.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.CharUtils;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.SymmEncoder;
import org.tron.common.utils.ByteArray;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;
import org.tron.walletserver.DuplicateNameException;
import org.tron.walletserver.I_TYPE;
import org.tron.walletserver.InvalidNameException;
import org.tron.walletserver.InvalidPasswordException;
import org.tron.walletserver.StringTronUtil;
import org.tron.walletserver.Wallet;

/* loaded from: classes21.dex */
public class WalletUtils {
    private static GrpcAPI.AccountResourceMessage.Builder accountResMessage;
    private static String address;
    private static Map<String, Long> assets;
    private static Map<String, Long> assetsV2;
    private static long balance;
    private static long bandwidth;
    private static long createTime;
    private static long delegated_frozen_bandwidth;
    private static long delegated_frozen_energy;
    private static SharedPreferences.Editor editor;
    private static long energy;
    private static long energy_time;
    private static Protocol.Account.Frozen.Builder forzen;
    private static long freeze_bandwidth_other;
    private static long freeze_energy_other;
    private static List<Protocol.Account.Frozen> frozen;
    private static List<Protocol.Account.Frozen> frozenList;
    private static Map<Long, Long> frozenMap;
    private static Gson gson = new Gson();
    private static long latestOperationTime;
    private static String name;
    private static Protocol.Account.AccountResource.Builder resBuild;
    private static SharedPreferences sharedPreferences;
    private static List<Protocol.Vote> votes;
    private static List<Protocol.Vote> votesList;
    private static Map<String, Long> votesMap;

    public static Bitmap strToQR(String str, int width, int height, Bitmap logo) {
        return CodeUtils.createImage(str, width, height, logo);
    }

    public static Bitmap strToQR2(String str, int width, int height) {
        if (str == null || str.equals("")) {
            return null;
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, width, height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getPravateKey(String privateKeyEncrypted, String password) {
        if (StringTronUtil.isEmpty(privateKeyEncrypted)) {
            return null;
        }
        byte[] priKeyAscEnced = privateKeyEncrypted.getBytes();
        byte[] priKeyHexEnced = Hex.decode(priKeyAscEnced);
        if (StringTronUtil.isEmpty(password)) {
            return null;
        }
        byte[] aesKey = StringTronUtil.getEncKey(password);
        byte[] priKeyHexPlain = SymmEncoder.AES128EcbDec(priKeyHexEnced, aesKey);
        if (priKeyHexPlain == null || priKeyHexPlain.length == 0) {
            return null;
        }
        String privateKey = Hex.toHexString(priKeyHexPlain);
        return privateKey;
    }

    private static String getprivateKeyEncrypted(String privateKey, String walletName, String password) {
        byte[] aseKey = StringTronUtil.getEncKey(password);
        byte[] privKeyPlain = Hex.decode(privateKey);
        byte[] privKeyEnced = SymmEncoder.AES128EcbEnc(privKeyPlain, aseKey);
        String privKeyStr = ByteArray.toHexString(privKeyEnced);
        return privKeyStr;
    }

    public static Wallet getWallet(String walletName, String password) throws CipherException, IOException {
        if (existWallet(walletName)) {
            Context context = AppContextUtil.getContext();
            SharedPreferences walletPref = context.getSharedPreferences(walletName, 0);
            String privateKey = null;
            String privateKeyEncrypted = null;
            String keystoreEncrypted = walletPref.getString(context.getString(R.string.wallet_keystore_key), "");
            if (!StringTronUtil.isEmpty(password)) {
                String privateKeyEncrypted2 = TronApplicaion.WALlET_AES.get(walletName);
                privateKeyEncrypted = walletPref.getString(context.getString(R.string.priv_key), "");
                if (!StringTronUtil.isEmpty(privateKeyEncrypted)) {
                    privateKey = getPravateKey(privateKeyEncrypted, password);
                } else if (!StringTronUtil.isEmpty(privateKeyEncrypted2)) {
                    privateKey = getPravateKey(privateKeyEncrypted2, password);
                } else {
                    privateKey = KeyStoreUtils.getPrivateWithKeyStore(keystoreEncrypted, password);
                    TronApplicaion.WALlET_AES.put(walletName, getprivateKeyEncrypted(privateKey, walletName, password));
                }
            }
            Wallet wallet = new Wallet(I_TYPE.PRIVATE, privateKey);
            if (!StringTronUtil.isEmpty(password)) {
                if (wallet.getECKey() == null || wallet.getECKey().getPrivKeyBytes() == null) {
                    throw new CipherException("error password");
                }
                if (wallet != null && !SpAPI.THIS.getWalletAddress(walletName).equals(wallet.getAddress())) {
                    throw new CipherException("error password");
                }
            }
            wallet.setKeyStore(keystoreEncrypted);
            wallet.setWatchOnly(walletPref.getBoolean(context.getString(R.string.is_watch_only_setup_key), false));
            wallet.setColdWallet(walletPref.getBoolean(context.getString(R.string.is_cold_wallet_key), false));
            wallet.setWalletName(walletPref.getString(context.getString(R.string.wallet_name_key), walletName));
            wallet.setEncryptedPassword(walletPref.getString(context.getString(R.string.pwd_key), ""));
            wallet.setAddress(walletPref.getString(context.getString(R.string.wallet_address_key), ""));
            wallet.setBackUp(walletPref.getBoolean(context.getString(R.string.backup_key), false));
            wallet.setIconRes(walletPref.getString(context.getString(R.string.wallet_icon_key), ""));
            wallet.setCreateType(walletPref.getInt(context.getString(R.string.wallet_createtype_key), 0));
            wallet.setColor(walletPref.getInt(context.getString(R.string.wallet_color_key), -1));
            wallet.setMnemonicLength(walletPref.getInt(context.getString(R.string.mnemonic_length), 0));
            wallet.setEncryptedPrivateKey(null);
            String publicKeyStr = walletPref.getString(context.getString(R.string.pub_key), "");
            if (!publicKeyStr.isEmpty()) {
                byte[] publicKey = Hex.decode(publicKeyStr.getBytes());
                wallet.setPublicKey(publicKey);
            }
            long createTime2 = walletPref.getLong(context.getString(R.string.wallet_createtime_key), 0L);
            if (createTime2 == 0) {
                createTime2 = System.currentTimeMillis();
                walletPref.edit().putLong(context.getString(R.string.wallet_createtime_key), wallet.getCreateTime()).commit();
            }
            wallet.setCreateTime(createTime2);
            if (!StringTronUtil.isEmpty(privateKeyEncrypted, privateKey)) {
                walletPref.edit().putString(context.getString(R.string.wallet_keystore_key), KeyStoreUtils.getKeyStoreWithPrivate(password, wallet)).commit();
                walletPref.edit().putString(context.getString(R.string.priv_key), "").commit();
                return wallet;
            }
            return wallet;
        }
        if (getWalletNames().size() != 0) {
            getWallet(getWalletNames().iterator().next());
        }
        return null;
    }

    public static Wallet getWallet(String walletName) {
        try {
            Wallet wallet = getWallet(walletName, null);
            return wallet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String mnemonic(String walletName, String password) throws CipherException, IOException {
        if (StringTronUtil.isEmpty(walletName, password)) {
            return null;
        }
        Context context = AppContextUtil.getContext();
        SharedPreferences walletPref = context.getSharedPreferences(walletName, 0);
        String mnemonicEncrypted = walletPref.getString(context.getString(R.string.mnemonic_key), "");
        String mnemonicEncrypted2 = walletPref.getString(context.getString(R.string.wallet_newmnemonic_key), "");
        if (StringTronUtil.isEmpty(mnemonicEncrypted)) {
            if (StringTronUtil.isEmpty(mnemonicEncrypted2)) {
                return null;
            }
            return KeyStoreUtils.getMnemonicWithKeyStore(mnemonicEncrypted2, password);
        }
        byte[] mnemonicAscEnced = mnemonicEncrypted.getBytes();
        byte[] mnemonicHexEnced = Hex.decode(mnemonicAscEnced);
        byte[] aesKey = StringTronUtil.getEncKey(password);
        byte[] mnemonicPlain = SymmEncoder.AESEcbDec(mnemonicHexEnced, aesKey);
        if (mnemonicPlain == null || mnemonicPlain.length == 0) {
            return null;
        }
        String mnemonic = new String(mnemonicPlain);
        Wallet wallet = new Wallet(I_TYPE.MNEMONIC, mnemonic);
        if (!StringTronUtil.isEmpty(password) && wallet.getECKey() != null && !SpAPI.THIS.getWalletAddress(walletName).equals(wallet.getAddress())) {
            throw new CipherException("error password");
        }
        walletPref.edit().putString(context.getString(R.string.wallet_newmnemonic_key), KeyStoreUtils.getKeyStoreWithMnemonic(password, mnemonic, getWallet(walletName).getAddress())).commit();
        walletPref.edit().putString(context.getString(R.string.mnemonic_key), "").commit();
        return mnemonic;
    }

    public static boolean hasMnemonic(String walletName) {
        if (StringTronUtil.isEmpty(walletName)) {
            return false;
        }
        Context context = AppContextUtil.getContext();
        SharedPreferences walletPref = context.getSharedPreferences(walletName, 0);
        String mnemonicEncrypted = walletPref.getString(context.getString(R.string.mnemonic_key), "");
        String mnemonicEncrypted2 = walletPref.getString(context.getString(R.string.wallet_newmnemonic_key), "");
        return (StringTronUtil.isEmpty(mnemonicEncrypted) && StringTronUtil.isEmpty(mnemonicEncrypted2)) ? false : true;
    }

    public static Wallet getSelectedWallet() {
        return getWallet(SpAPI.THIS.getSelectedWallet());
    }

    public static boolean existWallet(String walletName) {
        return SpAPI.THIS.existWallet(walletName);
    }

    public static Set<String> getWalletNames() {
        return SpAPI.THIS.getAllWallets();
    }

    public static void saveWallet(Wallet wallet, String password) throws InvalidPasswordException, InvalidNameException, DuplicateNameException, CipherException {
        if (!wallet.isWatchOnly() && (wallet.getECKey() == null || wallet.getECKey().getPrivKey() == null)) {
            throw new NullPointerException("Private Key is null");
        }
        if (!wallet.isWatchOnly() && !StringTronUtil.isOkPassword(password)) {
            throw new InvalidPasswordException("");
        }
        if (!StringTronUtil.validataLegalString2(wallet.getWalletName())) {
            throw new InvalidNameException("");
        }
        Context context = AppContextUtil.getContext();
        SpAPI.THIS.setWallet(wallet.getWalletName());
        SharedPreferences walletPreferences = context.getSharedPreferences(wallet.getWalletName(), 0);
        SharedPreferences.Editor walletEditor = walletPreferences.edit();
        walletEditor.putBoolean(context.getString(R.string.is_watch_only_setup_key), wallet.isWatchOnly());
        walletEditor.putBoolean(context.getString(R.string.is_cold_wallet_key), wallet.isColdWallet() && !wallet.isWatchOnly());
        walletEditor.putString(context.getString(R.string.wallet_name_key), wallet.getWalletName());
        walletEditor.putString(context.getString(R.string.wallet_address_key), wallet.getAddress());
        walletEditor.putString(context.getString(R.string.wallet_icon_key), wallet.getIconRes());
        walletEditor.putInt(context.getString(R.string.wallet_createtype_key), wallet.getCreateType());
        walletEditor.putLong(context.getString(R.string.wallet_createtime_key), wallet.getCreateTime());
        walletEditor.putInt(context.getString(R.string.wallet_color_key), wallet.getColor());
        walletEditor.putInt(context.getString(R.string.mnemonic_length), wallet.getMnemonicLength());
        if (!wallet.isWatchOnly()) {
            byte[] pwd = StringTronUtil.getPasswordHash(password);
            String pwdAsc = ByteArray.toHexString(pwd);
            byte[] pubKeyBytes = wallet.getECKey().getPubKey();
            String pubKeyStr = ByteArray.toHexString(pubKeyBytes);
            walletEditor.putString(context.getString(R.string.pwd_key), pwdAsc);
            walletEditor.putString(context.getString(R.string.pub_key), pubKeyStr);
            walletEditor.putBoolean(context.getString(R.string.backup_key), wallet.isBackUp());
            String keyStore = KeyStoreUtils.getKeyStoreWithPrivate(password, wallet);
            walletEditor.putString(context.getString(R.string.wallet_keystore_key), keyStore);
            if (!StringTronUtil.isEmpty(wallet.getMnemonic())) {
                String mnemonic = KeyStoreUtils.getKeyStoreWithMnemonic(password, wallet.getMnemonic(), wallet.getAddress());
                walletEditor.putString(context.getString(R.string.wallet_newmnemonic_key), mnemonic);
            }
        }
        walletEditor.commit();
    }

    public static void changePassword(Wallet wallet, String password, String oldPassword) throws InvalidPasswordException, InvalidNameException, CipherException, IOException {
        if (!wallet.isWatchOnly() && (wallet.getECKey() == null || wallet.getECKey().getPrivKey() == null)) {
            throw new NullPointerException("Private Key is null");
        }
        if (!wallet.isWatchOnly() && !StringTronUtil.isOkPassword(password)) {
            throw new InvalidPasswordException("");
        }
        if (!StringTronUtil.validataLegalString2(wallet.getWalletName())) {
            throw new InvalidNameException("");
        }
        Context context = AppContextUtil.getContext();
        SharedPreferences walletPreferences = context.getSharedPreferences(wallet.getWalletName(), 0);
        SharedPreferences.Editor walletEditor = walletPreferences.edit();
        walletEditor.putBoolean(context.getString(R.string.is_watch_only_setup_key), wallet.isWatchOnly());
        walletEditor.putBoolean(context.getString(R.string.is_cold_wallet_key), wallet.isColdWallet() && !wallet.isWatchOnly());
        walletEditor.putString(context.getString(R.string.wallet_name_key), wallet.getWalletName());
        walletEditor.putString(context.getString(R.string.wallet_address_key), wallet.getAddress());
        walletEditor.putString(context.getString(R.string.wallet_icon_key), wallet.getIconRes());
        walletEditor.putInt(context.getString(R.string.wallet_createtype_key), wallet.getCreateType());
        walletEditor.putLong(context.getString(R.string.wallet_createtime_key), wallet.getCreateTime());
        if (!wallet.isWatchOnly()) {
            byte[] pubKeyBytes = wallet.getECKey().getPubKey();
            String pubKeyStr = ByteArray.toHexString(pubKeyBytes);
            String oldMnemonic = null;
            if (hasMnemonic(wallet.getWalletName())) {
                oldMnemonic = mnemonic(wallet.getWalletName(), oldPassword);
            }
            walletEditor.putString(context.getString(R.string.pub_key), pubKeyStr);
            String keyStore = KeyStoreUtils.getKeyStoreWithPrivate(password, wallet);
            walletEditor.putString(context.getString(R.string.wallet_keystore_key), keyStore);
            if (!StringTronUtil.isEmpty(oldMnemonic)) {
                String mnemonic = null;
                try {
                    mnemonic = KeyStoreUtils.getKeyStoreWithMnemonic(password, oldMnemonic, wallet.getAddress());
                } catch (CipherException e) {
                    e.printStackTrace();
                }
                walletEditor.putString(context.getString(R.string.wallet_newmnemonic_key), mnemonic);
                walletEditor.putString(context.getString(R.string.mnemonic_key), "");
            }
            TronApplicaion.WALlET_AES.put(wallet.getWalletName(), "");
        }
        walletEditor.commit();
    }

    public static void changeWalletName(String oldName, String newName) throws DuplicateNameException {
        if (existWallet(oldName)) {
            Context context = AppContextUtil.getContext();
            SharedPreferences walletPref = context.getSharedPreferences(oldName, 0);
            SharedPreferences walletPreferences = context.getSharedPreferences(newName, 0);
            SharedPreferences.Editor walletEditor = walletPreferences.edit();
            boolean isWatchOnly = walletPref.getBoolean(context.getString(R.string.is_watch_only_setup_key), false);
            walletEditor.putBoolean(context.getString(R.string.is_watch_only_setup_key), isWatchOnly);
            walletEditor.putBoolean(context.getString(R.string.is_cold_wallet_key), walletPref.getBoolean(context.getString(R.string.is_cold_wallet_key), false));
            walletEditor.putString(context.getString(R.string.wallet_name_key), newName);
            walletEditor.putString(context.getString(R.string.wallet_address_key), walletPref.getString(context.getString(R.string.wallet_address_key), ""));
            walletEditor.putString(context.getString(R.string.wallet_icon_key), walletPref.getString(context.getString(R.string.wallet_icon_key), ""));
            walletEditor.putInt(context.getString(R.string.wallet_createtype_key), walletPref.getInt(context.getString(R.string.wallet_createtype_key), 0));
            walletEditor.putLong(context.getString(R.string.wallet_createtime_key), walletPref.getLong(context.getString(R.string.wallet_createtime_key), 0L));
            walletEditor.putStringSet(context.getString(R.string.set_trc10_key), walletPref.getStringSet(context.getString(R.string.set_trc10_key), new HashSet()));
            walletEditor.putStringSet(context.getString(R.string.set_trc20_key), walletPref.getStringSet(context.getString(R.string.set_trc20_key), new HashSet()));
            walletEditor.putBoolean(context.getString(R.string.set_alltrc10_key), walletPref.getBoolean(context.getString(R.string.set_alltrc10_key), true));
            walletEditor.putBoolean(context.getString(R.string.set_alltrc20_key), walletPref.getBoolean(context.getString(R.string.set_alltrc20_key), true));
            walletEditor.putBoolean(context.getString(R.string.set_hasaccount_key), walletPref.getBoolean(context.getString(R.string.set_hasaccount_key), false));
            if (!isWatchOnly) {
                walletEditor.putString(context.getString(R.string.pwd_key), walletPref.getString(context.getString(R.string.pwd_key), ""));
                walletEditor.putString(context.getString(R.string.pub_key), walletPref.getString(context.getString(R.string.pub_key), ""));
                walletEditor.putString(context.getString(R.string.priv_key), walletPref.getString(context.getString(R.string.priv_key), ""));
                walletEditor.putString(context.getString(R.string.mnemonic_key), walletPref.getString(context.getString(R.string.mnemonic_key), ""));
                walletEditor.putBoolean(context.getString(R.string.backup_key), walletPref.getBoolean(context.getString(R.string.backup_key), false));
                walletEditor.putString(context.getString(R.string.wallet_keystore_key), walletPref.getString(context.getString(R.string.wallet_keystore_key), ""));
                walletEditor.putString(context.getString(R.string.wallet_newmnemonic_key), walletPref.getString(context.getString(R.string.wallet_newmnemonic_key), ""));
            }
            walletEditor.commit();
            SpAPI.THIS.setWallet(newName, oldName);
        }
    }

    public static void saveWatchOnly(Wallet wallet) throws InvalidNameException, DuplicateNameException, CipherException {
        wallet.setWatchOnly(true);
        try {
            saveWallet(wallet, null);
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

    public static void saveAccount(Context context, String walletName, Protocol.Account account) {
        if (context != null && account != null && existWallet(walletName)) {
            sharedPreferences = context.getSharedPreferences(walletName, 0);
            editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.name_key), account.getAccountName().toStringUtf8());
            editor.putString(context.getString(R.string.address_key), StringTronUtil.encode58Check(account.getAddress().toByteArray()));
            editor.putLong(context.getString(R.string.balance_key), account.getBalance());
            editor.putLong(context.getString(R.string.energy_key), account.getAccountResource().getFrozenBalanceForEnergy().getFrozenBalance());
            editor.putLong(context.getString(R.string.energytime_key), account.getAccountResource().getFrozenBalanceForEnergy().getExpireTime());
            editor.putString(context.getString(R.string.assets_key), gson.toJson(account.getAssetMap()));
            editor.putString(context.getString(R.string.assets_v2_key), gson.toJson(account.getAssetV2Map()));
            votesList = account.getVotesList();
            votesMap = new HashMap();
            for (Protocol.Vote vote : votesList) {
                String voteAddress = StringTronUtil.encode58Check(vote.getVoteAddress().toByteArray());
                if (!voteAddress.equals("")) {
                    votesMap.put(voteAddress, Long.valueOf(vote.getVoteCount()));
                }
            }
            editor.putString(context.getString(R.string.votes_key), new Gson().toJson(votesMap));
            frozenList = account.getFrozenList();
            frozenMap = new HashMap();
            for (Protocol.Account.Frozen frozen2 : frozenList) {
                long balance2 = frozen2.getFrozenBalance();
                if (frozenMap.containsKey(Long.valueOf(frozen2.getExpireTime()))) {
                    balance2 += frozenMap.get(Long.valueOf(frozen2.getExpireTime())).longValue();
                }
                frozenMap.put(Long.valueOf(frozen2.getExpireTime()), Long.valueOf(balance2));
            }
            editor.putString(context.getString(R.string.frozen_key), new Gson().toJson(frozenMap));
            editor.putLong(context.getString(R.string.bandwidth_key), account.getNetUsage());
            editor.putLong(context.getString(R.string.delegated_frozen_balance_for_bandwidth_key), account.getDelegatedFrozenBalanceForBandwidth());
            editor.putLong(context.getString(R.string.delegated_frozen_balance_for_energy_key), account.getAccountResource().getDelegatedFrozenBalanceForEnergy());
            editor.putLong(context.getString(R.string.create_time_key), account.getCreateTime());
            editor.putLong(context.getString(R.string.latest_operation_time_key), account.getLatestOprationTime());
            editor.putLong(context.getString(R.string.freeze_bandwidth_other_key), account.getAcquiredDelegatedFrozenBalanceForBandwidth());
            editor.putLong(context.getString(R.string.freeze_energy_other_key), account.getAccountResource().getAcquiredDelegatedFrozenBalanceForEnergy());
            editor.apply();
        }
    }

    public static void saveAccountRes(Context context, String walletName, GrpcAPI.AccountResourceMessage accountRes) {
        if (context != null && accountRes != null && existWallet(walletName)) {
            sharedPreferences = context.getSharedPreferences(walletName, 0);
            SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor2.putLong(context.getString(R.string.net_limit_key), accountRes.getNetLimit());
            editor2.putLong(context.getString(R.string.net_used_key), accountRes.getNetUsed());
            editor2.putLong(context.getString(R.string.net_free_limit_key), accountRes.getFreeNetLimit());
            editor2.putLong(context.getString(R.string.net_free_used_key), accountRes.getFreeNetUsed());
            editor2.putLong(context.getString(R.string.energy_limit_key), accountRes.getEnergyLimit());
            editor2.putLong(context.getString(R.string.energy_used_key), accountRes.getEnergyUsed());
            editor2.putLong(context.getString(R.string.total_energy_limit_key), accountRes.getTotalEnergyLimit());
            editor2.putLong(context.getString(R.string.total_energy_weight_key), accountRes.getTotalEnergyWeight());
            editor2.putLong(context.getString(R.string.total_net_limit_key), accountRes.getTotalNetLimit());
            editor2.putLong(context.getString(R.string.total_net_weight_key), accountRes.getTotalNetWeight());
            editor2.apply();
        }
    }

    public static GrpcAPI.AccountResourceMessage getAccountRes(Context context, String walletName) {
        if (context == null || !existWallet(walletName)) {
            return GrpcAPI.AccountResourceMessage.getDefaultInstance();
        }
        sharedPreferences = context.getSharedPreferences(walletName, 0);
        accountResMessage = GrpcAPI.AccountResourceMessage.newBuilder();
        accountResMessage.setNetLimit(sharedPreferences.getLong(context.getString(R.string.net_limit_key), 0L));
        accountResMessage.setNetUsed(sharedPreferences.getLong(context.getString(R.string.net_used_key), 0L));
        accountResMessage.setFreeNetLimit(sharedPreferences.getLong(context.getString(R.string.net_free_limit_key), 0L));
        accountResMessage.setFreeNetUsed(sharedPreferences.getLong(context.getString(R.string.net_free_used_key), 0L));
        accountResMessage.setEnergyLimit(sharedPreferences.getLong(context.getString(R.string.energy_limit_key), 0L));
        accountResMessage.setEnergyUsed(sharedPreferences.getLong(context.getString(R.string.energy_used_key), 0L));
        accountResMessage.setTotalEnergyLimit(sharedPreferences.getLong(context.getString(R.string.total_energy_limit_key), 0L));
        accountResMessage.setTotalEnergyWeight(sharedPreferences.getLong(context.getString(R.string.total_energy_weight_key), 0L));
        accountResMessage.setTotalNetLimit(sharedPreferences.getLong(context.getString(R.string.total_net_limit_key), 0L));
        accountResMessage.setTotalNetWeight(sharedPreferences.getLong(context.getString(R.string.total_net_weight_key), 0L));
        return accountResMessage.build();
    }

    public static Protocol.Account getAccount(Context context, String walletName) {
        if (context != null && existWallet(walletName)) {
            Protocol.Account.Builder builder = Protocol.Account.newBuilder();
            sharedPreferences = context.getSharedPreferences(walletName, 0);
            name = sharedPreferences.getString(context.getString(R.string.name_key), "");
            address = sharedPreferences.getString(context.getString(R.string.address_key), "");
            balance = sharedPreferences.getLong(context.getString(R.string.balance_key), 0L);
            assets = (Map) gson.fromJson(sharedPreferences.getString(context.getString(R.string.assets_key), ""), new TypeToken<Map<String, Long>>() { // from class: org.tron.net.WalletUtils.1
            }.getType());
            assetsV2 = (Map) gson.fromJson(sharedPreferences.getString(context.getString(R.string.assets_v2_key), ""), new TypeToken<Map<String, Long>>() { // from class: org.tron.net.WalletUtils.2
            }.getType());
            votes = new ArrayList();
            votesMap = (Map) gson.fromJson(sharedPreferences.getString(context.getString(R.string.votes_key), ""), new TypeToken<Map<String, Long>>() { // from class: org.tron.net.WalletUtils.3
            }.getType());
            if (votesMap != null) {
                for (Map.Entry<String, Long> entry : votesMap.entrySet()) {
                    byte[] voteAddress = StringTronUtil.decodeFromBase58Check(entry.getKey());
                    if (voteAddress != null) {
                        Protocol.Vote.Builder voteBuilder = Protocol.Vote.newBuilder();
                        voteBuilder.setVoteAddress(ByteString.copyFrom(voteAddress));
                        voteBuilder.setVoteCount(entry.getValue().longValue());
                        votes.add(voteBuilder.build());
                    }
                }
            }
            frozen = new ArrayList();
            frozenMap = (Map) gson.fromJson(sharedPreferences.getString(context.getString(R.string.frozen_key), ""), new TypeToken<Map<Long, Long>>() { // from class: org.tron.net.WalletUtils.4
            }.getType());
            if (frozenMap != null) {
                for (Map.Entry<Long, Long> entry2 : frozenMap.entrySet()) {
                    Protocol.Account.Frozen.Builder frozenBuilder = Protocol.Account.Frozen.newBuilder();
                    frozenBuilder.setExpireTime(entry2.getKey().longValue());
                    frozenBuilder.setFrozenBalance(entry2.getValue().longValue());
                    frozen.add(frozenBuilder.build());
                }
            }
            bandwidth = sharedPreferences.getLong(context.getString(R.string.bandwidth_key), 0L);
            createTime = sharedPreferences.getLong(context.getString(R.string.create_time_key), 0L);
            latestOperationTime = sharedPreferences.getLong(context.getString(R.string.latest_operation_time_key), 0L);
            energy = sharedPreferences.getLong(context.getString(R.string.energy_key), 0L);
            energy_time = sharedPreferences.getLong(context.getString(R.string.energytime_key), 0L);
            delegated_frozen_bandwidth = sharedPreferences.getLong(context.getString(R.string.delegated_frozen_balance_for_bandwidth_key), 0L);
            delegated_frozen_energy = sharedPreferences.getLong(context.getString(R.string.delegated_frozen_balance_for_energy_key), 0L);
            freeze_bandwidth_other = sharedPreferences.getLong(context.getString(R.string.freeze_bandwidth_other_key), 0L);
            freeze_energy_other = sharedPreferences.getLong(context.getString(R.string.freeze_energy_other_key), 0L);
            resBuild = Protocol.Account.AccountResource.newBuilder();
            forzen = Protocol.Account.Frozen.newBuilder();
            forzen.setFrozenBalance(energy);
            forzen.setExpireTime(energy_time);
            resBuild.setDelegatedFrozenBalanceForEnergy(delegated_frozen_energy);
            resBuild.setFrozenBalanceForEnergy(forzen.build());
            resBuild.setAcquiredDelegatedFrozenBalanceForEnergy(freeze_energy_other);
            builder.setAccountResource(resBuild.build());
            builder.setDelegatedFrozenBalanceForBandwidth(delegated_frozen_bandwidth);
            builder.setAcquiredDelegatedFrozenBalanceForBandwidth(freeze_bandwidth_other);
            builder.setAccountName(ByteString.copyFromUtf8(name));
            try {
                if (StringTronUtil.decodeFromBase58Check(address) != null && StringTronUtil.isAddressValid(StringTronUtil.decodeFromBase58Check(address))) {
                    builder.setAddress(ByteString.copyFrom(StringTronUtil.decodeFromBase58Check(address)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            builder.setBalance(balance);
            if (assets != null) {
                builder.putAllAsset(assets);
            }
            if (assetsV2 != null) {
                builder.putAllAssetV2(assetsV2);
            }
            builder.addAllVotes(votes);
            builder.addAllFrozen(frozen);
            builder.setNetUsage(bandwidth);
            builder.setCreateTime(createTime);
            builder.setLatestOprationTime(latestOperationTime);
            return builder.build();
        }
        return Protocol.Account.getDefaultInstance();
    }

    public static void setSelectedWallet(String walletName) {
        SpAPI.THIS.setSelectedWallet(walletName);
        RxBus.getInstance().post(Event.SELECTEDWALLET);
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:121:0x052a -> B:18:0x0073). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:123:0x0530 -> B:18:0x0073). Please report as a decompilation issue!!! */
    public static Protocol.Transaction packTransaction(String strTransaction) {
        JSONObject parseObject = JSONObject.parseObject(strTransaction);
        JSONObject jSONObject = parseObject.getJSONObject("raw_data");
        JSONArray contracts = new JSONArray();
        JSONArray rawContractArray = jSONObject.getJSONArray("contract");
        int i = 0;
        while (i < rawContractArray.size()) {
            try {
                JSONObject contract = rawContractArray.getJSONObject(i);
                JSONObject parameter = contract.getJSONObject("parameter");
                String contractType = contract.getString("type");
                Any any = null;
                char c = 65535;
                switch (contractType.hashCode()) {
                    case -1705044092:
                        if (contractType.equals("WithdrawBalanceContract")) {
                            c = CharUtils.CR;
                            break;
                        }
                        break;
                    case -1485407205:
                        if (contractType.equals("AssetIssueContract")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -1048760864:
                        if (contractType.equals("ProposalCreateContract")) {
                            c = 22;
                            break;
                        }
                        break;
                    case -703089577:
                        if (contractType.equals("FreezeBalanceContract")) {
                            c = '\n';
                            break;
                        }
                        break;
                    case -651921570:
                        if (contractType.equals("UnfreezeBalanceContract")) {
                            c = 11;
                            break;
                        }
                        break;
                    case -544448037:
                        if (contractType.equals("SmartContract")) {
                            c = 15;
                            break;
                        }
                        break;
                    case -492394392:
                        if (contractType.equals("AccountUpdateContract")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case -439997029:
                        if (contractType.equals("AccountCreateContract")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -243778867:
                        if (contractType.equals("ExchangeTransactionContract")) {
                            c = 20;
                            break;
                        }
                        break;
                    case -2225434:
                        if (contractType.equals("ExchangeInjectContract")) {
                            c = 19;
                            break;
                        }
                        break;
                    case 16441433:
                        if (contractType.equals("ParticipateAssetIssueContract")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case 180125197:
                        if (contractType.equals("ProposalApproveContract")) {
                            c = 23;
                            break;
                        }
                        break;
                    case 270660495:
                        if (contractType.equals("ProposalDeleteContract")) {
                            c = 24;
                            break;
                        }
                        break;
                    case 336992568:
                        if (contractType.equals("VoteAssetContract")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 706457047:
                        if (contractType.equals("TransferAssetContract")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 710366781:
                        if (contractType.equals("TransferContract")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1252602738:
                        if (contractType.equals("UnfreezeAssetContract")) {
                            c = '\f';
                            break;
                        }
                        break;
                    case 1286958708:
                        if (contractType.equals("WitnessUpdateContract")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1339356071:
                        if (contractType.equals("WitnessCreateContract")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1392453279:
                        if (contractType.equals("ExchangeWithdrawContract")) {
                            c = 21;
                            break;
                        }
                        break;
                    case 1421429571:
                        if (contractType.equals("TriggerSmartContract")) {
                            c = 16;
                            break;
                        }
                        break;
                    case 1532035647:
                        if (contractType.equals("CreateSmartContract")) {
                            c = 17;
                            break;
                        }
                        break;
                    case 1699052801:
                        if (contractType.equals("VoteWitnessContract")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1892384185:
                        if (contractType.equals("UpdateAssetContract")) {
                            c = 14;
                            break;
                        }
                        break;
                    case 2106222417:
                        if (contractType.equals("ExchangeCreateContract")) {
                            c = 18;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Contract.AccountCreateContract.Builder accountCreateContractBuilder = Contract.AccountCreateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), accountCreateContractBuilder);
                        any = Any.pack(accountCreateContractBuilder.build());
                        break;
                    case 1:
                        Contract.TransferContract.Builder transferContractBuilder = Contract.TransferContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), transferContractBuilder);
                        any = Any.pack(transferContractBuilder.build());
                        break;
                    case 2:
                        Contract.TransferAssetContract.Builder transferAssetContractBuilder = Contract.TransferAssetContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), transferAssetContractBuilder);
                        any = Any.pack(transferAssetContractBuilder.build());
                        break;
                    case 3:
                        Contract.VoteAssetContract.Builder voteAssetContractBuilder = Contract.VoteAssetContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), voteAssetContractBuilder);
                        any = Any.pack(voteAssetContractBuilder.build());
                        break;
                    case 4:
                        Contract.VoteWitnessContract.Builder voteWitnessContractBuilder = Contract.VoteWitnessContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), voteWitnessContractBuilder);
                        any = Any.pack(voteWitnessContractBuilder.build());
                        break;
                    case 5:
                        Contract.WitnessCreateContract.Builder witnessCreateContractBuilder = Contract.WitnessCreateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), witnessCreateContractBuilder);
                        any = Any.pack(witnessCreateContractBuilder.build());
                        break;
                    case 6:
                        Contract.AssetIssueContract.Builder assetIssueContractBuilder = Contract.AssetIssueContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), assetIssueContractBuilder);
                        any = Any.pack(assetIssueContractBuilder.build());
                        break;
                    case 7:
                        Contract.WitnessUpdateContract.Builder witnessUpdateContractBuilder = Contract.WitnessUpdateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), witnessUpdateContractBuilder);
                        any = Any.pack(witnessUpdateContractBuilder.build());
                        break;
                    case '\b':
                        Contract.ParticipateAssetIssueContract.Builder participateAssetIssueContractBuilder = Contract.ParticipateAssetIssueContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), participateAssetIssueContractBuilder);
                        any = Any.pack(participateAssetIssueContractBuilder.build());
                        break;
                    case '\t':
                        Contract.AccountUpdateContract.Builder accountUpdateContractBuilder = Contract.AccountUpdateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), accountUpdateContractBuilder);
                        any = Any.pack(accountUpdateContractBuilder.build());
                        break;
                    case '\n':
                        Contract.FreezeBalanceContract.Builder freezeBalanceContractBuilder = Contract.FreezeBalanceContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), freezeBalanceContractBuilder);
                        any = Any.pack(freezeBalanceContractBuilder.build());
                        break;
                    case 11:
                        Contract.UnfreezeBalanceContract.Builder unfreezeBalanceContractBuilder = Contract.UnfreezeBalanceContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), unfreezeBalanceContractBuilder);
                        any = Any.pack(unfreezeBalanceContractBuilder.build());
                        break;
                    case '\f':
                        Contract.UnfreezeAssetContract.Builder unfreezeAssetContractBuilder = Contract.UnfreezeAssetContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), unfreezeAssetContractBuilder);
                        any = Any.pack(unfreezeAssetContractBuilder.build());
                        break;
                    case '\r':
                        Contract.WithdrawBalanceContract.Builder withdrawBalanceContractBuilder = Contract.WithdrawBalanceContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), withdrawBalanceContractBuilder);
                        any = Any.pack(withdrawBalanceContractBuilder.build());
                        break;
                    case 14:
                        Contract.UpdateAssetContract.Builder updateAssetContractBuilder = Contract.UpdateAssetContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), updateAssetContractBuilder);
                        any = Any.pack(updateAssetContractBuilder.build());
                        break;
                    case 15:
                        Protocol.SmartContract.Builder smartContractBuilder = Protocol.SmartContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), smartContractBuilder);
                        any = Any.pack(smartContractBuilder.build());
                        break;
                    case 16:
                        Contract.TriggerSmartContract.Builder triggerSmartContractBuilder = Contract.TriggerSmartContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), triggerSmartContractBuilder);
                        any = Any.pack(triggerSmartContractBuilder.build());
                        break;
                    case 17:
                        Contract.CreateSmartContract.Builder createSmartContractBuilder = Contract.CreateSmartContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), createSmartContractBuilder);
                        any = Any.pack(createSmartContractBuilder.build());
                        break;
                    case 18:
                        Contract.ExchangeCreateContract.Builder exchangeCreateContractBuilder = Contract.ExchangeCreateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), exchangeCreateContractBuilder);
                        any = Any.pack(exchangeCreateContractBuilder.build());
                        break;
                    case 19:
                        Contract.ExchangeInjectContract.Builder exchangeInjectContractBuilder = Contract.ExchangeInjectContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), exchangeInjectContractBuilder);
                        any = Any.pack(exchangeInjectContractBuilder.build());
                        break;
                    case 20:
                        Contract.ExchangeTransactionContract.Builder exchangeTransactionContractBuilder = Contract.ExchangeTransactionContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), exchangeTransactionContractBuilder);
                        any = Any.pack(exchangeTransactionContractBuilder.build());
                        break;
                    case 21:
                        Contract.ExchangeWithdrawContract.Builder exchangeWithdrawContractBuilder = Contract.ExchangeWithdrawContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), exchangeWithdrawContractBuilder);
                        any = Any.pack(exchangeWithdrawContractBuilder.build());
                        break;
                    case 22:
                        Contract.ProposalCreateContract.Builder ProposalCreateContractBuilder = Contract.ProposalCreateContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), ProposalCreateContractBuilder);
                        any = Any.pack(ProposalCreateContractBuilder.build());
                        break;
                    case 23:
                        Contract.ProposalApproveContract.Builder ProposalApproveContractBuilder = Contract.ProposalApproveContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), ProposalApproveContractBuilder);
                        any = Any.pack(ProposalApproveContractBuilder.build());
                        break;
                    case 24:
                        Contract.ProposalDeleteContract.Builder ProposalDeleteContractBuilder = Contract.ProposalDeleteContract.newBuilder();
                        JsonFormat.merge(parameter.getJSONObject("value").toJSONString(), ProposalDeleteContractBuilder);
                        any = Any.pack(ProposalDeleteContractBuilder.build());
                        break;
                }
                if (any != null) {
                    String value = ByteArray.toHexString(any.getValue().toByteArray());
                    parameter.put("value", (Object) value);
                    contract.put("parameter", (Object) parameter);
                    contracts.add(contract);
                }
            } catch (JsonFormat.ParseException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            i++;
        }
        jSONObject.put("contract", (Object) contracts);
        parseObject.put("raw_data", (Object) jSONObject);
        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
        try {
            JsonFormat.merge(parseObject.toJSONString(), transactionBuilder);
            return transactionBuilder.build();
        } catch (Exception e3) {
            return null;
        }
    }

    public static String printTransaction(Protocol.Transaction transaction) {
        return printTransactionToJSONs(transaction).toJSONString();
    }

    public static JSONObject printTransactionToJSONs(Protocol.Transaction transaction) {
        JSONObject parseObject = JSONObject.parseObject(JsonFormat.printToString(transaction));
        JSONArray contracts = new JSONArray();
        List<Protocol.Transaction.Contract> contractList = transaction.getRawData().getContractList();
        for (Protocol.Transaction.Contract contract : contractList) {
            JSONObject contractJson = null;
            try {
                Any contractParameter = contract.getParameter();
                switch (contract.getType()) {
                    case AccountCreateContract:
                        Contract.AccountCreateContract accountCreateContract = (Contract.AccountCreateContract) contractParameter.unpack(Contract.AccountCreateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(accountCreateContract));
                        break;
                    case TransferContract:
                        Contract.TransferContract transferContract = (Contract.TransferContract) contractParameter.unpack(Contract.TransferContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(transferContract));
                        break;
                    case TransferAssetContract:
                        Contract.TransferAssetContract transferAssetContract = (Contract.TransferAssetContract) contractParameter.unpack(Contract.TransferAssetContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(transferAssetContract));
                        break;
                    case VoteAssetContract:
                        Contract.VoteAssetContract voteAssetContract = (Contract.VoteAssetContract) contractParameter.unpack(Contract.VoteAssetContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(voteAssetContract));
                        break;
                    case VoteWitnessContract:
                        Contract.VoteWitnessContract voteWitnessContract = (Contract.VoteWitnessContract) contractParameter.unpack(Contract.VoteWitnessContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(voteWitnessContract));
                        break;
                    case WitnessCreateContract:
                        Contract.WitnessCreateContract witnessCreateContract = (Contract.WitnessCreateContract) contractParameter.unpack(Contract.WitnessCreateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(witnessCreateContract));
                        break;
                    case AssetIssueContract:
                        Contract.AssetIssueContract assetIssueContract = (Contract.AssetIssueContract) contractParameter.unpack(Contract.AssetIssueContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(assetIssueContract));
                        break;
                    case WitnessUpdateContract:
                        Contract.WitnessUpdateContract witnessUpdateContract = (Contract.WitnessUpdateContract) contractParameter.unpack(Contract.WitnessUpdateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(witnessUpdateContract));
                        break;
                    case ParticipateAssetIssueContract:
                        Contract.ParticipateAssetIssueContract participateAssetIssueContract = (Contract.ParticipateAssetIssueContract) contractParameter.unpack(Contract.ParticipateAssetIssueContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(participateAssetIssueContract));
                        break;
                    case AccountUpdateContract:
                        Contract.AccountUpdateContract accountUpdateContract = (Contract.AccountUpdateContract) contractParameter.unpack(Contract.AccountUpdateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(accountUpdateContract));
                        break;
                    case FreezeBalanceContract:
                        Contract.FreezeBalanceContract freezeBalanceContract = (Contract.FreezeBalanceContract) contractParameter.unpack(Contract.FreezeBalanceContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(freezeBalanceContract));
                        break;
                    case UnfreezeBalanceContract:
                        Contract.UnfreezeBalanceContract unfreezeBalanceContract = (Contract.UnfreezeBalanceContract) contractParameter.unpack(Contract.UnfreezeBalanceContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(unfreezeBalanceContract));
                        break;
                    case UnfreezeAssetContract:
                        Contract.UnfreezeAssetContract unfreezeAssetContract = (Contract.UnfreezeAssetContract) contractParameter.unpack(Contract.UnfreezeAssetContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(unfreezeAssetContract));
                        break;
                    case WithdrawBalanceContract:
                        Contract.WithdrawBalanceContract withdrawBalanceContract = (Contract.WithdrawBalanceContract) contractParameter.unpack(Contract.WithdrawBalanceContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(withdrawBalanceContract));
                        break;
                    case UpdateAssetContract:
                        Contract.UpdateAssetContract updateAssetContract = (Contract.UpdateAssetContract) contractParameter.unpack(Contract.UpdateAssetContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(updateAssetContract));
                        break;
                    case CreateSmartContract:
                        Contract.CreateSmartContract deployContract = (Contract.CreateSmartContract) contractParameter.unpack(Contract.CreateSmartContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(deployContract));
                        byte[] ownerAddress = deployContract.getOwnerAddress().toByteArray();
                        byte[] contractAddress = generateContractAddress(transaction, ownerAddress);
                        parseObject.put("contract_address", (Object) ByteArray.toHexString(contractAddress));
                        break;
                    case TriggerSmartContract:
                        Contract.TriggerSmartContract triggerSmartContract = (Contract.TriggerSmartContract) contractParameter.unpack(Contract.TriggerSmartContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(triggerSmartContract));
                        break;
                    case ProposalCreateContract:
                        Contract.ProposalCreateContract proposalCreateContract = (Contract.ProposalCreateContract) contractParameter.unpack(Contract.ProposalCreateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(proposalCreateContract));
                        break;
                    case ProposalApproveContract:
                        Contract.ProposalApproveContract proposalApproveContract = (Contract.ProposalApproveContract) contractParameter.unpack(Contract.ProposalApproveContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(proposalApproveContract));
                        break;
                    case ProposalDeleteContract:
                        Contract.ProposalDeleteContract proposalDeleteContract = (Contract.ProposalDeleteContract) contractParameter.unpack(Contract.ProposalDeleteContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(proposalDeleteContract));
                        break;
                    case ExchangeCreateContract:
                        Contract.ExchangeCreateContract exchangeCreateContract = (Contract.ExchangeCreateContract) contractParameter.unpack(Contract.ExchangeCreateContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(exchangeCreateContract));
                        break;
                    case ExchangeInjectContract:
                        Contract.ExchangeInjectContract exchangeInjectContract = (Contract.ExchangeInjectContract) contractParameter.unpack(Contract.ExchangeInjectContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(exchangeInjectContract));
                        break;
                    case ExchangeWithdrawContract:
                        Contract.ExchangeWithdrawContract exchangeWithdrawContract = (Contract.ExchangeWithdrawContract) contractParameter.unpack(Contract.ExchangeWithdrawContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(exchangeWithdrawContract));
                        break;
                    case ExchangeTransactionContract:
                        Contract.ExchangeTransactionContract exchangeTransactionContract = (Contract.ExchangeTransactionContract) contractParameter.unpack(Contract.ExchangeTransactionContract.class);
                        contractJson = JSONObject.parseObject(JsonFormat.printToString(exchangeTransactionContract));
                        break;
                }
                JSONObject parameter = new JSONObject();
                parameter.put("value", (Object) contractJson);
                parameter.put("type_url", (Object) contract.getParameterOrBuilder().getTypeUrl());
                JSONObject jsonContract = new JSONObject();
                jsonContract.put("parameter", (Object) parameter);
                jsonContract.put("type", (Object) contract.getType());
                contracts.add(jsonContract);
            } catch (InvalidProtocolBufferException e) {
            }
        }
        JSONObject rawData = JSONObject.parseObject(parseObject.get("raw_data").toString());
        rawData.put("contract", (Object) contracts);
        parseObject.put("raw_data", (Object) rawData);
        String txID = ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
        parseObject.put("txID", (Object) txID);
        return parseObject;
    }

    public static byte[] generateContractAddress(Protocol.Transaction trx, byte[] ownerAddress) {
        byte[] txRawDataHash = Sha256Hash.of(trx.getRawData().toByteArray()).getBytes();
        byte[] combined = new byte[txRawDataHash.length + ownerAddress.length];
        System.arraycopy(txRawDataHash, 0, combined, 0, txRawDataHash.length);
        System.arraycopy(ownerAddress, 0, combined, txRawDataHash.length, ownerAddress.length);
        return Hash.sha3omit12(combined);
    }
}
