package github.hotstu.trtcloader.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import github.hotstu.trtcloader.R;
import github.hotstu.trtcloader.model.ITRTCVideoCall;
import github.hotstu.trtcloader.model.ProfileManager;
import github.hotstu.trtcloader.model.TIMManager;
import github.hotstu.trtcloader.model.TRTCVideoCallImpl;
import github.hotstu.trtcloader.model.UserModel;

/**
 * 用于选择联系人
 *
 * @author guanyifeng
 */
public class SelectContactActivity extends AppCompatActivity {
    private static final String PER_SEARCH = "search_contact";
    private static final String PER_MODEL = "search_user_model";

    private TextView mCompleteBtn;
    private Toolbar mToolbar;
    private EditText mNameEt;
    private RecyclerView mSearchRv;
    private ProfileManager.NetworkAction mSearchCall;
    private ITRTCVideoCall mITRTCVideoCall;

    public static void start(Context context) {
        Intent starter = new Intent(context, SelectContactActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_activity_select_contact);
        initView();
        initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.MICROPHONE, PermissionConstants.CAMERA)
                    .request();
        }
    }

    private void initView() {
        mCompleteBtn = (TextView) findViewById(R.id.btn_complete);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNameEt = (EditText) findViewById(R.id.et_name);
        View btn_login = findViewById(R.id.btn_login);
        View being_call = findViewById(R.id.btn_being_call);
        being_call.setOnClickListener(v -> {
            EditText et_calling = (EditText) findViewById(R.id.et_calling);
            TRTCVideoCallImpl.CallModel callModel;
            try {
                callModel = new Gson().fromJson(et_calling.getText().toString(), TRTCVideoCallImpl.CallModel.class);
            } catch (Exception e) {
                Toast.makeText(SelectContactActivity.this, "解析失败", Toast.LENGTH_LONG).show();
                return;
            }
            TIMManager.getInstance().sendMsg(callModel);

        });
        View call = findViewById(R.id.btn_call);
        call.setOnClickListener(v -> {
            EditText et_being_name = findViewById(R.id.et_being_name);
            ProfileManager.getInstance().getUserInfoByUserId(et_being_name.getText().toString(), new ProfileManager.GetUserInfoCallback() {
                @Override
                public void onSuccess(UserModel model) {
                    List<UserModel> mContactList = new ArrayList<>();
                    mContactList.add(model);
                    TRTCVideoCallActivity.startCallSomeone(SelectContactActivity.this, mContactList);
                }

                @Override
                public void onFailed(int code, String msg) {

                }
            });

        });
        btn_login.setOnClickListener(v -> {
            String userId = mNameEt.getText().toString();
            ProfileManager.getInstance().login(userId, "", new ProfileManager.ActionCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailed(int code, String msg) {
                }
            });
        });
        mCompleteBtn.setOnClickListener(v -> {
            EditText et_being_name = (EditText) findViewById(R.id.et_being_name);

            ProfileManager.getInstance().getUserInfoByUserId(et_being_name.getText().toString(), new ProfileManager.GetUserInfoCallback() {
                @Override
                public void onSuccess(UserModel model) {
                    List<UserModel> mContactList = new ArrayList<>();
                    mContactList.add(model);
                    TRTCVideoCallActivity.startCallSomeone(SelectContactActivity.this, mContactList);
                }

                @Override
                public void onFailed(int code, String msg) {

                }
            });
        });

        mToolbar.setNavigationOnClickListener(v -> finish());
    }

}
