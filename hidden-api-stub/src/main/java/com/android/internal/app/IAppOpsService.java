package com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;


import java.util.List;

import android.app.AppOpsManagerHidden;

public interface IAppOpsService extends IInterface {

    List<AppOpsManagerHidden.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops)
            throws RemoteException;

    List<AppOpsManagerHidden.PackageOps> getUidOps(int uid, int[] ops)
            throws RemoteException;

    void setMode(int code, int uid, String packageName, int mode)
            throws RemoteException;

    void setUidMode(int code, int uid, int mode)
            throws RemoteException;

    abstract class Stub implements IAppOpsService {

        public static IAppOpsService asInterface(IBinder obj) {
            throw new RuntimeException("STUB");
        }
    }
}
