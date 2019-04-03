package com.starv.task.api.account.dao;

import com.starv.task.entity.UserInfo;
import org.springframework.data.repository.Repository;

/**
 * @Auther: Wang Lijie
 * @Date: 2019/4/3 13:58
 * @Description: TODO
 */
public interface UserInfoDao extends Repository<UserInfo,Integer> {

    UserInfo findUserInfoByUsername(String userName);

}
