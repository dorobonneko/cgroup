/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.cgroup;
import android.content.ContentProvider;
import android.net.Uri;
import android.database.Cursor;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.IBinder;

public class CgroupProvider extends ContentProvider {
	private IBinder mBinder;
	@Override
	public Bundle call(String authority, String method, String arg, Bundle extras) {
		// TODO: Implement this method
		return call(method, arg, extras);
	}

	@Override
	public Bundle call(String method, String arg, Bundle extras) {
		if (method != null)
			switch (method) {
                case "quit":
                    mBinder=null;
                    break;
				case "getBinder" :
					if(mBinder==null)
						return null;
                    if(!mBinder.pingBinder()){
                        mBinder=null;
                        return null;
                    }
					Bundle b=new Bundle();
					b.putParcelable("binder",new BinderContainer(mBinder));
					return b;
				case "setBinder" :
					extras.setClassLoader(BinderContainer.class.getClassLoader());
					BinderContainer bc=extras.getParcelable("binder",BinderContainer.class);
					if(bc!=null)
						mBinder=bc.binder;
					break;
			}
		return super.call(method, arg, extras);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO: Implement this method
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO: Implement this method
		return 0;
	}

}

