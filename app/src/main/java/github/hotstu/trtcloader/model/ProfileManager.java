package github.hotstu.trtcloader.model;

import android.text.TextUtils;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import github.hotstu.trtcloader.App;
import github.hotstu.trtcloader.GenerateTestUserSig;
import github.hotstu.trtcloader.ui.TRTCVideoCallActivity;

public class ProfileManager {
    private static final ProfileManager ourInstance = new ProfileManager();


    private final static String PER_DATA       = "per_profile_manager";
    private final static String PER_USER_MODEL = "per_user_model";
    private static final String PER_USER_ID    = "per_user_id";
    private static final String PER_TOKEN      = "per_user_token";
    private static final String TAG            = ProfileManager.class.getName();

    private UserModel mUserModel;
    private String mUserId;
    private String mToken;
    private boolean   isLogin = false;
    private TRTCVideoCallListener mTRTCVideoCallListener = new TRTCVideoCallListener() {
        // <editor-fold  desc="视频监听代码">
        @Override
        public void onError(int code, String msg) {
        }

        @Override
        public void onInvited(String sponsor, final List<String> userIdList, boolean isFromGroup, int callType) {
            //1. 收到邀请，先到服务器查询
            ProfileManager.getInstance().getUserInfoByUserId(sponsor, new ProfileManager.GetUserInfoCallback() {
                @Override
                public void onSuccess(final UserModel model) {
                    if (!CollectionUtils.isEmpty(userIdList)) {
                        ProfileManager.getInstance().getUserInfoBatch(userIdList, new ProfileManager.GetUserInfoBatchCallback() {
                            @Override
                            public void onSuccess(List<UserModel> modelList) {
                                TRTCVideoCallActivity.startBeingCall(App.sInstance, model, modelList);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                TRTCVideoCallActivity.startBeingCall(App.sInstance, model, null);
                            }
                        });
                    } else {
                        TRTCVideoCallActivity.startBeingCall(App.sInstance, model, null);
                    }
                }

                @Override
                public void onFailed(int code, String msg) {

                }
            });
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {

        }

        @Override
        public void onUserEnter(String userId) {

        }

        @Override
        public void onUserLeave(String userId) {

        }

        @Override
        public void onReject(String userId) {

        }

        @Override
        public void onNoResp(String userId) {

        }

        @Override
        public void onLineBusy(String userId) {

        }

        @Override
        public void onCallingCancel() {

        }

        @Override
        public void onCallingTimeout() {

        }

        @Override
        public void onCallEnd() {

        }

        @Override
        public void onUserVideoAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {

        }
        // </editor-fold  desc="视频监听代码">
    };


    public static ProfileManager getInstance() {
        return ourInstance;
    }

    private ProfileManager() {
    }

    public boolean isLogin() {
        return isLogin;
    }

    public UserModel getUserModel() {
        if (mUserModel == null) {
            loadUserModel();
        }
        return mUserModel;
    }

    public String getUserId() {
        if (mUserId == null) {
            mUserId = SPUtils.getInstance(PER_DATA).getString(PER_USER_ID, "");
        }
        return mUserId;
    }

    private void setUserId(String userId) {
        mUserId = userId;
        SPUtils.getInstance(PER_DATA).put(PER_USER_ID, userId);
    }

    private void setUserModel(UserModel model) {
        mUserModel = model;
        saveUserModel();
    }

    public String getToken() {
        if (mToken == null) {
            loadToken();
        }
        return mToken;
    }

    private void setToken(String token) {
        mToken = token;
        SPUtils.getInstance(PER_DATA).put(PER_TOKEN, mToken);
    }

    private void loadToken() {
        mToken = SPUtils.getInstance(PER_DATA).getString(PER_TOKEN, "");
    }


    public void getSms(String phone, final ActionCallback callback) {
        callback.onSuccess();
    }

    public void logout(final ActionCallback callback) {
        setUserId("");
        isLogin = false;
        ITRTCVideoCall mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(App.sInstance);
        mITRTCVideoCall.removeListener(mTRTCVideoCallListener);
        TRTCVideoCallImpl.destroySharedInstance();
        callback.onSuccess();
    }

    public void login(String userId, String sms, final ActionCallback callback) {
        if (isLogin) {
            callback.onSuccess();
            return;
        }
        isLogin = true;
        setUserId(userId);
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.userName = userId;
        userModel.phone = userId;
        userModel.userId = userId;
        setUserModel(userModel);
        initVideoCallData();
        callback.onSuccess();
    }

    public void autoLogin(String userId, String token, final ActionCallback callback) {
        isLogin = true;
        setUserId(userId);
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.userName = userId;
        userModel.phone = userId;
        userModel.userId = userId;
        setUserModel(userModel);
        initVideoCallData();
        callback.onSuccess();
    }


    private void initVideoCallData() {
        ITRTCVideoCall mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(App.sInstance);
        mITRTCVideoCall.init();
        mITRTCVideoCall.addListener(mTRTCVideoCallListener);
        int    appid   = GenerateTestUserSig.SDKAPPID;
        String userId  = ProfileManager.getInstance().getUserModel().userId;
        String userSig = GenerateTestUserSig.genTestUserSig(userId);
        mITRTCVideoCall.login(appid, userId, userSig, null);
    }

    public NetworkAction getUserInfoByUserId(String userId, final GetUserInfoCallback callback) {
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(userId);
        userModel.phone = userId;
        userModel.userId = userId;
        userModel.userName = userId;
        callback.onSuccess(userModel);
        return new NetworkAction();
    }

    public NetworkAction getUserInfoByPhone(String phone, final GetUserInfoCallback callback) {
        UserModel userModel = new UserModel();
        userModel.userAvatar = getAvatarUrl(phone);
        userModel.phone = phone;
        userModel.userId = phone;
        userModel.userName = phone;
        callback.onSuccess(userModel);
        return new NetworkAction();
    }

    public void getUserInfoBatch(List<String> userIdList, final GetUserInfoBatchCallback callback) {
        if (userIdList == null) {
            return;
        }
        List<UserModel> userModelList = new ArrayList<>();
        for (String userId : userIdList) {
            UserModel userModel = new UserModel();
            userModel.userAvatar = getAvatarUrl(userId);
            userModel.phone = userId;
            userModel.userId = userId;
            userModel.userName = userId;
            userModelList.add(userModel);
        }
        callback.onSuccess(userModelList);
    }

    private String getAvatarUrl(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        byte[] bytes = userId.getBytes();
        int    index = bytes[bytes.length - 1] % 10;
        String avatarName = "avatar" + index + "_100";
        return "https://imgcache.qq.com/qcloud/public/static//" + avatarName + ".20191230.png";
    }

    private void saveUserModel() {
        try {
            SPUtils.getInstance(PER_DATA).put(PER_USER_MODEL, GsonUtils.toJson(mUserModel));
        } catch (Exception e) {
        }
    }

    private void loadUserModel() {
        try {
            String json = SPUtils.getInstance(PER_DATA).getString(PER_USER_MODEL);
            mUserModel = GsonUtils.fromJson(json, UserModel.class);
        } catch (Exception e) {
        }
    }

    public static class NetworkAction {

        public NetworkAction() {
        }

        public void cancel() {
        }
    }

    // 操作回调
    public interface ActionCallback {
        void onSuccess();

        void onFailed(int code, String msg);
    }

    // 通过userid/phone获取用户信息回调
    public interface GetUserInfoCallback {
        void onSuccess(UserModel model);

        void onFailed(int code, String msg);
    }

    // 通过userId批量获取用户信息回调
    public interface GetUserInfoBatchCallback {
        void onSuccess(List<UserModel> model);

        void onFailed(int code, String msg);
    }
}
