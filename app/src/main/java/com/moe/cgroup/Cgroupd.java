/**
 * @Author 
 * @AIDE AIDE+
 */
package com.moe.cgroup;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.ContentProviderHolder;
import android.app.IActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.system.Os;
import com.moe.cgroup.entity.CgroupInfo;
import com.moe.cgroup.util.IContentProvicerUtils;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.app.LoadedApk;
import java.lang.reflect.InvocationTargetException;
import android.content.pm.IPackageManager;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.moe.cgroup.entity.PidInfo;
import android.content.IContentProvider;

public class Cgroupd extends CgroupMessage.Stub {
	private static File cgroup = new File("/sys/fs/cgroup");
    private FileFilter dirFilter=new DirFilterClass();
    private IPackageManager pm;
    private Looper myLooper;
	public Cgroupd(Looper looper) {
        this.myLooper=looper;
        System.out.println("init service");

        pm=IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        IBinder binder = ServiceManager.getService("activity");
        IActivityManager iam=null;
        IContentProvider mProvider=null;
        if (Build.VERSION.SDK_INT > 26)
            iam = ActivityManagerNative.asInterface(binder);
        else
            iam = IActivityManager.Stub.asInterface(binder);
        try {
            ContentProviderHolder provider=iam.getContentProviderExternal("com.moe.cgroup", 0, null, "com.moe.cgroup");
            System.out.println("provider");
            Bundle b=new Bundle();
            b.putParcelable("binder", new BinderContainer(this));
            mProvider=provider.provider;
            IContentProvicerUtils.callCompat(provider.provider, null, "com.moe.cgroup", "setBinder", null, b);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
		}finally{
            try {
                if (mProvider != null)
                    iam.removeContentProviderExternal("com.moe.cgroup", null);
            } catch (RemoteException e) {}
        }
	}

    @Override
    public void close() throws RemoteException {
        myLooper.quit();
        
        System.out.println("用户"+Os.getuid());
        ActivityThread.systemMain().getSystemContext().getContentResolver().call("com.moe.cgroup","quit",null,null);
        /*try {
            //ContentProviderHolder provider=iam.getContentProviderExternal("com.moe.cgroup", 0, null, null);
            Bundle b=new Bundle();
            IContentProvicerUtils.callCompat(provider, null, "com.moe.cgroup", "quit", null, b);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
		}*/
    }


    @Override
    public boolean setFreeze(int uid, int pid, boolean freeze) throws RemoteException {
        File freezeFile=new File(cgroup,String.format("uid_%d/pid_%d/cgroup.freeze",uid,pid));
        if(freezeFile.canWrite()){
            try {
                BufferedWriter bw=new BufferedWriter(new FileWriter(freezeFile));
                bw.write(freeze?"1":"0");
                bw.flush();
                bw.close();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

    @Override
    public boolean setAppFreeze(int uid, boolean freeze) throws RemoteException {
        File freezeFile=new File(cgroup,String.format("uid_%d/cgroup.freeze",uid));
        if(freezeFile.canWrite()){
            try {
                BufferedWriter bw=new BufferedWriter(new FileWriter(freezeFile));
                bw.write(freeze?"1":"0");
                bw.flush();
                bw.close();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

    
	@Override
	public List<CgroupInfo> getCgroupList() throws RemoteException {
		return getInfos();
    }

	@Override
	public int getVersion() throws RemoteException {
        // TODO: Implement this method
        return 0;
    }


	@Override
	public int getUid() throws RemoteException {
		return Os.getuid();
	}

	@Override
	public boolean isCgroupSupport() throws RemoteException {
		// TODO: Implement this method
		return cgroup.isDirectory();
	}
	private String[][] filterUid() {
		List<String[]> tmp=new ArrayList<>();
		for (String n:cgroup.list()) {
			if (n.startsWith("uid_")) {
				List<String> uid=new ArrayList<>();
				uid.add(n);
				for (String p:new File(cgroup, n).list()) {
					if (p.startsWith("pid_")) {
                        uid.add(p);
					}
                    if (uid.size() > 1)
                        tmp.add(uid.toArray(new String[uid.size()]));
				}
			}
		}
		return tmp.toArray(new String[tmp.size()][]);
	}
    
	private List<CgroupInfo> getInfos() {
		String[][] uids=filterUid();
		List<CgroupInfo> tmp=new ArrayList<>();
		for (String[] s:uids) {
			CgroupInfo ci=new CgroupInfo();
		}
		return tmp;
	}
    @Override
    public List<CgroupInfo> queryAll(int type) throws RemoteException {
        if (!isCgroupSupport())return null;
        File[] uids=cgroup.listFiles(dirFilter);
        if (uids == null)return null;
        List<CgroupInfo> tmp=new ArrayList<>();
        for (File f:uids) {
            int uid=Integer.parseInt(f.getName().substring(4));
            if(type>=1&&(uid<10000||uid>65535))continue;
            File[] pids=f.listFiles(dirFilter);
            if (pids.length == 0&&type>=2)continue;
            CgroupInfo info=new CgroupInfo();
            info.uid = uid;
            info.name = pm.getNameForUid(info.uid);
            if(!"com.moe.cgroup".equals(info.name))
                tmp.add(info);
            try {
                info.freeze = Integer.parseInt(readLine(new File(f, "cgroup.freeze")));
            } catch (NumberFormatException e) {}
            info.pids = new ArrayList<>();
            int count=0;
            for(int i=0;i<pids.length;i++){
                PidInfo pi=new PidInfo();
                pi.uid=uid;
                pi.pid=Integer.parseInt(pids[i].getName().substring(4));
                File freeze=new File(pids[i],"cgroup.freeze");
                if(freeze.exists())
                try {
                    pi.freeze=Integer.parseInt(readLine(freeze));
                    if(pi.freeze==1)
                        count++;
                } catch (NumberFormatException e) {
                    info.msg=e.toString();
                }
                pi.cmdline=readCmdline(pi.pid);
                info.pids.add(pi);
            }
            info.freezeCount=count;
            
        }
        return tmp;
    }

    @Override
    public String readCmdline(int pid) throws RemoteException {
        return readLine(new File(String.format("/proc/%d/cmdline",pid)));
    }

    public String readLine(File file){
        String line=null;
        BufferedReader br=null;
        try {
            br=new BufferedReader(new FileReader(file));
            line=br.readLine();
 
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {}
        }
        return line;
    }
    class FileFilterClass implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            // TODO: Implement this method
            return pathname.isFile();
        }
    }
    class DirFilterClass implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            // TODO: Implement this method
            return pathname.isDirectory();
        }


	}
    public static Context getContext(String packageName) throws PackageManager.NameNotFoundException {
        Context impl = ActivityThread.systemMain().getSystemContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        while (impl instanceof ContextWrapper) {
            impl = ((ContextWrapper) impl).getBaseContext();
        }

        try {
            Method method = impl.getClass().getDeclaredMethod("createAppContext", ActivityThread.class, LoadedApk.class);
            method.setAccessible(true);
            return (Context) method.invoke(null, ActivityThread.currentActivityThread(), ActivityThread.currentActivityThread().peekPackageInfo(packageName, true));
        } catch (NoSuchMethodException e) {} catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (SecurityException e) {} catch (IllegalArgumentException e) {}
        return null;
    }
	public static void main(String[] args) {
        System.out.println("boot");
		Looper.prepare();
		new Cgroupd(Looper.myLooper());
		Looper.loop();
	}
}

