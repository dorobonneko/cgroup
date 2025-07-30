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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.content.pm.PackageInfo;
import android.graphics.Paint;

public class CgroupInfoAdapter extends BaseAdapter {
	private List<CgroupInfo> list;
    private PackageManager pm;
	public CgroupInfoAdapter(List<CgroupInfo> list,PackageManager pm){
		this.list=list;
        this.pm=pm;
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
        vh.name.getPaint().setStrikeThruText(info.freeze==1);
        if(info.freezeCount>0)
            vh.status.setText(String.format("%d个进程%d个冻结",info.pids.size(),info.freezeCount));
        else
            vh.status.setText(String.format("%d个进程",info.pids.size()));
        
        boolean flag=false;
        try {
            /*Intent intent=pm.getLaunchIntentForPackage(info.name);
            if(intent!=null){
                List<ResolveInfo> list=pm.queryIntentActivities(intent,PackageManager.GET_ACTIVITIES);
                if(!list.isEmpty()){
                    ResolveInfo resolveinfo=list.get(0);
                    vh.name.setText(resolveinfo.activityInfo.loadLabel(pm));
                    vh.icon.setImageDrawable(resolveinfo.activityInfo.loadIcon(pm));
                }
            }*/
            if(!flag){
               //PackageInfo pinfo=pm.getPackageInfo(info.name,PackageManager.MATCH_UNINSTALLED_PACKAGES);
            ApplicationInfo app=pm.getApplicationInfo(info.name, PackageManager.GET_UNINSTALLED_PACKAGES);
            vh.name.setText(app.loadLabel(pm));
                Drawable icon=app.loadUnbadgedIcon(pm);
            vh.icon.setImageDrawable(icon);
            //vh.icon.setImageDrawable(pm.getApplicationIcon(info.name));
            }
        } catch (PackageManager.NameNotFoundException e) {
            vh.name.setText(info.name);
            vh.icon.setImageDrawable(null);
        }
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
            icon.setVisibility(View.VISIBLE);
		}
	}
}

