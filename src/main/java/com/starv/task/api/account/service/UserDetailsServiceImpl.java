package com.starv.task.api.account.service;

import com.starv.task.api.account.dao.UserInfoDao;
import com.starv.task.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoDao userInfoDao;

    @Autowired
    public UserDetailsServiceImpl(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        //这里可以可以通过username（登录时输入的用户名）然后到数据库中找到对应的用户信息，并构建成我们自己的UserInfo来返回。
        // TODO Auto-generated method stub


        //这里可以通过数据库来查找到实际的用户信息，这里我们先模拟下,后续我们用数据库来实现
//        if ("admin".equals(username)) {
//            //假设返回的用户信息如下;
//            UserInfo userInfo = new UserInfo("admin", "123456", "ROLE_ADMIN", true, true, true, true);
//            return userInfo;
//
//        }
        UserInfo userInfo = userInfoDao.findUserInfoByUsername(username);
        if (null != userInfo){
            return userInfo;
        }

        return null;

    }
}
