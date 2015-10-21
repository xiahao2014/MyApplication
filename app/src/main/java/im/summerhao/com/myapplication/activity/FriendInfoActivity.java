package im.summerhao.com.myapplication.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.utils.StringUtil;

/**
 * 好友信息界面
 * Created by lenovo on 2015/10/16.
 */
public class FriendInfoActivity extends ActivitySupport {
    private TextView friend_account,friend_name;
    private String to;
    private String to_name;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        friend_account = (TextView) findViewById(R.id.friend_account);
        friend_name = (TextView) findViewById(R.id.friend_name);


        to = getIntent().getStringExtra("to");
        user = ContacterManager.getByUserJid(to, XmppConnectionManager.getInstance().getConnection());
        if (null == user) {
            to_name = StringUtil.getUserNameByJid(to);
        } else {
            to_name = user.getName() == null ? user.getJID() : user.getName();
        }

        friend_account.setText("账户名:"+StringUtil.getUserNameByJid(to));
        friend_name.setText("备注名:"+to_name);



    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        View inflate = getLayoutInflater().inflate(R.layout.toobar_button, toolbar);
        TextView tv = (TextView) inflate.findViewById(R.id.title);
        tv.setText("好友信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExit();
            }
        });
    }

}
