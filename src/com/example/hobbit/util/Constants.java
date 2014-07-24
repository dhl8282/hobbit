package com.example.hobbit.util;

public class Constants {
    public static final String PICTURE_DIR = "/Pictures/";
    public static final String HOBBIT_DIR = "/Hobbit/";
    public static final String HTTP_AP_NOEA_1 = "https://s3-ap-northeast-1.amazonaws.com";
    public static final String AWS_ACCESS_KEY_ID = "AKIAJN3LZWGJGDHCWFCA";
    public static final String AWS_SECRET_KEY = "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";

    public static final String PICTURE_BUCKET_ORIGINAL = "bucket-for-hobbit-original";
    public static final String PICTURE_BUCKET_THUMNAIL = "bucket-for-hobbit-thumnail";
    public static final String PICTURE_NAME = "NameOfThePicture";

    public static final String INTENT_EXTRA_MISSION = "intent_extra_mission";
    public static final String INTENT_EXTRA_PARENT_MISSION = "intent_extra_parent_mission";
    public static final String INTENT_EXTRA_PHOTO_BITMAP = "intent_extra_photo_bitmap";
    public static final String INTENT_EXTRA_PHOTO_ABS_PATH = "intent_extra_photo_abs_path";

    public static final String MISSION_MONGO_DB_ID = "_id";
    public static final String MISSION_TITLE = "title";
    public static final String MISSION_HINT = "hint";
    public static final String MISSION_LOC= "loc";
    public static final String MISSION_COUNT_VIEW = "view";
    public static final String MISSION_COUNT_TRY = "try";
    public static final String MISSION_COUNT_SUCCESS = "success";
    public static final String MISSION_COUNT_FAIL = "fail";
    public static final String MISSION_PARENT_MISSION_ID = "parentMissionId";
    public static final String MISSION_MISSION_RESPONSE_ID = "missionResponseId";
    public static final String MISSION_PARENT_USER_ID = "parentUserId";
    public static final String MISSION_BOOLEAN_REPLY = "reply";
    public static final String MISSION_BOOLEAN_NONE = "-1";
    public static final String MISSION_BOOLEAN_SUCCESS = "1";
    public static final String MISSION_BOOLEAN_FAIL = "0";

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
    public static final String USER_TOTAL_LOGIN = "totalLogin";

    public static final String FACEBOOK = "facebook";
    public static final String USER_OBJECT = "user_object";

    public static final int ONE = 1;
    public static final int DEFAULT_MISSION_NUMBER = 10;
    public static final int DEFAULT_PICTURE_LENGTH = 400;
    public static final int DEFAULT_PICTURE_QUALITY = 30;
    public static final int IMAGE_QUALITY_FULL = 100;
    public static final int IMAGE_QUALITY_THUMNAIL = 30;

    public static String makeOriginalUrl(String id) {
         return HTTP_AP_NOEA_1 + "/" + PICTURE_BUCKET_ORIGINAL + "/" + id;
    }

    public static String makeThumnailUrl(String id) {
        return HTTP_AP_NOEA_1 + "/" + PICTURE_BUCKET_THUMNAIL + "/" + id;
    }
}