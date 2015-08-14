package com.yjk.mobilesafety.test;

import java.util.List;
import java.util.Random;

import com.yjk.mobilesafety.db.BlackNumberDBOpenHelper;
import com.yjk.mobilesafety.db.dao.BlackNumberDao;
import com.yjk.mobilesafety.domain.BlackNumberInfo;
import com.yjk.mobilesafety.utils.SmsUtils;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	
	public void testCreateDB() throws Exception{
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	
	public void testAdd()throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long number = 1805923;
		Random random = new Random();
		for(int i = 0; i < 100; ++i){
			dao.add(String.valueOf(number + i),
					String.valueOf(random.nextInt(3) + 1));
		}
	}
	
	public void testFindAll()throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		
		for(BlackNumberInfo info : infos){
			System.out.println(info.toString());
		}
	}
	
	
	public void testDelete()throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}
	
	public void testUpdate()throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}
	
	public void testFind()throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		
		assertEquals(true, result);
	}
	
	public void testSmsUtils()throws Exception{
		//SmsUtils.restoreSms(getContext(), false);
	}
	
}
