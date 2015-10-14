package im.summerhao.com.myapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;

/**
 * 注册页面
 */
public class RegisterActivity extends Activity {

    private EditText ui_username_input, ui_password_input;
    private Button ui_btn_register;
    private SharedPreferences preferences;
    private IQ result;
    String accountStr;
    String passwordStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ui_username_input = (EditText) findViewById(R.id.ui_username_input);
        ui_password_input = (EditText) findViewById(R.id.ui_password_input);
        ui_btn_register = (Button) findViewById(R.id.ui_btn_register);


        ui_btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountStr = ui_username_input.getText().toString().trim();
                passwordStr = ui_password_input.getText().toString().trim();

                if (TextUtils.isEmpty(accountStr)) {
                    Toast.makeText(RegisterActivity.this, "请检查账户", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordStr) || passwordStr.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空，并且长度大于6位", Toast.LENGTH_LONG).show();
                    return;
                }

                // 创建账户任务
                new AsyncTask<Void, Void, IQ>() {
                    private ProgressDialog dialog;

                    protected void onPreExecute() {
                        dialog = ProgressDialog.show(RegisterActivity.this, null, "正在联系服务器...");
                        preferences = getSharedPreferences(Constant.LOGIN_SET, Context.MODE_PRIVATE);
                        //从Xim_login_set文件中取得对应的键的值

                        String host = preferences.getString("xmpp_host", null);
                        System.out.println("取得服务器地址是：" + host);

                    }

                    @Override
                    protected IQ doInBackground(Void... params) {
                        XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
                        try {
                            connection.connect();
                            Registration reg = new Registration();
                            reg.setType(IQ.Type.SET);
                            reg.setTo(connection.getServiceName());
                            reg.setUsername(accountStr);
                            reg.setPassword(passwordStr);
                            PacketFilter filter = new AndFilter(new PacketIDFilter(reg
                                    .getPacketID()), new PacketTypeFilter(IQ.class));
                            PacketCollector collector = connection.createPacketCollector(filter);
                            connection.sendPacket(reg);

                            result = (IQ) collector.nextResult(SmackConfiguration
                                    .getPacketReplyTimeout());
                            // Stop queuing results
                            collector.cancel();// 停止请求results（是否成功的结果）

                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }


                        return result;
                    }


                    protected void onPostExecute(IQ result) {
                        dialog.dismiss();
                        if (result == null) {
                            Toast.makeText(getApplicationContext(), "服务器没有返回结果",
                                    Toast.LENGTH_SHORT).show();
                        } else if (result.getType() == IQ.Type.ERROR) {
                            if (result.getError().toString().equalsIgnoreCase(
                                    "conflict(409)")) {
                                Toast.makeText(getApplicationContext(), "这个账号已经存在",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "注册失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (result.getType() == IQ.Type.RESULT) {
                            Toast.makeText(getApplicationContext(), "恭喜你注册成功",
                                    Toast.LENGTH_SHORT).show();

                            finish();

                        }
                    }
                }.execute();


            }
        });

    }

}
