package im.summerhao.com.myapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import im.summerhao.com.myapplication.bean.Notice;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.MessageManager;
import im.summerhao.com.myapplication.manager.NoticeManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.mode.IMMessage;
import im.summerhao.com.myapplication.utils.DateUtil;

/**
 *
 * Created by lenovo on 2015/10/15.
 */
public abstract class AChatActivity extends ActivitySupport {


    private Chat chat = null;
    private List<IMMessage> message_pool = null;
    protected String to;
    protected String from;
    private static int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        to = getIntent().getStringExtra("to");
        from=getIntent().getStringExtra("from");
        System.out.println(to);
        System.out.println(from);

        if (to == null)
            return;
        chat = XmppConnectionManager.getInstance().getConnection()
                .getChatManager().createChat(to, null);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        message_pool = MessageManager.getInstance(context)
                .getMessageListByFrom(to, 1, pageSize);
        if (null != message_pool && message_pool.size() > 0)
            Collections.sort(message_pool);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        registerReceiver(receiver, filter);

        NoticeManager.getInstance(context).updateStatusByFrom(to, Notice.READ);
        super.onResume();

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                IMMessage message = intent
                        .getParcelableExtra(IMMessage.IMMESSAGE_KEY);
                message_pool.add(message);
                receiveNewMessage(message);
                refreshMessage(message_pool);

                System.out.println("接收广播!!");
            }
        }

    };

    protected abstract void receiveNewMessage(IMMessage message);

    protected abstract void refreshMessage(List<IMMessage> messages);

    protected List<IMMessage> getMessages() {
        return message_pool;
    }

    protected void sendMessage(String messageContent) throws Exception {

        String time = DateUtil.date2Str(Calendar.getInstance(),
                Constant.MS_FORMART);
        Message message = new Message();
        message.setProperty(IMMessage.KEY_TIME, time);
        message.setBody(messageContent);
        chat.sendMessage(message);

        IMMessage newMessage = new IMMessage();
        newMessage.setMsgType(1);
        newMessage.setFromSubJid(chat.getParticipant());
        newMessage.setContent(messageContent);
        newMessage.setTime(time);
        message_pool.add(newMessage);
        MessageManager.getInstance(context).saveIMMessage(newMessage);

        refreshMessage(message_pool);

    }

    /**
     * 下滑加载信息,true 返回成功，false 数据已经全部加载，全部查完了，
     *
     */
    protected Boolean addNewMessage() {
        List<IMMessage> newMsgList = MessageManager.getInstance(context)
                .getMessageListByFrom(to, message_pool.size(), pageSize);
        if (newMsgList != null && newMsgList.size() > 0) {
            message_pool.addAll(newMsgList);
            Collections.sort(message_pool);
            return true;
        }
        return false;
    }

    protected void resh() {
        refreshMessage(message_pool);
    }
}
