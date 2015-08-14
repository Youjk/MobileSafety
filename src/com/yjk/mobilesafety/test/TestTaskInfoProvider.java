package com.yjk.mobilesafety.test;

import java.util.List;

import com.yjk.mobilesafety.domain.TaskInfo;
import com.yjk.mobilesafety.engine.TaskInfoProvider;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestTaskInfoProvider extends AndroidTestCase {
	public void testGetTaskInfos()throws Exception{
		List<TaskInfo> infos = TaskInfoProvider.getTastInfo(getContext());
		
	}
}
