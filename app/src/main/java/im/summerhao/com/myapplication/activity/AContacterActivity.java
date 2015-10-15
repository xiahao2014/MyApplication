package im.summerhao.com.myapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.summerhao.com.myapplication.bean.Notice;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.utils.StringUtil;

/**
 *
 * Created by lenovo on 2015/10/15.
 */
public abstract class AContacterActivity extends ActivitySupport{

    private ContacterReceiver receiver = null;
    protected int noticeNum = 0;// 通知数量，未读消息数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        receiver = new ContacterReceiver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
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
        registerReceiver(receiver, filter);
        super.onResume();
    }

    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            User user = intent.getParcelableExtra(User.userKey);
            Notice notice = (Notice) intent.getSerializableExtra("notice");

            if (Constant.ROSTER_ADDED.equals(action)) {
                addUserReceive(user);
            } else if (Constant.ROSTER_DELETED.equals(action)) {
                deleteUserReceive(user);
            } else if (Constant.ROSTER_PRESENCE_CHANGED.equals(action)) {
                changePresenceReceive(user);
            } else if (Constant.ROSTER_UPDATED.equals(action)) {
                updateUserReceive(user);
            } else if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
                subscripUserReceive(intent
                        .getStringExtra(Constant.ROSTER_SUB_FROM));
            } else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                msgReceive(notice);
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
                boolean isSuccess = intent.getBooleanExtra(Constant.RECONNECT_STATE, true);
                handReConnect(isSuccess);
            }
        }
    }

    /**
     * roster添加了一个subcriber
     *
     * @param user
     */
    protected abstract void addUserReceive(User user);

    /**
     * roster删除了一个subscriber
     *
     * @param user
     */
    protected abstract void deleteUserReceive(User user);

    /**
     * roster中的一个subscriber的状态信息信息发生了改变
     *
     * @param user
     */
    protected abstract void changePresenceReceive(User user);

    /**
     * roster中的一个subscriber信息更新了
     *
     * @param user
     */
    protected abstract void updateUserReceive(User user);

    /**
     * 收到一个好友添加请求
     *
     * @param subFrom
     */
    protected abstract void subscripUserReceive(String subFrom);

    /**
     * 有新消息进来
     *
     * @param notice
     */
    protected abstract void msgReceive(Notice notice);

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
     * 修改这个好友的昵称
     *
     * @param user
     * @param nickname
     */
    protected void setNickname(User user, String nickname) {
		/*
		 * ContacterManager.addUserToGroup(user,user.getGroupName(),
		 * ConnectionUtils.getConnection());
		 */
        ContacterManager.setNickname(user, nickname, XmppConnectionManager
                .getInstance().getConnection());
    }

    /**
     * 把一个好友添加到一个组中 先移除当前分组，然后添加到新分组
     *
     * @param user
     * @param groupName
     */
    protected void addUserToGroup(final User user, final String groupName) {

        if (null == user) {
            return;
        }
        if (StringUtil.notEmpty(groupName) && Constant.ALL_FRIEND != groupName
                && Constant.NO_GROUP_FRIEND != groupName) {
            ContacterManager.addUserToGroup(user, groupName,
                    XmppConnectionManager.getInstance().getConnection());
        }
    }

    /**
     * 把一个好友从组中删除
     *
     * @param user
     * @param groupName
     */
    protected void removeUserFromGroup(User user, String groupName) {

        if (null == user) {
            return;
        }
        if (StringUtil.notEmpty(groupName)
                && !Constant.ALL_FRIEND.equals(groupName)
                && !Constant.NO_GROUP_FRIEND.equals(groupName))
            ContacterManager.removeUserFromGroup(user, groupName,
                    XmppConnectionManager.getInstance().getConnection());

    }

    /**
     * 添加一个联系人
     *
     * @param userJid
     *            联系人JID
     * @param nickname
     *            联系人昵称
     * @param groups
     *            联系人添加到哪些组
     * @throws XMPPException
     */
    protected void createSubscriber(String userJid, String nickname,
                                    String[] groups) throws XMPPException {
        XmppConnectionManager.getInstance().getConnection().getRoster()
                .createEntry(userJid, nickname, groups);
    }

    /**
     * 删除一个联系人
     *
     * @param userJid
     *            联系人的JID
     * @throws XMPPException
     */
    protected void removeSubscriber(String userJid) throws XMPPException {
        ContacterManager.deleteUser(userJid);

    }

    /**
     * 修改一个组的组名
     *
     * @param oldGroupName
     */
    protected void updateGroupName(String oldGroupName, String newGroupName) {
        XmppConnectionManager.getInstance().getConnection().getRoster()
                .getGroup(oldGroupName).setName(newGroupName);
    }

    /**
     * 添加分组
     *
     * @param newGroupName
     */
    protected void addGroup(String newGroupName) {
        ContacterManager.addGroup(newGroupName, XmppConnectionManager
                .getInstance().getConnection());
    }

    /**
     * 创建聊天室
     *
     * @param roomName
     * @return
     */
    protected static boolean createRoom(String roomName) {
        MultiUserChat muc = new MultiUserChat(XmppConnectionManager
                .getInstance().getConnection(), roomName
                + "@conference.wangxc");
        try {
            muc.create("room_nickname");
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            for (Iterator<?> fields = form.getFields(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType())
                        && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            /** 设置聊天室拥有者 */
            List<String> owners = new ArrayList<String>();
            owners.add("admin@wangxc");
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            muc.sendConfigurationForm(submitForm);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建一个聊天
     *
     * @param user
     */
    protected void createChat(User user) {
//        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("to", user.getJID());
//        intent.putExtra("from", user.getFrom());
//        startActivity(intent);
    }

    /**
     * 重连接返回
     *
     * @param isSuccess
     */
    protected abstract void handReConnect(boolean isSuccess);

    /**
     * 判断用户名是否存在
     *
     * @param userJid
     * @param groups
     * @return
     */
    protected boolean isExitJid(String userJid, List<ContacterManager.MRosterGroup> groups) {
        for (ContacterManager.MRosterGroup g : groups) {
            List<User> users = g.getUsers();
            if (users != null && users.size() > 0) {
                for (User u : users) {
                    if (u.getJID().equals(userJid)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
