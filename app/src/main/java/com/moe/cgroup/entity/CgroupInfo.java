/**
 * @Author 
 * @AIDE AIDE+
*/
package com.moe.cgroup.entity;
import android.os.Parcelable;
import android.os.Parcel;
import java.util.ArrayList;
import java.util.List;

public class CgroupInfo implements Parcelable {
	public int uid,freezeCount,freeze;
	public List<PidInfo> pids;
    public String name,msg;
	private CgroupInfo(Parcel p) {
		uid = p.readInt();
        freeze=p.readInt();
        freezeCount=p.readInt();
        pids=p.readArrayList(PidInfo.class.getClassLoader());
        name=p.readString();
        msg=p.readString();
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
        dest.writeInt(freeze);
        dest.writeInt(freezeCount);
        dest.writeList(pids==null?new ArrayList<>():pids);
        dest.writeString(name);
        dest.writeString(msg);
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

