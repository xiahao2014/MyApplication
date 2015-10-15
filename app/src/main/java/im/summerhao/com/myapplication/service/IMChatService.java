package im.summerhao.com.myapplication.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.util.Calendar;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.ChatActivity;
import im.summerhao.com.myapplication.bean.Notice;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.MessageManager;
import im.summerhao.com.myapplication.manager.NoticeManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.mode.IMMessage;
import im.summerhao.com.myapplication.utils.DateUtil;

/**
 * 聊天服务
 * Created by lenovo on 2015/10/15.
 */
public class IMChatService extends Service {

    private Context context;
    private NotificationManager notificationManager;
    private FileTransferRequest request;
    private File file;

    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initChatManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initChatManager() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        XMPPConnection conn = XmppConnectionManager.getInstance().getConnection();
        conn.addPacketListener(pListener, new MessageTypeFilter(Message.Type.chat));
        //监听文件传输
        FileTransferManager fileTransferManager = new FileTransferManager(conn);
        fileTransferManager.addFileTransferListener(new RecFileTransferListener());

    }

    PacketListener pListener = new PacketListener() {
        @Override
        public void processPacket(Packet arg0) {
            Message message = (Message) arg0;
            if (message != null && message.getBody() != null
                    && !message.getBody().equals("null")) {
                IMMessage msg = new IMMessage();
                String time = DateUtil.date2Str(Calendar.getInstance(),
                        Constant.MS_FORMART);
                msg.setTime(time);
                msg.setContent(message.getBody());
                if (Message.Type.error == message.getType()) {
                    msg.setType(IMMessage.ERROR);
                } else {
                    msg.setType(IMMessage.SUCCESS);
                }
                String from = message.getFrom().split("/")[0];
                msg.setFromSubJid(from);

                // 生成通知
                NoticeManager noticeManager = NoticeManager.getInstance(context);
                Notice notice = new Notice();
                notice.setTitle("会话信息");
                notice.setNoticeType(Notice.CHAT_MSG);
                notice.setContent(message.getBody());
                notice.setFrom(from);
                notice.setStatus(Notice.UNREAD);
                notice.setNoticeTime(time);

                // 历史记录
                IMMessage newMessage = new IMMessage();
                newMessage.setMsgType(0);
                newMessage.setFromSubJid(from);
                newMessage.setContent(message.getBody());
                newMessage.setTime(time);
                MessageManager.getInstance(context).saveIMMessage(newMessage);
                long noticeId = -1;

                noticeId = noticeManager.saveNotice(notice);

                if (noticeId != -1) {

                    System.out.println("发送广播!!");

                    Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
                    intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
                    intent.putExtra("notice", notice);
                    sendBroadcast(intent);
                    setNotiType(R.drawable.im,getResources().getString(R.string.new_message),
                            notice.getContent(), ChatActivity.class, from);
                }
            }
        }
    };

    private class RecFileTransferListener implements FileTransferListener {
        @Override
        public void fileTransferRequest(FileTransferRequest prequest) {
            //接受附件
            System.out.println("The file received from: " + prequest.getRequestor());

            file = new File("mnt/sdcard/Xim/" + prequest.getFileName());
            request = prequest;

            final IncomingFileTransfer infiletransfer = request.accept();
            try {
                infiletransfer.recieveFile(file);
            } catch (XMPPException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            showToast("接收到一个文件：\n保存在sdcard/Xim");

        }
    }

    //在service显示Toast
    private void showToast(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO 自动生成的方法存根
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showDialog(){
        final IncomingFileTransfer infiletransfer = request.accept();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("文件接收")
                .setMessage("是否接收来自"+request.getRequestor()+"的文件？")
                .setPositiveButton("接收", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法存根
                        try {
                            infiletransfer.recieveFile(file);
                        }
                        catch (XMPPException e)	{
                            showToast("接收失败!");
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法存根
                        request.reject();
                        dialog.cancel();
                    }
                }).show();
    }



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

    @SuppressWarnings({ "rawtypes" })
    private void setNotiType(int iconId, String contentTitle,
                             String contentText, Class activity, String from) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */

        Intent notifyIntent = new Intent(this, activity);
        notifyIntent.putExtra("to", from);
		/* 创建PendingIntent作为设置递延运行的Activity */
        PendingIntent appIntent = PendingIntent.getActivity(this, 0,notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
        Notification myNoti = new Notification();
        // 点击自动消失
        myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
        myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
        myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
        myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
        myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
        notificationManager.notify(0, myNoti);
    }
}
