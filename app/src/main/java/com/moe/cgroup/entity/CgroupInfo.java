/**
 * @Author 
 * @AIDE AIDE+
*/
package com.moe.cgroup.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class CgroupInfo implements Parcelable {
	public int uid;
	public int[] pids;
    public String name;
	private CgroupInfo(Parcel p) {
		uid = p.readInt();
		int len = p.readInt();
		if (len > 0) {
			pids = new int[len];
			p.readIntArray(pids);
		}
        name=p.readString();
	}
	public CgroupInfo() {
	}
	@Override
	public int describeContents() {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(uid);
		if (pids == null){
			dest.writeInt(0);
		}else {
			dest.writeInt(pids.length);
			dest.writeIntArray(pids);
		}
        dest.writeString(name);
	}

	public static final Parcelable.Creator<CgroupInfo> CREATOR = new Creator<CgroupInfo>() {

		@Override
		public CgroupInfo createFromParcel(Parcel source) {
			return new CgroupInfo(source);
		}

		@Override
		public CgroupInfo[] newArray(int size) {
			return new CgroupInfo[size];
		}
	};
}

