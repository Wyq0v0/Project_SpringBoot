package com.wyq.project_springboot.utils;

public class RedisConstants {
    public static final String USERACCESS_TOKEN_KEY = "useraccess:token:";
    public static final long USERACCESS_TOKEN_TTL = 720L;
    public static final String MOMENT_KEY = "moment:id:";
    public static final long MOMENT_TTL = 5L;
    public static final String MOMENT_THUMBS_UP_KEY = "moment:thumbs_up:";
    public static final String MOMENT_THUMBS_UP_RANK_KEY = "moment:thumbs_up_rank";
    public static final String MOMENT_COMMENT_KEY = "moment:comment:";
    public static final String MOMENT_COMMENT_RANK_KEY = "moment:comment_rank";
    public static final String COMMENT_THUMBS_UP_KEY = "comment:thumbs_up:";
    public static final String CONCERN_KEY = "concern:";
    public static final String NOTICE_ALL_KEY = "notice:all";
    public static final String NOTICE_PERSON_KEY = "notice:person:";
    public static final String USER_SIGN_IN_KEY = "sign_in:";
    public static final String EXP_TASK_KEY = "exp_task:";
    public static final String SIGN_IN_TASK_KEY = "1";
    public static final String SEND_MOMENT_TASK_KEY = "2";
    public static final String SEND_COMMENT_TASK_KEY = "3";
    public static final String ADDRESS_KEY = "address:";
}
