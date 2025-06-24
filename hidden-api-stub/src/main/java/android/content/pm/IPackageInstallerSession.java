package android.content.pm;

import android.content.IntentSender;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public interface IPackageInstallerSession extends IInterface {

    void setClientProgress(float progress)
            throws RemoteException;

    void addClientProgress(float progress)
            throws RemoteException;

    String[] getNames()
            throws RemoteException;

    ParcelFileDescriptor openWrite(String name, long offsetBytes, long lengthBytes)
            throws RemoteException;

    ParcelFileDescriptor openRead(String name)
            throws RemoteException;

    void write(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor fd)
            throws RemoteException;

    void removeSplit(String splitName)
                throws RemoteException;

    void close()
            throws RemoteException;

    // removed from 28
    void commit(IntentSender statusReceiver)
            throws RemoteException;

    void commit(IntentSender statusReceiver, boolean forTransferred)
            throws RemoteException;

    void transfer(String packageName)
                throws RemoteException;

    void abandon()
            throws RemoteException;

    boolean isMultiPackage()
            throws RemoteException;

    int[] getChildSessionIds()
            throws RemoteException;

    void addChildSessionId(int sessionId)
            throws RemoteException;

    void removeChildSessionId(int sessionId)
            throws RemoteException;

    int getParentSessionId()
            throws RemoteException;

    boolean isStaged()
            throws RemoteException;

    abstract class Stub extends Binder implements IPackageInstallerSession {

        public static IPackageInstallerSession asInterface(IBinder binder) {
            throw new UnsupportedOperationException();
        }
    }
}
