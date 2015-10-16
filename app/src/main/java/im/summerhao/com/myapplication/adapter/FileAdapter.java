package im.summerhao.com.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import im.summerhao.com.myapplication.R;

/**
 * Created by lenovo on 2015/10/16.
 */
public class FileAdapter extends BaseAdapter {

    private Bitmap filetype, folder, pkey;
    private List<String> filenames;
    private List<String> filepaths;
    private Context wappcontext = null;
    private LayoutInflater inflater;


    public FileAdapter(Context context, List<String> items,	List<String> pathlist) {
        this.wappcontext = context;

        this.folder = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
        this.filetype = BitmapFactory.decodeResource(context.getResources(), R.drawable.filetype);
        this.pkey = BitmapFactory.decodeResource(context.getResources(), R.drawable.p12);
        this.filenames = items;
        this.filepaths = pathlist;
    }

    public int getCount() {
        return filenames.size();
    }

    public Object getItem(int position) {
        return filenames.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView)
        {
            inflater = (LayoutInflater) this.wappcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.files_row, null);

            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.frmfiles_icon);
            holder.nameView = (TextView) convertView.findViewById(R.id.frmfiles_name);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = new File(filepaths.get(position).toString());

        String filename = file.getName().toLowerCase();
        if (file.isDirectory())
        {
            holder.iconView.setImageBitmap(folder);
        }
        else if(filename.contains(".p12"))
        {
            holder.iconView.setImageBitmap(pkey);
        }
        else
        {
            holder.iconView.setImageBitmap(filetype);
        }
        holder.nameView.setText(file.getName());

        return convertView;
    }

    static class ViewHolder {
        ImageView iconView;
        TextView nameView;
    }
}
