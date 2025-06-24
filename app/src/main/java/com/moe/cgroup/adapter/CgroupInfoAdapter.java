/**
 * @Date 
 * @AIDE AIDE+ 
 */
package com.moe.cgroup.adapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.cgroup.entity.CgroupInfo;
import com.moe.cgroup.R;

public class CgroupInfoAdapter extends BaseAdapter {
	private List<CgroupInfo> list;
	public CgroupInfoAdapter(List<CgroupInfo> list){
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO: Implement this method
		return list.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO: Implement this method
		if(convertView==null){
			convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);
			convertView.setTag(new ViewHolder(convertView));
		}
		ViewHolder vh=(CgroupInfoAdapter.ViewHolder) convertView.getTag();
		CgroupInfo info=list.get(position);
		vh.name.setText(info.name);
        vh.status.setText(String.format("UID:%d|%d个进程",info.uid,info.pids.length));
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		// TODO: Implement this method
		return position;
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

