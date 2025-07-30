package com.moe.cgroup.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class PidInfo implements Parcelable {
    public int uid;
    public int pid;
    public int freeze;
    public String cmdline;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(pid);
        dest.writeInt(freeze);
        dest.writeString(cmdline);
    }
    
    
    public static final Creator<PidInfo> CREATOR=new Creator<PidInfo>(){

        @Override
        public PidInfo createFromParcel(Parcel source) {
            PidInfo pi=new PidInfo();
            pi.uid=source.readInt();
            pi.pid=source.readInt();
            pi.freeze=source.readInt();
            pi.cmdline=source.readString();
            return pi;
        }

        @Override
        public PidInfo[] newArray(int size) {
            return new PidInfo[size];
        }
    };
    
}
