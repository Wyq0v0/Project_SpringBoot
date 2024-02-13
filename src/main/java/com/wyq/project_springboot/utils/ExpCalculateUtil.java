package com.wyq.project_springboot.utils;

import static com.wyq.project_springboot.utils.ExpConstants.*;

public class ExpCalculateUtil {
    public static int ExpToLevel(int exp){
        if(exp < LEVEL_1){
            return 0;
        }else if(LEVEL_1 < exp && exp < LEVEL_2){
            return 1;
        }else if(LEVEL_2 < exp && exp < LEVEL_3){
            return 2;
        }else if(LEVEL_3 < exp && exp < LEVEL_4){
            return 3;
        }else if(LEVEL_4 < exp && exp < LEVEL_5){
            return 4;
        }else if(LEVEL_5 < exp && exp < LEVEL_6){
            return 5;
        }else if(LEVEL_6 < exp && exp < LEVEL_7){
            return 6;
        }else if(LEVEL_7 < exp && exp < LEVEL_8){
            return 7;
        }else if(LEVEL_8 < exp && exp < LEVEL_9){
            return 8;
        }else if(LEVEL_9 < exp && exp < LEVEL_10){
            return 9;
        }else {
            return 10;
        }
    }

    public static int getNextLevelValue(int exp){
        if(exp < LEVEL_1){
            return LEVEL_1;
        }else if(LEVEL_1 < exp && exp < LEVEL_2){
            return LEVEL_2;
        }else if(LEVEL_2 < exp && exp < LEVEL_3){
            return LEVEL_3;
        }else if(LEVEL_3 < exp && exp < LEVEL_4){
            return LEVEL_4;
        }else if(LEVEL_4 < exp && exp < LEVEL_5){
            return LEVEL_5;
        }else if(LEVEL_5 < exp && exp < LEVEL_6){
            return LEVEL_6;
        }else if(LEVEL_6 < exp && exp < LEVEL_7){
            return LEVEL_7;
        }else if(LEVEL_7 < exp && exp < LEVEL_8){
            return LEVEL_8;
        }else if(LEVEL_8 < exp && exp < LEVEL_9){
            return LEVEL_9;
        }else if(LEVEL_9 < exp && exp < LEVEL_10){
            return LEVEL_10;
        }else {
            return 0;
        }
    }
}
