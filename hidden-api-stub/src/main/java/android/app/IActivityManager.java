package android.app;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.content.pm.*;
import java.util.List;

public interface IActivityManager extends IInterface {
    ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token, String tag)
            throws RemoteException;

    ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token)
            throws RemoteException;

    void removeContentProviderExternal(String name, IBinder token)
            throws RemoteException;

    int checkPermission(String permission, int pid, int uid)
            throws RemoteException;

    void registerProcessObserver(IProcessObserver observer)
            throws RemoteException;

    void registerUidObserver(IUidObserver observer, int which, int cutpoint, String callingPackage)
            throws RemoteException;

    void forceStopPackage(String packageName, int userId)
            throws RemoteException;

    int startActivityAsUser(IApplicationThread caller, String callingPackage,
                            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
                            int requestCode, int flags, ProfilerInfo profilerInfo,
                            Bundle options, int userId)
            throws RemoteException;
	ParceledListSlice getRecentTasks(int maxNum, int flags, int userId);
	
	void killBackgroundProcesses(String packageName, int userId);
	void unregisterUidObserver(IUidObserver observer);
    boolean isUidActive(int uid, String callingPackage);
    int getUidProcessState(int uid,String callingPackage);
    void registerTaskStackListener(ITaskStackListener listener);
    void unregisterTaskStackListener(ITaskStackListener listener);
    boolean isAppFreezerSupported();
	boolean isAppFreezerEnabled();
	boolean enableAppFreezer(boolean enable);
	boolean isProcessFrozen(int pid);
	void registerUidFrozenStateChangedCallback(IUidFrozenStateChangedCallback callback);
    void unregisterUidFrozenStateChangedCallback(IUidFrozenStateChangedCallback callback);
    int[] getUidFrozenState(int[] uids);
	List<ActivityManager.RunningTaskInfo> getTasks(int maxNum);
	
    abstract class Stub extends Binder implements IActivityManager {

        public static IActivityManager asInterface(IBinder obj) {
            throw new RuntimeException("STUB");
        }
    }
}
