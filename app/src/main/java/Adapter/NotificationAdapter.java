package Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.daniyalnawaz.easeapp.R;

import java.util.List;

import ModelClasses.Notification;

/**
 * Created by Daniyal Nawaz on 3/20/2016.
 */
public class NotificationAdapter extends BaseAdapter{
    List<Notification> list;
    Context context;

    public NotificationAdapter(Context context, List<Notification> list){
        this.context = context;
        this.list = list;
        Log.i("Lize Size Adapter",this.list.size()+"");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.notification_item, null);
        }

        TextView txtName = (TextView) convertView.findViewById(R.id.txt_message);
        TextView txtAddress = (TextView) convertView.findViewById(R.id.txt_date_time);

        Notification row_pos = list.get(position);
        // setting the image resource and title
        txtName.setText(row_pos.getMessage());
        txtAddress.setText(row_pos.getDateTime());

        return convertView;
    }
}
