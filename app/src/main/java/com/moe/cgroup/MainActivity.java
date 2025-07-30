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
import android.widget.Toast;
import java.io.IOException;
import java.io.PrintWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.view.View;
import android.app.AlertDialog;
import com.moe.cgroup.adapter.ProcessAdapter;
import android.content.DialogInterface;
import com.moe.cgroup.entity.PidInfo;

public class MainActivity extends Activity implements Handler.Callback,ListView.OnItemClickListener {
    private CgroupMessage cm;
    private final static int RESULT=0,ERROR=1,UNCONNECT=2,REFRESH=3,MESSAGE=4,CHECK=5;
    private ListView listview;
    private Handler handler;
    private CgroupInfoAdapter adapter;
    private List<CgroupInfo> list;
    private TextView tv;
    private boolean active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler(Looper.getMainLooper(), this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        listview.setDividerHeight(0);
        listview.setAdapter(adapter = new CgroupInfoAdapter(list = new ArrayList<>(), getPackageManager()));
        tv = findViewById(R.id.empty);
        tv.setText("正在初始化");
        listview.setEmptyView(tv);
        checkState();
        if (cm != null)return;
        new Start().start();
        handler.obtainMessage(MESSAGE,"root启动").sendToTarget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0,0,0,"关闭服务并退出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 0:
                if(cm!=null){
                    try {
                        cm.close();
                    } catch (RemoteException e) {}
                    cm = null;
                    }
                    finish();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CgroupInfo info=list.get(position);
        new AlertDialog.Builder(this).setTitle(info.name).setAdapter(new ProcessAdapter(info.pids), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final PidInfo pid=info.pids.get(which);
                    new AlertDialog.Builder(MainActivity.this).setMessage(pid.cmdline).setPositiveButton(pid.freeze == 0 ?"冻结": "解冻", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(cm!=null){
                                    try {
                                        cm.setFreeze(pid.uid, pid.pid, pid.freeze == 0);
                                    } catch (RemoteException e) {}
                                }
                            }
                        }).show();
                }
            }).setPositiveButton(info.freeze == 0 ?"冻结": "解冻", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(cm!=null){
                        try {
                            cm.setAppFreeze(info.uid, info.freeze == 0);
                        } catch (RemoteException e) {}
                    }
                }
            }).show();
    }

    
    
    private void checkState() {
        Bundle b=getContentResolver().call("com.moe.cgroup", "getBinder", null, null);
        if (b != null) {
            b.setClassLoader(BinderContainer.class.getClassLoader());
            BinderContainer bc=b.getParcelable("binder");
            if (bc != null) {
                IBinder binder=bc.binder;
                if (binder != null&&binder.isBinderAlive()) {
                    cm = CgroupMessage.Stub.asInterface(binder);
                }
            }
        }
    }
    @Override
    protected void onStart() {
        active = true;
        super.onStart();
        handler.sendEmptyMessage(REFRESH);
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
        handler.removeMessages(REFRESH);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH:
                onRefresh();
                break;
            case RESULT:
                list.clear();
                list.addAll((List<CgroupInfo>)msg.obj);
                adapter.notifyDataSetChanged();
                if (active){
                    handler.removeMessages(REFRESH);
                    handler.sendEmptyMessageDelayed(REFRESH, 1000);
                    }
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
                    //Toast.makeText(this,"未启动",Toast.LENGTH_SHORT).show();
                }break;
            case MESSAGE:
                Toast.makeText(this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                break;
            case CHECK:
                checkState();
                if(cm!=null)
                    handler.sendEmptyMessage(REFRESH);
                break;
        }
        return true;
    }

    private void onRefresh() {
        if (cm == null) {
            handler.sendEmptyMessage(UNCONNECT);
            return;
        }
        try {
            List<CgroupInfo> list=cm.queryAll(2);
            if (list == null)
                handler.sendEmptyMessage(ERROR);
            else {
                sort(list);
                handler.obtainMessage(RESULT, list).sendToTarget();
            }
        } catch (RemoteException e) {
            handler.sendEmptyMessage(UNCONNECT);
        }

    }
    private void sort(List<CgroupInfo> list) {
        List<CgroupInfo> tmp=new ArrayList<>(list);
        list.clear();
        int count=0;
        for (CgroupInfo info:tmp) {
            if (info.freezeCount > 0) {
                list.add(0, info);
            } else {
                list.add(info);
            }
        }
    }
    class Start extends Thread {

        @Override
        public void run() {
            Process p=null;
            try {
                p = Runtime.getRuntime().exec("su");
                PrintWriter pw=new PrintWriter(p.getOutputStream());
                pw.println("path=`pm path com.moe.cgroup`");
                pw.println("path=${path#package:}");
                pw.println("killall Cgroupd");
                pw.println("app_process -Djava.class.path=\"$path\" /system/bin --nice-name=Cgroupd  com.moe.cgroup.Cgroupd&");
                pw.println("exit");
                pw.flush();
                if (p.waitFor() != 0) {
                    handler.obtainMessage(MESSAGE,"服务启动失败").sendToTarget();
                } else {
                    sleep(1000);
                    //checkState();
                    handler.sendEmptyMessage(CHECK);
                }
            } catch (Exception e) {
                handler.obtainMessage(MESSAGE,"没有root权限").sendToTarget();
            } finally {
                if (p != null)
                    p.destroy();
            }
        }
        
    }
}
