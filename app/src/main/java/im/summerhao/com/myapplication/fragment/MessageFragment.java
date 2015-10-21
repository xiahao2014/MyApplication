package im.summerhao.com.myapplication.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.ChatActivity;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.adapter.RecentChartAdapter;
import im.summerhao.com.myapplication.bean.ChartHisBean;
import im.summerhao.com.myapplication.bean.Notice;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.MessageManager;

public class MessageFragment extends BaseFragment {

    private static final String TAG = "MessageFragment";
    private MainActivity mMainActivity;
    private ContacterReceiver receiver = null;
    protected int noticeNum = 0;// 通知数量，未读消息数量
    private RecentChartAdapter noticeAdapter = null;
    private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
    private ListView inviteList = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.message_layout,
                container, false);
        inviteList = (ListView) messageLayout.findViewById(R.id.main_chat_room_list);

        inviteList.setAdapter(noticeAdapter);
        noticeAdapter.setOnClickListener(contacterOnClickJ);
        return messageLayout;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new ContacterReceiver();
        mMainActivity = (MainActivity) getActivity();
        inviteNotices = MessageManager.getInstance(mMainActivity)
                .getRecentContactsWithLastMsg();
        noticeAdapter = new RecentChartAdapter(mMainActivity, inviteNotices);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(Constant.ROSTER_ADDED);
        filter.addAction(Constant.ROSTER_DELETED);
        filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
        filter.addAction(Constant.ROSTER_UPDATED);
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        // 好友请求
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.ACTION_SYS_MSG);

        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        mMainActivity.registerReceiver(receiver, filter);

        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_MESSAGE;

        refreshList();
        super.onResume();

    }

    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            User user = intent.getParcelableExtra(User.userKey);
            Notice notice = (Notice) intent.getSerializableExtra("notice");

            if (Constant.ROSTER_ADDED.equals(action)) {
                //addUserReceive(user);
                refreshList();

            } else if (Constant.ROSTER_DELETED.equals(action)) {
                //deleteUserReceive(user);
                if (user == null)
                    return;
                Toast.makeText(context,
                        (user.getName() == null) ? user.getJID() : user.getName()
                                + "被删除了", Toast.LENGTH_SHORT).show();
                refreshList();

            } else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
                // changePresenceReceive(user);

                if (user == null)
                    return;
                if (ContacterManager.contacters.get(user.getJID()) == null)
                    return;
                if (!user.isAvailable())
                    if (ContacterManager.contacters.get(user.getJID()).isAvailable())
                        Toast.makeText(context,
                                (user.getName() == null) ? user.getJID() : user
                                        .getName() + "上线了", Toast.LENGTH_SHORT).show();
                if (user.isAvailable())
                    if (!ContacterManager.contacters.get(user.getJID()).isAvailable())
                        Toast.makeText(context,
                                (user.getName() == null) ? user.getJID() : user
                                        .getName() + "下线了", Toast.LENGTH_SHORT).show();
                refreshList();

            } else if (Constant.ROSTER_UPDATED.equals(action)) {
                //updateUserReceive(user);
                refreshList();
            } else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
                subscripUserReceive(intent.getStringExtra(Constant.ROSTER_SUB_FROM));
            } else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                msgReceive(notice);
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
                boolean isSuccess = intent.getBooleanExtra(Constant.RECONNECT_STATE, true);
                // handReConnect(isSuccess);

                if (Constant.RECONNECT_STATE_SUCCESS == isSuccess) {
                    //iv_status.setImageDrawable(getResources().getDrawable(
                    //       R.drawable.status_online));

                } else if (Constant.RECONNECT_STATE_FAIL == isSuccess) {
                    //iv_status.setImageDrawable(getResources().getDrawable(
                    //       R.drawable.status_offline));
                }
            }
        }
    }

    protected void subscripUserReceive(final String subFrom) {
        Notice notice = new Notice();
        notice.setFrom(subFrom);
        notice.setNoticeType(Notice.CHAT_MSG);
    }


    /**
     * 有新消息进来，最近联系人界面更新
     */
    protected void msgReceive(Notice notice) {
        for (ChartHisBean ch : inviteNotices) {
            if (ch.getFrom().equals(notice.getFrom())) {
                ch.setContent(notice.getContent());
                ch.setNoticeTime(notice.getNoticeTime());
                Integer x = ch.getNoticeSum() == null ? 0 : ch.getNoticeSum();
                ch.setNoticeSum(x + 1);
            }
        }
        noticeAdapter.setNoticeList(inviteNotices);
        noticeAdapter.notifyDataSetChanged();
        setPaoPao();
    }

    /**
     * 上面滚动条上的气泡设置 有新消息来的通知气泡，数量设置,
     */
    private void setPaoPao() {
        if (null != inviteNotices && inviteNotices.size() > 0) {
            int paoCount = 0;
            for (ChartHisBean c : inviteNotices) {
                Integer countx = c.getNoticeSum();
                paoCount += (countx == null ? 0 : countx);
            }
            if (paoCount == 0) {
                //noticePaopao.setVisibility(View.GONE);
                return;
            }
            //noticePaopao.setText(paoCount + "");
            //noticePaopao.setVisibility(View.VISIBLE);
        } else {
            //noticePaopao.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新当前的列表
     */
    private void refreshList() {
        /** 刷新好友列表 */
//        rGroups = ContacterManager.getGroups(XmppConnectionManager
//                .getInstance().getConnection().getRoster());
//        for (String newGroupName : newNames) {
//            ContacterManager.MRosterGroup mg = new ContacterManager.MRosterGroup(newGroupName,
//                    new ArrayList<User>());
//            rGroups.add(rGroups.size() - 1, mg);
//        }
//        expandAdapter.setContacter(rGroups);
//        expandAdapter.notifyDataSetChanged();

        /** 刷新最近联系人列表 */
        inviteNotices = MessageManager.getInstance(mMainActivity)
                .getRecentContactsWithLastMsg();
        noticeAdapter.setNoticeList(inviteNotices);
        noticeAdapter.notifyDataSetChanged();
        /**
         * 有新消息进来的气泡设置
         */
        setPaoPao();
    }


    /**
     * 通知点击
     */
    private View.OnClickListener contacterOnClickJ = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            createChat((User) v.findViewById(R.id.new_content).getTag());
        }
    };

    /**
     * 创建一个聊天
     *
     * @param user
     */
    protected void createChat(User user) {
        Intent intent = new Intent(mMainActivity, ChatActivity.class);
        intent.putExtra("to", user.getJID());
        intent.putExtra("from", user.getFrom());

        startActivity(intent);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }
}
