package im.summerhao.com.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import org.jivesoftware.smackx.packet.VCard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.adapter.SignViewAdapter;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.ui.RoundedImageView;
import im.summerhao.com.myapplication.ui.ToolBarActivity;
import im.summerhao.com.myapplication.utils.FileUtil;

/**
 * 注册页面
 */
public class RegisterActivity extends ToolBarActivity implements View.OnClickListener {

    // private EditText ui_username_input, ui_password_input;

    /**
     * 选择照片返回码
     */
    private static final int selectCode = 123;

    /**
     * 拍照返回码
     */
    private static final int cameraCode = 124;
    /**
     * 系统裁剪返回码
     */
    private static final int picCode = 125;

    // 拍照文件
    private File tempFile;
    private SharedPreferences preferences;
    private IQ result;
    String accountStr;
    String passwordStr;
    private ViewPager viewPager;
    private View createAccount, perfectAccount, uploadAvatar;
    private RoundedImageView avatar;
    private SignViewAdapter adapter;
    private AlertDialog dialog;
    private byte[] avatarBytes;
    String accountNickName;
    XMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        viewPager = (ViewPager) findViewById(R.id.activity_sign_view_pager);
        // 禁止滑动
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        createAccount = getLayoutInflater().inflate(R.layout.activity_sign_view_create_account, null);
        uploadAvatar = getLayoutInflater().inflate(R.layout.activity_sign_view_upload_avatar, null);
        perfectAccount = getLayoutInflater().inflate(R.layout.activity_sign_view_perfect_account, null);
        avatar = (RoundedImageView) uploadAvatar.findViewById(R.id.activity_sign_view_upload_avatar_avatar);

        List<View> views = new ArrayList<View>();
        views.add(createAccount);
        views.add(perfectAccount);
        views.add(uploadAvatar);

        adapter = new SignViewAdapter(views);
        // 适配器内容监听器
        adapter.setOnSignViewClickListener(this);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        TextView textView = (TextView) getLayoutInflater().inflate(R.layout.toobar_button, toolbar).findViewById(R.id.title);
        textView.setText("注册");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ui_btn_register:
                EditText ui_username_input = (EditText) createAccount.findViewById(R.id.ui_username_input);
                EditText ui_password_input = (EditText) createAccount.findViewById(R.id.ui_password_input);


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
                        connection = XmppConnectionManager.getInstance().getConnection();
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
                            viewPager.setCurrentItem(1);

                        }
                    }
                }.execute();


                break;
            // 完善资料布局监听
            case R.id.activity_sign_view_perfect_account_commit:
                TextView nickname = (TextView) perfectAccount.findViewById(R.id.activity_sign_view_perfect_account_nickname);
                accountNickName = nickname.getText().toString().trim();
                if (TextUtils.isEmpty(accountNickName)) {
                    Toast.makeText(RegisterActivity.this, "昵称不能胡来", Toast.LENGTH_LONG).show();
                    return;
                }
                viewPager.setCurrentItem(2);
                break;
            // 上传头像布局监听
            case R.id.activity_sign_view_upload_avatar_avatar:
            case R.id.activity_sign_view_upload_avatar_layout:
                showDialog();
                break;
            case R.id.activity_sign_view_upload_avatar_commit:
                if (avatarBytes == null) {
                    showDialog();
                } else {
                    // 上传头像，完成注册任务
                    new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog dialog;

                        protected void onPreExecute() {
                            dialog = ProgressDialog.show(RegisterActivity.this, null, "正在保存账户...");
                        }

                        protected Boolean doInBackground(Void... voids) {
                            try {
                                VCard vCard = new VCard();
                                vCard.load(connection);
                                vCard.setNickName(accountNickName);
                                vCard.setAvatar(avatarBytes);
                                vCard.save(connection);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        protected void onPostExecute(Boolean aBoolean) {
                            dialog.dismiss();
                            if (aBoolean) {

                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "额,就差这一步了,再试一次", Toast.LENGTH_LONG).show();
                            }
                        }


                    }.execute();
                }
                break;

        }
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("选择照片")
                    .setItems(R.array.select_photo_items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    tempFile = FileUtil.getCameraFile();
                                    // 进入拍照
                                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                                    startActivityForResult(intentCamera, cameraCode);
                                    break;
                                case 1:
                                    // 浏览图库
                                    Intent intentSelect = new Intent();
                                    intentSelect.setType("image/*");
                                    intentSelect.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intentSelect, selectCode);
                                    break;
                            }
                        }
                    }).create();
        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // 拍照
            case cameraCode:
                // 获取照片,开始裁剪
                FileUtil.doCropPhoto(RegisterActivity.this, Uri.fromFile(tempFile), picCode);
                break;
            // 图库
            case selectCode:
                Uri uri = data.getData();
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String pathStr = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (!TextUtils.isEmpty(pathStr)) {
                            // 文件后缀判断
                            if (pathStr.endsWith("jpg") || pathStr.endsWith("png")) {
                                // 获取照片,开始裁剪
                                FileUtil.doCropPhoto(RegisterActivity.this, uri, picCode);
                            }
                        }
                    }
                }
                break;
            // 裁剪
            case picCode:
                if (data != null) {
                    Bitmap photoPic = data.getParcelableExtra("data");
                    if (photoPic != null) {
                        avatar.setImageDrawable(FileUtil.Bitmap2Drawable(photoPic));
                        avatarBytes = FileUtil.Bitmap2Bytes(photoPic);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
