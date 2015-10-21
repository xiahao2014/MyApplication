package im.summerhao.com.myapplication.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.adapter.NoticeAdapter;
import im.summerhao.com.myapplication.bean.Notice;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.NoticeManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.utils.StringUtil;

/**
 * Created by lenovo on 2015/10/19.
 */
public class MyNoticeActivity extends ActivitySupport {


    private ImageView titleBack;
    private ListView noticeList = null;
    private NoticeAdapter noticeAdapter = null;
    private List<Notice> inviteNotices = new ArrayList<Notice>();
    private ContacterReceiver receiver = null;
    private NoticeManager noticeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_notice);
        init();
    }

    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    private void init() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        receiver = new ContacterReceiver();
        noticeList = (ListView) findViewById(R.id.my_notice_list);
        noticeManager = NoticeManager.getInstance(context);
        inviteNotices = noticeManager.getNoticeListByTypeAndPage(Notice.All);
        noticeAdapter = new NoticeAdapter(context, inviteNotices);
        noticeList.setAdapter(noticeAdapter);
        noticeList.setOnItemClickListener(inviteListClick);
    }

    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Notice notice = (Notice) intent.getSerializableExtra("notice");
            inviteNotices.add(notice);
            refresh();
        }
    }

    private AdapterView.OnItemClickListener inviteListClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                long arg3) {
            final Notice notice = (Notice) view.findViewById(R.id.new_content)
                    .getTag();
            // 消息类型判断
            if (Notice.ADD_FRIEND == notice.getNoticeType()
                    && notice.getStatus() == Notice.UNREAD) {// 添加好友
                showAddFriendDialag(notice);
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                refresh();
            default:
                break;
        }
    }

    /**
     * 回复一个presence信息给用户
     *
     * @param type
     * @param to
     */
    protected void sendSubscribe(Presence.Type type, String to) {
        Presence presence = new Presence(type);
        presence.setTo(to);
        XmppConnectionManager.getInstance().getConnection()
                .sendPacket(presence);
    }

    /**
     * 删除一个条目
     *
     * @param subFrom
     */
    public void removeInviteNotice(String subFrom) {
        for (Notice notice : inviteNotices) {
            if (subFrom.equals(notice.getId())) {
                inviteNotices.remove(notice);
                break;
            }
        }
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clearall:
                NoticeManager.getInstance(context).delAll();
                refresh();
                break;
            case R.id.menu_exit:
                isExit();
                break;
        }
        return true;

    }

    /**
     * .弹出添加好友的对话框
     *
     * @param notice
     */
    private void showAddFriendDialag(final Notice notice) {
        final String subFrom = notice.getFrom();
        new AlertDialog.Builder(context)
                .setMessage(subFrom + "请求添加您为好友")
                .setTitle("提示")
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 接受请求
                        sendSubscribe(Presence.Type.subscribed, subFrom);
                        sendSubscribe(Presence.Type.subscribe, subFrom);
                        NoticeManager noticeManager = NoticeManager
                                .getInstance(context);
                        noticeManager.updateAddFriendStatus(
                                notice.getId(),
                                Notice.READ,
                                "已经同意" + StringUtil
                                        .getUserNameByJid(notice.getFrom()) + "的好友申请");
                        refresh();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSubscribe(Presence.Type.unsubscribe, subFrom);
                        NoticeManager noticeManager = NoticeManager
                                .getInstance(context);
                        noticeManager.updateAddFriendStatus(
                                notice.getId(),
                                Notice.READ,
                                "已经拒绝" + StringUtil
                                        .getUserNameByJid(notice.getFrom()) + "的好友申请");
                        refresh();
                    }
                }).show();
    }

    private void refresh() {
        inviteNotices = noticeManager.getNoticeListByTypeAndPage(Notice.All);
        Collections.sort(inviteNotices);
        noticeAdapter.setNoticeList(inviteNotices);
        noticeAdapter.notifyDataSetChanged();
    }
}
