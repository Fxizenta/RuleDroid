package com.woodforest.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.flurry.android.Constants;
import com.woodforest.OnlineBankingProfileData;
import com.woodforest.R;
import com.woodforest.androidrsshandler.Rss;
import com.woodforest.bank.online.accounts.AccountActivityResponse;
import com.woodforest.bank.online.accounts.EStatementsResponse;
import com.woodforest.bank.online.accounts.ListAccountsResponse;
import com.woodforest.bank.online.billpay.CancelPaymentRequest;
import com.woodforest.bank.online.billpay.CancelPaymentResponse;
import com.woodforest.bank.online.billpay.IsEnrolledResponse;
import com.woodforest.bank.online.billpay.PayeesResponse;
import com.woodforest.bank.online.billpay.PaymentResponse;
import com.woodforest.bank.online.billpay.SearchPaymentsResponse;
import com.woodforest.bank.online.enrollment.ServiceEnrollmentResponse;
import com.woodforest.bank.online.giftcards.BalanceRequest;
import com.woodforest.bank.online.giftcards.BalanceResponse;
import com.woodforest.bank.online.models.DeviceSupport;
import com.woodforest.bank.online.org.LocationSearchResponse;
import com.woodforest.bank.online.publicinformation.BlackoutDateResponse;
import com.woodforest.bank.online.remotedeposits.DepositHistoryResponse;
import com.woodforest.bank.online.remotedeposits.DepositProcessingStatus;
import com.woodforest.bank.online.remotedeposits.RemoteDepositResponse;
import com.woodforest.bank.online.security.SessionAccount;
import com.woodforest.bank.online.security.UserSettingsResponse;
import com.woodforest.bank.online.transfer.ScheduledTransfersResponse;
import com.woodforest.bank.online.transfer.TransferCancelRequest;
import com.woodforest.bank.online.transfer.TransferCancelResponse;
import com.woodforest.bank.online.transfer.TransferHistoryResponse;
import com.woodforest.bank.online.transfer.TransferMaintenanceScope;
import com.woodforest.bank.online.transfer.TransferScheduleResponse;
import com.woodforest.bank.onlinebanking.BankException;
import com.woodforest.enterprise.entities.Ack;
import com.woodforest.enterprise.security.authentication.AuthenticationResponse;
import com.woodforest.enterprise.security.authentication.AuthenticationResult;
import com.woodforest.enterprise.security.sessions.SessionExceptionDetail;
import com.woodforest.enterprise.security.sessions.SessionState;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/* loaded from: classes.dex */
public class Webservices extends Service {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$enterprise$security$authentication$AuthenticationResult = null;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest = null;
    public static final String PREFS_NAME = "WoodforestSettings";
    private static final String WFS = "By3WqLNTGf";
    private static final String baseIP = "https://api.woodforest.com/";
    private static final String baseURL = "https://api.woodforest.com/Xml/";
    private static final String kAbout = "https://mobile.woodforest.com/xml.ashx/rss/about";
    public static final String kAboutUs = "https://api.woodforest.com/doc/about?fullscreen=true";
    private static final String kAccountActivity = "https://api.woodforest.com/Xml/account/%1$d/activity?to=%2$s&from=%3$s&show=50";
    private static final String kAccountDetail = "http://beta.woodforest.net:8080/xml.ashx/bank/account/%1$d";
    private static final String kAccounts = "https://api.woodforest.com/Xml/account";
    private static final String kAgreeDepositTerms = "https://api.woodforest.com/Xml/remote_deposit/terms";
    private static final String kBillPay = "https://api.woodforest.com/Xml/billpay";
    private static final String kBlackOutDates = "https://api.woodforest.com/Xml/blackoutdates";
    private static final String kCancelPayment = "https://api.woodforest.com/Xml/billpay/payment/cancel";
    private static final String kCancelTransfer = "https://api.woodforest.com/Xml/transfer/%1$d/cancel";
    private static final String kChallengeService = "https://0025dtmjones.woodforest.net:444/Xml/login/authenticate";
    public static final String kContactUs = "https://api.woodforest.com/doc/contact?fullscreen=true";
    private static final int kDaysHistory = -90;
    public static final String kDepositDetails = "https://api.woodforest.com/Xml/remote_deposit/item?id=%1$s";
    private static final String kDepositHistory = "https://api.woodforest.com/Xml/remote_deposit";
    public static final String kDepositImage = "https://api.woodforest.com/Xml/remote_deposit/image?id=%1$s&side=%2$s";
    public static final String kDepositImageXml = "https://api.woodforest.com/Xml/remote_deposit/item?id=%1$s&image=%2$s";
    private static final String kDepositNonce = "https://api.woodforest.com/Xml/remote_deposit/manage/init";
    private static final String kEstatements = "https://api.woodforest.com/Xml/account/%1$d/statement";
    public static final String kFaq = "https://api.woodforest.com/doc/faq?fullscreen=true";
    private static final String kGiftCardBalance = "https://api.woodforest.com/Xml/giftcard";
    private static final String kGiftCardInfo = "http://beta.woodforest.net:8080/xml.ashx/giftcard/%1$s";
    private static final String kGiftCardNonce = "https://api.woodforest.com/Xml/giftcard/init";
    private static final String kKeepAlive = "http://beta.woodforest.net:8080/xml.ashx/bank/keep";
    private static final int kLocationResults = 15;
    private static final String kLocationSearch = "https://api.woodforest.com/Xml/location/?%1$s&start=%2$s&show=%3$s";
    private static final String kLocationSearchAddress = "https://mobile.woodforest.com/xml.ashx/locations?start=%2$d&show=%3$d&%1$s";
    private static final String kLocationSearchLatLong = "https://api.woodforest.com/Xml/location/?lat=%1$f&long=%2$f&start=%3$d&show=%4$d";
    private static final String kLogInService = "https://api.woodforest.com/Xml/login/authenticate";
    private static final String kLogOff = "https://api.woodforest.com/Xml/logout";
    private static final String kMakeDeposit = "https://api.woodforest.com/Xml/remote_deposit";
    private static final String kMakePayment = "https://api.woodforest.com/Xml/billpay/payment";
    private static final String kMakeTransfer = "https://api.woodforest.com/Xml/transfer";
    private static final String kPayees = "https://api.woodforest.com/Xml/billpay/payees";
    private static final String kPaymentCancelNonce = "https://api.woodforest.com/Xml/billpay/payment/cancel/init";
    private static final String kPaymentCreateNonce = "https://api.woodforest.com/Xml/billpay/payment/init";
    private static final String kPaymentHistory = "https://api.woodforest.com/Xml/billpay/payment/history";
    public static final String kPrivacy = "https://api.woodforest.com/doc/privacy?fullscreen=true";
    private static final String kScheduledPayments = "https://api.woodforest.com/Xml/billpay/payment/scheduled";
    private static final String kScheduledTransfers = "https://api.woodforest.com/Xml/transfer/scheduled";
    public static final String kTermsURL = "https://api.woodforest.com/doc/terms/remote_deposit";
    private static final String kTransferCancelNonce = "https://api.woodforest.com/Xml/transfer/cancel/init";
    private static final String kTransferCreateNonce = "https://api.woodforest.com/Xml/transfer/create/init";
    private static final String kTransferHistory = "https://api.woodforest.com/Xml/transfer/history?count=50&includeTotal=true&start=0";
    private static final String kUserSettings = "https://api.woodforest.com/Xml/profile/settings";
    public static final String kVersion = "https://api.woodforest.com/Xml/version";
    private AuthenticationResponse authResponse;
    private OnlineBankingProfileData cachedData;
    private List<Cookie> cookies;
    private HttpGet httpGet;
    private HttpPost httpPost;
    private HttpClient httpclient;
    private Thread networkManager;
    public static final Object syncRoot = new Object();
    public static final Object cacheSyncRoot = new Object();
    public static final Object authSyncRoot = new Object();
    private final Binder binder = new LocalBinder();
    private String user_culture = "";
    HttpContext localContext = new BasicHttpContext();
    CookieStore cookieStore = new BasicCookieStore();
    private List<OnlineBankingRequest> pendingRequests = new ArrayList();
    private Runnable callbackHandler = new Runnable() { // from class: com.woodforest.services.Webservices.1
        private static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest;

        static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest() {
            int[] iArr = $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest;
            if (iArr == null) {
                iArr = new int[OnlineBankingTypeRequest.valuesCustom().length];
                try {
                    iArr[OnlineBankingTypeRequest.About.ordinal()] = 20;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AccountActivity.ordinal()] = 4;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Accounts.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AgreeToTerms.ordinal()] = 27;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AnswerChallenge.ordinal()] = 2;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.BillPaySSO.ordinal()] = 25;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.BlackOutDates.ordinal()] = 24;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.CancelPayment.ordinal()] = 11;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.CancelTransfer.ordinal()] = 13;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.DepositHistory.ordinal()] = 29;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.DepositImages.ordinal()] = 30;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.EStatements.ordinal()] = 23;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Faq.ordinal()] = 18;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.GiftCardInfo.ordinal()] = 17;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.KeepAlive.ordinal()] = 21;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LocationSearchAddress.ordinal()] = 16;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LocationSearchLatLong.ordinal()] = Webservices.kLocationResults;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LogIn.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LogOff.ordinal()] = 14;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.MakeDeposit.ordinal()] = 28;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.MakePayment.ordinal()] = 10;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Payees.ordinal()] = 7;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.PaymentHistory.ordinal()] = 9;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Privacy.ordinal()] = 19;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.ScheduledPayments.ordinal()] = 8;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.ScheduledTransfers.ordinal()] = 5;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.TransferFunds.ordinal()] = 12;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.TransferHistory.ordinal()] = 6;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.UserSettings.ordinal()] = 26;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.VersionCheck.ordinal()] = 22;
                } catch (NoSuchFieldError e30) {
                }
                $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest = iArr;
            }
            return iArr;
        }

        @Override // java.lang.Runnable
        public void run() {
            OnlineBankingRequest r = null;
            do {
                try {
                    r = Webservices.this.handleCheckForPendingRequests();
                } catch (InterruptedException e) {
                    for (OnlineBankingRequest cancelRequest : Webservices.this.pendingRequests) {
                        if (cancelRequest.getCallback() != null) {
                            cancelRequest.getCallback().connectionError(Webservices.this.getResources().getString(R.string.res_0x7f060014_errormessage_nohttpresponse));
                        }
                    }
                } catch (ClientProtocolException e2) {
                    if (r.getCallback() != null) {
                        r.getCallback().connectionError(Webservices.this.getResources().getString(R.string.res_0x7f060014_errormessage_nohttpresponse));
                    }
                } catch (IOException e3) {
                    if (r.getCallback() != null) {
                        r.getCallback().connectionError(Webservices.this.getResources().getString(R.string.res_0x7f060014_errormessage_nohttpresponse));
                    }
                }
                switch ($SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest()[r.getType().ordinal()]) {
                    case 1:
                        Webservices.this.handleLogIn(r);
                        break;
                    case Constants.MODE_LANDSCAPE /* 2 */:
                        Webservices.this.handleAnswerChallenge(r);
                        break;
                    case 3:
                        Webservices.this.handleAccounts(r);
                        break;
                    case 4:
                        Webservices.this.handleAccountActivity(r);
                        break;
                    case 5:
                        Webservices.this.handleScheduledTransfers(r);
                        break;
                    case 6:
                        Webservices.this.handleTransferHistory(r);
                        break;
                    case 7:
                        Webservices.this.handlePayees(r);
                        break;
                    case 8:
                        Webservices.this.handleScheduledPayments(r);
                        break;
                    case 9:
                        Webservices.this.handlePaymentHistory(r);
                        break;
                    case 10:
                        Webservices.this.handleMakePayment(r);
                        break;
                    case 11:
                        Webservices.this.handleCancelPayment(r);
                        break;
                    case 12:
                        Webservices.this.handleMakeTransfer(r);
                        break;
                    case 13:
                        Webservices.this.handleCancelTransfer(r);
                        break;
                    case 14:
                        Webservices.this.handleLogOff(r);
                        break;
                    case Webservices.kLocationResults /* 15 */:
                        Webservices.this.handleLocationSearchLatLong(r);
                        break;
                    case 16:
                        Webservices.this.handleLocationSearchAddress(r);
                        break;
                    case 17:
                        Webservices.this.handleGiftCardInfo(r);
                        break;
                    case 18:
                    case 19:
                    case 20:
                        Webservices.this.handleRss(r);
                        break;
                    case 21:
                        Webservices.this.handleKeepAlive();
                        break;
                    case 22:
                        Webservices.this.handleVersionCheck(r);
                        break;
                    case 23:
                        Webservices.this.handleEStatements(r);
                        break;
                    case 24:
                        Webservices.this.handleBlackOutDates(r);
                        break;
                    case 25:
                        Webservices.this.handleSSO(r);
                        break;
                    case 26:
                        Webservices.this.handleUserSettings(r);
                        break;
                    case 27:
                        Webservices.this.handleAgreeToTerms(r);
                        break;
                    case 28:
                        Webservices.this.handleMakeDeposit(r);
                        break;
                    case 29:
                        Webservices.this.handleDepositHistory(r);
                        break;
                    case 30:
                        Webservices.this.handleDepositImages(r);
                        break;
                }
            } while (!Thread.interrupted());
        }
    };

    static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$enterprise$security$authentication$AuthenticationResult() {
        int[] iArr = $SWITCH_TABLE$com$woodforest$enterprise$security$authentication$AuthenticationResult;
        if (iArr == null) {
            iArr = new int[AuthenticationResult.valuesCustom().length];
            try {
                iArr[AuthenticationResult.AccountDisabled.ordinal()] = 8;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[AuthenticationResult.AccountExpired.ordinal()] = 9;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[AuthenticationResult.AccountLockedOut.ordinal()] = 7;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[AuthenticationResult.Authenticated.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[AuthenticationResult.AuthenticationDomainDisabled.ordinal()] = 24;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[AuthenticationResult.BlockedWhileImpersonated.ordinal()] = 26;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[AuthenticationResult.CannotChangePassword.ordinal()] = 14;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[AuthenticationResult.CannotIssueChallenge.ordinal()] = 17;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[AuthenticationResult.Challenge.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[AuthenticationResult.ChallengeExpired.ordinal()] = 18;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[AuthenticationResult.CredentialsDoNotMeetRequirements.ordinal()] = 13;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[AuthenticationResult.ImpersonationDisabled.ordinal()] = 25;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[AuthenticationResult.InvalidChallengeAnswer.ordinal()] = 19;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[AuthenticationResult.InvalidCredentials.ordinal()] = 6;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[AuthenticationResult.InvalidSequenceToken.ordinal()] = 20;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[AuthenticationResult.MustAuthenticate.ordinal()] = 5;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[AuthenticationResult.MustChangePassword.ordinal()] = 11;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[AuthenticationResult.PasswordExpired.ordinal()] = 12;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[AuthenticationResult.PasswordRecovery.ordinal()] = 4;
            } catch (NoSuchFieldError e19) {
            }
            try {
                iArr[AuthenticationResult.PasswordRecoveryExpired.ordinal()] = kLocationResults;
            } catch (NoSuchFieldError e20) {
            }
            try {
                iArr[AuthenticationResult.PasswordRecoveryNotEligible.ordinal()] = 16;
            } catch (NoSuchFieldError e21) {
            }
            try {
                iArr[AuthenticationResult.SequenceExpired.ordinal()] = 21;
            } catch (NoSuchFieldError e22) {
            }
            try {
                iArr[AuthenticationResult.SequenceIncomplete.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                iArr[AuthenticationResult.SequenceNotEligible.ordinal()] = 22;
            } catch (NoSuchFieldError e24) {
            }
            try {
                iArr[AuthenticationResult.UnknownException.ordinal()] = 1;
            } catch (NoSuchFieldError e25) {
            }
            try {
                iArr[AuthenticationResult.UnsupportedVersion.ordinal()] = 10;
            } catch (NoSuchFieldError e26) {
            }
            $SWITCH_TABLE$com$woodforest$enterprise$security$authentication$AuthenticationResult = iArr;
        }
        return iArr;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest() {
        int[] iArr = $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest;
        if (iArr == null) {
            iArr = new int[OnlineBankingTypeRequest.valuesCustom().length];
            try {
                iArr[OnlineBankingTypeRequest.About.ordinal()] = 20;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[OnlineBankingTypeRequest.AccountActivity.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[OnlineBankingTypeRequest.Accounts.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[OnlineBankingTypeRequest.AgreeToTerms.ordinal()] = 27;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[OnlineBankingTypeRequest.AnswerChallenge.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[OnlineBankingTypeRequest.BillPaySSO.ordinal()] = 25;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[OnlineBankingTypeRequest.BlackOutDates.ordinal()] = 24;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[OnlineBankingTypeRequest.CancelPayment.ordinal()] = 11;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[OnlineBankingTypeRequest.CancelTransfer.ordinal()] = 13;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[OnlineBankingTypeRequest.DepositHistory.ordinal()] = 29;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[OnlineBankingTypeRequest.DepositImages.ordinal()] = 30;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[OnlineBankingTypeRequest.EStatements.ordinal()] = 23;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[OnlineBankingTypeRequest.Faq.ordinal()] = 18;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[OnlineBankingTypeRequest.GiftCardInfo.ordinal()] = 17;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[OnlineBankingTypeRequest.KeepAlive.ordinal()] = 21;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[OnlineBankingTypeRequest.LocationSearchAddress.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[OnlineBankingTypeRequest.LocationSearchLatLong.ordinal()] = kLocationResults;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[OnlineBankingTypeRequest.LogIn.ordinal()] = 1;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[OnlineBankingTypeRequest.LogOff.ordinal()] = 14;
            } catch (NoSuchFieldError e19) {
            }
            try {
                iArr[OnlineBankingTypeRequest.MakeDeposit.ordinal()] = 28;
            } catch (NoSuchFieldError e20) {
            }
            try {
                iArr[OnlineBankingTypeRequest.MakePayment.ordinal()] = 10;
            } catch (NoSuchFieldError e21) {
            }
            try {
                iArr[OnlineBankingTypeRequest.Payees.ordinal()] = 7;
            } catch (NoSuchFieldError e22) {
            }
            try {
                iArr[OnlineBankingTypeRequest.PaymentHistory.ordinal()] = 9;
            } catch (NoSuchFieldError e23) {
            }
            try {
                iArr[OnlineBankingTypeRequest.Privacy.ordinal()] = 19;
            } catch (NoSuchFieldError e24) {
            }
            try {
                iArr[OnlineBankingTypeRequest.ScheduledPayments.ordinal()] = 8;
            } catch (NoSuchFieldError e25) {
            }
            try {
                iArr[OnlineBankingTypeRequest.ScheduledTransfers.ordinal()] = 5;
            } catch (NoSuchFieldError e26) {
            }
            try {
                iArr[OnlineBankingTypeRequest.TransferFunds.ordinal()] = 12;
            } catch (NoSuchFieldError e27) {
            }
            try {
                iArr[OnlineBankingTypeRequest.TransferHistory.ordinal()] = 6;
            } catch (NoSuchFieldError e28) {
            }
            try {
                iArr[OnlineBankingTypeRequest.UserSettings.ordinal()] = 26;
            } catch (NoSuchFieldError e29) {
            }
            try {
                iArr[OnlineBankingTypeRequest.VersionCheck.ordinal()] = 22;
            } catch (NoSuchFieldError e30) {
            }
            $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest = iArr;
        }
        return iArr;
    }

    public void clearCookies() {
        ((DefaultHttpClient) this.httpclient).getCookieStore().clear();
        getCookies();
    }

    public void abort() {
        try {
            if (this.httpclient != null) {
                if (this.httpPost != null) {
                    this.httpPost.abort();
                }
                if (this.httpGet != null) {
                    this.httpGet.abort();
                }
            }
        } catch (Exception e) {
            System.out.println("HTTPHelp : Abort Exception : " + e);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (intent.hasExtra("culture")) {
            this.user_culture = intent.getStringExtra("culture");
        }
        return this.binder;
    }

    /* loaded from: classes.dex */
    public class LocalBinder extends Binder implements IWebservices {
        private static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest;

        static /* synthetic */ int[] $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest() {
            int[] iArr = $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest;
            if (iArr == null) {
                iArr = new int[OnlineBankingTypeRequest.valuesCustom().length];
                try {
                    iArr[OnlineBankingTypeRequest.About.ordinal()] = 20;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AccountActivity.ordinal()] = 4;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Accounts.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AgreeToTerms.ordinal()] = 27;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.AnswerChallenge.ordinal()] = 2;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.BillPaySSO.ordinal()] = 25;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.BlackOutDates.ordinal()] = 24;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.CancelPayment.ordinal()] = 11;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.CancelTransfer.ordinal()] = 13;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.DepositHistory.ordinal()] = 29;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.DepositImages.ordinal()] = 30;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.EStatements.ordinal()] = 23;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Faq.ordinal()] = 18;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.GiftCardInfo.ordinal()] = 17;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.KeepAlive.ordinal()] = 21;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LocationSearchAddress.ordinal()] = 16;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LocationSearchLatLong.ordinal()] = Webservices.kLocationResults;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LogIn.ordinal()] = 1;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.LogOff.ordinal()] = 14;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.MakeDeposit.ordinal()] = 28;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.MakePayment.ordinal()] = 10;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Payees.ordinal()] = 7;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.PaymentHistory.ordinal()] = 9;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.Privacy.ordinal()] = 19;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.ScheduledPayments.ordinal()] = 8;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.ScheduledTransfers.ordinal()] = 5;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.TransferFunds.ordinal()] = 12;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.TransferHistory.ordinal()] = 6;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.UserSettings.ordinal()] = 26;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[OnlineBankingTypeRequest.VersionCheck.ordinal()] = 22;
                } catch (NoSuchFieldError e30) {
                }
                $SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest = iArr;
            }
            return iArr;
        }

        public LocalBinder() {
        }

        @Override // com.woodforest.services.IWebservices
        public Object requestDataOrRegisterListener(OnlineBankingRequest request) {
            switch ($SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest()[request.getType().ordinal()]) {
                case 3:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getAccounts() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setAccounts(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getAccounts();
                        }
                    }
                case 4:
                    synchronized (Webservices.cacheSyncRoot) {
                        AccountActivityResponse act = Webservices.this.cachedData.getActivityForAccountWithId(request.getReference());
                        if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.clearActivityForAccountWithId(request.getReference());
                            break;
                        } else {
                            if (act != null && request.getId() == null) {
                                Webservices.this.checkTimeForKeepAlive();
                                return act;
                            }
                            break;
                        }
                    }
                    break;
                case 5:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getScheduledTransfers() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setScheduledTransfers(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getScheduledTransfers();
                        }
                    }
                case 6:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getTransferHistory() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setTransferHistory(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getTransferHistory();
                        }
                    }
                case 7:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getPayees() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setPayees(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getPayees();
                        }
                    }
                case 8:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getScheduledPayments() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setScheduledPayments(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getScheduledPayments();
                        }
                    }
                case 9:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getPaymentHistory() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setPaymentHistory(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getPaymentHistory();
                        }
                    }
                case 18:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getFaq() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setFaq(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getFaq();
                        }
                    }
                case 19:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getPrivacy() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setPrivacy(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getPrivacy();
                        }
                    }
                case 20:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getAbout() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setAbout(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getAbout();
                        }
                    }
                case 24:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getBlackOutDates() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue()) {
                            Webservices.this.cachedData.setBlackOutDates(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getBlackOutDates();
                        }
                    }
                case 25:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getEnrollment() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue() || Webservices.this.cachedData.getEnrollment().getSystemProtection().getBlocking()) {
                            Webservices.this.cachedData.setEnrollment(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getEnrollment();
                        }
                    }
                    break;
                case 26:
                    synchronized (Webservices.cacheSyncRoot) {
                        if (Webservices.this.cachedData.getSettings() == null) {
                            break;
                        } else if (request.getRefresh().booleanValue() || Webservices.this.cachedData.getSettings().getSettings().getProtectionProfile().getRemoteDeposits().getAvailableOn() == null) {
                            Webservices.this.cachedData.setSettings(null);
                            break;
                        } else {
                            Webservices.this.checkTimeForKeepAlive();
                            return Webservices.this.cachedData.getSettings();
                        }
                    }
                    break;
            }
            synchronized (Webservices.syncRoot) {
                if (request.getCallback() != null) {
                    Webservices.this.queueRequestWithCallback(request);
                } else {
                    Webservices.this.pendingRequests.add(request);
                }
                Webservices.syncRoot.notify();
            }
            return null;
        }

        @Override // com.woodforest.services.IWebservices
        public void unregisterListener(IWebservicesCallback callBack) {
            synchronized (Webservices.syncRoot) {
                int i = 0;
                while (true) {
                    if (i >= Webservices.this.pendingRequests.size()) {
                        break;
                    }
                    if (((OnlineBankingRequest) Webservices.this.pendingRequests.get(i)).getCallback() != callBack) {
                        i++;
                    } else {
                        ((OnlineBankingRequest) Webservices.this.pendingRequests.get(i)).setCallback(null);
                        break;
                    }
                }
            }
        }

        @Override // com.woodforest.services.IWebservices
        public Boolean hasSessionActive() {
            return Webservices.this.hasSessionActive();
        }

        @Override // com.woodforest.services.IWebservices
        public void refreshCache() {
            Webservices.this.clearCache();
            Webservices.this.GetOnlineBankingData();
        }

        @Override // com.woodforest.services.IWebservices
        public void clearSession() {
            clearCookies();
            Webservices.this.clearCache();
        }

        @Override // com.woodforest.services.IWebservices
        public String getUserAgent() {
            return Webservices.this.getUserAgent();
        }

        @Override // com.woodforest.services.IWebservices
        public List<Cookie> getCookies() {
            return Webservices.this.cookies;
        }

        @Override // com.woodforest.services.IWebservices
        public void clearCookies() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OnlineBankingRequest handleCheckForPendingRequests() throws InterruptedException {
        OnlineBankingRequest r;
        synchronized (syncRoot) {
            if (this.pendingRequests.isEmpty()) {
                syncRoot.wait();
            }
            r = this.pendingRequests.get(0);
            this.pendingRequests.remove(0);
        }
        return r;
    }

    protected void handleDepositImages(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(String.format(kDepositImageXml, r.getGuid().toString(), "front"));
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            RemoteDepositResponse front = new RemoteDepositResponse();
            front.startParse(response);
            if (handleResponseFromServer(front, r)) {
                String response2 = doGet(String.format(kDepositImageXml, r.getGuid().toString(), "back"));
                if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                    RemoteDepositResponse back = new RemoteDepositResponse();
                    back.startParse(response2);
                    if (handleResponseFromServer(back, r) && r.getCallback() != null) {
                        ((IWebservicesDepositsCallback) r.getCallback()).receivedDepositImages(front.getDeposit().getFrontImage().getData(), back.getDeposit().getBackImage().getData());
                    }
                }
            }
        }
    }

    protected void handleUserSettings(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getSettings() == null) {
            String response = doGet(kUserSettings);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                UserSettingsResponse settings = new UserSettingsResponse();
                settings.startParse(response);
                if (handleResponseFromServer(settings, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setSettings(settings);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesSettingsCallback) r.getCallback()).receivedUserSettings(this.cachedData.getSettings());
            }
        }
    }

    protected void handleMakeDeposit(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kDepositNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            r.getDeposit().setSequence(ack.getSequence());
            String response2 = doPost("https://api.woodforest.com/Xml/remote_deposit", r.getDeposit().toXml());
            if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                RemoteDepositResponse confirmation = new RemoteDepositResponse();
                confirmation.startParse(response2);
                if (handleResponseFromServer(confirmation, r)) {
                    if (confirmation.getDeposit().getStatus().getCode() != DepositProcessingStatus.Review) {
                        if (r.getCallback() != null) {
                            ((IWebservicesDepositsCallback) r.getCallback()).depositReceived(confirmation);
                            return;
                        }
                        return;
                    }
                    try {
                        Thread.sleep(12000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String response3 = doGet(String.format(kDepositDetails, confirmation.getDeposit().getOid().toString()));
                    if (!checkForExceptionOnResponse(response3, r).booleanValue()) {
                        RemoteDepositResponse confirmation2 = new RemoteDepositResponse();
                        confirmation2.startParse(response3);
                        if (handleResponseFromServer(confirmation2, r) && r.getCallback() != null) {
                            ((IWebservicesDepositsCallback) r.getCallback()).depositReceived(confirmation2);
                        }
                    }
                }
            }
        }
    }

    protected void handleDepositHistory(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet("https://api.woodforest.com/Xml/remote_deposit");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            DepositHistoryResponse history = new DepositHistoryResponse();
            history.startParse(response);
            if (handleResponseFromServer(history, r)) {
                synchronized (cacheSyncRoot) {
                    this.cachedData.setDepositHistory(history);
                    if (r.getCallback() != null) {
                        ((IWebservicesDepositsCallback) r.getCallback()).receivedDepositHistory(history);
                    }
                }
            }
        }
    }

    protected void handleAgreeToTerms(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kDepositNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            String response2 = doPost(kAgreeDepositTerms, String.format("accept=%1$s&sequence=%2$s", Boolean.valueOf(r.isTermsDecision()), ack.getSequence().getValue()), "application/x-www-form-urlencoded");
            if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                ServiceEnrollmentResponse confirmation = new ServiceEnrollmentResponse();
                confirmation.startParse(response2);
                if (handleResponseFromServer(confirmation, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setSettings(null);
                    }
                    if (r.getCallback() != null) {
                        ((IWebservicesDepositsCallback) r.getCallback()).receivedTermsDecision(confirmation);
                    }
                }
            }
        }
    }

    protected void handleSSO(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(kBillPay);
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            IsEnrolledResponse enrolled = new IsEnrolledResponse();
            enrolled.startParse(response);
            if (enrolled.getSystemProtection().getBlocking() || handleResponseFromServer(enrolled, r)) {
                synchronized (cacheSyncRoot) {
                    this.cachedData.setEnrollment(enrolled);
                    if (r.getCallback() != null) {
                        ((IWebservicesPaymentsCallback) r.getCallback()).receivedEnrollmentResponse(enrolled);
                    }
                }
            }
        }
    }

    protected void handleKeepAlive() throws ClientProtocolException, IOException {
        doPost(kKeepAlive, "");
    }

    protected void handleVersionCheck(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(kVersion);
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            DeviceSupport ver = new DeviceSupport();
            ver.startParse(response);
            if (handleResponseFromServer(ver, r) && r.getCallback() != null) {
                ((IWebservicesVersionCheckCallback) r.getCallback()).versionInformation(ver);
            }
        }
    }

    public void checkTimeForKeepAlive() {
        Date current = new Date();
        if (this.cookies != null) {
            for (Cookie c : this.cookies) {
                if (c.getName().equalsIgnoreCase("RequestToken") && c.getExpiryDate().getTime() - 300000 < current.getTime()) {
                    synchronized (syncRoot) {
                        if (this.pendingRequests.size() == 0) {
                            this.pendingRequests.add(new OnlineBankingRequest(OnlineBankingTypeRequest.KeepAlive, 0, true));
                            syncRoot.notify();
                        }
                    }
                }
            }
        }
    }

    public void queueRequestWithCallback(OnlineBankingRequest request) {
        if (this.pendingRequests.size() == 0) {
            this.pendingRequests.add(request);
            return;
        }
        for (int i = 0; i < this.pendingRequests.size(); i++) {
            if (this.pendingRequests.get(i).getCallback() == null) {
                this.pendingRequests.add(i, request);
                return;
            }
        }
        this.pendingRequests.add(request);
    }

    protected void handleRss(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = "";
        switch ($SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest()[r.getType().ordinal()]) {
            case 18:
                response = doGet(kFaq);
                break;
            case 19:
                response = doGet(kPrivacy);
                break;
            case 20:
                response = doGet(kAbout);
                break;
        }
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Rss rss = new Rss();
            rss.startParse(response);
            synchronized (cacheSyncRoot) {
                switch ($SWITCH_TABLE$com$woodforest$services$OnlineBankingTypeRequest()[r.getType().ordinal()]) {
                    case 18:
                        this.cachedData.setFaq(rss);
                        break;
                    case 19:
                        this.cachedData.setPrivacy(rss);
                        break;
                    case 20:
                        this.cachedData.setAbout(rss);
                        break;
                }
                if (r.getCallback() != null) {
                    ((IWebservicesRssCallback) r.getCallback()).receivedRss(rss);
                }
            }
        }
    }

    protected void handleGiftCardInfo(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kGiftCardNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack nonce = new Ack();
            nonce.startParse(response);
            if (handleResponseFromServer(nonce, r)) {
                BalanceRequest req = new BalanceRequest();
                req.setPan(r.getLongId());
                req.setSequence(nonce.getSequence());
                String response2 = doPost(kGiftCardBalance, req.toXml());
                BalanceResponse balance = new BalanceResponse();
                balance.startParse(response2);
                if (!checkForExceptionOnResponse(response2, r).booleanValue() && handleResponseFromServer(balance, r) && r.getCallback() != null) {
                    ((IWebservicesGiftCardCallback) r.getCallback()).receivedGiftCardInfo(balance);
                }
            }
        }
    }

    protected void handlePayees(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(kPayees);
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            PayeesResponse payees = new PayeesResponse();
            payees.startParse(response);
            if (handleResponseFromServer(payees, r)) {
                synchronized (cacheSyncRoot) {
                    this.cachedData.setPayees(payees);
                    if (r.getCallback() != null) {
                        ((IWebservicesPaymentsCallback) r.getCallback()).receivedPayees(payees);
                    }
                }
            }
        }
    }

    protected void handleBlackOutDates(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(kBlackOutDates);
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            BlackoutDateResponse dates = new BlackoutDateResponse();
            dates.startParse(response);
            if (handleResponseFromServer(dates, r)) {
                synchronized (cacheSyncRoot) {
                    this.cachedData.setBlackOutDates(dates);
                    if (r.getCallback() != null) {
                        ((IWebservicesPaymentsCallback) r.getCallback()).receivedDates(dates);
                    }
                }
            }
        }
    }

    protected void handleLocationSearchLatLong(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doGet(String.format(Locale.US, kLocationSearchLatLong, Double.valueOf(r.getLatitude()), Double.valueOf(r.getLongitude()), Integer.valueOf(r.getIndexStart()), Integer.valueOf(kLocationResults)));
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            LocationSearchResponse results = handleLocationResponse(r, response);
            if (handleResponseFromServer(results, r) && r.getCallback() != null) {
                synchronized (cacheSyncRoot) {
                    ((IWebservicesLocationsCallback) r.getCallback()).receivedLocationResults(results);
                }
            }
        }
    }

    private LocationSearchResponse handleLocationResponse(OnlineBankingRequest r, String response) {
        LocationSearchResponse results = new LocationSearchResponse();
        results.startParse(response);
        if (!handleResponseFromServer(results, r)) {
            return null;
        }
        synchronized (cacheSyncRoot) {
            if (r.getIndexStart() == 0) {
                this.cachedData.setLocationResults(results);
            } else {
                this.cachedData.getLocationResults().getLocations().addAll(results.getLocations());
                results = this.cachedData.getLocationResults();
            }
        }
        return results;
    }

    protected void handleLocationSearchAddress(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        List<String> locationInfo = r.getLocationInfo();
        StringBuilder queryString = new StringBuilder();
        if (locationInfo.get(0).trim().length() > 0) {
            queryString.append(String.format("&postal=%1$s", locationInfo.get(0)));
        }
        if (locationInfo.get(1).trim().length() > 0) {
            queryString.append(String.format("&street=%1$s", URLEncoder.encode(locationInfo.get(1), "UTF-8")));
        }
        if (locationInfo.get(2).trim().length() > 0) {
            queryString.append(String.format("&city=%1$s", URLEncoder.encode(locationInfo.get(2), "UTF-8")));
        }
        if (locationInfo.get(3).trim().length() > 0) {
            queryString.append(String.format("&state=%1$s", URLEncoder.encode(locationInfo.get(3), "UTF-8")));
        }
        String response = doGet(String.format(kLocationSearch, queryString.toString(), Integer.valueOf(r.getIndexStart()), Integer.valueOf(kLocationResults)));
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            LocationSearchResponse results = handleLocationResponse(r, response);
            if (handleResponseFromServer(results, r) && r.getCallback() != null) {
                synchronized (cacheSyncRoot) {
                    ((IWebservicesLocationsCallback) r.getCallback()).receivedLocationResults(results);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLogOff(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        synchronized (syncRoot) {
            this.pendingRequests.clear();
        }
        synchronized (cacheSyncRoot) {
            this.cachedData = new OnlineBankingProfileData();
        }
        synchronized (authSyncRoot) {
            this.authResponse = null;
        }
        String response = doGet(kLogOff);
        if (!checkForExceptionOnResponse(response, r).booleanValue() && r.getCallback() != null) {
            ((IWebservicesLogOffCallback) r.getCallback()).logOffResult(Boolean.parseBoolean(response));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCancelPayment(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kPaymentCancelNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            if (handleResponseFromServer(ack, r)) {
                CancelPaymentRequest req = new CancelPaymentRequest();
                req.setSequence(ack.getSequence());
                req.setPayment(r.getLongId());
                String response2 = doPost(kCancelPayment, req.toXml());
                if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                    CancelPaymentResponse cancel = new CancelPaymentResponse();
                    cancel.startParse(response2);
                    if (handleResponseFromServer(cancel, r) && r.getCallback() != null) {
                        ((IWebservicesPaymentsCallback) r.getCallback()).paymentCanceled(cancel);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCancelTransfer(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kTransferCancelNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            if (handleResponseFromServer(ack, r)) {
                TransferCancelRequest cancel = new TransferCancelRequest();
                cancel.setSequence(ack.getSequence());
                cancel.setScope(TransferMaintenanceScope.EntireSchedule);
                String response2 = doPost(String.format(kCancelTransfer, Long.valueOf(r.getLongId())), cancel.toXml());
                if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                    TransferCancelResponse confirmation = new TransferCancelResponse();
                    confirmation.startParse(response2);
                    if (handleResponseFromServer(confirmation, r) && r.getCallback() != null) {
                        ((IWebservicesTransfersCallback) r.getCallback()).transferCanceled(confirmation);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAccountActivity(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getActivity() == null || r.getId() != null || this.cachedData.getActivityForAccountWithId(r.getReference()) == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(5, kDaysHistory);
            Date from = cal.getTime();
            Date current = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String getData = String.format(kAccountActivity, Integer.valueOf(r.getReference()), df.format(current), df.format(from));
            if (r.getId() != null) {
                getData = String.valueOf(getData) + String.format("&after=%1$s", r.getId());
            }
            String response = doGet(getData);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                AccountActivityResponse act = new AccountActivityResponse();
                act.startParse(response);
                if (handleResponseFromServer(act, r)) {
                    synchronized (cacheSyncRoot) {
                        if (r.getId() != null) {
                            this.cachedData.getActivityForAccountWithId(r.getReference()).addActivity(act);
                        } else {
                            this.cachedData.getActivity().add(act);
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesAccountsCallback) r.getCallback()).receivedAccountActivity(this.cachedData.getActivityForAccountWithId(r.getReference()));
            }
        }
    }

    protected void handleEStatements(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String getData = String.format(kEstatements, Integer.valueOf(r.getReference()));
        String response = doGet(getData);
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            EStatementsResponse statements = new EStatementsResponse();
            statements.startParse(response);
            if (handleResponseFromServer(statements, r) && r.getCallback() != null) {
                ((IWebservicesStatementCallback) r.getCallback()).receivedEStatements(statements);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePaymentHistory(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getPaymentHistory() == null) {
            String getData = String.format(kPaymentHistory, new Object[0]);
            String response = doGet(getData);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                SearchPaymentsResponse history = new SearchPaymentsResponse();
                history.startParse(response);
                if (handleResponseFromServer(history, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setPaymentHistory(history);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesPaymentsCallback) r.getCallback()).receivedPaymentHistory(this.cachedData.getPaymentHistory());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScheduledPayments(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getScheduledPayments() == null) {
            String getData = String.format(kScheduledPayments, new Object[0]);
            String response = doGet(getData);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                SearchPaymentsResponse scheduledPayments = new SearchPaymentsResponse();
                scheduledPayments.startParse(response);
                if (handleResponseFromServer(scheduledPayments, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setScheduledPayments(scheduledPayments);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesPaymentsCallback) r.getCallback()).receivedScheduledPayments(this.cachedData.getScheduledPayments());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScheduledTransfers(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getScheduledTransfers() == null) {
            String getData = String.format(kScheduledTransfers, new Object[0]);
            String response = doGet(getData);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                ScheduledTransfersResponse scheduledTransfers = new ScheduledTransfersResponse();
                scheduledTransfers.startParse(response);
                if (handleResponseFromServer(scheduledTransfers, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setScheduledTransfers(scheduledTransfers);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesTransfersCallback) r.getCallback()).receivedScheduledTransfers(this.cachedData.getScheduledTransfers());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTransferHistory(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getTransferHistory() == null) {
            String getData = String.format(kTransferHistory, new Object[0]);
            String response = doGet(getData);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                TransferHistoryResponse transferHistory = new TransferHistoryResponse();
                transferHistory.startParse(response);
                if (handleResponseFromServer(transferHistory, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setTransferHistory(transferHistory);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesTransfersCallback) r.getCallback()).receivedTransferHistory(this.cachedData.getTransferHistory());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAnswerChallenge(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kLogInService, r.getRequest().toXml());
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            AuthenticationResponse newAuthResponse = new AuthenticationResponse();
            newAuthResponse.startParse(response);
            synchronized (authSyncRoot) {
                this.authResponse = newAuthResponse;
                ((IWebservicesAuthenticationCallback) r.getCallback()).authenticationResponseReceived(this.authResponse);
            }
            CheckAuthentication();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLogIn(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        synchronized (authSyncRoot) {
            if (hasSessionActive().booleanValue() && this.authResponse != null && this.authResponse.getResult() == AuthenticationResult.Authenticated) {
                if (r.getCallback() != null) {
                    ((IWebservicesAuthenticationCallback) r.getCallback()).authenticationResponseReceived(this.authResponse);
                }
                return;
            }
            String response = doPost(kLogInService, r.getRequest().toXml());
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                AuthenticationResponse newAuthResponse = new AuthenticationResponse();
                newAuthResponse.startParse(response);
                synchronized (authSyncRoot) {
                    this.authResponse = newAuthResponse;
                    if (r.getCallback() != null) {
                        ((IWebservicesAuthenticationCallback) r.getCallback()).authenticationResponseReceived(this.authResponse);
                    }
                }
                CheckAuthentication();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAccounts(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        if (r.getRefresh().booleanValue() || this.cachedData.getAccounts() == null) {
            String response = doGet(kAccounts);
            if (!checkForExceptionOnResponse(response, r).booleanValue()) {
                ListAccountsResponse act = new ListAccountsResponse();
                act.startParse(response);
                if (handleResponseFromServer(act, r)) {
                    synchronized (cacheSyncRoot) {
                        this.cachedData.setAccounts(act);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (r.getCallback() != null) {
            synchronized (cacheSyncRoot) {
                ((IWebservicesAccountsCallback) r.getCallback()).receivedAccounts(this.cachedData.getAccounts());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMakePayment(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kPaymentCreateNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            r.getPayment().setSequence(ack.getSequence());
            String response2 = doPost(kMakePayment, r.getPayment().toXml());
            if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                PaymentResponse confirmation = new PaymentResponse();
                confirmation.startParse(response2);
                if (handleResponseFromServer(confirmation, r) && r.getCallback() != null) {
                    ((IWebservicesPaymentsCallback) r.getCallback()).paymentCompleted(confirmation);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMakeTransfer(OnlineBankingRequest r) throws ClientProtocolException, IOException {
        String response = doPost(kTransferCreateNonce, "");
        if (!checkForExceptionOnResponse(response, r).booleanValue()) {
            Ack ack = new Ack();
            ack.startParse(response);
            r.getFunds().setSequence(ack.getSequence());
            String response2 = doPost(kMakeTransfer, r.getFunds().toXml());
            if (!checkForExceptionOnResponse(response2, r).booleanValue()) {
                TransferScheduleResponse confirmation = new TransferScheduleResponse();
                confirmation.startParse(response2);
                if (handleResponseFromServer(confirmation, r) && r.getCallback() != null) {
                    ((IWebservicesTransfersCallback) r.getCallback()).transferCompleted(confirmation);
                }
            }
        }
    }

    private Boolean checkForExceptionOnResponse(String response, OnlineBankingRequest r) {
        if (!BankException.isException(response).booleanValue()) {
            return false;
        }
        SessionExceptionDetail exception = new SessionExceptionDetail();
        exception.startParse(response);
        if (exception.getSessionState() == SessionState.Closed) {
            this.cookieStore.clear();
        }
        if (r.getCallback() != null) {
            r.getCallback().receivedException(exception);
        }
        return true;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        this.localContext.setAttribute("http.cookie-store", this.cookieStore);
        this.cachedData = new OnlineBankingProfileData();
        this.httpclient = getNewHttpClient();
        this.networkManager = new Thread(this.callbackHandler);
        this.networkManager.start();
        getCookies();
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.networkManager.interrupt();
        this.httpclient.getConnectionManager().shutdown();
    }

    protected void CheckAuthentication() {
        if (this.authResponse != null) {
            switch ($SWITCH_TABLE$com$woodforest$enterprise$security$authentication$AuthenticationResult()[(this.authResponse.getResult() == null ? AuthenticationResult.UnknownException : this.authResponse.getResult()).ordinal()]) {
                case 1:
                    clearCookies();
                    return;
                case Constants.MODE_LANDSCAPE /* 2 */:
                    SharedPreferences settings = getSharedPreferences("WoodforestSettings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    try {
                        editor.putString("deviceToken", this.authResponse.getDeviceToken().getValue());
                    } catch (Exception e) {
                        Log.v("Error", "DT lost");
                    } finally {
                        editor.commit();
                    }
                    GetOnlineBankingData();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void GetOnlineBankingData() {
        synchronized (syncRoot) {
            GetAccounts();
            syncRoot.notify();
        }
    }

    private void GetAccounts() {
        OnlineBankingRequest req = new OnlineBankingRequest(OnlineBankingTypeRequest.Accounts, 0, false);
        this.pendingRequests.add(req);
    }

    private void GetAccountActivity() {
        List<OnlineBankingRequest> requests = new ArrayList<>();
        synchronized (cacheSyncRoot) {
            for (SessionAccount ad : this.cachedData.getAccounts().getAccounts().getsessionAccountCollection()) {
                OnlineBankingRequest req = new OnlineBankingRequest(OnlineBankingTypeRequest.AccountActivity, ad.getReference(), false);
                requests.add(req);
            }
        }
        synchronized (syncRoot) {
            for (int i = requests.size() - 1; i >= 0; i--) {
                if (this.pendingRequests.size() > 0) {
                    this.pendingRequests.add(1, requests.get(i));
                } else {
                    this.pendingRequests.add(requests.get(i));
                }
            }
            syncRoot.notify();
        }
    }

    private void GetTransferData() {
        OnlineBankingRequest req = new OnlineBankingRequest(OnlineBankingTypeRequest.TransferHistory, 0, false);
        this.pendingRequests.add(req);
        OnlineBankingRequest req2 = new OnlineBankingRequest(OnlineBankingTypeRequest.ScheduledTransfers, 0, false);
        this.pendingRequests.add(req2);
    }

    private void GetPaymentData() {
        OnlineBankingRequest req = new OnlineBankingRequest(OnlineBankingTypeRequest.PaymentHistory, 0, false);
        this.pendingRequests.add(req);
        OnlineBankingRequest req2 = new OnlineBankingRequest(OnlineBankingTypeRequest.ScheduledPayments, 0, false);
        this.pendingRequests.add(req2);
    }

    private void GetPayees() {
        OnlineBankingRequest req = new OnlineBankingRequest(OnlineBankingTypeRequest.Payees, 0, false);
        this.pendingRequests.add(req);
    }

    private String doPost(String url, String postData, String contentType) throws IOException, ClientProtocolException {
        this.httpPost = new HttpPost(url);
        if (this.user_culture != "") {
            this.httpPost.addHeader("Accept-Language", this.user_culture);
        }
        this.httpPost.setHeader("User-Agent", getUserAgent());
        boolean token = false;
        for (Cookie cookie : this.cookieStore.getCookies()) {
            if (cookie.getName().equalsIgnoreCase("did")) {
                token = true;
            }
        }
        if (!token) {
            SharedPreferences settings = getSharedPreferences("WoodforestSettings", 0);
            try {
                if (settings.contains("deviceToken")) {
                    BasicClientCookie tokenCookie = new BasicClientCookie("did", settings.getString("deviceToken", ""));
                    tokenCookie.setDomain("api.woodforest.com");
                    this.cookieStore.addCookie(tokenCookie);
                }
            } catch (Exception e) {
                Log.v("Error", "DT lost");
            }
        }
        try {
            StringEntity ent = new StringEntity(postData, "UTF-8");
            ent.setContentType(contentType);
            ent.setContentEncoding("UTF-8");
            this.httpPost.setEntity(ent);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        this.httpPost.addHeader("Accept", "text/xml");
        this.httpPost.addHeader("Content-Type", contentType);
        ResponseHandler<String> handler = new ResponseHandler<String>() { // from class: com.woodforest.services.Webservices.2
            @Override // org.apache.http.client.ResponseHandler
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                HttpEntity entity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 503) {
                    SessionExceptionDetail notAvailable = new SessionExceptionDetail();
                    notAvailable.setMessage(Webservices.this.getResources().getString(R.string.FeatureUnavailableMessage));
                    notAvailable.setSessionState(SessionState.Open);
                    return notAvailable.toXml();
                }
                if (response.getStatusLine().getStatusCode() == 404) {
                    SessionExceptionDetail notAvailable2 = new SessionExceptionDetail();
                    notAvailable2.setMessage(Webservices.this.getResources().getString(R.string.HtmlNotFoundError));
                    notAvailable2.setSessionState(SessionState.Open);
                    return notAvailable2.toXml();
                }
                if (response.getStatusLine().getStatusCode() == 403) {
                    SessionExceptionDetail notAvailable3 = new SessionExceptionDetail();
                    notAvailable3.setMessage(Webservices.this.getResources().getString(R.string.HtmlNotFoundError));
                    notAvailable3.setSessionState(SessionState.Open);
                    return notAvailable3.toXml();
                }
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
                return null;
            }
        };
        Log.v("POSTUrl", url);
        Log.v("POST", String.format("%1s : %2s", this.httpPost.getRequestLine().toString(), this.httpPost.getEntity().getContentType().getValue()));
        Log.v("POSTData", postData);
        Log.v("POSTDataSize", String.valueOf(postData.length()));
        String result = (String) this.httpclient.execute(this.httpPost, handler, this.localContext);
        Log.v("POSTResult", result);
        setCookies(this.cookieStore.getCookies());
        printCookies();
        return result;
    }

    private String doPost(String url, String postData) throws IOException, ClientProtocolException {
        return doPost(url, postData, "text/xml");
    }

    private void getCookies() {
    }

    private void setCookies(List<Cookie> cookies2) {
        this.cookies = cookies2;
        for (Cookie cookie : cookies2) {
            if (cookie.getName().equalsIgnoreCase("did")) {
                SharedPreferences settings = getSharedPreferences("WoodforestSettings", 0);
                SharedPreferences.Editor editor = settings.edit();
                try {
                    editor.putString("deviceToken", cookie.getValue());
                } catch (Exception e) {
                    Log.v("Error", "DT lost");
                } finally {
                    editor.commit();
                }
            }
        }
        CookieManager.getInstance().setAcceptCookie(true);
        if (this.cookies != null) {
            for (Cookie cookie2 : this.cookies) {
                String cookieString = String.valueOf(cookie2.getName()) + "=" + cookie2.getValue() + "; domain=" + cookie2.getDomain();
                CookieManager.getInstance().setCookie(cookie2.getDomain(), cookieString);
            }
        }
        CookieSyncManager.getInstance().sync();
    }

    private String doGet(String url) throws IOException, ClientProtocolException {
        this.httpGet = new HttpGet(url);
        this.httpGet.setHeader("User-Agent", getUserAgent());
        if (this.user_culture != "") {
            this.httpGet.addHeader("Accept-Language", this.user_culture);
        }
        ResponseHandler<String> handler = new ResponseHandler<String>() { // from class: com.woodforest.services.Webservices.3
            @Override // org.apache.http.client.ResponseHandler
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                HttpEntity entity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 503) {
                    SessionExceptionDetail notAvailable = new SessionExceptionDetail();
                    notAvailable.setMessage(Webservices.this.getResources().getString(R.string.FeatureUnavailableMessage));
                    notAvailable.setSessionState(SessionState.Open);
                    return notAvailable.toXml();
                }
                if (response.getStatusLine().getStatusCode() == 404) {
                    SessionExceptionDetail notAvailable2 = new SessionExceptionDetail();
                    notAvailable2.setMessage(Webservices.this.getResources().getString(R.string.HtmlNotFoundError));
                    notAvailable2.setSessionState(SessionState.Open);
                    return notAvailable2.toXml();
                }
                if (response.getStatusLine().getStatusCode() == 403) {
                    SessionExceptionDetail notAvailable3 = new SessionExceptionDetail();
                    notAvailable3.setMessage(Webservices.this.getResources().getString(R.string.HtmlNotFoundError));
                    notAvailable3.setSessionState(SessionState.Open);
                    return notAvailable3.toXml();
                }
                if (response.getStatusLine().getStatusCode() == 401.1d) {
                    SessionExceptionDetail notAvailable4 = new SessionExceptionDetail();
                    notAvailable4.setMessage(Webservices.this.getResources().getString(R.string.BillPayNotEnrolled));
                    notAvailable4.setSessionState(SessionState.Open);
                    return notAvailable4.toXml();
                }
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
                return null;
            }
        };
        Log.v("GETUrl", url);
        String result = (String) this.httpclient.execute(this.httpGet, handler, this.localContext);
        Log.v("GETResult", result);
        this.cookies = this.cookieStore.getCookies();
        printCookies();
        setCookies(this.cookieStore.getCookies());
        return result;
    }

    private InputStream doGetData(String url) throws IOException, ClientProtocolException {
        this.httpGet = new HttpGet(url);
        this.httpGet.setHeader("User-Agent", getUserAgent());
        if (this.user_culture != "") {
            this.httpGet.addHeader("Accept-Language", this.user_culture);
        }
        Log.v("GETUrlData", url);
        HttpResponse response = this.httpclient.execute(this.httpGet, this.localContext);
        this.cookies = this.cookieStore.getCookies();
        printCookies();
        setCookies(this.cookieStore.getCookies());
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        InputStream instream = bufHttpEntity.getContent();
        return instream;
    }

    protected Boolean hasSessionActive() {
        return true;
    }

    private void printCookies() {
        if (this.cookies != null) {
            for (Cookie c : this.cookies) {
                Object[] objArr = new Object[3];
                objArr[0] = c.getName();
                objArr[1] = c.getValue();
                objArr[2] = c.getExpiryDate() != null ? c.getExpiryDate().toString() : "N/A";
                Log.v("Cookie", String.format("%1$s, %2$s, %3$s", objArr));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCache() {
        synchronized (syncRoot) {
            this.pendingRequests.clear();
        }
        synchronized (cacheSyncRoot) {
            this.cachedData = new OnlineBankingProfileData();
        }
        synchronized (authSyncRoot) {
            this.authResponse = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getUserAgent() {
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return String.format("Mozilla/5.0 (Linux; U; Android %3$s; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3 WFB_Android/%1$s; UUID/%2$s", pinfo.versionName, Settings.Secure.getString(getContentResolver(), "android_id"), Build.VERSION.RELEASE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3";
        }
    }

    private boolean handleResponseFromServer(Ack response, OnlineBankingRequest r) {
        if (response.getSuccess()) {
            return true;
        }
        if (r.getCallback() != null) {
            r.getCallback().requestFailed(response);
        }
        return false;
    }
}
