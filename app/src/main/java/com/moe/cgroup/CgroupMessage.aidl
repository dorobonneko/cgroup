package com.moe.cgroup;

import com.moe.cgroup.entity.CgroupInfo;

interface CgroupMessage{
	List<CgroupInfo> getCgroupList();
	List<CgroupInfo> queryAll(int type);
	int getUid();
	int getVersion();
	boolean isCgroupSupport();
	boolean setFreeze(int uid,int pid,boolean freeze);
	boolean setAppFreeze(int uid,boolean freeze);
	void close();
	String readCmdline(int pid);
}
