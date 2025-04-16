package com.revolutionmoney.moneyexchange;

import com.google.myproject.JsonParseException;
import com.revolutionmoney.moneyexchange.Interfaces;
import com.revolutionmoney.moneyexchange.payload.AcceptPendingTransactionRequest;
import com.revolutionmoney.moneyexchange.payload.AccessBillPayRequest;
import com.revolutionmoney.moneyexchange.payload.ActivateCardRequest;
import com.revolutionmoney.moneyexchange.payload.AddFundsRequestV2;
import com.revolutionmoney.moneyexchange.payload.AddPayeeRequest;
import com.revolutionmoney.moneyexchange.payload.AuthenticateUserRequest;
import com.revolutionmoney.moneyexchange.payload.AuthenticateUserWithEmailRequest;
import com.revolutionmoney.moneyexchange.payload.AuthenticateUserWithSecurityAnswerRequest;
import com.revolutionmoney.moneyexchange.payload.AuthorizeCheckRequest;
import com.revolutionmoney.moneyexchange.payload.CDIAuthenticateBySmsFinishRequest;
import com.revolutionmoney.moneyexchange.payload.CDIAuthenticateBySmsRequest;
import com.revolutionmoney.moneyexchange.payload.CDIAuthenticationTypesRequest;
import com.revolutionmoney.moneyexchange.payload.CDIImportAccountWithSubscriberChallengeRequest;
import com.revolutionmoney.moneyexchange.payload.CDIImportCredentialsWithSubscriberChallengeRequest;
import com.revolutionmoney.moneyexchange.payload.CDILinkFundingSourcesRequest;
import com.revolutionmoney.moneyexchange.payload.CDISetSolidAndSubscriberDataSourceFieldsRequest;
import com.revolutionmoney.moneyexchange.payload.CancelPendingTransactionRequest;
import com.revolutionmoney.moneyexchange.payload.CheckEligibilityRequest;
import com.revolutionmoney.moneyexchange.payload.ConfirmTransactionRequest;
import com.revolutionmoney.moneyexchange.payload.DeleteBankAccountRequestV2;
import com.revolutionmoney.moneyexchange.payload.DepositCheckRequest;
import com.revolutionmoney.moneyexchange.payload.GetAccountDetailsRequest;
import com.revolutionmoney.moneyexchange.payload.GetAccountInfoRequest;
import com.revolutionmoney.moneyexchange.payload.GetAddressesRequest;
import com.revolutionmoney.moneyexchange.payload.GetChildTransactionsRequest;
import com.revolutionmoney.moneyexchange.payload.GetCitiesRequest;
import com.revolutionmoney.moneyexchange.payload.GetContactsRequest;
import com.revolutionmoney.moneyexchange.payload.GetDealsAndOffersRequest;
import com.revolutionmoney.moneyexchange.payload.GetFeatureRequest;
import com.revolutionmoney.moneyexchange.payload.GetFeeRequestV2;
import com.revolutionmoney.moneyexchange.payload.GetFullDetailOnSavingsObjectRequest;
import com.revolutionmoney.moneyexchange.payload.GetParentAccountInfoRequest;
import com.revolutionmoney.moneyexchange.payload.GetPendedMessageRequest;
import com.revolutionmoney.moneyexchange.payload.GetPublicKeyRequest;
import com.revolutionmoney.moneyexchange.payload.GetSubAccountTransactionsRequest;
import com.revolutionmoney.moneyexchange.payload.GetTransactionsRequest;
import com.revolutionmoney.moneyexchange.payload.GetUpdateCheckerInfoRequest;
import com.revolutionmoney.moneyexchange.payload.LinkBankAccountRequest;
import com.revolutionmoney.moneyexchange.payload.LinkCreditCardRequestV2;
import com.revolutionmoney.moneyexchange.payload.LinkPhoneOrEmailRequest;
import com.revolutionmoney.moneyexchange.payload.LogErrorRequestV2;
import com.revolutionmoney.moneyexchange.payload.LogoutMobileUserRequest;
import com.revolutionmoney.moneyexchange.payload.MakePaymentRequest;
import com.revolutionmoney.moneyexchange.payload.ManagePDARequest;
import com.revolutionmoney.moneyexchange.payload.NegotiatePendingTransactionRequest;
import com.revolutionmoney.moneyexchange.payload.PerformActionOnDealRequest;
import com.revolutionmoney.moneyexchange.payload.PerformActionOnOfferRequest;
import com.revolutionmoney.moneyexchange.payload.PerformActionOnVoucherRequest;
import com.revolutionmoney.moneyexchange.payload.ReferAFriendRequest;
import com.revolutionmoney.moneyexchange.payload.RegisterForNotificationsRequest;
import com.revolutionmoney.moneyexchange.payload.RegisterSubaccountRequest;
import com.revolutionmoney.moneyexchange.payload.RegisterUserRequestV2;
import com.revolutionmoney.moneyexchange.payload.RegisterUserWithSSNRequestV2;
import com.revolutionmoney.moneyexchange.payload.RequestMoneyRequest;
import com.revolutionmoney.moneyexchange.payload.SearchPayeeByNameRequest;
import com.revolutionmoney.moneyexchange.payload.SelfServiceRequest;
import com.revolutionmoney.moneyexchange.payload.SendMoneyBasicRequestV2;
import com.revolutionmoney.moneyexchange.payload.SendMoneyRequest;
import com.revolutionmoney.moneyexchange.payload.SendVerificationRequest;
import com.revolutionmoney.moneyexchange.payload.SubmitKBAAnswersRequest;
import com.revolutionmoney.moneyexchange.payload.SubscribeCapabilityRequest;
import com.revolutionmoney.moneyexchange.payload.TransferMoneyRequest;
import com.revolutionmoney.moneyexchange.payload.UpdateSessionTimeoutRequest;
import com.revolutionmoney.moneyexchange.payload.UpgradeWithSSNRequest;
import com.revolutionmoney.moneyexchange.payload.ValidateAddressRequestV2;
import com.revolutionmoney.moneyexchange.payload.ValidateBankAccountRequest;
import com.revolutionmoney.moneyexchange.payload.ValidatePhoneOrEmailLinkRequest;
import com.revolutionmoney.moneyexchange.payload.ValidateSecurityAnswerRequest;
import com.revolutionmoney.moneyexchange.payload.ValidateStarterCardRequestV2;
import com.revolutionmoney.moneyexchange.payload.VelocityCheckRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class WebserviceClient {
    private static final String SERVE_CERTIFICATE_HN = "svcmob.servevirtual.net";
    private static Map<Class, String> routes = new HashMap();
    private String carrier;
    private String deviceId;
    private JsonSerializer jd;
    private Interfaces.ILogger logger;
    private String phoneNumber;
    private String urlBase;

    /* loaded from: classes.dex */
    public static class NullLogger implements Interfaces.ILogger {
        @Override // com.revolutionmoney.moneyexchange.Interfaces.ILogger
        public void debug(String str) {
        }

        @Override // com.revolutionmoney.moneyexchange.Interfaces.ILogger
        public void error(String str) {
        }

        @Override // com.revolutionmoney.moneyexchange.Interfaces.ILogger
        public void info(String str) {
        }

        @Override // com.revolutionmoney.moneyexchange.Interfaces.ILogger
        public void warn(String str) {
        }
    }

    static {
        routes.put(GetPublicKeyRequest.class, "V1/PublicKey/Get");
        routes.put(AuthenticateUserRequest.class, "V1/User/Authenticate");
        routes.put(AuthenticateUserWithEmailRequest.class, "V1/User/AuthenticateWithEmail");
        routes.put(GetSubAccountTransactionsRequest.class, "V1/SubAccountTransactions/Get");
        routes.put(ReferAFriendRequest.class, "V1/Friend/Refer");
        routes.put(SendMoneyRequest.class, "V1/Money/Send");
        routes.put(TransferMoneyRequest.class, "V1/Money/Transfer");
        routes.put(RequestMoneyRequest.class, "V1/Money/Request");
        routes.put(AcceptPendingTransactionRequest.class, "V1/PendingTransaction/Accept");
        routes.put(CancelPendingTransactionRequest.class, "V1/PendingTransaction/Cancel");
        routes.put(NegotiatePendingTransactionRequest.class, "V1/PendingTransaction/Negotiate");
        routes.put(LogoutMobileUserRequest.class, "V1/User/Logout");
        routes.put(UpdateSessionTimeoutRequest.class, "V1/SessionTimeout/Update");
        routes.put(ValidatePhoneOrEmailLinkRequest.class, "V1/PhoneOrEmail/Validate");
        routes.put(LinkPhoneOrEmailRequest.class, "V1/PhoneOrEmail/Link");
        routes.put(GetAddressesRequest.class, "V1/Addresses/Get");
        routes.put(LogErrorRequestV2.class, "V2/Error/Log");
        routes.put(RegisterForNotificationsRequest.class, "V1/User/RegisterForNotifications");
        routes.put(GetContactsRequest.class, "V1/User/GetContacts");
        routes.put(GetParentAccountInfoRequest.class, "V1/ParentAccountInfo/Get");
        routes.put(GetUpdateCheckerInfoRequest.class, "V1/UpdateCheckerInfo/Get");
        routes.put(GetPendedMessageRequest.class, "V1/Pended/GetMessage");
        routes.put(SendVerificationRequest.class, "V1/Verification/Send");
        routes.put(SelfServiceRequest.class, "V1/User/SelfService");
        routes.put(AddFundsRequestV2.class, "V2/Funds/Add");
        routes.put(LinkCreditCardRequestV2.class, "V2/CreditCard/Link");
        routes.put(SendMoneyBasicRequestV2.class, "V2/Money/SendBasic");
        routes.put(AuthenticateUserWithSecurityAnswerRequest.class, "V2/User/AuthenticateWithSecurityAnswer");
        routes.put(GetAccountInfoRequest.class, "V2/AccountInfo/Get");
        routes.put(GetTransactionsRequest.class, "V2/Transactions/Get");
        routes.put(LinkBankAccountRequest.class, "V2/BankAccount/Link");
        routes.put(RegisterSubaccountRequest.class, "V2/Subaccount/Register");
        routes.put(RegisterUserRequestV2.class, "V2/User/Register");
        routes.put(RegisterUserWithSSNRequestV2.class, "V2/User/RegisterWithSSN");
        routes.put(SubmitKBAAnswersRequest.class, "V2/KBAAnswers/Submit");
        routes.put(UpgradeWithSSNRequest.class, "V2/User/UpgradeWithSSN");
        routes.put(ValidateBankAccountRequest.class, "V2/BankAccount/Validate");
        routes.put(ValidateSecurityAnswerRequest.class, "V2/User/ValidateSecurityAnswer");
        routes.put(GetFeeRequestV2.class, "V2/Fee/Get");
        routes.put(ConfirmTransactionRequest.class, "V2/Money/Confirm");
        routes.put(DeleteBankAccountRequestV2.class, "V2/BankAccount/Delete");
        routes.put(GetDealsAndOffersRequest.class, "V1/DealsAndOffers/Get");
        routes.put(GetFullDetailOnSavingsObjectRequest.class, "V1/SavingsObject/GetFullDetail");
        routes.put(GetCitiesRequest.class, "V1/Cities/Get");
        routes.put(PerformActionOnDealRequest.class, "V1/Deal/PerformAction");
        routes.put(PerformActionOnOfferRequest.class, "V1/Offer/PerformAction");
        routes.put(PerformActionOnVoucherRequest.class, "V1/Voucher/PerformAction");
        routes.put(ValidateAddressRequestV2.class, "V2/User/ValidateAddress");
        routes.put(ValidateStarterCardRequestV2.class, "V2/User/ValidateStarterCard");
        routes.put(SearchPayeeByNameRequest.class, "V1/BillPay/SearchByName");
        routes.put(AddPayeeRequest.class, "V1/BillPay/Add");
        routes.put(MakePaymentRequest.class, "V1/BillPay/Pay");
        routes.put(AccessBillPayRequest.class, "V1/BillPay/Access");
        routes.put(DepositCheckRequest.class, "V1/RemoteDepositCapture/DepositCheck");
        routes.put(SubscribeCapabilityRequest.class, "V1/RemoteDepositCapture/Subscribe");
        routes.put(VelocityCheckRequest.class, "V1/RemoteDepositCapture/VelocityCheck");
        routes.put(CheckEligibilityRequest.class, "V1/RemoteDepositCapture/CheckEligibility");
        routes.put(AuthorizeCheckRequest.class, "V1/AuthorizeCheck");
        routes.put(CDIAuthenticationTypesRequest.class, "V1/CDI/GetAuthenticationTypes");
        routes.put(CDIAuthenticateBySmsRequest.class, "V1/CDI/AuthenticateBySms");
        routes.put(CDIAuthenticateBySmsFinishRequest.class, "V1/CDI/AuthenticateBySmsFinish");
        routes.put(CDIImportAccountWithSubscriberChallengeRequest.class, "V1/CDI/ImportAccountWithSubscriberChallenge");
        routes.put(CDIImportCredentialsWithSubscriberChallengeRequest.class, "V1/CDI/ImportCredentialsWithSubscriberChallenge");
        routes.put(CDILinkFundingSourcesRequest.class, "V1/CDI/LinkFundingSources");
        routes.put(CDISetSolidAndSubscriberDataSourceFieldsRequest.class, "V1/CDI/SetSolidAndSubscriberDataSourceFields");
        routes.put(ActivateCardRequest.class, "V1/ActivateCard");
        routes.put(GetFeatureRequest.class, "V1/Features/Get");
        routes.put(ManagePDARequest.class, "V1/PDAccount/Manage");
        routes.put(GetChildTransactionsRequest.class, "V1/ChildTransactions/Get");
        routes.put(GetAccountDetailsRequest.class, "V1/AccountDetails/Get");
    }

    public WebserviceClient(String str) {
        this(str, null, null, null, new NullLogger());
    }

    public WebserviceClient(String str, Interfaces.ILogger iLogger) {
        this(str, null, null, null, iLogger);
    }

    public WebserviceClient(String str, String str2, String str3, String str4, Interfaces.ILogger iLogger) {
        this.jd = new JsonSerializer();
        this.urlBase = str;
        this.logger = iLogger;
        this.carrier = str4 == null ? "" : str4;
        this.phoneNumber = str2 == null ? "" : str2;
        this.deviceId = str3 == null ? "" : str3;
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.revolutionmoney.moneyexchange.WebserviceClient.1
            @Override // javax.net.ssl.HostnameVerifier
            public boolean verify(String str5, SSLSession sSLSession) {
                return str5.equals(sSLSession.getPeerHost());
            }
        });
    }

    private String executeRequest(String str, Class<?> cls) throws IOException {
        HttpsURLConnection httpsURLConnection;
        HttpsURLConnection httpsURLConnection2 = null;
        URL url = new URL(this.urlBase + getRoute(cls));
        if (url.getHost().equalsIgnoreCase("svcmobd1.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobd2.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobd3.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobq1.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobq2.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobq3.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobu0.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobu1.servevirtual.net") || url.getHost().equalsIgnoreCase("svcmobu2.servevirtual.net") || url.getHost().equalsIgnoreCase("mobgateq1.servevirtual.net") || url.getHost().equalsIgnoreCase("10.88.193.115") || url.getHost().equalsIgnoreCase("svcmobstage.servevirtual.net")) {
            trustAllConnections(false);
        } else if (url.getHost().equalsIgnoreCase(SERVE_CERTIFICATE_HN)) {
            trustAllConnections(true);
        }
        try {
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
        } catch (Throwable th) {
            th = th;
        }
        try {
            prepHeadersForMobileProxy(httpsURLConnection);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setConnectTimeout(90000);
            httpsURLConnection.connect();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
            outputStreamWriter.write(str);
            outputStreamWriter.flush();
            String readResponse = readResponse(httpsURLConnection);
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
            return readResponse;
        } catch (Throwable th2) {
            httpsURLConnection2 = httpsURLConnection;
            th = th2;
            if (httpsURLConnection2 != null) {
                httpsURLConnection2.disconnect();
            }
            throw th;
        }
    }

    private static String getRoute(Class cls) {
        return routes.get(cls);
    }

    private void prepHeadersForMobileProxy(HttpsURLConnection httpsURLConnection) {
        httpsURLConnection.setRequestProperty("DeviceId", this.deviceId);
        httpsURLConnection.setRequestProperty("PhoneNumber", this.phoneNumber);
        httpsURLConnection.setRequestProperty("Carrier", this.carrier);
    }

    private String readResponse(HttpURLConnection httpURLConnection) throws IOException {
        StringBuilder sb = new StringBuilder();
        int responseCode = httpURLConnection.getResponseCode();
        String headerField = httpURLConnection.getHeaderField("Content-Type");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            sb.append(readLine).append("\n");
        }
        if (responseCode != 200) {
            throw new IOException(String.format("Bad HTTP response code: %d\nOr MIME type: %s", Integer.valueOf(responseCode), headerField));
        }
        return sb.toString();
    }

    public static void trustAllConnections(final boolean z) {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.revolutionmoney.moneyexchange.WebserviceClient.2
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String str, SSLSession sSLSession) {
                    if (z) {
                        return WebserviceClient.SERVE_CERTIFICATE_HN.equals(sSLSession.getPeerHost());
                    }
                    return true;
                }
            });
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(null, new X509TrustManager[]{new X509TrustManager() { // from class: com.revolutionmoney.moneyexchange.WebserviceClient.3
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <R, T> T getResponse(R r, Class<T> cls) throws IOException, JsonParseException {
        String serialize = this.jd.serialize(r);
        this.logger.info(String.format("request %s %s", r.getClass(), serialize));
        String executeRequest = executeRequest(serialize, r.getClass());
        this.logger.info(String.format("response %s", executeRequest));
        return (T) this.jd.deserialize(executeRequest, (Class) cls);
    }

    public <R, T> T getResponse(String str, R r, Class<T> cls) throws IOException, JsonParseException {
        String str2 = this.urlBase;
        this.urlBase = str;
        this.logger.info(String.format("Switching to URL %s ", this.urlBase));
        T t = (T) getResponse(r, cls);
        this.urlBase = str2;
        this.logger.info(String.format("Switching to URL back to %s ", this.urlBase));
        return t;
    }
}
