package com.bitpie.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.bitpie.activity.chat.ChatActivity_;
import com.bitpie.activity.extract.HistoryAddressExtractActivity_;
import com.bitpie.activity.idverification.KycVerifyActivity_;
import com.bitpie.bithd.activity.ViewPasswordActivity_;
import com.bitpie.ui.base.dialog.DialogExpieToken_;
import org.a.a.b.c;
import org.a.a.b.d;
import org.a.a.b.e;
import org.a.a.b.h;
import org.a.a.b.i;
import org.a.a.b.j;
import org.a.a.b.k;
import org.a.a.b.n;
import org.a.a.b.o;
import org.a.a.b.p;

/* loaded from: classes.dex */
public final class Preference_ extends n {

    /* loaded from: classes.dex */
    public static final class PreferenceEditor_ extends e<PreferenceEditor_> {
        PreferenceEditor_(SharedPreferences sharedPreferences) {
            super(sharedPreferences);
        }

        public h<PreferenceEditor_> adPledgeEnough() {
            return intField("adPledgeEnough");
        }

        public o<PreferenceEditor_> addresses() {
            return stringField(HistoryAddressExtractActivity_.ADDRESSES_EXTRA);
        }

        public c<PreferenceEditor_> agreeTermsOfService() {
            return booleanField("agreeTermsOfService");
        }

        public o<PreferenceEditor_> bip39Language() {
            return stringField("bip39Language");
        }

        public h<PreferenceEditor_> canPledegeVip() {
            return intField("canPledegeVip");
        }

        public o<PreferenceEditor_> coinCode() {
            return stringField("coinCode");
        }

        public o<PreferenceEditor_> coinCoinsConfigureJson() {
            return stringField("coinCoinsConfigureJson");
        }

        public h<PreferenceEditor_> coinDecimal() {
            return intField("coinDecimal");
        }

        public o<PreferenceEditor_> coinsJsonHash() {
            return stringField("coinsJsonHash");
        }

        public o<PreferenceEditor_> currency() {
            return stringField("currency");
        }

        public h<PreferenceEditor_> defaultAddressType() {
            return intField("defaultAddressType");
        }

        public h<PreferenceEditor_> defaultBankCardId() {
            return intField("defaultBankCardId");
        }

        public h<PreferenceEditor_> doNotShowVersionCode() {
            return intField("doNotShowVersionCode");
        }

        public o<PreferenceEditor_> exMarket() {
            return stringField("exMarket");
        }

        public o<PreferenceEditor_> exOtcCoinCode() {
            return stringField("exOtcCoinCode");
        }

        public h<PreferenceEditor_> instantBuyDefaultPaymentMethodValue() {
            return intField("instantBuyDefaultPaymentMethodValue");
        }

        public h<PreferenceEditor_> instantSellDefaultBankCardId() {
            return intField("instantSellDefaultBankCardId");
        }

        public c<PreferenceEditor_> isConfirmTips() {
            return booleanField("isConfirmTips");
        }

        public c<PreferenceEditor_> isTransfer() {
            return booleanField("isTransfer");
        }

        public c<PreferenceEditor_> isUseBitcoinCashNewAddressFormat() {
            return booleanField("isUseBitcoinCashNewAddressFormat");
        }

        public h<PreferenceEditor_> keyIndex() {
            return intField("keyIndex");
        }

        public h<PreferenceEditor_> kycLevel() {
            return intField(KycVerifyActivity_.KYC_LEVEL_EXTRA);
        }

        public o<PreferenceEditor_> kycRealName() {
            return stringField("kycRealName");
        }

        public o<PreferenceEditor_> kycRealNames() {
            return stringField("kycRealNames");
        }

        public c<PreferenceEditor_> languageExSupport() {
            return booleanField("languageExSupport");
        }

        public h<PreferenceEditor_> minerFeesGear() {
            return intField("minerFeesGear");
        }

        public h<PreferenceEditor_> notificationId() {
            return intField("notificationId");
        }

        public h<PreferenceEditor_> notificationIdHandled() {
            return intField("notificationIdHandled");
        }

        public h<PreferenceEditor_> notificationIdRead() {
            return intField("notificationIdRead");
        }

        public h<PreferenceEditor_> notificationNewCount() {
            return intField("notificationNewCount");
        }

        public j<PreferenceEditor_> offsetTime() {
            return longField("offsetTime");
        }

        public c<PreferenceEditor_> openAccounts() {
            return booleanField("openAccounts");
        }

        public c<PreferenceEditor_> openExchange() {
            return booleanField("openExchange");
        }

        public c<PreferenceEditor_> openPieStore() {
            return booleanField("openPieStore");
        }

        public h<PreferenceEditor_> orderPledgeEnough() {
            return intField("orderPledgeEnough");
        }

        public c<PreferenceEditor_> otcOnlineStatus() {
            return booleanField("otcOnlineStatus");
        }

        public c<PreferenceEditor_> otcVipNotice() {
            return booleanField("otcVipNotice");
        }

        public o<PreferenceEditor_> password() {
            return stringField(ViewPasswordActivity_.PASSWORD_EXTRA);
        }

        public o<PreferenceEditor_> phoneCountryCode() {
            return stringField("phoneCountryCode");
        }

        public h<PreferenceEditor_> pieKeyIndex() {
            return intField("pieKeyIndex");
        }

        public j<PreferenceEditor_> piePurchaseServerTimeOffset() {
            return longField("piePurchaseServerTimeOffset");
        }

        public o<PreferenceEditor_> pinCode() {
            return stringField("pinCode");
        }

        public o<PreferenceEditor_> pinCodeType() {
            return stringField("pinCodeType");
        }

        public j<PreferenceEditor_> pledgeServerTimeOffset() {
            return longField("pledgeServerTimeOffset");
        }

        public h<PreferenceEditor_> precision() {
            return intField("precision");
        }

        public h<PreferenceEditor_> price() {
            return intField("price");
        }

        public o<PreferenceEditor_> realName() {
            return stringField("realName");
        }

        public h<PreferenceEditor_> receivingAddressIndex() {
            return intField("receivingAddressIndex");
        }

        public o<PreferenceEditor_> renrenbitCookie() {
            return stringField("renrenbitCookie");
        }

        public o<PreferenceEditor_> seed() {
            return stringField("seed");
        }

        public o<PreferenceEditor_> seedPhraseEntropy() {
            return stringField("seedPhraseEntropy");
        }

        public c<PreferenceEditor_> seedPhraseEntropyWritten() {
            return booleanField("seedPhraseEntropyWritten");
        }

        public c<PreferenceEditor_> seedPhraseWrittenAgain() {
            return booleanField("seedPhraseWrittenAgain");
        }

        public j<PreferenceEditor_> serverTimeOffset() {
            return longField("serverTimeOffset");
        }

        public h<PreferenceEditor_> serverType() {
            return intField("serverType");
        }

        public c<PreferenceEditor_> showMinerFeeOption() {
            return booleanField("showMinerFeeOption");
        }

        public o<PreferenceEditor_> token() {
            return stringField(DialogExpieToken_.TOKEN_ARG);
        }

        public o<PreferenceEditor_> tokenCode() {
            return stringField("tokenCode");
        }

        public o<PreferenceEditor_> tokenInfo() {
            return stringField("tokenInfo");
        }

        public h<PreferenceEditor_> txAcceleratorCnt() {
            return intField("txAcceleratorCnt");
        }

        public c<PreferenceEditor_> updateAddressDataBase() {
            return booleanField("updateAddressDataBase");
        }

        public o<PreferenceEditor_> userAddress() {
            return stringField("userAddress");
        }

        public o<PreferenceEditor_> userAvatar() {
            return stringField("userAvatar");
        }

        public j<PreferenceEditor_> userBalance() {
            return longField("userBalance");
        }

        public o<PreferenceEditor_> userBalanceEt() {
            return stringField("userBalanceEt");
        }

        public h<PreferenceEditor_> userGender() {
            return intField("userGender");
        }

        public h<PreferenceEditor_> userId() {
            return intField("userId");
        }

        public o<PreferenceEditor_> userName() {
            return stringField(ChatActivity_.USER_NAME_EXTRA);
        }

        public o<PreferenceEditor_> userPhone() {
            return stringField("userPhone");
        }

        public c<PreferenceEditor_> verified() {
            return booleanField("verified");
        }

        public j<PreferenceEditor_> vipAdBuyTimeOffset() {
            return longField("vipAdBuyTimeOffset");
        }

        public j<PreferenceEditor_> vipAdSellTimeOffset() {
            return longField("vipAdSellTimeOffset");
        }

        public h<PreferenceEditor_> vipPledgeEnough() {
            return intField("vipPledgeEnough");
        }

        public j<PreferenceEditor_> visiableBannerId() {
            return longField("visiableBannerId");
        }
    }

    public Preference_(Context context) {
        super(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public i adPledgeEnough() {
        return intField("adPledgeEnough", 0);
    }

    public p addresses() {
        return stringField(HistoryAddressExtractActivity_.ADDRESSES_EXTRA, "");
    }

    public d agreeTermsOfService() {
        return booleanField("agreeTermsOfService", false);
    }

    public p bip39Language() {
        return stringField("bip39Language", "");
    }

    public i canPledegeVip() {
        return intField("canPledegeVip", 0);
    }

    public p coinCode() {
        return stringField("coinCode", "");
    }

    public p coinCoinsConfigureJson() {
        return stringField("coinCoinsConfigureJson", "");
    }

    public i coinDecimal() {
        return intField("coinDecimal", 0);
    }

    public p coinsJsonHash() {
        return stringField("coinsJsonHash", "");
    }

    public p currency() {
        return stringField("currency", "");
    }

    public i defaultAddressType() {
        return intField("defaultAddressType", 0);
    }

    public i defaultBankCardId() {
        return intField("defaultBankCardId", 0);
    }

    public i doNotShowVersionCode() {
        return intField("doNotShowVersionCode", 0);
    }

    public PreferenceEditor_ edit() {
        return new PreferenceEditor_(getSharedPreferences());
    }

    public p exMarket() {
        return stringField("exMarket", "");
    }

    public p exOtcCoinCode() {
        return stringField("exOtcCoinCode", "");
    }

    public i instantBuyDefaultPaymentMethodValue() {
        return intField("instantBuyDefaultPaymentMethodValue", 0);
    }

    public i instantSellDefaultBankCardId() {
        return intField("instantSellDefaultBankCardId", 0);
    }

    public d isConfirmTips() {
        return booleanField("isConfirmTips", false);
    }

    public d isTransfer() {
        return booleanField("isTransfer", false);
    }

    public d isUseBitcoinCashNewAddressFormat() {
        return booleanField("isUseBitcoinCashNewAddressFormat", false);
    }

    public i keyIndex() {
        return intField("keyIndex", 0);
    }

    public i kycLevel() {
        return intField(KycVerifyActivity_.KYC_LEVEL_EXTRA, 0);
    }

    public p kycRealName() {
        return stringField("kycRealName", "");
    }

    public p kycRealNames() {
        return stringField("kycRealNames", "");
    }

    public d languageExSupport() {
        return booleanField("languageExSupport", false);
    }

    public i minerFeesGear() {
        return intField("minerFeesGear", 0);
    }

    public i notificationId() {
        return intField("notificationId", 0);
    }

    public i notificationIdHandled() {
        return intField("notificationIdHandled", 0);
    }

    public i notificationIdRead() {
        return intField("notificationIdRead", 0);
    }

    public i notificationNewCount() {
        return intField("notificationNewCount", 0);
    }

    public k offsetTime() {
        return longField("offsetTime", 0L);
    }

    public d openAccounts() {
        return booleanField("openAccounts", false);
    }

    public d openExchange() {
        return booleanField("openExchange", false);
    }

    public d openPieStore() {
        return booleanField("openPieStore", false);
    }

    public i orderPledgeEnough() {
        return intField("orderPledgeEnough", 0);
    }

    public d otcOnlineStatus() {
        return booleanField("otcOnlineStatus", false);
    }

    public d otcVipNotice() {
        return booleanField("otcVipNotice", false);
    }

    public p password() {
        return stringField(ViewPasswordActivity_.PASSWORD_EXTRA, "");
    }

    public p phoneCountryCode() {
        return stringField("phoneCountryCode", "");
    }

    public i pieKeyIndex() {
        return intField("pieKeyIndex", 0);
    }

    public k piePurchaseServerTimeOffset() {
        return longField("piePurchaseServerTimeOffset", 0L);
    }

    public p pinCode() {
        return stringField("pinCode", "");
    }

    public p pinCodeType() {
        return stringField("pinCodeType", "");
    }

    public k pledgeServerTimeOffset() {
        return longField("pledgeServerTimeOffset", 0L);
    }

    public i precision() {
        return intField("precision", 0);
    }

    public i price() {
        return intField("price", 0);
    }

    public p realName() {
        return stringField("realName", "");
    }

    public i receivingAddressIndex() {
        return intField("receivingAddressIndex", 0);
    }

    public p renrenbitCookie() {
        return stringField("renrenbitCookie", "");
    }

    public p seed() {
        return stringField("seed", "");
    }

    public p seedPhraseEntropy() {
        return stringField("seedPhraseEntropy", "");
    }

    public d seedPhraseEntropyWritten() {
        return booleanField("seedPhraseEntropyWritten", false);
    }

    public d seedPhraseWrittenAgain() {
        return booleanField("seedPhraseWrittenAgain", false);
    }

    public k serverTimeOffset() {
        return longField("serverTimeOffset", 0L);
    }

    public i serverType() {
        return intField("serverType", 0);
    }

    public d showMinerFeeOption() {
        return booleanField("showMinerFeeOption", false);
    }

    public p token() {
        return stringField(DialogExpieToken_.TOKEN_ARG, "");
    }

    public p tokenCode() {
        return stringField("tokenCode", "");
    }

    public p tokenInfo() {
        return stringField("tokenInfo", "");
    }

    public i txAcceleratorCnt() {
        return intField("txAcceleratorCnt", 0);
    }

    public d updateAddressDataBase() {
        return booleanField("updateAddressDataBase", false);
    }

    public p userAddress() {
        return stringField("userAddress", "");
    }

    public p userAvatar() {
        return stringField("userAvatar", "");
    }

    public k userBalance() {
        return longField("userBalance", 0L);
    }

    public p userBalanceEt() {
        return stringField("userBalanceEt", "");
    }

    public i userGender() {
        return intField("userGender", 0);
    }

    public i userId() {
        return intField("userId", 0);
    }

    public p userName() {
        return stringField(ChatActivity_.USER_NAME_EXTRA, "");
    }

    public p userPhone() {
        return stringField("userPhone", "");
    }

    public d verified() {
        return booleanField("verified", false);
    }

    public k vipAdBuyTimeOffset() {
        return longField("vipAdBuyTimeOffset", 0L);
    }

    public k vipAdSellTimeOffset() {
        return longField("vipAdSellTimeOffset", 0L);
    }

    public i vipPledgeEnough() {
        return intField("vipPledgeEnough", 0);
    }

    public k visiableBannerId() {
        return longField("visiableBannerId", 0L);
    }
}
