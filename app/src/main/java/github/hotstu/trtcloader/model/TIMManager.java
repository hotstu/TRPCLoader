package github.hotstu.trtcloader.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author songwd
 * @desc
 * @since 3/16/20
 */
public class TIMManager {

    private static TIMManager sInstance;
    public static TIMManager getInstance() {
        if (sInstance == null) {
            synchronized (TIMManager.class) {
                if (sInstance == null) {
                    sInstance = new TIMManager();
                }
            }
        }
        return sInstance;
    }

    private List<TIMMessageListener> listeners = new ArrayList<>();

    public boolean isInited() {
        return true;
    }

    public void sendMsg(TRTCVideoCallImpl.CallModel model) {
        TIMMessage message = new TIMMessage();
        message.data = new Gson().toJson(model);
        message.sender = ProfileManager.getInstance().getUserId();
        message.timestamp = System.currentTimeMillis();
        for (TIMMessageListener listener : listeners) {
            listener.onNewMessages(message);
        }

    }
    public void addMessageListener(TIMMessageListener listener) {
        if (listeners.indexOf(listener) == -1) {
            listeners.add(listener);
        }
    }

    public void removeMessageListener(TIMMessageListener listener) {
        listeners.remove(listener);
    }
}
