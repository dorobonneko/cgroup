package com.moe.cgroup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;
import com.moe.cgroup.adapter.CgroupInfoAdapter;
import com.moe.cgroup.entity.CgroupInfo;
import java.util.ArrayList;
import java.util.List;
import android.os.RemoteException;

public class MainActivity extends Activity implements Handler.Callback
{
    private CgroupMessage cm;
    private final static int RESULT=0,ERROR=1,UNCONNECT=2;
    private ListView listview;
    private Handler handler;
    private CgroupInfoAdapter adapter;
    private List<CgroupInfo> list;
    private TextView tv;;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        handler=new Handler(Looper.getMainLooper(),this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listview=findViewById(R.id.listview);
        listview.setAdapter(adapter=new CgroupInfoAdapter(list=new ArrayList<>()));
        tv=new TextView(this);
        tv.setText("正在初始化");
        listview.setEmptyView(tv);
        Bundle b=getContentResolver().call("com.moe.cgroup","getBinder",null,null);
        if(b!=null){
            b.setClassLoader(BinderContainer.class.getClassLoader());
            BinderContainer bc=b.getParcelable("binder");
            if(bc!=null){
                IBinder binder=bc.binder;
                if(binder!=null){
                    cm=CgroupMessage.Stub.asInterface(binder);
                }
            }

        }
        onRefresh();
    }
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case RESULT:
                    list.clear();
                    list.addAll((List<CgroupInfo>)msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
                case ERROR:{
                    list.clear();
                    adapter.notifyDataSetChanged();
                    tv.setText("设备不支持");
                    }break;
                case UNCONNECT:{
                    list.clear();
                    adapter.notifyDataSetChanged();
                    tv.setText("服务未启动");
                    }break;
            }
            return true;
        }

        private void onRefresh(){
            if(cm==null){
                handler.sendEmptyMessage(UNCONNECT);
                return;
                }
            try {
                List<CgroupInfo> list=cm.queryAll();
                if (list == null)
                    handler.sendEmptyMessage(ERROR);
                else
                    handler.obtainMessage(RESULT,list).sendToTarget();
            } catch (RemoteException e) {
                handler.sendEmptyMessage(ERROR);
            }

        }
}
