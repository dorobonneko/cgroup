/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.cgroup.util;

import android.annotation.*;
import android.os.*;

import android.content.AttributionSource;
import android.content.IContentProvider;
import android.system.Os;

public class IContentProvicerUtils
{
	public static Bundle callCompat(IContentProvider provider, @Nullable String callingPkg, @Nullable String authority, @Nullable String method, @Nullable String arg, @Nullable Bundle extras) throws RemoteException {
        Bundle result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            result = provider.call((new AttributionSource.Builder(Os.getuid())).setPackageName(callingPkg).build(), authority, method, arg, extras);
        } else if (Build.VERSION.SDK_INT >= 30) {
            result = provider.call(callingPkg, (String) null, authority, method, arg, extras);
        } else if (Build.VERSION.SDK_INT >= 29) {
            result = provider.call(callingPkg, authority, method, arg, extras);
        } else {
            result = provider.call(callingPkg, method, arg, extras);
        }

        return result;
    }
}
