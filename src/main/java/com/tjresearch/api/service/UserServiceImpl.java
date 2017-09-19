package com.tjresearch.api.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.tjresearch.api.core.APIMapping;
import com.tjresearch.api.pojo.UserInfo;
@Service
public class UserServiceImpl {
	@APIMapping("bit.api.user.getUser")
	public UserInfo getUser(Long userId){
		Assert.notNull(userId);
		UserInfo info = new UserInfo();
		info.setName("小明");
		info.setSex("男");
		info.setUserId(userId);
		info.setIdcard("123");
		return info;
	}

}
