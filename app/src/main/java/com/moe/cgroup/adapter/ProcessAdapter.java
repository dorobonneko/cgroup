package com.moe.cgroup.adapter;
import android.widget.ListAdapter;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import com.moe.cgroup.ProcessInfo;
import java.util.List;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.moe.cgroup.entity.PidInfo;
import android.widget.ImageView;
import com.moe.cgroup.R;

public class ProcessAdapter extends BaseAdapter {
    private List<PidInfo> list;
    public ProcessAdapter(List<PidInfo> list){
        this.list=list;
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
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh=(ProcessAdapter.ViewHolder) convertView.getTag();
        PidInfo info=list.get(position);
        vh.name.setText(info.cmdline);
        vh.status.setText(info.pid+(info.freeze==1?"已冻结":"活动中"));
        return convertView;
    }

    
    class ViewHolder{
        ImageView icon;
        TextView name,status;
        ViewHolder(View v){
            icon=v.findViewById(R.id.icon);
            name=v.findViewById(R.id.name);
            status=v.findViewById(R.id.status);
        }
	}
    
}
