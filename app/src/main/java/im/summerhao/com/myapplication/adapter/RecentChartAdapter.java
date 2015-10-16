package im.summerhao.com.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.bean.ChartHisBean;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;

/**
 * Created by lenovo on 2015/10/16.
 */
public class RecentChartAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ChartHisBean> inviteUsers;
    public Context context;
    private View.OnClickListener contacterOnClick;

    public RecentChartAdapter(Context context, List<ChartHisBean> inviteUsers) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.inviteUsers = inviteUsers;
    }

    public void setNoticeList(List<ChartHisBean> inviteUsers) {
        this.inviteUsers = inviteUsers;
    }

    @Override
    public int getCount() {
        return inviteUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChartHisBean notice = inviteUsers.get(position);
        Integer ppCount = notice.getNoticeSum();
        ViewHolderx holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recent_chart_item, null);
            holder = new ViewHolderx();
            holder.newTitle = (TextView) convertView
                    .findViewById(R.id.new_title);
            holder.itemIcon = (ImageView) convertView
                    .findViewById(R.id.new_icon);
            holder.newContent = (TextView) convertView
                    .findViewById(R.id.new_content);
            holder.newDate = (TextView) convertView.findViewById(R.id.new_date);
            holder.paopao = (TextView) convertView.findViewById(R.id.paopao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderx) convertView.getTag();
        }
        String jid = notice.getFrom();
        User u = ContacterManager.getNickname(jid, XmppConnectionManager
                .getInstance().getConnection());
        if (null == u) {
            u = new User();
            u.setJID(jid);
            u.setName(jid);
        }

        holder.newTitle.setText(u.getName());
        holder.itemIcon.setBackgroundResource(R.drawable.head1);
        holder.newContent.setText(notice.getContent());
        holder.newContent.setTag(u);
        holder.newDate.setText(notice.getNoticeTime().substring(5, 16));

        if (ppCount != null && ppCount > 0) {
            holder.paopao.setText(ppCount + "");
            holder.paopao.setVisibility(View.VISIBLE);

        } else {
            holder.paopao.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(contacterOnClick);

        return convertView;
    }

    public class ViewHolderx {
        public ImageView itemIcon;
        public TextView newTitle;
        public TextView newContent;
        public TextView newDate;
        public TextView paopao;

    }

    public void setOnClickListener(View.OnClickListener contacterOnClick) {

        this.contacterOnClick = contacterOnClick;
    }

}
