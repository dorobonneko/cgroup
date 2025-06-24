/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.cgroup;
import android.os.*;

public class ProcessInfo implements Parcelable
{
	public int pid;
	public boolean freeze;
	public String cmdline;
	public String cpuset;
	public ProcessInfo(Parcel p){
		
	}
	public ProcessInfo(){
		
	}
	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO: Implement this method
	}

	public static Creator<ProcessInfo> CREATOR=new Creator<ProcessInfo>(){

		@Override
		public ProcessInfo createFromParcel(Parcel source)
		{
			// TODO: Implement this method
			return new ProcessInfo(source);
		}

		@Override
		public ProcessInfo[] newArray(int size)
		{
			// TODO: Implement this method
			return new ProcessInfo[size];
		}
	};
}
