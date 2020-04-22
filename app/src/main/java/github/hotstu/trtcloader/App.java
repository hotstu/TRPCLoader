package github.hotstu.trtcloader;

import android.app.Application;


public class App extends Application {
    public static App sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
