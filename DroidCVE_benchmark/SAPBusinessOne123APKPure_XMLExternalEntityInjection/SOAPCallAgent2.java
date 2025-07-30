package b1.mobile.http.agent;

import android.util.Xml;
import b1.mobile.android.R;
import b1.mobile.http.client.SessionKickOffException;
import b1.mobile.http.client.TransactionClient;
import b1.mobile.http.interfaces.IHTTPCallListener;
import b1.mobile.util.MLog;
import b1.mobile.util.ResUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class SOAPCallAgent2 extends HTTPCallAgent {
    public static final String KILL_SESSION = "KillSession";
    String mSearializedXMLString;

    public SOAPCallAgent2() {
        this.mURL = TransactionClient.getURL();
    }

    public void call(String serializedXMLString, IHTTPCallListener listener) {
        this.mSearializedXMLString = serializedXMLString;
        super.call(listener);
    }

    @Override // b1.mobile.http.agent.HTTPCallAgent
    protected void doExecute() {
        TransactionClient.doConnect("POST", getConnection(), this);
    }

    @Override // b1.mobile.http.agent.HTTPCallAgent
    protected HttpURLConnection getConnection() {
        HttpURLConnection httpURLConnection = super.getConnection();
        httpURLConnection.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
        return httpURLConnection;
    }

    @Override // b1.mobile.http.agent.HTTPCallAgent, b1.mobile.http.interfaces.IHTTPInputOutput
    public void write(OutputStream out) throws IOException {
        out.write(this.mSearializedXMLString.getBytes(Charset.forName("UTF-8")));
    }

    @Override // b1.mobile.http.agent.HTTPCallAgent, b1.mobile.http.interfaces.IHTTPInputOutput
    public void read(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        this.mResponseBody = scanner.hasNext() ? scanner.next() : "";
    }

    @Override // b1.mobile.http.agent.HTTPCallAgent
    protected void processData() throws Throwable {
        this.mResponseBody = getJsonData();
        if (this.mResponseBody.isEmpty()) {
            throw new ResponseEmptyException(ResUtil.getStringRes(R.string.NO_DATA));
        }
    }

    private String getJsonData() throws SessionKickOffException {
        String text = "";
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(this.mResponseBody));
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 2 && parser.getName().equals("KillSession")) {
                    throw new SessionKickOffException();
                }
                if (eventType == 4) {
                    text = parser.getText().trim();
                    if (!text.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            MLog.e(ex, ex.getMessage(), new Object[0]);
        } catch (XmlPullParserException ex2) {
            MLog.e(ex2, ex2.getMessage(), new Object[0]);
        }
        return text;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0027, code lost:
    
        r3.next();
        r0 = r3.getText();
     */
    @Override // b1.mobile.http.agent.HTTPCallAgent
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void processInternalError() {
        /*
            r8 = this;
            r7 = 0
            java.lang.String r0 = ""
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()
            java.io.StringReader r5 = new java.io.StringReader     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            java.lang.String r6 = r8.mResponseBody     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            r5.<init>(r6)     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            r3.setInput(r5)     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            int r1 = r3.getEventType()     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
        L15:
            r5 = 1
            if (r1 == r5) goto L2e
            r5 = 2
            if (r1 != r5) goto L49
            java.lang.String r5 = r3.getName()     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            java.lang.String r6 = "reason"
            boolean r5 = r5.equals(r6)     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            if (r5 == 0) goto L49
            r3.next()     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            java.lang.String r0 = r3.getText()     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
        L2e:
            java.lang.String r5 = "Description: "
            int r4 = r0.indexOf(r5)
            r5 = -1
            if (r4 == r5) goto L48
            java.lang.String r5 = "Description: "
            int r5 = r5.length()
            int r5 = r5 + r4
            java.lang.String r5 = r0.substring(r5)
            java.lang.String r5 = r5.trim()
            r8.mResponseBody = r5
        L48:
            return
        L49:
            int r1 = r3.next()     // Catch: org.xmlpull.v1.XmlPullParserException -> L4e java.io.IOException -> L59
            goto L15
        L4e:
            r2 = move-exception
            java.lang.String r5 = r2.getMessage()
            java.lang.Object[] r6 = new java.lang.Object[r7]
            b1.mobile.util.MLog.e(r2, r5, r6)
            goto L2e
        L59:
            r2 = move-exception
            java.lang.String r5 = r2.getMessage()
            java.lang.Object[] r6 = new java.lang.Object[r7]
            b1.mobile.util.MLog.e(r2, r5, r6)
            goto L2e
        */
        throw new UnsupportedOperationException("Method not decompiled: b1.mobile.http.agent.SOAPCallAgent2.processInternalError():void");
    }
}
