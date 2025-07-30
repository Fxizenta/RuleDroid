package com.nextcloud.talk.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.exoplayer2.source.rtsp.SessionDescription;
import com.nextcloud.talk.activities.CallActivity;
import com.nextcloud.talk.adapters.ParticipantDisplayItem;
import com.nextcloud.talk.adapters.ParticipantsAdapter;
import com.nextcloud.talk.api.NcApi;
import com.nextcloud.talk.application.NextcloudTalkApplication;
import com.nextcloud.talk.controllers.AccountVerificationController;
import com.nextcloud.talk.databinding.CallActivityBinding;
import com.nextcloud.talk.events.ConfigurationChangeEvent;
import com.nextcloud.talk.events.MediaStreamEvent;
import com.nextcloud.talk.events.NetworkEvent;
import com.nextcloud.talk.events.PeerConnectionEvent;
import com.nextcloud.talk.events.SessionDescriptionSendEvent;
import com.nextcloud.talk.events.WebSocketCommunicationEvent;
import com.nextcloud.talk.models.ExternalSignalingServer;
import com.nextcloud.talk.models.database.UserEntity;
import com.nextcloud.talk.models.json.capabilities.CapabilitiesOverall;
import com.nextcloud.talk.models.json.conversations.Conversation;
import com.nextcloud.talk.models.json.conversations.RoomOverall;
import com.nextcloud.talk.models.json.conversations.RoomsOverall;
import com.nextcloud.talk.models.json.generic.GenericOverall;
import com.nextcloud.talk.models.json.participants.Participant;
import com.nextcloud.talk.models.json.participants.ParticipantsOverall;
import com.nextcloud.talk.models.json.signaling.DataChannelMessage;
import com.nextcloud.talk.models.json.signaling.DataChannelMessageNick;
import com.nextcloud.talk.models.json.signaling.NCIceCandidate;
import com.nextcloud.talk.models.json.signaling.NCMessagePayload;
import com.nextcloud.talk.models.json.signaling.NCMessageWrapper;
import com.nextcloud.talk.models.json.signaling.NCSignalingMessage;
import com.nextcloud.talk.models.json.signaling.Signaling;
import com.nextcloud.talk.models.json.signaling.SignalingOverall;
import com.nextcloud.talk.models.json.signaling.settings.IceServer;
import com.nextcloud.talk.models.json.signaling.settings.SignalingSettingsOverall;
import com.nextcloud.talk.ui.dialog.AudioOutputDialog;
import com.nextcloud.talk.utils.ApiUtils;
import com.nextcloud.talk.utils.DisplayUtils;
import com.nextcloud.talk.utils.NotificationUtils;
import com.nextcloud.talk.utils.animations.PulseAnimation;
import com.nextcloud.talk.utils.bundle.BundleKeys;
import com.nextcloud.talk.utils.database.user.UserUtils;
import com.nextcloud.talk.utils.power.PowerManagerUtils;
import com.nextcloud.talk.utils.preferences.AppPreferences;
import com.nextcloud.talk.utils.singletons.ApplicationWideCurrentRoomHolder;
import com.nextcloud.talk.webrtc.Globals;
import com.nextcloud.talk.webrtc.MagicAudioManager;
import com.nextcloud.talk.webrtc.MagicWebRTCUtils;
import com.nextcloud.talk.webrtc.MagicWebSocketInstance;
import com.nextcloud.talk.webrtc.PeerConnectionWrapper;
import com.nextcloud.talk.webrtc.WebSocketConnectionHelper;
import com.nextcloud.talk2.R;
import com.wooplr.spotlight.SpotlightView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import me.zhanghai.android.effortlesspermissions.OpenAppDetailsDialogFragment;
import okhttp3.Cache;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcel;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import pub.devrel.easypermissions.AfterPermissionGranted;

/* loaded from: classes2.dex */
public class CallActivity extends CallBaseActivity {
    private static final String MICROPHONE_PIP_INTENT_EXTRA_ACTION = "microphone_pip_action";
    private static final String MICROPHONE_PIP_INTENT_NAME = "microphone_pip_intent";
    private static final int MICROPHONE_PIP_REQUEST_MUTE = 1;
    private static final int MICROPHONE_PIP_REQUEST_UNMUTE = 2;
    private static final String[] PERMISSIONS_CALL = {"android.permission.CAMERA", "android.permission.RECORD_AUDIO"};
    private static final String[] PERMISSIONS_CAMERA = {"android.permission.CAMERA"};
    private static final String[] PERMISSIONS_MICROPHONE = {"android.permission.RECORD_AUDIO"};
    public static final String TAG = "CallActivity";
    public static final String VIDEO_STREAM_TYPE_SCREEN = "screen";
    public static final String VIDEO_STREAM_TYPE_VIDEO = "video";

    @Inject
    AppPreferences appPreferences;
    private MediaConstraints audioConstraints;
    public MagicAudioManager audioManager;
    private AudioOutputDialog audioOutputDialog;
    private AudioSource audioSource;
    private String baseUrl;
    private CallActivityBinding binding;

    @Inject
    Cache cache;
    private String callSession;
    private CameraEnumerator cameraEnumerator;
    private String conversationName;
    private String conversationPassword;
    private UserEntity conversationUser;
    private String credentials;
    private CallStatus currentCallStatus;

    @Inject
    EventBus eventBus;
    private ExternalSignalingServer externalSignalingServer;
    private Handler handler;
    private boolean hasExternalSignalingServer;
    private boolean hasMCU;
    private List<PeerConnection.IceServer> iceServers;
    private boolean isIncomingCallFromNotification;
    private boolean isVoiceOnlyCall;
    private AudioTrack localAudioTrack;
    private MediaStream localStream;
    private VideoTrack localVideoTrack;
    private BroadcastReceiver mReceiver;
    private MediaPlayer mediaPlayer;

    @Inject
    NcApi ncApi;
    private Map<String, ParticipantDisplayItem> participantDisplayItems;
    private ParticipantsAdapter participantsAdapter;
    private PeerConnectionFactory peerConnectionFactory;
    private PowerManagerUtils powerManagerUtils;
    private PulseAnimation pulseAnimation;
    private String roomId;
    private String roomToken;
    private EglBase rootEglBase;
    private MediaConstraints sdpConstraints;
    private MediaConstraints sdpConstraintsForMCU;
    private Disposable signalingDisposable;
    private SpotlightView spotlightView;

    @Inject
    UserUtils userUtils;
    private VideoCapturer videoCapturer;
    private MediaConstraints videoConstraints;
    private VideoSource videoSource;
    private MagicWebSocketInstance webSocketClient;
    private WebSocketConnectionHelper webSocketConnectionHelper;
    private List<PeerConnectionWrapper> peerConnectionWrapperList = new ArrayList();
    private Map<String, Participant> participantMap = new HashMap();
    private boolean videoOn = false;
    private boolean microphoneOn = false;
    private Handler callControlHandler = new Handler();
    private Handler callInfosHandler = new Handler();
    private Handler cameraSwitchHandler = new Handler();
    private boolean isPTTActive = false;

    @Parcel
    /* loaded from: classes2.dex */
    public enum CallStatus {
        CONNECTING,
        CALLING_TIMEOUT,
        JOINED,
        IN_CONVERSATION,
        RECONNECTING,
        OFFLINE,
        LEAVING,
        PUBLISHER_FAILED
    }

    @Override // com.nextcloud.talk.activities.CallBaseActivity, com.nextcloud.talk.activities.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Log.d(TAG, "onCreate");
        super.onCreate(bundle);
        NextcloudTalkApplication.INSTANCE.getSharedApplication().getComponentApplication().inject(this);
        CallActivityBinding inflate = CallActivityBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        hideNavigationIfNoPipAvailable();
        Bundle extras = getIntent().getExtras();
        this.roomId = extras.getString(BundleKeys.INSTANCE.getKEY_ROOM_ID(), "");
        this.roomToken = extras.getString(BundleKeys.INSTANCE.getKEY_ROOM_TOKEN(), "");
        this.conversationUser = (UserEntity) extras.getParcelable(BundleKeys.INSTANCE.getKEY_USER_ENTITY());
        this.conversationPassword = extras.getString(BundleKeys.INSTANCE.getKEY_CONVERSATION_PASSWORD(), "");
        this.conversationName = extras.getString(BundleKeys.INSTANCE.getKEY_CONVERSATION_NAME(), "");
        this.isVoiceOnlyCall = extras.getBoolean(BundleKeys.INSTANCE.getKEY_CALL_VOICE_ONLY(), false);
        if (extras.containsKey(BundleKeys.INSTANCE.getKEY_FROM_NOTIFICATION_START_CALL())) {
            this.isIncomingCallFromNotification = extras.getBoolean(BundleKeys.INSTANCE.getKEY_FROM_NOTIFICATION_START_CALL());
        }
        this.credentials = ApiUtils.getCredentials(this.conversationUser.getUsername(), this.conversationUser.getToken());
        String string = extras.getString(BundleKeys.INSTANCE.getKEY_MODIFIED_BASE_URL(), "");
        this.baseUrl = string;
        if (TextUtils.isEmpty(string)) {
            this.baseUrl = this.conversationUser.getBaseUrl();
        }
        this.powerManagerUtils = new PowerManagerUtils();
        if (extras.getString("state", "").equalsIgnoreCase("resume")) {
            setCallState(CallStatus.IN_CONVERSATION);
        } else {
            setCallState(CallStatus.CONNECTING);
        }
        initClickListeners();
        this.binding.microphoneButton.setOnTouchListener(new MicrophoneButtonTouchListener());
        this.pulseAnimation = PulseAnimation.create().with(this.binding.microphoneButton).setDuration(310).setRepeatCount(-1).setRepeatMode(2);
        basicInitialization();
        this.participantDisplayItems = new HashMap();
        initViews();
        if (!isConnectionEstablished()) {
            initiateCall();
        }
        updateSelfVideoViewPosition();
    }

    @Override // com.nextcloud.talk.activities.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        try {
            this.cache.evictAll();
        } catch (IOException unused) {
            Log.e(TAG, "Failed to evict cache");
        }
    }

    private void initClickListeners() {
        this.binding.pictureInPictureButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$FIxuByLZKVBt4iGDKcPzVg6bL3I
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$0$CallActivity(view);
            }
        });
        this.binding.audioOutputButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$b2jWK88yEwheeaeN21bqNZTQXxk
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$1$CallActivity(view);
            }
        });
        this.binding.microphoneButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$VkpFuePGTqCCL0EISlrxyfYII6A
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$2$CallActivity(view);
            }
        });
        this.binding.microphoneButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$uSR1pZvPcw12q5KcLtuQYPpcSzQ
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return CallActivity.this.lambda$initClickListeners$3$CallActivity(view);
            }
        });
        this.binding.cameraButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$zSOTSh2bgBFlRniVxW4IpzXfP9M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$4$CallActivity(view);
            }
        });
        this.binding.hangupButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$KBPJxEyZFxDcmgQk-vz0yb2aFZY
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$5$CallActivity(view);
            }
        });
        this.binding.switchSelfVideoButton.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$TNhjLk6rPI3md9ER136kflWIQVI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$6$CallActivity(view);
            }
        });
        this.binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$_-73JN9O-Y6E_m6BPd9Gi3sizw4
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                CallActivity.this.lambda$initClickListeners$7$CallActivity(adapterView, view, i, j);
            }
        });
        this.binding.callStates.callStateRelativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$yFfLEju16lavbNIsLcYu54BOS4k
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallActivity.this.lambda$initClickListeners$8$CallActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initClickListeners$0$CallActivity(View view) {
        enterPipMode();
    }

    public /* synthetic */ void lambda$initClickListeners$1$CallActivity(View view) {
        AudioOutputDialog audioOutputDialog = new AudioOutputDialog(this);
        this.audioOutputDialog = audioOutputDialog;
        audioOutputDialog.show();
    }

    public /* synthetic */ void lambda$initClickListeners$2$CallActivity(View view) {
        onMicrophoneClick();
    }

    public /* synthetic */ boolean lambda$initClickListeners$3$CallActivity(View view) {
        if (!this.microphoneOn) {
            this.callControlHandler.removeCallbacksAndMessages(null);
            this.callInfosHandler.removeCallbacksAndMessages(null);
            this.cameraSwitchHandler.removeCallbacksAndMessages(null);
            this.isPTTActive = true;
            this.binding.callControls.setVisibility(0);
            if (!this.isVoiceOnlyCall) {
                this.binding.switchSelfVideoButton.setVisibility(0);
            }
        }
        onMicrophoneClick();
        return true;
    }

    public /* synthetic */ void lambda$initClickListeners$4$CallActivity(View view) {
        onCameraClick();
    }

    public /* synthetic */ void lambda$initClickListeners$5$CallActivity(View view) {
        setCallState(CallStatus.LEAVING);
        hangup(true);
    }

    public /* synthetic */ void lambda$initClickListeners$6$CallActivity(View view) {
        switchCamera();
    }

    public /* synthetic */ void lambda$initClickListeners$7$CallActivity(AdapterView adapterView, View view, int i, long j) {
        animateCallControls(true, 0L);
    }

    public /* synthetic */ void lambda$initClickListeners$8$CallActivity(View view) {
        if (this.currentCallStatus.equals(CallStatus.CALLING_TIMEOUT)) {
            setCallState(CallStatus.RECONNECTING);
            hangupNetworkCalls(false);
        }
    }

    private void createCameraEnumerator() {
        boolean z;
        try {
            z = Camera2Enumerator.isSupported(this);
        } catch (Throwable th) {
            Log.w(TAG, "Camera2Enumerator threw an error", th);
            z = false;
        }
        if (z) {
            this.cameraEnumerator = new Camera2Enumerator(this);
        } else {
            this.cameraEnumerator = new Camera1Enumerator(MagicWebRTCUtils.shouldEnableVideoHardwareAcceleration());
        }
    }

    private void basicInitialization() {
        this.rootEglBase = EglBase.CC.create();
        createCameraEnumerator();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        this.peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).setVideoEncoderFactory(new DefaultVideoEncoderFactory(this.rootEglBase.getEglBaseContext(), true, true)).setVideoDecoderFactory(new DefaultVideoDecoderFactory(this.rootEglBase.getEglBaseContext())).createPeerConnectionFactory();
        this.audioConstraints = new MediaConstraints();
        this.videoConstraints = new MediaConstraints();
        this.localStream = this.peerConnectionFactory.createLocalMediaStream("NCMS");
        this.audioManager = MagicAudioManager.create(getApplicationContext(), this.isVoiceOnlyCall);
        Log.d(TAG, "Starting the audio manager...");
        this.audioManager.start(new MagicAudioManager.AudioManagerListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$pr2x1lCMdZCb0kTBpVvzJq7CMR8
            @Override // com.nextcloud.talk.webrtc.MagicAudioManager.AudioManagerListener
            public final void onAudioDeviceChanged(MagicAudioManager.AudioDevice audioDevice, Set set) {
                CallActivity.this.onAudioManagerDevicesChanged(audioDevice, set);
            }
        });
        if (this.isVoiceOnlyCall) {
            setAudioOutputChannel(MagicAudioManager.AudioDevice.EARPIECE);
        } else {
            setAudioOutputChannel(MagicAudioManager.AudioDevice.SPEAKER_PHONE);
        }
        this.iceServers = new ArrayList();
        this.sdpConstraints = new MediaConstraints();
        this.sdpConstraintsForMCU = new MediaConstraints();
        this.sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", BooleanUtils.TRUE));
        this.sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", this.isVoiceOnlyCall ? BooleanUtils.FALSE : BooleanUtils.TRUE));
        this.sdpConstraintsForMCU.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", BooleanUtils.FALSE));
        this.sdpConstraintsForMCU.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", BooleanUtils.FALSE));
        this.sdpConstraintsForMCU.optional.add(new MediaConstraints.KeyValuePair("internalSctpDataChannels", BooleanUtils.TRUE));
        this.sdpConstraintsForMCU.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", BooleanUtils.TRUE));
        this.sdpConstraints.optional.add(new MediaConstraints.KeyValuePair("internalSctpDataChannels", BooleanUtils.TRUE));
        this.sdpConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", BooleanUtils.TRUE));
        if (!this.isVoiceOnlyCall) {
            cameraInitialization();
        }
        microphoneInitialization();
    }

    public void setAudioOutputChannel(MagicAudioManager.AudioDevice audioDevice) {
        MagicAudioManager magicAudioManager = this.audioManager;
        if (magicAudioManager != null) {
            magicAudioManager.selectAudioDevice(audioDevice);
            updateAudioOutputButton(this.audioManager.getCurrentAudioDevice());
        }
    }

    private void updateAudioOutputButton(MagicAudioManager.AudioDevice audioDevice) {
        int i = AnonymousClass19.$SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice[audioDevice.ordinal()];
        if (i == 1) {
            this.binding.audioOutputButton.getHierarchy().setPlaceholderImage(AppCompatResources.getDrawable(this.context, R.drawable.ic_baseline_bluetooth_audio_24));
        } else if (i == 2) {
            this.binding.audioOutputButton.getHierarchy().setPlaceholderImage(AppCompatResources.getDrawable(this.context, R.drawable.ic_volume_up_white_24dp));
        } else if (i == 3) {
            this.binding.audioOutputButton.getHierarchy().setPlaceholderImage(AppCompatResources.getDrawable(this.context, R.drawable.ic_baseline_phone_in_talk_24));
        } else if (i == 4) {
            this.binding.audioOutputButton.getHierarchy().setPlaceholderImage(AppCompatResources.getDrawable(this.context, R.drawable.ic_baseline_headset_mic_24));
        } else {
            Log.e(TAG, "Icon for audio output not available");
        }
        DrawableCompat.setTint(this.binding.audioOutputButton.getDrawable(), -1);
    }

    private void handleFromNotification() {
        this.ncApi.getRooms(this.credentials, ApiUtils.getUrlForRooms(ApiUtils.getConversationApiVersion(this.conversationUser, new int[]{4, 1}), this.baseUrl)).retry(3L).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<RoomsOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.1
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(RoomsOverall roomsOverall) {
                Iterator<Conversation> it = roomsOverall.getOcs().getData().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Conversation next = it.next();
                    if (CallActivity.this.roomId.equals(next.getRoomId())) {
                        CallActivity.this.roomToken = next.getToken();
                        break;
                    }
                }
                CallActivity.this.checkPermissions();
            }
        });
    }

    private void initViews() {
        Log.d(TAG, "initViews");
        this.binding.callInfosLinearLayout.setVisibility(0);
        this.binding.selfVideoViewWrapper.setVisibility(0);
        if (!isPipModePossible()) {
            this.binding.pictureInPictureButton.setVisibility(8);
        }
        if (this.isVoiceOnlyCall) {
            this.binding.switchSelfVideoButton.setVisibility(8);
            this.binding.cameraButton.setVisibility(8);
            this.binding.selfVideoRenderer.setVisibility(8);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(3, R.id.callInfosLinearLayout);
            layoutParams.setMargins(0, 0, 0, Math.round(getApplicationContext().getResources().getDimension(R.dimen.call_controls_height)));
            this.binding.gridview.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams2.setMargins(0, 0, 0, 0);
            this.binding.gridview.setLayoutParams(layoutParams2);
            if (this.cameraEnumerator.getDeviceNames().length < 2) {
                this.binding.switchSelfVideoButton.setVisibility(8);
            }
            initSelfVideoView();
        }
        this.binding.gridview.setOnTouchListener(new View.OnTouchListener() { // from class: com.nextcloud.talk.activities.CallActivity.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0) {
                    return false;
                }
                CallActivity.this.animateCallControls(true, 0L);
                return false;
            }
        });
        this.binding.conversationRelativeLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.nextcloud.talk.activities.CallActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0) {
                    return false;
                }
                CallActivity.this.animateCallControls(true, 0L);
                return false;
            }
        });
        animateCallControls(true, 0L);
        initGridAdapter();
    }

    private void initSelfVideoView() {
        try {
            this.binding.selfVideoRenderer.init(this.rootEglBase.getEglBaseContext(), null);
        } catch (IllegalStateException e) {
            Log.d(TAG, "selfVideoRenderer already initialized", e);
        }
        this.binding.selfVideoRenderer.setZOrderMediaOverlay(true);
        this.binding.selfVideoRenderer.setEnableHardwareScaler(false);
        this.binding.selfVideoRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.binding.selfVideoRenderer.setOnTouchListener(new SelfVideoTouchListener());
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x002b, code lost:
    
        r8 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0029, code lost:
    
        if (r0 > 1) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0021, code lost:
    
        if (r0 > 2) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x002d, code lost:
    
        r8 = 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initGridAdapter() {
        /*
            r10 = this;
            java.lang.String r0 = "CallActivity"
            java.lang.String r1 = "initGridAdapter"
            android.util.Log.d(r0, r1)
            java.util.Map<java.lang.String, com.nextcloud.talk.adapters.ParticipantDisplayItem> r0 = r10.participantDisplayItems
            int r0 = r0.size()
            android.content.res.Resources r1 = r10.getResources()
            r2 = 1
            r3 = 2
            if (r1 == 0) goto L24
            android.content.res.Resources r1 = r10.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            int r1 = r1.orientation
            if (r1 != r2) goto L24
            if (r0 <= r3) goto L2d
            goto L2b
        L24:
            if (r0 <= r3) goto L29
            r2 = 3
            r8 = 3
            goto L2e
        L29:
            if (r0 <= r2) goto L2d
        L2b:
            r8 = 2
            goto L2e
        L2d:
            r8 = 1
        L2e:
            com.nextcloud.talk.databinding.CallActivityBinding r0 = r10.binding
            android.widget.GridView r0 = r0.gridview
            r0.setNumColumns(r8)
            com.nextcloud.talk.databinding.CallActivityBinding r0 = r10.binding
            android.widget.RelativeLayout r0 = r0.conversationRelativeLayout
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            com.nextcloud.talk.activities.CallActivity$4 r1 = new com.nextcloud.talk.activities.CallActivity$4
            r1.<init>()
            r0.addOnGlobalLayoutListener(r1)
            com.nextcloud.talk.databinding.CallActivityBinding r0 = r10.binding
            android.widget.LinearLayout r0 = r0.callInfosLinearLayout
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            com.nextcloud.talk.activities.CallActivity$5 r1 = new com.nextcloud.talk.activities.CallActivity$5
            r1.<init>()
            r0.addOnGlobalLayoutListener(r1)
            com.nextcloud.talk.adapters.ParticipantsAdapter r0 = new com.nextcloud.talk.adapters.ParticipantsAdapter
            java.util.Map<java.lang.String, com.nextcloud.talk.adapters.ParticipantDisplayItem> r5 = r10.participantDisplayItems
            com.nextcloud.talk.databinding.CallActivityBinding r1 = r10.binding
            android.widget.RelativeLayout r6 = r1.conversationRelativeLayout
            com.nextcloud.talk.databinding.CallActivityBinding r1 = r10.binding
            android.widget.LinearLayout r7 = r1.callInfosLinearLayout
            boolean r9 = r10.isVoiceOnlyCall
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8, r9)
            r10.participantsAdapter = r0
            com.nextcloud.talk.databinding.CallActivityBinding r0 = r10.binding
            android.widget.GridView r0 = r0.gridview
            com.nextcloud.talk.adapters.ParticipantsAdapter r1 = r10.participantsAdapter
            r0.setAdapter(r1)
            java.lang.Boolean r0 = r10.isInPipMode
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L7e
            r10.updateUiForPipMode()
        L7e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nextcloud.talk.activities.CallActivity.initGridAdapter():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPermissions() {
        if (this.isVoiceOnlyCall) {
            onMicrophoneClick();
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS_CALL, 100);
        } else {
            onRequestPermissionsResult(100, PERMISSIONS_CALL, new int[]{1, 1});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isConnectionEstablished() {
        return this.currentCallStatus.equals(CallStatus.JOINED) || this.currentCallStatus.equals(CallStatus.IN_CONVERSATION);
    }

    @AfterPermissionGranted(100)
    private void onPermissionsGranted() {
        String[] strArr = PERMISSIONS_CALL;
        if (EffortlessPermissions.hasPermissions(this, strArr)) {
            if (!this.videoOn && !this.isVoiceOnlyCall) {
                onCameraClick();
            }
            if (!this.microphoneOn) {
                onMicrophoneClick();
            }
            if (!this.isVoiceOnlyCall) {
                if (this.cameraEnumerator.getDeviceNames().length == 0) {
                    this.binding.cameraButton.setVisibility(8);
                }
                if (this.cameraEnumerator.getDeviceNames().length > 1) {
                    this.binding.switchSelfVideoButton.setVisibility(0);
                }
            }
            if (isConnectionEstablished()) {
                return;
            }
            fetchSignalingSettings();
            return;
        }
        if (EffortlessPermissions.somePermissionPermanentlyDenied(this, strArr)) {
            checkIfSomeAreApproved();
        }
    }

    private void checkIfSomeAreApproved() {
        if (!this.isVoiceOnlyCall) {
            if (this.cameraEnumerator.getDeviceNames().length == 0) {
                this.binding.cameraButton.setVisibility(8);
            }
            if (this.cameraEnumerator.getDeviceNames().length > 1) {
                this.binding.switchSelfVideoButton.setVisibility(0);
            }
            if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_CAMERA)) {
                if (!this.videoOn) {
                    onCameraClick();
                }
            } else {
                this.binding.cameraButton.getHierarchy().setPlaceholderImage(R.drawable.ic_videocam_off_white_24px);
                this.binding.cameraButton.setAlpha(0.7f);
                this.binding.switchSelfVideoButton.setVisibility(8);
            }
        }
        if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_MICROPHONE)) {
            if (!this.microphoneOn) {
                onMicrophoneClick();
            }
        } else {
            this.binding.microphoneButton.getHierarchy().setPlaceholderImage(R.drawable.ic_mic_off_white_24px);
        }
        if (isConnectionEstablished()) {
            return;
        }
        fetchSignalingSettings();
    }

    @AfterPermissionDenied(100)
    private void onPermissionsDenied() {
        if (!this.isVoiceOnlyCall) {
            if (this.cameraEnumerator.getDeviceNames().length == 0) {
                this.binding.cameraButton.setVisibility(8);
            } else if (this.cameraEnumerator.getDeviceNames().length == 1) {
                this.binding.switchSelfVideoButton.setVisibility(8);
            }
        }
        if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_CAMERA) || EffortlessPermissions.hasPermissions(this, PERMISSIONS_MICROPHONE)) {
            checkIfSomeAreApproved();
        } else {
            if (isConnectionEstablished()) {
                return;
            }
            fetchSignalingSettings();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAudioManagerDevicesChanged(MagicAudioManager.AudioDevice audioDevice, Set<MagicAudioManager.AudioDevice> set) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + set + ", currentDevice: " + audioDevice);
        if (audioDevice.equals(MagicAudioManager.AudioDevice.WIRED_HEADSET) || audioDevice.equals(MagicAudioManager.AudioDevice.SPEAKER_PHONE) || audioDevice.equals(MagicAudioManager.AudioDevice.BLUETOOTH)) {
            this.powerManagerUtils.updatePhoneState(PowerManagerUtils.PhoneState.WITHOUT_PROXIMITY_SENSOR_LOCK);
        } else {
            this.powerManagerUtils.updatePhoneState(PowerManagerUtils.PhoneState.WITH_PROXIMITY_SENSOR_LOCK);
        }
        AudioOutputDialog audioOutputDialog = this.audioOutputDialog;
        if (audioOutputDialog != null) {
            audioOutputDialog.updateOutputDeviceList();
        }
        updateAudioOutputButton(audioDevice);
    }

    private void cameraInitialization() {
        VideoCapturer createCameraCapturer = createCameraCapturer(this.cameraEnumerator);
        this.videoCapturer = createCameraCapturer;
        if (createCameraCapturer != null) {
            SurfaceTextureHelper create = SurfaceTextureHelper.create("CaptureThread", this.rootEglBase.getEglBaseContext());
            this.videoSource = this.peerConnectionFactory.createVideoSource(false);
            this.videoCapturer.initialize(create, getApplicationContext(), this.videoSource.getCapturerObserver());
        }
        VideoTrack createVideoTrack = this.peerConnectionFactory.createVideoTrack("NCv0", this.videoSource);
        this.localVideoTrack = createVideoTrack;
        this.localStream.addTrack(createVideoTrack);
        this.localVideoTrack.setEnabled(false);
        this.localVideoTrack.addSink(this.binding.selfVideoRenderer);
    }

    private void microphoneInitialization() {
        AudioSource createAudioSource = this.peerConnectionFactory.createAudioSource(this.audioConstraints);
        this.audioSource = createAudioSource;
        AudioTrack createAudioTrack = this.peerConnectionFactory.createAudioTrack("NCa0", createAudioSource);
        this.localAudioTrack = createAudioTrack;
        createAudioTrack.setEnabled(false);
        this.localStream.addTrack(this.localAudioTrack);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator cameraEnumerator) {
        String[] deviceNames = cameraEnumerator.getDeviceNames();
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String str : deviceNames) {
            if (cameraEnumerator.isFrontFacing(str)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                CameraVideoCapturer createCapturer = cameraEnumerator.createCapturer(str, null);
                if (createCapturer != null) {
                    this.binding.selfVideoRenderer.setMirror(true);
                    return createCapturer;
                }
            }
        }
        Logging.d(TAG, "Looking for other cameras.");
        for (String str2 : deviceNames) {
            if (!cameraEnumerator.isFrontFacing(str2)) {
                Logging.d(TAG, "Creating other camera capturer.");
                CameraVideoCapturer createCapturer2 = cameraEnumerator.createCapturer(str2, null);
                if (createCapturer2 != null) {
                    this.binding.selfVideoRenderer.setMirror(false);
                    return createCapturer2;
                }
            }
        }
        return null;
    }

    public void onMicrophoneClick() {
        String[] strArr = PERMISSIONS_MICROPHONE;
        if (EffortlessPermissions.hasPermissions(this, strArr)) {
            if (!this.appPreferences.getPushToTalkIntroShown()) {
                this.spotlightView = new SpotlightView.Builder(this).introAnimationDuration(300L).enableRevealAnimation(true).performClick(false).fadeinTextDuration(400L).headingTvColor(getResources().getColor(R.color.colorPrimary)).headingTvSize(20).headingTvText(getResources().getString(R.string.nc_push_to_talk)).subHeadingTvColor(getResources().getColor(R.color.bg_default)).subHeadingTvSize(16).subHeadingTvText(getResources().getString(R.string.nc_push_to_talk_desc)).maskColor(Color.parseColor("#dc000000")).target(this.binding.microphoneButton).lineAnimDuration(400L).lineAndArcColor(getResources().getColor(R.color.colorPrimary)).enableDismissAfterShown(true).dismissOnBackPress(true).usageId("pushToTalk").show();
                this.appPreferences.setPushToTalkIntroShown(true);
            }
            if (!this.isPTTActive) {
                boolean z = !this.microphoneOn;
                this.microphoneOn = z;
                if (z) {
                    this.binding.microphoneButton.getHierarchy().setPlaceholderImage(R.drawable.ic_mic_white_24px);
                    updatePictureInPictureActions(R.drawable.ic_mic_white_24px, getResources().getString(R.string.nc_pip_microphone_mute), 1);
                } else {
                    this.binding.microphoneButton.getHierarchy().setPlaceholderImage(R.drawable.ic_mic_off_white_24px);
                    updatePictureInPictureActions(R.drawable.ic_mic_off_white_24px, getResources().getString(R.string.nc_pip_microphone_unmute), 2);
                }
                toggleMedia(this.microphoneOn, false);
            } else {
                this.binding.microphoneButton.getHierarchy().setPlaceholderImage(R.drawable.ic_mic_white_24px);
                this.pulseAnimation.start();
                toggleMedia(true, false);
            }
            if (!this.isVoiceOnlyCall || isConnectionEstablished()) {
                return;
            }
            fetchSignalingSettings();
            return;
        }
        if (EffortlessPermissions.somePermissionPermanentlyDenied(this, strArr)) {
            OpenAppDetailsDialogFragment.show(R.string.nc_microphone_permission_permanently_denied, R.string.nc_permissions_settings, this);
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(strArr, 100);
        } else {
            onRequestPermissionsResult(100, strArr, new int[]{1});
        }
    }

    public void onCameraClick() {
        String[] strArr = PERMISSIONS_CAMERA;
        if (EffortlessPermissions.hasPermissions(this, strArr)) {
            boolean z = !this.videoOn;
            this.videoOn = z;
            if (z) {
                this.binding.cameraButton.getHierarchy().setPlaceholderImage(R.drawable.ic_videocam_white_24px);
                if (this.cameraEnumerator.getDeviceNames().length > 1) {
                    this.binding.switchSelfVideoButton.setVisibility(0);
                }
            } else {
                this.binding.cameraButton.getHierarchy().setPlaceholderImage(R.drawable.ic_videocam_off_white_24px);
                this.binding.switchSelfVideoButton.setVisibility(8);
            }
            toggleMedia(this.videoOn, true);
            return;
        }
        if (EffortlessPermissions.somePermissionPermanentlyDenied(this, strArr)) {
            OpenAppDetailsDialogFragment.show(R.string.nc_camera_permission_permanently_denied, R.string.nc_permissions_settings, this);
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(strArr, 100);
        } else {
            onRequestPermissionsResult(100, strArr, new int[]{1});
        }
    }

    public void switchCamera() {
        CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) this.videoCapturer;
        if (cameraVideoCapturer != null) {
            cameraVideoCapturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() { // from class: com.nextcloud.talk.activities.CallActivity.6
                @Override // org.webrtc.CameraVideoCapturer.CameraSwitchHandler
                public void onCameraSwitchError(String str) {
                }

                @Override // org.webrtc.CameraVideoCapturer.CameraSwitchHandler
                public void onCameraSwitchDone(boolean z) {
                    CallActivity.this.binding.selfVideoRenderer.setMirror(z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleMedia(boolean z, boolean z2) {
        String str;
        List<PeerConnectionWrapper> list;
        if (z2) {
            if (z) {
                this.binding.cameraButton.setAlpha(1.0f);
                startVideoCapture();
                str = "videoOn";
            } else {
                this.binding.cameraButton.setAlpha(0.7f);
                VideoCapturer videoCapturer = this.videoCapturer;
                if (videoCapturer != null) {
                    try {
                        videoCapturer.stopCapture();
                    } catch (InterruptedException unused) {
                        Log.d(TAG, "Failed to stop capturing video while sensor is near the ear");
                    }
                }
                str = "videoOff";
            }
            MediaStream mediaStream = this.localStream;
            if (mediaStream != null && mediaStream.videoTracks.size() > 0) {
                this.localStream.videoTracks.get(0).setEnabled(z);
            }
            if (z) {
                this.binding.selfVideoRenderer.setVisibility(0);
            } else {
                this.binding.selfVideoRenderer.setVisibility(4);
            }
        } else {
            if (z) {
                this.binding.microphoneButton.setAlpha(1.0f);
                str = "audioOn";
            } else {
                this.binding.microphoneButton.setAlpha(0.7f);
                str = "audioOff";
            }
            MediaStream mediaStream2 = this.localStream;
            if (mediaStream2 != null && mediaStream2.audioTracks.size() > 0) {
                this.localStream.audioTracks.get(0).setEnabled(z);
            }
        }
        if (!isConnectionEstablished() || (list = this.peerConnectionWrapperList) == null) {
            return;
        }
        if (!this.hasMCU) {
            Iterator<PeerConnectionWrapper> it = list.iterator();
            while (it.hasNext()) {
                it.next().sendChannelData(new DataChannelMessage(str));
            }
        } else {
            for (PeerConnectionWrapper peerConnectionWrapper : list) {
                if (peerConnectionWrapper.getSessionId().equals(this.webSocketClient.getSessionId())) {
                    peerConnectionWrapper.sendChannelData(new DataChannelMessage(str));
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateCallControls(final boolean z, long j) {
        float f;
        if (this.isVoiceOnlyCall) {
            SpotlightView spotlightView = this.spotlightView;
            if (spotlightView == null || spotlightView.getVisibility() == 8) {
                return;
            }
            this.spotlightView.setVisibility(8);
            return;
        }
        if (this.isPTTActive) {
            return;
        }
        if (z) {
            this.callControlHandler.removeCallbacksAndMessages(null);
            this.callInfosHandler.removeCallbacksAndMessages(null);
            this.cameraSwitchHandler.removeCallbacksAndMessages(null);
            f = 1.0f;
            if (this.binding.callControls.getVisibility() != 0) {
                this.binding.callControls.setAlpha(0.0f);
                this.binding.callControls.setVisibility(0);
                this.binding.callInfosLinearLayout.setAlpha(0.0f);
                this.binding.callInfosLinearLayout.setVisibility(0);
                this.binding.switchSelfVideoButton.setAlpha(0.0f);
                if (this.videoOn) {
                    this.binding.switchSelfVideoButton.setVisibility(0);
                }
            } else {
                this.callControlHandler.postDelayed(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$4EWoKgyYaRRqw5CNw3YEqaYMLRY
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallActivity.this.lambda$animateCallControls$9$CallActivity();
                    }
                }, 5000L);
                return;
            }
        } else {
            f = 0.0f;
        }
        this.binding.callControls.setEnabled(false);
        this.binding.callControls.animate().translationY(0.0f).alpha(f).setDuration(1000L).setStartDelay(j).setListener(new AnimatorListenerAdapter() { // from class: com.nextcloud.talk.activities.CallActivity.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (!z) {
                    CallActivity.this.binding.callControls.setVisibility(8);
                    if (CallActivity.this.spotlightView != null && CallActivity.this.spotlightView.getVisibility() != 8) {
                        CallActivity.this.spotlightView.setVisibility(8);
                    }
                } else {
                    CallActivity.this.callControlHandler.postDelayed(new Runnable() { // from class: com.nextcloud.talk.activities.CallActivity.7.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (CallActivity.this.isPTTActive) {
                                return;
                            }
                            CallActivity.this.animateCallControls(false, 0L);
                        }
                    }, AccountVerificationController.DELAY_IN_MILLIS);
                }
                CallActivity.this.binding.callControls.setEnabled(true);
            }
        });
        this.binding.callInfosLinearLayout.setEnabled(false);
        this.binding.callInfosLinearLayout.animate().translationY(0.0f).alpha(f).setDuration(1000L).setStartDelay(j).setListener(new AnimatorListenerAdapter() { // from class: com.nextcloud.talk.activities.CallActivity.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (!z) {
                    CallActivity.this.binding.callInfosLinearLayout.setVisibility(8);
                } else {
                    CallActivity.this.callInfosHandler.postDelayed(new Runnable() { // from class: com.nextcloud.talk.activities.CallActivity.8.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (CallActivity.this.isPTTActive) {
                                return;
                            }
                            CallActivity.this.animateCallControls(false, 0L);
                        }
                    }, AccountVerificationController.DELAY_IN_MILLIS);
                }
                CallActivity.this.binding.callInfosLinearLayout.setEnabled(true);
            }
        });
        this.binding.switchSelfVideoButton.setEnabled(false);
        this.binding.switchSelfVideoButton.animate().translationY(0.0f).alpha(f).setDuration(1000L).setStartDelay(j).setListener(new AnimatorListenerAdapter() { // from class: com.nextcloud.talk.activities.CallActivity.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (!z) {
                    CallActivity.this.binding.switchSelfVideoButton.setVisibility(8);
                }
                CallActivity.this.binding.switchSelfVideoButton.setEnabled(true);
            }
        });
    }

    public /* synthetic */ void lambda$animateCallControls$9$CallActivity() {
        animateCallControls(false, 0L);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        if (!this.currentCallStatus.equals(CallStatus.LEAVING)) {
            setCallState(CallStatus.LEAVING);
            hangup(true);
        }
        this.powerManagerUtils.updatePhoneState(PowerManagerUtils.PhoneState.IDLE);
        super.onDestroy();
    }

    private void fetchSignalingSettings() {
        Log.d(TAG, "fetchSignalingSettings");
        final int signalingApiVersion = ApiUtils.getSignalingApiVersion(this.conversationUser, new int[]{3, 2, 1});
        this.ncApi.getSignalingSettings(this.credentials, ApiUtils.getUrlForSignalingSettings(signalingApiVersion, this.baseUrl)).subscribeOn(Schedulers.io()).retry(3L).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SignalingSettingsOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.10
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(SignalingSettingsOverall signalingSettingsOverall) {
                if (signalingSettingsOverall.getOcs() != null && signalingSettingsOverall.getOcs().getSettings() != null) {
                    CallActivity.this.externalSignalingServer = new ExternalSignalingServer();
                    if (TextUtils.isEmpty(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingServer()) || TextUtils.isEmpty(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingTicket())) {
                        CallActivity.this.hasExternalSignalingServer = false;
                    } else {
                        CallActivity.this.externalSignalingServer = new ExternalSignalingServer();
                        CallActivity.this.externalSignalingServer.setExternalSignalingServer(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingServer());
                        CallActivity.this.externalSignalingServer.setExternalSignalingTicket(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingTicket());
                        CallActivity.this.hasExternalSignalingServer = true;
                    }
                    Log.d(CallActivity.TAG, "   hasExternalSignalingServer: " + CallActivity.this.hasExternalSignalingServer);
                    if (CallActivity.this.conversationUser.getUserId().equals("?")) {
                        try {
                            CallActivity.this.conversationUser.setExternalSignalingServer(LoganSquare.serialize(CallActivity.this.externalSignalingServer));
                        } catch (IOException e) {
                            Log.e(CallActivity.TAG, "Failed to serialize external signaling server", e);
                        }
                    } else {
                        try {
                            CallActivity.this.userUtils.createOrUpdateUser(null, null, null, null, null, null, null, Long.valueOf(CallActivity.this.conversationUser.getId()), null, null, LoganSquare.serialize(CallActivity.this.externalSignalingServer)).subscribeOn(Schedulers.io()).subscribe();
                        } catch (IOException e2) {
                            Log.e(CallActivity.TAG, "Failed to serialize external signaling server", e2);
                        }
                    }
                    if (signalingSettingsOverall.getOcs().getSettings().getStunServers() != null) {
                        List<IceServer> stunServers = signalingSettingsOverall.getOcs().getSettings().getStunServers();
                        if (signalingApiVersion == 3) {
                            for (IceServer iceServer : stunServers) {
                                if (iceServer.getUrls() != null) {
                                    for (String str : iceServer.getUrls()) {
                                        Log.d(CallActivity.TAG, "   STUN server url: " + str);
                                        CallActivity.this.iceServers.add(new PeerConnection.IceServer(str));
                                    }
                                }
                            }
                        } else if (signalingSettingsOverall.getOcs().getSettings().getStunServers() != null) {
                            for (IceServer iceServer2 : stunServers) {
                                Log.d(CallActivity.TAG, "   STUN server url: " + iceServer2.getUrl());
                                CallActivity.this.iceServers.add(new PeerConnection.IceServer(iceServer2.getUrl()));
                            }
                        }
                    }
                    if (signalingSettingsOverall.getOcs().getSettings().getTurnServers() != null) {
                        for (IceServer iceServer3 : signalingSettingsOverall.getOcs().getSettings().getTurnServers()) {
                            if (iceServer3.getUrls() != null) {
                                for (String str2 : iceServer3.getUrls()) {
                                    Log.d(CallActivity.TAG, "   TURN server url: " + str2);
                                    CallActivity.this.iceServers.add(new PeerConnection.IceServer(str2, iceServer3.getUsername(), iceServer3.getCredential()));
                                }
                            }
                        }
                    }
                }
                CallActivity.this.checkCapabilities();
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                Log.e(CallActivity.TAG, th.getMessage(), th);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkCapabilities() {
        this.ncApi.getCapabilities(this.credentials, ApiUtils.getUrlForCapabilities(this.baseUrl)).retry(3L).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<CapabilitiesOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.11
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(CapabilitiesOverall capabilitiesOverall) {
                if (CallActivity.this.hasExternalSignalingServer) {
                    CallActivity.this.setupAndInitiateWebSocketsConnection();
                } else {
                    CallActivity.this.joinRoomAndCall();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void joinRoomAndCall() {
        this.callSession = ApplicationWideCurrentRoomHolder.getInstance().getSession();
        int conversationApiVersion = ApiUtils.getConversationApiVersion(this.conversationUser, new int[]{4, 1});
        Log.d(TAG, "joinRoomAndCall");
        Log.d(TAG, "   baseUrl= " + this.baseUrl);
        Log.d(TAG, "   roomToken= " + this.roomToken);
        Log.d(TAG, "   callSession= " + this.callSession);
        String urlForParticipantsActive = ApiUtils.getUrlForParticipantsActive(conversationApiVersion, this.baseUrl, this.roomToken);
        Log.d(TAG, "   url= " + urlForParticipantsActive);
        if (TextUtils.isEmpty(this.callSession)) {
            this.ncApi.joinRoom(this.credentials, urlForParticipantsActive, this.conversationPassword).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(3L).subscribe(new Observer<RoomOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.12
                @Override // io.reactivex.Observer
                public void onSubscribe(Disposable disposable) {
                }

                @Override // io.reactivex.Observer
                public void onNext(RoomOverall roomOverall) {
                    CallActivity.this.callSession = roomOverall.getOcs().getData().getSessionId();
                    Log.d(CallActivity.TAG, " new callSession by joinRoom= " + CallActivity.this.callSession);
                    ApplicationWideCurrentRoomHolder.getInstance().setSession(CallActivity.this.callSession);
                    ApplicationWideCurrentRoomHolder.getInstance().setCurrentRoomId(CallActivity.this.roomId);
                    ApplicationWideCurrentRoomHolder.getInstance().setCurrentRoomToken(CallActivity.this.roomToken);
                    ApplicationWideCurrentRoomHolder.getInstance().setUserInRoom(CallActivity.this.conversationUser);
                    CallActivity.this.callOrJoinRoomViaWebSocket();
                }

                @Override // io.reactivex.Observer
                public void onError(Throwable th) {
                    Log.e(CallActivity.TAG, "joinRoom onError", th);
                }

                @Override // io.reactivex.Observer
                public void onComplete() {
                    Log.d(CallActivity.TAG, "joinRoom onComplete");
                }
            });
        } else {
            callOrJoinRoomViaWebSocket();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callOrJoinRoomViaWebSocket() {
        if (this.hasExternalSignalingServer) {
            this.webSocketClient.joinRoomWithRoomTokenAndSession(this.roomToken, this.callSession);
        } else {
            performCall();
        }
    }

    private void performCall() {
        this.ncApi.joinCall(this.credentials, ApiUtils.getUrlForCall(ApiUtils.getCallApiVersion(this.conversationUser, new int[]{4, 1}), this.baseUrl, this.roomToken), Integer.valueOf(this.isVoiceOnlyCall ? 3 : 7)).subscribeOn(Schedulers.io()).retry(3L).observeOn(AndroidSchedulers.mainThread()).subscribe(new AnonymousClass13());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nextcloud.talk.activities.CallActivity$13, reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass13 implements Observer<GenericOverall> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ ObservableSource lambda$onNext$0(Observable observable) throws Exception {
            return observable;
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable th) {
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable disposable) {
        }

        AnonymousClass13() {
        }

        @Override // io.reactivex.Observer
        public void onNext(GenericOverall genericOverall) {
            if (CallActivity.this.currentCallStatus.equals(CallStatus.LEAVING)) {
                return;
            }
            CallActivity.this.setCallState(CallStatus.JOINED);
            ApplicationWideCurrentRoomHolder.getInstance().setInCall(true);
            ApplicationWideCurrentRoomHolder.getInstance().setDialing(false);
            if (!TextUtils.isEmpty(CallActivity.this.roomToken)) {
                NotificationUtils.INSTANCE.cancelExistingNotificationsForRoom(CallActivity.this.getApplicationContext(), CallActivity.this.conversationUser, CallActivity.this.roomToken);
            }
            if (CallActivity.this.hasExternalSignalingServer) {
                return;
            }
            CallActivity.this.ncApi.pullSignalingMessages(CallActivity.this.credentials, ApiUtils.getUrlForSignaling(ApiUtils.getSignalingApiVersion(CallActivity.this.conversationUser, new int[]{3, 2, 1}), CallActivity.this.baseUrl, CallActivity.this.roomToken)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).repeatWhen(new Function() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$13$O-HIeK4hbqGhiuSSuOB6THEzF10
                @Override // io.reactivex.functions.Function
                public final Object apply(Object obj) {
                    return CallActivity.AnonymousClass13.lambda$onNext$0((Observable) obj);
                }
            }).takeWhile(new Predicate() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$13$NY59tI_TLyRh6Qwjd8wR5ahy5dg
                @Override // io.reactivex.functions.Predicate
                public final boolean test(Object obj) {
                    return CallActivity.AnonymousClass13.this.lambda$onNext$1$CallActivity$13((SignalingOverall) obj);
                }
            }).retry(3L, new Predicate() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$13$zLo9oSFCPfuHAGVqLUryLLwNNiQ
                @Override // io.reactivex.functions.Predicate
                public final boolean test(Object obj) {
                    return CallActivity.AnonymousClass13.this.lambda$onNext$2$CallActivity$13((Throwable) obj);
                }
            }).subscribe(new Observer<SignalingOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.13.1
                @Override // io.reactivex.Observer
                public void onSubscribe(Disposable disposable) {
                    CallActivity.this.signalingDisposable = disposable;
                }

                @Override // io.reactivex.Observer
                public void onNext(SignalingOverall signalingOverall) {
                    CallActivity.this.receivedSignalingMessages(signalingOverall.getOcs().getSignalings());
                }

                @Override // io.reactivex.Observer
                public void onError(Throwable th) {
                    CallActivity.this.dispose(CallActivity.this.signalingDisposable);
                }

                @Override // io.reactivex.Observer
                public void onComplete() {
                    CallActivity.this.dispose(CallActivity.this.signalingDisposable);
                }
            });
        }

        public /* synthetic */ boolean lambda$onNext$1$CallActivity$13(SignalingOverall signalingOverall) throws Exception {
            return CallActivity.this.isConnectionEstablished();
        }

        public /* synthetic */ boolean lambda$onNext$2$CallActivity$13(Throwable th) throws Exception {
            return CallActivity.this.isConnectionEstablished();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupAndInitiateWebSocketsConnection() {
        if (this.webSocketConnectionHelper == null) {
            this.webSocketConnectionHelper = new WebSocketConnectionHelper();
        }
        MagicWebSocketInstance magicWebSocketInstance = this.webSocketClient;
        if (magicWebSocketInstance == null) {
            this.webSocketClient = WebSocketConnectionHelper.getExternalSignalingInstanceForServer(this.externalSignalingServer.getExternalSignalingServer(), this.conversationUser, this.externalSignalingServer.getExternalSignalingTicket(), TextUtils.isEmpty(this.credentials));
        } else if (magicWebSocketInstance.isConnected() && this.currentCallStatus.equals(CallStatus.PUBLISHER_FAILED)) {
            this.webSocketClient.restartWebSocket();
        }
        joinRoomAndCall();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initiateCall() {
        if (!TextUtils.isEmpty(this.roomToken)) {
            checkPermissions();
        } else {
            handleFromNotification();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(WebSocketCommunicationEvent webSocketCommunicationEvent) {
        String type = webSocketCommunicationEvent.getType();
        type.hashCode();
        char c = 65535;
        switch (type.hashCode()) {
            case -1776485779:
                if (type.equals("signalingMessage")) {
                    c = 0;
                    break;
                }
                break;
            case -1724586871:
                if (type.equals(Globals.PARTICIPANTS_UPDATE)) {
                    c = 1;
                    break;
                }
                break;
            case 99162322:
                if (type.equals("hello")) {
                    c = 2;
                    break;
                }
                break;
            case 225349217:
                if (type.equals("peerReadyForRequestingOffer")) {
                    c = 3;
                    break;
                }
                break;
            case 664691716:
                if (type.equals("roomJoined")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Log.d(TAG, "onMessageEvent 'signalingMessage'");
                processMessage((NCSignalingMessage) this.webSocketClient.getJobWithId(Integer.valueOf(webSocketCommunicationEvent.getHashMap().get(Globals.JOB_ID))));
                return;
            case 1:
                Log.d(TAG, "onMessageEvent 'participantsUpdate'");
                HashMap<String, String> hashMap = webSocketCommunicationEvent.getHashMap();
                if (hashMap == null) {
                    return;
                }
                String str = hashMap.get(Globals.ROOM_TOKEN);
                String str2 = hashMap.get("all");
                String str3 = hashMap.get(Globals.UPDATE_IN_CALL);
                String str4 = hashMap.get(Globals.JOB_ID);
                if (this.roomToken.equals(str)) {
                    if (str2 == null || !Boolean.parseBoolean(str2)) {
                        if (str4 != null) {
                            processUsersInRoom((List) this.webSocketClient.getJobWithId(Integer.valueOf(str4)));
                            return;
                        }
                        return;
                    } else {
                        if (SessionDescription.SUPPORTED_SDP_VERSION.equals(str3)) {
                            Log.d(TAG, "Most probably a moderator ended the call for all.");
                            hangup(true);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 2:
                Log.d(TAG, "onMessageEvent 'hello'");
                if (webSocketCommunicationEvent.getHashMap().containsKey("oldResumeId")) {
                    return;
                }
                if (this.currentCallStatus.equals(CallStatus.RECONNECTING)) {
                    hangup(false);
                    return;
                } else {
                    initiateCall();
                    return;
                }
            case 3:
                Log.d(TAG, "onMessageEvent 'peerReadyForRequestingOffer'");
                this.webSocketClient.requestOfferForSessionIdWithType(webSocketCommunicationEvent.getHashMap().get("sessionId"), "video");
                return;
            case 4:
                Log.d(TAG, "onMessageEvent 'roomJoined'");
                startSendingNick();
                if (webSocketCommunicationEvent.getHashMap().get(Globals.ROOM_TOKEN).equals(this.roomToken)) {
                    performCall();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispose(Disposable disposable) {
        Disposable disposable2;
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        } else {
            if (disposable != null || (disposable2 = this.signalingDisposable) == null || disposable2.isDisposed()) {
                return;
            }
            this.signalingDisposable.dispose();
            this.signalingDisposable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void receivedSignalingMessages(List<Signaling> list) {
        if (list != null) {
            Iterator<Signaling> it = list.iterator();
            while (it.hasNext()) {
                try {
                    receivedSignalingMessage(it.next());
                } catch (IOException e) {
                    Log.e(TAG, "Failed to process received signaling message", e);
                }
            }
        }
    }

    private void receivedSignalingMessage(Signaling signaling) throws IOException {
        String type = signaling.getType();
        if (isConnectionEstablished() || this.currentCallStatus.equals(CallStatus.CONNECTING)) {
            if ("usersInRoom".equals(type)) {
                processUsersInRoom((List) signaling.getMessageWrapper());
            } else if ("message".equals(type)) {
                processMessage((NCSignalingMessage) LoganSquare.parse(signaling.getMessageWrapper().toString(), NCSignalingMessage.class));
            } else {
                Log.e(TAG, "unexpected message type when receiving signaling message");
            }
        }
    }

    private void processMessage(NCSignalingMessage nCSignalingMessage) {
        PeerConnectionWrapper peerConnectionWrapperForSessionIdAndType;
        if (nCSignalingMessage.getRoomType().equals("video") || nCSignalingMessage.getRoomType().equals(VIDEO_STREAM_TYPE_SCREEN)) {
            String str = null;
            if (nCSignalingMessage.getPayload() != null && nCSignalingMessage.getPayload().getType() != null) {
                str = nCSignalingMessage.getPayload().getType();
            } else if (nCSignalingMessage.getType() != null) {
                str = nCSignalingMessage.getType();
            }
            if ("offer".equals(str)) {
                peerConnectionWrapperForSessionIdAndType = getOrCreatePeerConnectionWrapperForSessionIdAndType(nCSignalingMessage.getFrom(), nCSignalingMessage.getRoomType(), false);
            } else {
                peerConnectionWrapperForSessionIdAndType = getPeerConnectionWrapperForSessionIdAndType(nCSignalingMessage.getFrom(), nCSignalingMessage.getRoomType());
            }
            if ("unshareScreen".equals(str) || (("offer".equals(str) || "answer".equals(str) || "candidate".equals(str) || "endOfCandidates".equals(str)) && peerConnectionWrapperForSessionIdAndType != null)) {
                str.hashCode();
                char c = 65535;
                switch (str.hashCode()) {
                    case -2119151358:
                        if (str.equals("endOfCandidates")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1412808770:
                        if (str.equals("answer")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 105650780:
                        if (str.equals("offer")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 508663171:
                        if (str.equals("candidate")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1394434290:
                        if (str.equals("unshareScreen")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        peerConnectionWrapperForSessionIdAndType.drainIceCandidates();
                        return;
                    case 1:
                    case 2:
                        peerConnectionWrapperForSessionIdAndType.setNick(nCSignalingMessage.getPayload().getNick());
                        org.webrtc.SessionDescription sessionDescription = new org.webrtc.SessionDescription(SessionDescription.Type.fromCanonicalForm(str), MagicWebRTCUtils.preferCodec(nCSignalingMessage.getPayload().getSdp(), "H264", false));
                        if (peerConnectionWrapperForSessionIdAndType.getPeerConnection() != null) {
                            peerConnectionWrapperForSessionIdAndType.getPeerConnection().setRemoteDescription(peerConnectionWrapperForSessionIdAndType.getMagicSdpObserver(), sessionDescription);
                            return;
                        }
                        return;
                    case 3:
                        NCIceCandidate iceCandidate = nCSignalingMessage.getPayload().getIceCandidate();
                        peerConnectionWrapperForSessionIdAndType.addCandidate(new IceCandidate(iceCandidate.getSdpMid(), iceCandidate.getSdpMLineIndex(), iceCandidate.getCandidate()));
                        return;
                    case 4:
                        endPeerConnection(nCSignalingMessage.getFrom(), true);
                        return;
                    default:
                        return;
                }
            }
            return;
        }
        Log.e(TAG, "unexpected RoomType while processing NCSignalingMessage");
    }

    private void hangup(boolean z) {
        Log.d(TAG, "hangup! shutDownView=" + z);
        stopCallingSound();
        dispose(null);
        if (z) {
            VideoCapturer videoCapturer = this.videoCapturer;
            if (videoCapturer != null) {
                try {
                    videoCapturer.stopCapture();
                } catch (InterruptedException unused) {
                    Log.e(TAG, "Failed to stop capturing while hanging up");
                }
                this.videoCapturer.dispose();
                this.videoCapturer = null;
            }
            this.binding.selfVideoRenderer.release();
            AudioSource audioSource = this.audioSource;
            if (audioSource != null) {
                audioSource.dispose();
                this.audioSource = null;
            }
            runOnUiThread(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$si8ZRcHz18HYsEu0c94GBSIqO4E
                @Override // java.lang.Runnable
                public final void run() {
                    CallActivity.this.lambda$hangup$10$CallActivity();
                }
            });
            if (this.videoSource != null) {
                this.videoSource = null;
            }
            if (this.peerConnectionFactory != null) {
                this.peerConnectionFactory = null;
            }
            this.localAudioTrack = null;
            this.localVideoTrack = null;
            if (TextUtils.isEmpty(this.credentials) && this.hasExternalSignalingServer) {
                WebSocketConnectionHelper.deleteExternalSignalingInstanceForUserEntity(-1L);
            }
        }
        for (int i = 0; i < this.peerConnectionWrapperList.size(); i++) {
            endPeerConnection(this.peerConnectionWrapperList.get(i).getSessionId(), false);
        }
        MediaStream mediaStream = this.localStream;
        if (mediaStream != null) {
            mediaStream.dispose();
            this.localStream = null;
            Log.d(TAG, "Disposed localStream");
        } else {
            Log.d(TAG, "localStream is null");
        }
        hangupNetworkCalls(z);
        ApplicationWideCurrentRoomHolder.getInstance().setInCall(false);
    }

    public /* synthetic */ void lambda$hangup$10$CallActivity() {
        MagicAudioManager magicAudioManager = this.audioManager;
        if (magicAudioManager != null) {
            magicAudioManager.stop();
            this.audioManager = null;
        }
    }

    private void hangupNetworkCalls(final boolean z) {
        Log.d(TAG, "hangupNetworkCalls. shutDownView=" + z);
        this.ncApi.leaveCall(this.credentials, ApiUtils.getUrlForCall(ApiUtils.getCallApiVersion(this.conversationUser, new int[]{4, 1}), this.baseUrl, this.roomToken)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GenericOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.14
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(GenericOverall genericOverall) {
                if (!z) {
                    if (CallActivity.this.currentCallStatus == CallStatus.RECONNECTING || CallActivity.this.currentCallStatus == CallStatus.PUBLISHER_FAILED) {
                        CallActivity.this.initiateCall();
                        return;
                    }
                    return;
                }
                CallActivity.this.finish();
            }
        });
    }

    private void startVideoCapture() {
        VideoCapturer videoCapturer = this.videoCapturer;
        if (videoCapturer != null) {
            videoCapturer.startCapture(1280, 720, 30);
        }
    }

    private void processUsersInRoom(List<HashMap<String, Object>> list) {
        MagicWebSocketInstance magicWebSocketInstance;
        Log.d(TAG, "processUsersInRoom");
        ArrayList<String> arrayList = new ArrayList();
        HashSet<String> hashSet = new HashSet();
        this.hasMCU = this.hasExternalSignalingServer && (magicWebSocketInstance = this.webSocketClient) != null && magicWebSocketInstance.hasMCU();
        Log.d(TAG, "   hasMCU is " + this.hasMCU);
        String str = this.callSession;
        if (this.hasMCU) {
            str = this.webSocketClient.getSessionId();
        }
        Log.d(TAG, "   currentSessionId is " + str);
        for (HashMap<String, Object> hashMap : list) {
            long longValue = ((Long) hashMap.get("inCall")).longValue();
            if (hashMap.get("sessionId").equals(str)) {
                Log.d(TAG, "   inCallFlag of currentSessionId: " + longValue);
                if (longValue == 0) {
                    Log.d(TAG, "Most probably a moderator ended the call for all.");
                    hangup(true);
                }
            } else {
                Log.d(TAG, "   inCallFlag of participant " + hashMap.get("sessionId").toString().substring(0, 4) + " : " + longValue);
                if (longValue != 0) {
                    arrayList.add(hashMap.get("sessionId").toString());
                } else {
                    hashSet.add(hashMap.get("sessionId").toString());
                }
            }
        }
        for (PeerConnectionWrapper peerConnectionWrapper : this.peerConnectionWrapperList) {
            if (!peerConnectionWrapper.isMCUPublisher()) {
                hashSet.add(peerConnectionWrapper.getSessionId());
            }
        }
        hashSet.removeAll(arrayList);
        arrayList.removeAll(hashSet);
        if (isConnectionEstablished() || this.currentCallStatus.equals(CallStatus.CONNECTING)) {
            if (arrayList.size() > 0 && !this.hasMCU) {
                getPeersForCall();
            }
            if (this.hasMCU) {
                getOrCreatePeerConnectionWrapperForSessionIdAndType(this.webSocketClient.getSessionId(), "video", true);
            }
            for (String str2 : arrayList) {
                Log.d(TAG, "   newSession joined: " + str2);
                getOrCreatePeerConnectionWrapperForSessionIdAndType(str2, "video", false);
            }
            if (arrayList.size() > 0 && !this.currentCallStatus.equals(CallStatus.IN_CONVERSATION)) {
                setCallState(CallStatus.IN_CONVERSATION);
            }
            for (String str3 : hashSet) {
                Log.d(TAG, "   oldSession that will be removed is: " + str3);
                endPeerConnection(str3, false);
            }
        }
    }

    private void getPeersForCall() {
        Log.d(TAG, "getPeersForCall");
        this.ncApi.getPeersForCall(this.credentials, ApiUtils.getUrlForCall(ApiUtils.getCallApiVersion(this.conversationUser, new int[]{4, 1}), this.baseUrl, this.roomToken)).subscribeOn(Schedulers.io()).subscribe(new Observer<ParticipantsOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.15
            @Override // io.reactivex.Observer
            public void onComplete() {
            }

            @Override // io.reactivex.Observer
            public void onSubscribe(Disposable disposable) {
            }

            @Override // io.reactivex.Observer
            public void onNext(ParticipantsOverall participantsOverall) {
                CallActivity.this.participantMap = new HashMap();
                for (Participant participant : participantsOverall.getOcs().getData()) {
                    CallActivity.this.participantMap.put(participant.getSessionId(), participant);
                }
            }

            @Override // io.reactivex.Observer
            public void onError(Throwable th) {
                Log.e(CallActivity.TAG, "error while executing getPeersForCall", th);
            }
        });
    }

    private void deletePeerConnection(PeerConnectionWrapper peerConnectionWrapper) {
        peerConnectionWrapper.removePeerConnection();
        this.peerConnectionWrapperList.remove(peerConnectionWrapper);
    }

    private PeerConnectionWrapper getPeerConnectionWrapperForSessionIdAndType(String str, String str2) {
        for (int i = 0; i < this.peerConnectionWrapperList.size(); i++) {
            if (this.peerConnectionWrapperList.get(i).getSessionId().equals(str) && this.peerConnectionWrapperList.get(i).getVideoStreamType().equals(str2)) {
                return this.peerConnectionWrapperList.get(i);
            }
        }
        return null;
    }

    private PeerConnectionWrapper getOrCreatePeerConnectionWrapperForSessionIdAndType(String str, String str2, boolean z) {
        PeerConnectionWrapper peerConnectionWrapper;
        PeerConnectionWrapper peerConnectionWrapperForSessionIdAndType = getPeerConnectionWrapperForSessionIdAndType(str, str2);
        if (peerConnectionWrapperForSessionIdAndType != null) {
            return peerConnectionWrapperForSessionIdAndType;
        }
        if (this.peerConnectionFactory == null) {
            Log.e(TAG, "peerConnectionFactory was null in getOrCreatePeerConnectionWrapperForSessionIdAndType.");
            Toast.makeText(this.context, this.context.getResources().getString(R.string.nc_common_error_sorry), 1).show();
            hangup(true);
            return null;
        }
        boolean z2 = this.hasMCU;
        if (z2 && z) {
            peerConnectionWrapper = new PeerConnectionWrapper(this.peerConnectionFactory, this.iceServers, this.sdpConstraintsForMCU, str, this.callSession, this.localStream, true, true, str2);
        } else if (z2) {
            peerConnectionWrapper = new PeerConnectionWrapper(this.peerConnectionFactory, this.iceServers, this.sdpConstraints, str, this.callSession, null, false, true, str2);
        } else if (!VIDEO_STREAM_TYPE_SCREEN.equals(str2)) {
            peerConnectionWrapper = new PeerConnectionWrapper(this.peerConnectionFactory, this.iceServers, this.sdpConstraints, str, this.callSession, this.localStream, false, false, str2);
        } else {
            peerConnectionWrapper = new PeerConnectionWrapper(this.peerConnectionFactory, this.iceServers, this.sdpConstraints, str, this.callSession, null, false, false, str2);
        }
        this.peerConnectionWrapperList.add(peerConnectionWrapper);
        if (z) {
            startSendingNick();
        }
        return peerConnectionWrapper;
    }

    private List<PeerConnectionWrapper> getPeerConnectionWrapperListForSessionId(String str) {
        ArrayList arrayList = new ArrayList();
        for (PeerConnectionWrapper peerConnectionWrapper : this.peerConnectionWrapperList) {
            if (peerConnectionWrapper.getSessionId().equals(str)) {
                arrayList.add(peerConnectionWrapper);
            }
        }
        return arrayList;
    }

    private void endPeerConnection(final String str, boolean z) {
        List<PeerConnectionWrapper> peerConnectionWrapperListForSessionId = getPeerConnectionWrapperListForSessionId(str);
        if (peerConnectionWrapperListForSessionId.isEmpty()) {
            return;
        }
        for (int i = 0; i < peerConnectionWrapperListForSessionId.size(); i++) {
            PeerConnectionWrapper peerConnectionWrapper = peerConnectionWrapperListForSessionId.get(i);
            if (peerConnectionWrapper.getSessionId().equals(str) && (VIDEO_STREAM_TYPE_SCREEN.equals(peerConnectionWrapper.getVideoStreamType()) || !z)) {
                runOnUiThread(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$YVNgmbKHa-vSReLG0ZGM2OakHsI
                    @Override // java.lang.Runnable
                    public final void run() {
                        CallActivity.this.lambda$endPeerConnection$11$CallActivity(str);
                    }
                });
                deletePeerConnection(peerConnectionWrapper);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: removeMediaStream, reason: merged with bridge method [inline-methods] */
    public void lambda$endPeerConnection$11$CallActivity(String str) {
        Log.d(TAG, "removeMediaStream");
        this.participantDisplayItems.remove(str);
        if (isDestroyed()) {
            return;
        }
        initGridAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfigurationChangeEvent configurationChangeEvent) {
        PowerManagerUtils powerManagerUtils = this.powerManagerUtils;
        Resources resources = getResources();
        Objects.requireNonNull(resources);
        powerManagerUtils.setOrientation(resources.getConfiguration().orientation);
        initGridAdapter();
        updateSelfVideoViewPosition();
    }

    private void updateSelfVideoViewPosition() {
        double d;
        double dimension;
        double d2;
        Log.d(TAG, "updateSelfVideoViewPosition");
        if (this.isInPipMode.booleanValue()) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.binding.selfVideoRenderer.getLayoutParams();
        int convertPixelToDp = (int) DisplayUtils.convertPixelToDp(getApplicationContext().getResources().getDisplayMetrics().widthPixels, getApplicationContext());
        float f = 0.0f;
        float f2 = this.binding.callInfosLinearLayout.getVisibility() == 0 ? 250.0f : 20.0f;
        if (getResources().getConfiguration().orientation == 2) {
            layoutParams.height = (int) getResources().getDimension(R.dimen.large_preview_dimension);
            layoutParams.width = -2;
            d = convertPixelToDp;
            dimension = getResources().getDimension(R.dimen.large_preview_dimension);
            d2 = 0.8d;
        } else {
            if (getResources().getConfiguration().orientation == 1) {
                layoutParams.height = -2;
                layoutParams.width = (int) getResources().getDimension(R.dimen.large_preview_dimension);
                d = convertPixelToDp;
                dimension = getResources().getDimension(R.dimen.large_preview_dimension);
                d2 = 0.5d;
            }
            this.binding.selfVideoRenderer.setLayoutParams(layoutParams);
            int convertDpToPixel = (int) DisplayUtils.convertDpToPixel(f, getApplicationContext());
            this.binding.selfVideoViewWrapper.setY(f2);
            this.binding.selfVideoViewWrapper.setX(convertDpToPixel);
        }
        f = (float) (d - (dimension * d2));
        this.binding.selfVideoRenderer.setLayoutParams(layoutParams);
        int convertDpToPixel2 = (int) DisplayUtils.convertDpToPixel(f, getApplicationContext());
        this.binding.selfVideoViewWrapper.setY(f2);
        this.binding.selfVideoViewWrapper.setX(convertDpToPixel2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PeerConnectionEvent peerConnectionEvent) {
        String sessionId = peerConnectionEvent.getSessionId();
        if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.PEER_CLOSED) {
            endPeerConnection(sessionId, VIDEO_STREAM_TYPE_SCREEN.equals(peerConnectionEvent.getVideoStreamType()));
            return;
        }
        boolean z = false;
        if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.SENSOR_FAR || peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.SENSOR_NEAR) {
            if (this.isVoiceOnlyCall) {
                return;
            }
            if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.SENSOR_FAR && this.videoOn) {
                z = true;
            }
            if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_CAMERA)) {
                if ((this.currentCallStatus.equals(CallStatus.CONNECTING) || isConnectionEstablished()) && this.videoOn && z != this.localVideoTrack.enabled()) {
                    toggleMedia(z, true);
                    return;
                }
                return;
            }
            return;
        }
        if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.NICK_CHANGE) {
            if (this.participantDisplayItems.get(sessionId) != null) {
                this.participantDisplayItems.get(sessionId).setNick(peerConnectionEvent.getNick());
            }
            this.participantsAdapter.notifyDataSetChanged();
            return;
        }
        if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.VIDEO_CHANGE && !this.isVoiceOnlyCall) {
            if (this.participantDisplayItems.get(sessionId) != null) {
                this.participantDisplayItems.get(sessionId).setStreamEnabled(peerConnectionEvent.getChangeValue().booleanValue());
            }
            this.participantsAdapter.notifyDataSetChanged();
        } else if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.AUDIO_CHANGE) {
            if (this.participantDisplayItems.get(sessionId) != null) {
                this.participantDisplayItems.get(sessionId).setAudioEnabled(peerConnectionEvent.getChangeValue().booleanValue());
            }
            this.participantsAdapter.notifyDataSetChanged();
        } else if (peerConnectionEvent.getPeerConnectionEventType() == PeerConnectionEvent.PeerConnectionEventType.PUBLISHER_FAILED) {
            this.currentCallStatus = CallStatus.PUBLISHER_FAILED;
            this.webSocketClient.clearResumeId();
            hangup(false);
        }
    }

    private void startSendingNick() {
        final DataChannelMessageNick dataChannelMessageNick = new DataChannelMessageNick();
        dataChannelMessageNick.setType("nickChanged");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userid", this.conversationUser.getUserId());
        hashMap.put("name", this.conversationUser.getDisplayName());
        dataChannelMessageNick.setPayload(hashMap);
        for (int i = 0; i < this.peerConnectionWrapperList.size(); i++) {
            if (this.peerConnectionWrapperList.get(i).isMCUPublisher()) {
                final PeerConnectionWrapper peerConnectionWrapper = this.peerConnectionWrapperList.get(i);
                Observable.interval(1L, TimeUnit.SECONDS).repeatUntil(new BooleanSupplier() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$Fos71wvyanTn4TlITaT9rsd50b4
                    @Override // io.reactivex.functions.BooleanSupplier
                    public final boolean getAsBoolean() {
                        return CallActivity.this.lambda$startSendingNick$12$CallActivity();
                    }
                }).observeOn(Schedulers.io()).subscribe(new Observer<Long>() { // from class: com.nextcloud.talk.activities.CallActivity.16
                    @Override // io.reactivex.Observer
                    public void onComplete() {
                    }

                    @Override // io.reactivex.Observer
                    public void onError(Throwable th) {
                    }

                    @Override // io.reactivex.Observer
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override // io.reactivex.Observer
                    public void onNext(Long l) {
                        peerConnectionWrapper.sendNickChannelData(dataChannelMessageNick);
                    }
                });
                return;
            }
        }
    }

    public /* synthetic */ boolean lambda$startSendingNick$12$CallActivity() throws Exception {
        return !isConnectionEstablished() || isDestroyed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MediaStreamEvent mediaStreamEvent) {
        boolean z = false;
        if (mediaStreamEvent.getMediaStream() != null) {
            if (mediaStreamEvent.getMediaStream().videoTracks != null && mediaStreamEvent.getMediaStream().videoTracks.size() > 0) {
                z = true;
            }
            setupVideoStreamForLayout(mediaStreamEvent.getMediaStream(), mediaStreamEvent.getSession(), z, mediaStreamEvent.getVideoStreamType());
            return;
        }
        setupVideoStreamForLayout(null, mediaStreamEvent.getSession(), false, mediaStreamEvent.getVideoStreamType());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(SessionDescriptionSendEvent sessionDescriptionSendEvent) throws IOException {
        NCMessageWrapper nCMessageWrapper = new NCMessageWrapper();
        nCMessageWrapper.setEv("message");
        nCMessageWrapper.setSessionId(this.callSession);
        NCSignalingMessage nCSignalingMessage = new NCSignalingMessage();
        nCSignalingMessage.setTo(sessionDescriptionSendEvent.getPeerId());
        nCSignalingMessage.setRoomType(sessionDescriptionSendEvent.getVideoStreamType());
        nCSignalingMessage.setType(sessionDescriptionSendEvent.getType());
        NCMessagePayload nCMessagePayload = new NCMessagePayload();
        nCMessagePayload.setType(sessionDescriptionSendEvent.getType());
        if (!"candidate".equals(sessionDescriptionSendEvent.getType())) {
            nCMessagePayload.setSdp(sessionDescriptionSendEvent.getSessionDescription().description);
            nCMessagePayload.setNick(this.conversationUser.getDisplayName());
        } else {
            nCMessagePayload.setIceCandidate(sessionDescriptionSendEvent.getNcIceCandidate());
        }
        nCSignalingMessage.setPayload(nCMessagePayload);
        nCMessageWrapper.setSignalingMessage(nCSignalingMessage);
        if (!this.hasExternalSignalingServer) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("{\"fn\":\"" + StringEscapeUtils.escapeJson(LoganSquare.serialize(nCMessageWrapper.getSignalingMessage())) + "\",\"sessionId\":\"" + StringEscapeUtils.escapeJson(this.callSession) + "\",\"ev\":\"message\"}");
            this.ncApi.sendSignalingMessages(this.credentials, ApiUtils.getUrlForSignaling(ApiUtils.getSignalingApiVersion(this.conversationUser, new int[]{3, 2, 1}), this.baseUrl, this.roomToken), arrayList.toString()).retry(3L).subscribeOn(Schedulers.io()).subscribe(new Observer<SignalingOverall>() { // from class: com.nextcloud.talk.activities.CallActivity.17
                @Override // io.reactivex.Observer
                public void onComplete() {
                }

                @Override // io.reactivex.Observer
                public void onSubscribe(Disposable disposable) {
                }

                @Override // io.reactivex.Observer
                public void onNext(SignalingOverall signalingOverall) {
                    CallActivity.this.receivedSignalingMessages(signalingOverall.getOcs().getSignalings());
                }

                @Override // io.reactivex.Observer
                public void onError(Throwable th) {
                    Log.e(CallActivity.TAG, "", th);
                }
            });
            return;
        }
        this.webSocketClient.sendCallMessage(nCMessageWrapper);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EffortlessPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }

    private void setupVideoStreamForLayout(MediaStream mediaStream, String str, boolean z, String str2) {
        String nick;
        String urlForGuestAvatar;
        String str3 = "";
        if (this.hasExternalSignalingServer) {
            nick = this.webSocketClient.getDisplayNameForSession(str);
        } else {
            PeerConnectionWrapper peerConnectionWrapperForSessionIdAndType = getPeerConnectionWrapperForSessionIdAndType(str, str2);
            nick = peerConnectionWrapperForSessionIdAndType != null ? peerConnectionWrapperForSessionIdAndType.getNick() : "";
        }
        String str4 = nick;
        if (this.hasMCU) {
            str3 = this.webSocketClient.getUserIdForSession(str);
        } else if (this.participantMap.get(str).getActorType() == Participant.ActorType.USERS) {
            str3 = this.participantMap.get(str).getActorId();
        }
        String str5 = str3;
        if (!TextUtils.isEmpty(str5)) {
            urlForGuestAvatar = ApiUtils.getUrlForAvatar(this.baseUrl, str5, true);
        } else {
            urlForGuestAvatar = ApiUtils.getUrlForGuestAvatar(this.baseUrl, str4, true);
        }
        this.participantDisplayItems.put(str, new ParticipantDisplayItem(str5, str, str4, urlForGuestAvatar, mediaStream, str2, z, this.rootEglBase));
        initGridAdapter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCallState(CallStatus callStatus) {
        CallStatus callStatus2 = this.currentCallStatus;
        if (callStatus2 == null || !callStatus2.equals(callStatus)) {
            this.currentCallStatus = callStatus;
            Handler handler = this.handler;
            if (handler == null) {
                this.handler = new Handler(Looper.getMainLooper());
            } else {
                handler.removeCallbacksAndMessages(null);
            }
            switch (AnonymousClass19.$SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[callStatus.ordinal()]) {
                case 1:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$GI3TGFIA3oBzLAmCHWMxENx1lXU
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$13$CallActivity();
                        }
                    });
                    return;
                case 2:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$Cg2j8oL6MMCsZoSeVJR3UWzX88Q
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$14$CallActivity();
                        }
                    });
                    return;
                case 3:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$oeaywW_Lds9BOCjdpzfnrpK-eEc
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$15$CallActivity();
                        }
                    });
                    return;
                case 4:
                    this.handler.postDelayed(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$6lZgQIaaEMjpqLNLOFtQ4saj0og
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$16$CallActivity();
                        }
                    }, 45000L);
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$h2fWAJdB_6WedahR5LJW9U86uNo
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$17$CallActivity();
                        }
                    });
                    return;
                case 5:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$dQnHSE6yxHeDPuH2UaFJCGht5Is
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$18$CallActivity();
                        }
                    });
                    return;
                case 6:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$y2_HfU5Io9sHZVaA8IHYfOd2nRU
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$19$CallActivity();
                        }
                    });
                    return;
                case 7:
                    this.handler.post(new Runnable() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$NkzMFrWPgotoNHYM-n4jJeIVIvM
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallActivity.this.lambda$setCallState$20$CallActivity();
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.nextcloud.talk.activities.CallActivity$19, reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass19 {
        static final /* synthetic */ int[] $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus;
        static final /* synthetic */ int[] $SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice;

        static {
            int[] iArr = new int[CallStatus.values().length];
            $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus = iArr;
            try {
                iArr[CallStatus.CONNECTING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.CALLING_TIMEOUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.RECONNECTING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.JOINED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.IN_CONVERSATION.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.OFFLINE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$activities$CallActivity$CallStatus[CallStatus.LEAVING.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            int[] iArr2 = new int[MagicAudioManager.AudioDevice.values().length];
            $SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice = iArr2;
            try {
                iArr2[MagicAudioManager.AudioDevice.BLUETOOTH.ordinal()] = 1;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice[MagicAudioManager.AudioDevice.SPEAKER_PHONE.ordinal()] = 2;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice[MagicAudioManager.AudioDevice.EARPIECE.ordinal()] = 3;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$nextcloud$talk$webrtc$MagicAudioManager$AudioDevice[MagicAudioManager.AudioDevice.WIRED_HEADSET.ordinal()] = 4;
            } catch (NoSuchFieldError unused11) {
            }
        }
    }

    public /* synthetic */ void lambda$setCallState$13$CallActivity() {
        playCallingSound();
        if (this.isIncomingCallFromNotification) {
            this.binding.callStates.callStateTextView.setText(R.string.nc_call_incoming);
        } else {
            this.binding.callStates.callStateTextView.setText(R.string.nc_call_ringing);
        }
        this.binding.callConversationNameTextView.setText(this.conversationName);
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 0) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        }
        if (this.binding.gridview.getVisibility() != 4) {
            this.binding.gridview.setVisibility(4);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 0) {
            this.binding.callStates.callStateProgressBar.setVisibility(0);
        }
        if (this.binding.callStates.errorImageView.getVisibility() != 8) {
            this.binding.callStates.errorImageView.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$setCallState$14$CallActivity() {
        hangup(false);
        this.binding.callStates.callStateTextView.setText(R.string.nc_call_timeout);
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 0) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 8) {
            this.binding.callStates.callStateProgressBar.setVisibility(8);
        }
        if (this.binding.gridview.getVisibility() != 4) {
            this.binding.gridview.setVisibility(4);
        }
        this.binding.callStates.errorImageView.setImageResource(R.drawable.ic_av_timer_timer_24dp);
        if (this.binding.callStates.errorImageView.getVisibility() != 0) {
            this.binding.callStates.errorImageView.setVisibility(0);
        }
    }

    public /* synthetic */ void lambda$setCallState$15$CallActivity() {
        playCallingSound();
        this.binding.callStates.callStateTextView.setText(R.string.nc_call_reconnecting);
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 0) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        }
        if (this.binding.gridview.getVisibility() != 4) {
            this.binding.gridview.setVisibility(4);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 0) {
            this.binding.callStates.callStateProgressBar.setVisibility(0);
        }
        if (this.binding.callStates.errorImageView.getVisibility() != 8) {
            this.binding.callStates.errorImageView.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$setCallState$16$CallActivity() {
        setCallState(CallStatus.CALLING_TIMEOUT);
    }

    public /* synthetic */ void lambda$setCallState$17$CallActivity() {
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        if (this.isIncomingCallFromNotification) {
            this.binding.callStates.callStateTextView.setText(R.string.nc_call_incoming);
        } else {
            this.binding.callStates.callStateTextView.setText(R.string.nc_call_ringing);
        }
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 0) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 0) {
            this.binding.callStates.callStateProgressBar.setVisibility(0);
        }
        if (this.binding.gridview.getVisibility() != 4) {
            this.binding.gridview.setVisibility(4);
        }
        if (this.binding.callStates.errorImageView.getVisibility() != 8) {
            this.binding.callStates.errorImageView.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$setCallState$18$CallActivity() {
        stopCallingSound();
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        if (!this.isVoiceOnlyCall) {
            this.binding.callInfosLinearLayout.setVisibility(8);
        }
        if (!this.isPTTActive) {
            animateCallControls(false, 5000L);
        }
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 4) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(4);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 8) {
            this.binding.callStates.callStateProgressBar.setVisibility(8);
        }
        if (this.binding.gridview.getVisibility() != 0) {
            this.binding.gridview.setVisibility(0);
        }
        if (this.binding.callStates.errorImageView.getVisibility() != 8) {
            this.binding.callStates.errorImageView.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$setCallState$19$CallActivity() {
        stopCallingSound();
        this.binding.callStates.callStateTextView.setText(R.string.nc_offline);
        if (this.binding.callStates.callStateRelativeLayout.getVisibility() != 0) {
            this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        }
        if (this.binding.gridview.getVisibility() != 4) {
            this.binding.gridview.setVisibility(4);
        }
        if (this.binding.callStates.callStateProgressBar.getVisibility() != 8) {
            this.binding.callStates.callStateProgressBar.setVisibility(8);
        }
        this.binding.callStates.errorImageView.setImageResource(R.drawable.ic_signal_wifi_off_white_24dp);
        if (this.binding.callStates.errorImageView.getVisibility() != 0) {
            this.binding.callStates.errorImageView.setVisibility(0);
        }
    }

    public /* synthetic */ void lambda$setCallState$20$CallActivity() {
        if (isDestroyed()) {
            return;
        }
        stopCallingSound();
        this.binding.callModeTextView.setText(getDescriptionForCallType());
        this.binding.callStates.callStateTextView.setText(R.string.nc_leaving_call);
        this.binding.callStates.callStateRelativeLayout.setVisibility(0);
        this.binding.gridview.setVisibility(4);
        this.binding.callStates.callStateProgressBar.setVisibility(0);
        this.binding.callStates.errorImageView.setVisibility(8);
    }

    private String getDescriptionForCallType() {
        String string = getResources().getString(R.string.nc_app_product_name);
        return this.isVoiceOnlyCall ? String.format(getResources().getString(R.string.nc_call_voice), string) : String.format(getResources().getString(R.string.nc_call_video), string);
    }

    private void playCallingSound() {
        Uri parse;
        stopCallingSound();
        if (this.isIncomingCallFromNotification) {
            parse = NotificationUtils.INSTANCE.getCallRingtoneUri(getApplicationContext(), this.appPreferences);
        } else {
            parse = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/raw/tr110_1_kap8_3_freiton1");
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mediaPlayer = mediaPlayer;
        try {
            mediaPlayer.setDataSource(this, parse);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(4).setUsage(2).build());
            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.nextcloud.talk.activities.-$$Lambda$CallActivity$lTmp1O5FKYYNhJ8qY8JG5aCGIVs
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer2) {
                    CallActivity.this.lambda$playCallingSound$21$CallActivity(mediaPlayer2);
                }
            });
            this.mediaPlayer.prepareAsync();
        } catch (IOException unused) {
            Log.e(TAG, "Failed to play sound");
        }
    }

    public /* synthetic */ void lambda$playCallingSound$21$CallActivity(MediaPlayer mediaPlayer) {
        this.mediaPlayer.start();
    }

    private void stopCallingSound() {
        MediaPlayer mediaPlayer = this.mediaPlayer;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /* loaded from: classes2.dex */
    private class MicrophoneButtonTouchListener implements View.OnTouchListener {
        private MicrophoneButtonTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == 1 && CallActivity.this.isPTTActive) {
                CallActivity.this.isPTTActive = false;
                CallActivity.this.binding.microphoneButton.getHierarchy().setPlaceholderImage(R.drawable.ic_mic_off_white_24px);
                CallActivity.this.pulseAnimation.stop();
                CallActivity.this.toggleMedia(false, false);
                CallActivity.this.animateCallControls(false, 5000L);
            }
            return true;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(NetworkEvent networkEvent) {
        Handler handler;
        if (networkEvent.getNetworkConnectionEvent() == NetworkEvent.NetworkConnectionEvent.NETWORK_CONNECTED) {
            Handler handler2 = this.handler;
            if (handler2 != null) {
                handler2.removeCallbacksAndMessages(null);
                return;
            }
            return;
        }
        if (networkEvent.getNetworkConnectionEvent() != NetworkEvent.NetworkConnectionEvent.NETWORK_DISCONNECTED || (handler = this.handler) == null) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override // android.app.Activity
    public void onPictureInPictureModeChanged(boolean z, Configuration configuration) {
        super.onPictureInPictureModeChanged(z, configuration);
        Log.d(TAG, "onPictureInPictureModeChanged");
        Log.d(TAG, "isInPictureInPictureMode= " + z);
        this.isInPipMode = Boolean.valueOf(z);
        if (z) {
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.nextcloud.talk.activities.CallActivity.18
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || !CallActivity.MICROPHONE_PIP_INTENT_NAME.equals(intent.getAction())) {
                        return;
                    }
                    int intExtra = intent.getIntExtra(CallActivity.MICROPHONE_PIP_INTENT_EXTRA_ACTION, 0);
                    if (intExtra == 1 || intExtra == 2) {
                        CallActivity.this.onMicrophoneClick();
                    }
                }
            };
            this.mReceiver = broadcastReceiver;
            registerReceiver(broadcastReceiver, new IntentFilter(MICROPHONE_PIP_INTENT_NAME));
            updateUiForPipMode();
            return;
        }
        unregisterReceiver(this.mReceiver);
        this.mReceiver = null;
        updateUiForNormalMode();
    }

    void updatePictureInPictureActions(int i, String str, int i2) {
        if (isGreaterEqualOreo() && isPipModePossible()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new RemoteAction(Icon.createWithResource(this, i), str, str, PendingIntent.getBroadcast(this, i2, new Intent(MICROPHONE_PIP_INTENT_NAME).putExtra(MICROPHONE_PIP_INTENT_EXTRA_ACTION, i2), 0)));
            this.mPictureInPictureParamsBuilder.setActions(arrayList);
            setPictureInPictureParams(this.mPictureInPictureParamsBuilder.build());
        }
    }

    @Override // com.nextcloud.talk.activities.CallBaseActivity
    public void updateUiForPipMode() {
        Log.d(TAG, "updateUiForPipMode");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, 0, 0, 0);
        this.binding.gridview.setLayoutParams(layoutParams);
        this.binding.callControls.setVisibility(8);
        this.binding.callInfosLinearLayout.setVisibility(8);
        this.binding.selfVideoViewWrapper.setVisibility(8);
        this.binding.callStates.callStateRelativeLayout.setVisibility(8);
        if (this.participantDisplayItems.size() > 1) {
            this.binding.pipCallConversationNameTextView.setText(this.conversationName);
            this.binding.pipGroupCallOverlay.setVisibility(0);
        } else {
            this.binding.pipGroupCallOverlay.setVisibility(8);
        }
        this.binding.selfVideoRenderer.release();
    }

    @Override // com.nextcloud.talk.activities.CallBaseActivity
    public void updateUiForNormalMode() {
        Log.d(TAG, "updateUiForNormalMode");
        if (this.isVoiceOnlyCall) {
            this.binding.callControls.setVisibility(0);
        } else {
            this.binding.callControls.setVisibility(4);
        }
        initViews();
        this.binding.callInfosLinearLayout.setVisibility(0);
        this.binding.selfVideoViewWrapper.setVisibility(0);
        this.binding.pipGroupCallOverlay.setVisibility(8);
    }

    @Override // com.nextcloud.talk.activities.CallBaseActivity
    void suppressFitsSystemWindows() {
        this.binding.controllerCallLayout.setFitsSystemWindows(false);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.eventBus.post(new ConfigurationChangeEvent());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SelfVideoTouchListener implements View.OnTouchListener {
        private SelfVideoTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            long eventTime = motionEvent.getEventTime() - motionEvent.getDownTime();
            if (motionEvent.getActionMasked() == 2) {
                float rawY = motionEvent.getRawY() - (CallActivity.this.binding.selfVideoViewWrapper.getHeight() / 2.0f);
                float rawX = motionEvent.getRawX() - (CallActivity.this.binding.selfVideoViewWrapper.getWidth() / 2.0f);
                CallActivity.this.binding.selfVideoViewWrapper.setY(rawY);
                CallActivity.this.binding.selfVideoViewWrapper.setX(rawX);
            } else if (motionEvent.getActionMasked() == 1 && eventTime < 100) {
                CallActivity.this.switchCamera();
            }
            return true;
        }
    }
}
