package im.summerhao.com.myapplication.activity;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

import im.summerhao.com.myapplication.manager.XmppConnectionManager;

/**
 *
 * Created by lenovo on 2015/10/13.
 */
public class IMApplication extends Application{

    private List<Activity> activityList = new LinkedList<Activity>();
    public static IMApplication im;

    @Override
    public void onCreate() {
        super.onCreate();
        im = this;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        XmppConnectionManager.getInstance().disconnect();
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
