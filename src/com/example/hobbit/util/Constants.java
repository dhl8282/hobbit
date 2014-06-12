package com.example.hobbit.util;

public class Constants {
    public static final String PICTURE_DIR = "/Pictures/";
    public static final String HOBBIT_DIR = "/Hobbit/";
    public static final String HTTP_US_WEST_2 = "https://s3-us-west-2.amazonaws.com";
    public static final String AWS_ACCESS_KEY_ID = "AKIAIPW7VHISX5CAHTEQ";
    public static final String AWS_SECRET_KEY = "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";

    public static final String PICTURE_BUCKET = "bucket-for-hobbit";
    public static final String PICTURE_NAME = "NameOfThePicture";

    public static final String INTENT_EXTRA_MISSION = "intent_extra_mission";
    public static final String INTENT_EXTRA_PARENT_MISSION = "intent_extra_parent_mission";
    public static final String INTENT_EXTRA_PHOTO_BITMAP = "intent_extra_photo_bitmap";
    public static final String INTENT_EXTRA_PHOTO_ABS_PATH = "intent_extra_photo_abs_path";
    
    public static final String MISSON_MONGO_DB_ID = "_id";
    public static final String MISSON_TITLE = "title";
    public static final String MISSON_HINT = "hint";
    public static final String MISSON_LOC= "loc";
    public static final String MISSON_COUNT_VIEW = "view";
    public static final String MISSON_COUNT_TRY = "try";
    public static final String MISSON_COUNT_SUCCESS = "success";
    public static final String MISSON_COUNT_FAIL = "fail";
    public static final String MISSON_PARENT_MISSION_ID = "parentMissionId";
    public static final String MISSON_PARENT_USER_ID = "parentUserId";
    
    public static final String USER_ID = "userId";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_FIRST_NAME = "firstName";
    public static final String USER_USERNAME = "username";
    public static final String USER_EMAIL = "email";
    public static final String USER_GENDER = "gender";
    public static final String USER_CREATED_MISSIONS = "createdMissions";
    public static final String USER_REPLIED_MISSIONS = "repliedMissions";
    public static final String USER_SUCCEED_MISSIONS = "succeedMissions";
    public static final String USER_FAILED_MISSIONS = "failedMissions";
    
    public static final String FACEBOOK = "facebook";
    public static final String USER_OBJECT = "user_object";
    
    public static final int ONE = 1;
    public static final int DEFAULT_MISSION_NUMBER = 10;
    public static final int DEFAULT_PICTURE_LENGTH = 360;
    public static final int DEFAULT_PICTURE_QUALITY = 30;
    
    public static String makeUrl(String id) {
    	return HTTP_US_WEST_2 + "/" + PICTURE_BUCKET + "/" + id;
    }
}