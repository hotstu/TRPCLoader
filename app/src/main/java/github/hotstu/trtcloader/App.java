package github.hotstu.trtcloader;

import android.app.Application;

/**
 * @author songwd
 * @desc
 * @since 3/17/20
 */
public class App extends Application {
    public static App sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
