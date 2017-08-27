package com.LZH.util;

/**
 * Created by asus on 2017/4/20.
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOWER = "FOLLOWER";
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(int entityId, int entityType){
        return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityId, int entityType){
        return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }
    //粉丝列表
    public static String getBizFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
    //关注的对象
    public static String getBizFolloweeKey(int userId ,int entityType){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getEventQueue(){
        return BIZ_EVENTQUEUE;
    }

    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
}
