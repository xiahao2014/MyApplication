package im.summerhao.com.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import im.summerhao.com.myapplication.mode.LoginConfig;

/**
 * Activity 帮助支持接口
 * Created by lenovo on 2015/10/13.
 *
 */
public interface IActivitySupport {
    /**
     *
     * 获取EimApplication.
     *
     *
     */
    public abstract IMApplication getImApplication();

    /**
     *
     * 终止服务.
     *
     *
     */
    public abstract void stopService();

    /**
     *
     * 开启服务.
     *
     *
     */
    public abstract void startService();

    /**
     *
     * 校验网络-如果没有网络就弹出设置,并返回true.
     *
     *
     */
    public abstract boolean validateInternet();

    /**
     *
     * 校验网络-如果没有网络就返回true.
     *
     *
     */
    public abstract boolean hasInternetConnected();

    /**
     *
     * 退出应用.
     *
     *
     */
    public abstract void isExit();

    /**
     *
     * 判断GPS是否已经开启.
     *
     *
     */
    public abstract boolean hasLocationGPS();

    /**
     *
     * 判断基站是否已经开启.
     *
     *
     */
    public abstract boolean hasLocationNetWork();

    /**
     *
     * 检查内存卡.
     *
     *
     */
    public abstract void checkMemoryCard();

    /**
     *
     * 显示toast.
     *
     * @param text
     *            内容
     * @param longint
     *            内容显示多长时间
     *
     */
    public abstract void showToast(String text, int longint);

    /**
     *
     * 短时间显示toast.
     *
     * @param text
     *
     */
    public abstract void showToast(String text);

    /**
     *
     * 获取进度条.
     *
     * @return
     *
     */
    public abstract ProgressDialog getProgressDialog();

    /**
     *
     * 返回当前Activity上下文.
     *
     * @return
     *
     */
    public abstract Context getContext();

    /**
     *
     * 获取当前登录用户的SharedPreferences配置.
     *
     * @return
     *
     */
    public SharedPreferences getLoginUserSharedPre();

    /**
     *
     * 保存用户配置.
     *
     * @param loginConfig
     *
     */
    public void saveLoginConfig(LoginConfig loginConfig);

    /**
     *
     * 获取用户配置.
     *
     * @param
     *
     */
    public LoginConfig getLoginConfig();

    /**
     *
     * 用户是否在线（当前网络是否重连成功）
     *
     * @param
     *
     */
    public boolean getUserOnlineState();

    /**
     * 设置用户在线状态 true 在线 false 不在线
     *
     * @param isOnline
     */
    public void setUserOnlineState(boolean isOnline);

    /**
     *
     * 发出Notification的method.
     *
     * @param iconId
     *            图标
     * @param contentTitle
     *            标题
     * @param contentText
     *            你内容
     * @param activity
     *
     */
    @SuppressWarnings("rawtypes")
    public void setNotiType(int iconId, String contentTitle,
                            String contentText, Class activity, String from);
}
