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

public class Cgroupd extends CgroupMessage.Stub {
	private static File cgroup = new File("/sys/fs/cgroup");
    private FileFilter dirFilter=new DirFilterClass();
    private IPackageManager pm;
	public Cgroupd() {
        System.out.println("init service");

        pm=IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        IActivityManager iam=null;
        IBinder binder = ServiceManager.getService("activity");
        if (Build.VERSION.SDK_INT > 26)
            iam = ActivityManagerNative.asInterface(binder);
        else
            iam = IActivityManager.Stub.asInterface(binder);
        try {
            ContentProviderHolder provider=iam.getContentProviderExternal("com.moe.cgroup", 0, null, null);
            System.out.println("provider");
            Bundle b=new Bundle();
            b.putParcelable("binder", new BinderContainer(this));
            IContentProvicerUtils.callCompat(provider.provider, null, "com.moe.cgroup", "setBinder", null, b);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
		}
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
    public List<CgroupInfo> queryAll() throws RemoteException {
        if (!isCgroupSupport())return null;
        File[] uids=cgroup.listFiles(dirFilter);
        if (uids == null)return null;
        List<CgroupInfo> tmp=new ArrayList<>();
        for (File f:uids) {
            int uid=Integer.parseInt(f.getName().substring(4));
            if(uid<10000||uid>65535)continue;
            File[] pids=f.listFiles(dirFilter);
            if (pids.length == 0)continue;
            CgroupInfo info=new CgroupInfo();
            info.uid = uid;
            info.pids = new int[pids.length];
            for(int i=0;i<pids.length;i++){
                info.pids[i]=Integer.parseInt(pids[i].getName().substring(4));
            }
            info.name = pm.getNameForUid(info.uid);
            tmp.add(info);
        }
        return tmp;
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
		new Cgroupd();
		Looper.loop();
	}
}

