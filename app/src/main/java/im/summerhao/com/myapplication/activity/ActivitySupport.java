package im.summerhao.com.myapplication.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.mode.LoginConfig;
import im.summerhao.com.myapplication.service.IMChatService;
import im.summerhao.com.myapplication.service.IMContactService;
import im.summerhao.com.myapplication.ui.ToolBarActivity;

public class ActivitySupport extends ToolBarActivity implements IActivitySupport {


    protected Context context = null;
    protected SharedPreferences preferences;
    protected IMApplication imApplication;
    protected ProgressDialog pg = null;
    protected NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pg = new ProgressDialog(context);
        imApplication = (IMApplication) getApplication();
        imApplication.addActivity(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return pg;
    }


    @Override
    public IMApplication getImApplication() {
        return imApplication;
    }

    @Override
    public void startService() {

        // 好友联系人服务
        Intent server = new Intent(context, IMContactService.class);
        context.startService(server);
        // 聊天服务
        Intent chatServer = new Intent(context, IMChatService.class);
        context.startService(chatServer);
//        // 自动恢复连接服务
//        Intent reConnectService = new Intent(context, ReConnectService.class);
//        context.startService(reConnectService);
//        // 系统消息连接服务
//        Intent imSystemMsgService = new Intent(context,	IMSystemMsgService.class);
//        context.startService(imSystemMsgService);
//        System.out.println("开启服务");

    }

    @Override
    public void stopService() {

        // 好友联系人服务
        Intent server = new Intent(context, IMContactService.class);
        context.stopService(server);
        // 聊天服务
        Intent chatServer = new Intent(context, IMChatService.class);
        context.stopService(chatServer);


//        // 自动恢复连接服务
//        Intent reConnectService = new Intent(context, ReConnectService.class);
//        context.stopService(reConnectService);
//        // 系统消息连接服务
//        Intent imSystemMsgService = new Intent(context, IMSystemMsgService.class);
//        context.stopService(imSystemMsgService);
//        System.out.println("销毁服务");

    }

    @Override
    public void isExit() {
        new AlertDialog.Builder(context).setTitle("确定退出吗?")
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService();
                        imApplication.exit();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

    }


    @Override
    public boolean hasInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateInternet() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            openWirelessSet();
            return false;
        } else {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        openWirelessSet();
        return false;
    }


    @Override
    public boolean hasLocationGPS() {
        LocationManager manager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (manager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasLocationNetWork() {
        LocationManager manager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (manager
                .isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void checkMemoryCard() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.prompt)
                    .setMessage("请检查内存卡")
                    .setPositiveButton(R.string.menu_settings,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                    Intent intent = new Intent(
                                            Settings.ACTION_SETTINGS);
                                    context.startActivity(intent);
                                }
                            })
                    .setNegativeButton("退出",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                    imApplication.exit();
                                }
                            }).create().show();
        }
    }

    /**
     * 打开无线网络设置界面
     */
    public void openWirelessSet() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder
                .setTitle(R.string.prompt)
                .setMessage(context.getString(R.string.check_connection))
                .setPositiveButton(R.string.menu_settings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                                Intent intent = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                context.startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.close,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        });
        dialogBuilder.show();
    }

    @Override
    public void showToast(String text, int longint) {
        Toast.makeText(context, text, longint).show();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭键盘事件
     */
    public void closeInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && this.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public SharedPreferences getLoginUserSharedPre() {
        return preferences;
    }

    @Override
    public void saveLoginConfig(LoginConfig loginConfig) {
        preferences.edit()
                .putString(Constant.XMPP_HOST, loginConfig.getXmppHost())
                .apply();


        preferences.edit()
                .putInt(Constant.XMPP_PORT, loginConfig.getXmppPort())
                .apply();
        preferences.edit()
                .putString(Constant.XMPP_SEIVICE_NAME, loginConfig.getXmppServiceName())
                .apply();
        preferences.edit()
                .putString(Constant.USERNAME, loginConfig.getUsername())
                .apply();
        preferences.edit()
                .putString(Constant.PASSWORD, loginConfig.getPassword())
                .apply();
        preferences.edit()
                .putBoolean(Constant.IS_AUTOLOGIN, loginConfig.isAutoLogin())
                .apply();
        preferences.edit()
                .putBoolean(Constant.IS_NOVISIBLE, loginConfig.isNovisible())
                .apply();
        preferences.edit()
                .putBoolean(Constant.IS_REMEMBER, loginConfig.isRemember())
                .apply();
        preferences.edit()
                .putBoolean(Constant.IS_ONLINE, loginConfig.isOnline())
                .apply();
        preferences.edit()
                .putBoolean(Constant.IS_FIRSTSTART, loginConfig.isFirstStart())
                .apply();
    }

    @Override
    public LoginConfig getLoginConfig() {
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,
                getResources().getString(R.string.xmpp_host)));
        loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT,
                getResources().getInteger(R.integer.xmpp_port)));
        loginConfig.setUsername(preferences.getString(Constant.USERNAME, null));
        loginConfig.setPassword(preferences.getString(Constant.PASSWORD, null));
        loginConfig.setXmppServiceName(preferences.getString(
                Constant.XMPP_SEIVICE_NAME,
                getResources().getString(R.string.xmpp_service_name)));
        loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
                getResources().getBoolean(R.bool.is_autologin)));
        loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
                getResources().getBoolean(R.bool.is_novisible)));
        loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
                getResources().getBoolean(R.bool.is_remember)));
        loginConfig.setFirstStart(preferences.getBoolean(
                Constant.IS_FIRSTSTART, true));
        return loginConfig;
    }

    @Override
    public boolean getUserOnlineState() {
        return preferences.getBoolean(Constant.IS_ONLINE, true);
    }

    @Override
    public void setUserOnlineState(boolean isOnline) {
        preferences.edit().putBoolean(Constant.IS_ONLINE, isOnline).apply();
    }

    @Override
    public void setNotiType(int iconId, String contentTitle, String contentText, Class activity, String from) {
        Intent notifyIntent = new Intent(this, activity);
        notifyIntent.putExtra("to", from);
        PendingIntent appIntent = PendingIntent.getActivity(this, 0,
                notifyIntent, 0);

        Notification myNoti = new Notification();
        myNoti.flags = Notification.FLAG_AUTO_CANCEL;
        myNoti.icon = iconId;
        myNoti.tickerText = contentTitle;
        myNoti.defaults = Notification.DEFAULT_SOUND;
        myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
        notificationManager.notify(0, myNoti);
    }
}
