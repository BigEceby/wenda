package com.LZH.service;

import com.LZH.util.JedisAdapter;
import com.LZH.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by asus on 2017/4/23.
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        String followeekey = RedisKeyUtil.getBizFolloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerkey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeekey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0 ;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        String followeekey = RedisKeyUtil.getBizFolloweeKey(userId,entityType);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerkey,String.valueOf(userId));
        tx.zrem(followeekey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0 ;
    }

    //获取关注者列表
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerkey,0,count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset,int count){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerkey,offset,count));
    }

    //获取所关注的列表
    public List<Integer> getFollowees(int entityType, int userId, int count){
        String followeekey = RedisKeyUtil.getBizFollowerKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeekey,0,count));
    }

    public List<Integer> getFollowees(int entityType, int userId, int offset,int count){
        String followeekey = RedisKeyUtil.getBizFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeekey,offset,count));
    }

    public long getFollowerCount(int entityType, int entityId){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerkey);
    }

    public long getFolloweeCount(int entityType, int userId){
        String followeekey = RedisKeyUtil.getBizFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeekey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerkey = RedisKeyUtil.getBizFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerkey,String.valueOf(userId)) != null;
    }


    private List<Integer> getIdsFromSet(Set<String> set){
        List<Integer> ids = new ArrayList<Integer>();
        for(String str : set){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
}
