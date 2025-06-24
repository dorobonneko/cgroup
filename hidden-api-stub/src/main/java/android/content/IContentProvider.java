package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ICancellationSignal;
import android.os.RemoteException;


import java.io.FileNotFoundException;

public interface IContentProvider {

    Cursor query(String callingPkg, Uri url, String[] projection, String selection,
                 String[] selectionArgs, String sortOrder, ICancellationSignal cancellationSignal)
            throws RemoteException;

    Cursor query(String callingPkg, Uri url,  String[] projection,
                 Bundle queryArgs,  ICancellationSignal cancellationSignal)
            throws RemoteException;

    Bundle call(String callingPkg, String method,  String arg,  Bundle extras)
            throws RemoteException;

    Bundle call(String callingPkg, String authority, String method,  String arg,  Bundle extras)
            throws RemoteException;

    Bundle call(String callingPkg, String featureId, String authority, String method,  String arg,  Bundle extras)
            throws RemoteException;

    Bundle call(AttributionSource attributionSource, String authority, String method, String arg, Bundle extras)
            throws RemoteException;
		
    AssetFileDescriptor openAssetFile(
            String callingPkg, Uri url, String mode, ICancellationSignal signal)
            throws RemoteException, FileNotFoundException;
}
