package im.summerhao.com.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.MessageManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.mode.IMMessage;
import im.summerhao.com.myapplication.utils.ExpressionUtil;
import im.summerhao.com.myapplication.utils.StringUtil;

/**
 * 聊天界面
 * Created by lenovo on 2015/10/15.
 */
public class ChatActivity extends AChatActivity {

    private ImageView titleBack;
    private MessageListAdapter adapter = null;
    private EditText messageInput = null;
    private Button messageSendBtn = null;
    private ImageButton userInfo;
    private ImageButton expression, files;
    private ListView listView;
    private int recordCount;
    private View listHead;
    private Button listHeadButton;
    // 聊天人
    private User user;
    private TextView tvChatTitle;
    private String to_name;
    private ProgressBar pb;
    private Dialog builder;
    private int[] imageIds = new int[107];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        init();



    }

    private void init() {
//        titleBack = (ImageView) findViewById(R.id.title_back);
//        titleBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.title_btn_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 与谁聊天
        tvChatTitle = (TextView) findViewById(R.id.to_chat_name);
        user = ContacterManager.getByUserJid(to, XmppConnectionManager.getInstance().getConnection());
        if (null == user) {
            to_name = StringUtil.getUserNameByJid(to);
        } else {
            to_name = user.getName() == null ? user.getJID() : user.getName();
        }

        tvChatTitle.setText(to_name);

//        userInfo = (ImageButton) findViewById(R.id.user_info);
//        userInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent();
////                intent.setClass(context, FriendInfoActivity.class);
////                startActivity(intent);
//            }
//        });

        //进度条
        pb = (ProgressBar) findViewById(R.id.chat_pb);

        listView = (ListView) findViewById(R.id.chat_list);
        listView.setCacheColorHint(0);
        adapter = new MessageListAdapter(ChatActivity.this, getMessages(), listView);

        LayoutInflater mynflater = LayoutInflater.from(context);
        listHead = mynflater.inflate(R.layout.chatlistheader, null);
        listHeadButton = (Button) listHead.findViewById(R.id.buttonChatHistory);
        listHeadButton.setOnClickListener(chatHistoryCk);
        listView.addHeaderView(listHead);
        listView.setAdapter(adapter);


        expression = (ImageButton) findViewById(R.id.face_select);
        expression.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                createExpressionDialog();
            }
        });


        files = (ImageButton) findViewById(R.id.file_select);
        files.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, FileActivity.class);
                startActivityForResult(intent, 2);
            }
        });


        messageInput = (EditText) findViewById(R.id.chat_content);
        messageSendBtn = (Button) findViewById(R.id.chat_sendbtn);
        messageSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if ("".equals(message)) {
                    Toast.makeText(ChatActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        sendMessage(message);
                        messageInput.setText("");
                    } catch (Exception e) {
                        showToast("信息发送失败");
                        messageInput.setText(message);
                    }
                    closeInput();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title_right, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_user_info:

                Intent intent = new Intent();
                intent.setClass(context, FriendInfoActivity.class);
                intent.putExtra("to", user.getJID());
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建一个表情选择对话框
     */
    private void createExpressionDialog() {
        builder = new Dialog(ChatActivity.this);
        GridView gridView = createGridView();
        builder.setContentView(gridView);
        builder.setTitle("默认表情");
        builder.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeResource(getResources(), imageIds[arg2 % imageIds.length]);
                ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
                String str = null;
                if (arg2 < 10) {
                    str = "f00" + arg2;
                } else if (arg2 < 100) {
                    str = "f0" + arg2;
                } else {
                    str = "f" + arg2;
                }
                SpannableString spannableString = new SpannableString(str);
                spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                messageInput.append(spannableString);
                builder.dismiss();
            }
        });
    }


    /**
     * 生成一个表情对话框中的gridview
     *
     * @return
     */
    private GridView createGridView() {
        final GridView view = new GridView(this);
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        //生成107个表情的id，封装
        for (int i = 0; i < 107; i++) {
            try {
                if (i < 10) {
                    Field field = R.drawable.class.getDeclaredField("f00" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                } else if (i < 100) {
                    Field field = R.drawable.class.getDeclaredField("f0" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                } else {
                    Field field = R.drawable.class.getDeclaredField("f" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
        view.setAdapter(simpleAdapter);
        view.setNumColumns(6);
        view.setBackgroundColor(Color.rgb(214, 211, 214));
        view.setHorizontalSpacing(1);
        view.setVerticalSpacing(1);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER);
        return view;
    }

    //覆盖该方法，取得fileactivity的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //取得传回的值
        if (requestCode == 2 && resultCode == 2 && data != null) {

            String filepath = data.getStringExtra("filepath");
            System.out.println("文件大小：" + filepath.length());
            if (filepath.length() > 0) {
                sendFile(filepath);
            }
        }
    }

    //发送文件
    private void sendFile(String filepath) {


        final FileTransferManager fileTransferManager = new FileTransferManager(XmppConnectionManager.getInstance().getConnection());
        //发送给服务器，（获取自己的服务器，和好友）
        final OutgoingFileTransfer fileTransfer = fileTransferManager.createOutgoingFileTransfer(from);

        System.out.println("发送对象" + from);
        final File file = new File(filepath);
        System.out.println("发送文件" + file);

        try {
            fileTransfer.sendFile(file, "Sending");
        } catch (Exception e) {
            Toast.makeText(ChatActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    while (true) {
                        Thread.sleep(500L);
                        FileTransfer.Status status = fileTransfer.getStatus();
                        if ((status == FileTransfer.Status.error)
                                || (status == FileTransfer.Status.complete)
                                || (status == FileTransfer.Status.cancelled)
                                || (status == FileTransfer.Status.refused)) {
                            handler.sendEmptyMessage(4);
                            break;
                        } else if (status == FileTransfer.Status.negotiating_transfer) {
                            //..
                        } else if (status == FileTransfer.Status.negotiated) {
                            //..
                        } else if (status == FileTransfer.Status.initial) {
                            //..
                        } else if (status == FileTransfer.Status.negotiating_stream) {
                            //..
                        } else if (status == FileTransfer.Status.in_progress) {
                            //进度条显示
                            handler.sendEmptyMessage(2);

                            long p = fileTransfer.getBytesSent() * 100L / fileTransfer.getFileSize();

                            android.os.Message message = handler.obtainMessage();
                            message.arg1 = Math.round((float) p);
                            message.what = 3;
                            message.sendToTarget();
                            Toast.makeText(ChatActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    //附件进度条
                    if (pb.getVisibility() == View.GONE) {
                        pb.setMax(100);
                        pb.setProgress(1);
                        pb.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    pb.setProgress(msg.arg1);
                    break;
                case 4:
                    pb.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void receiveNewMessage(IMMessage message) {
    }

    @Override
    protected void refreshMessage(List<IMMessage> messages) {
        adapter.refreshList(messages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordCount = MessageManager.getInstance(context)
                .getChatCountWithSb(to);
        if (recordCount <= 0) {
            listHead.setVisibility(View.GONE);
        } else {
            listHead.setVisibility(View.VISIBLE);
        }
        adapter.refreshList(getMessages());
    }


    private class MessageListAdapter extends BaseAdapter {

        private List<IMMessage> items;
        private Context context;
        private ListView adapterList;
        private LayoutInflater inflater;

        public MessageListAdapter(Context context, List<IMMessage> items,
                                  ListView adapterList) {
            this.context = context;
            this.items = items;
            this.adapterList = adapterList;
        }

        public void refreshList(List<IMMessage> items) {
            this.items = items;
            this.notifyDataSetChanged();
            adapterList.setSelection(items.size() - 1);
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            IMMessage message = items.get(position);
            if (message.getMsgType() == 0) {
                convertView = this.inflater.inflate(
                        R.layout.formclient_chat_in, null);
            } else {
                convertView = this.inflater.inflate(
                        R.layout.formclient_chat_out, null);
            }
            TextView useridView = (TextView) convertView
                    .findViewById(R.id.formclient_row_userid);
            TextView dateView = (TextView) convertView
                    .findViewById(R.id.formclient_row_date);
            TextView msgView = (TextView) convertView
                    .findViewById(R.id.formclient_row_msg);
            if (message.getMsgType() == 0) {
                if (null == user) {
                    useridView.setText(StringUtil.getUserNameByJid(to));
                } else {
                    useridView.setText(user.getName());
                }

            } else {
                useridView.setText("我");
            }
            dateView.setText(message.getTime());
            //msgView.setText(message.getContent());
            String str = message.getContent();
            //消息具体内容
            String zhengze = "f0[0-9]{2}|f10[0-7]";
            //正则表达式，用来判断消息内是否有表情
            try {
                SpannableString spannableString = ExpressionUtil.getExpressionString(context, str, zhengze);
                msgView.setText(spannableString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return convertView;
        }

    }


    /**
     * 点击进入聊天记录
     */
    private View.OnClickListener chatHistoryCk = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(context, ChatHistoryActivity.class);
//            intent.putExtra("to", to);
//            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
