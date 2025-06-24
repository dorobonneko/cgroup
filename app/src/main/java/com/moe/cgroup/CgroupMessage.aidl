package com.moe.cgroup;

import com.moe.cgroup.entity.CgroupInfo;

interface CgroupMessage{
	List<CgroupInfo> getCgroupList();
	List<CgroupInfo> queryAll();
	int getUid();
	int getVersion();
	boolean isCgroupSupport();
}
