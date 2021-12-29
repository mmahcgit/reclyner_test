package com.emmahc.smartchair.utils;


import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by dev.oni on 2018. 10. 27..
 * Copyright or OutSourcing Source
 * certificate dev.oni
 */

public class CalendarUtil {
    private static int count;
    private static int position;
    private static boolean flag;

    public static final Integer CURRENT_YEAR = DateTime.now().getYear();

    public static final Integer CURRENT_MONTH = DateTime.now().getMonthOfYear();

    public static final Integer CURRENT_DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static final Integer LAST_DAY_OF_CURRENT_MONTH = DateTime.now()
            .dayOfMonth().getMaximumValue();

    public static final Integer LAST_HOUR_OF_CURRENT_DAY = DateTime.now()
            .hourOfDay().getMaximumValue();


    public static final Integer LAST_MINUTE_OF_CURRENT_HOUR = DateTime.now().minuteOfHour().getMaximumValue();

    public static final Integer LAST_SECOND_OF_CURRENT_MINUTE = DateTime.now().secondOfMinute().getMaximumValue();


    public static DateTime getLastDateOfMonth() {
        return new DateTime(CURRENT_YEAR, CURRENT_MONTH,
                LAST_DAY_OF_CURRENT_MONTH, LAST_HOUR_OF_CURRENT_DAY,
                LAST_MINUTE_OF_CURRENT_HOUR, LAST_SECOND_OF_CURRENT_MINUTE);
    }

    // Function to ind missing number
    public static void catchMissingDays(ArrayList<Integer> missingDataArray, ArrayList<Integer> a) {
        //a 정렬
        Collections.sort(a);

        ArrayList<Integer> dupliteDeletedArray = new ArrayList<Integer>();
        //a 배열 중복제거
        for(int i=0;i<a.size()-1;i++){

            if(a.get(i) == a.get(i+1)){
                a.set(i,0);
            }

        }
        for(int i=0;i<a.size();i++){
            if(a.get(i) != 0){
                dupliteDeletedArray.add(a.get(i));
            }
        }


        int missing;
        int tmp=0;
        for(int i=tmp;i<a.size();i++)
        {
            int x = a.get(++tmp);
            int y = a.get(i) +1;

            if(x != y )
            {
                missing = y;
                break;
            }
        }
        /**
         * @param CalendarUtil.LAST_DAY_OF_CURRENT_MONTH
         * 이번달 마지막 날을 리턴한다.
         */
        for (; position < CalendarUtil.LAST_DAY_OF_CURRENT_MONTH; position++) {
            try{
                if ((a.get(position) - count) != position) {
                    System.out.println("Missing Number: " + (position + count));
                    missingDataArray.add(position + count);
                    flag = true;
                    count++;
                    break;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                count = position = 0;
                flag = false;
                return;
            }
        }

        if (flag) {
            flag = false;
            catchMissingDays(missingDataArray,a);
        }
    }

    /**
     * 루틴 사용전 반드시 지켜야 할 사항!
     * 1. 배열 내림차순 Sort 되어 있는지 확인.
     */
    private static int[] findMissingNumber(ArrayList<Integer> a){
        int tmp=0;
        try{
            for(int i=tmp;i<a.size();i++) {
                int x = a.get(++tmp);
                int y = a.get(i) +1;

                if(x != y )
                    return new int[]{tmp,y};
            }
        }catch (IndexOutOfBoundsException e){
            return new int[]{tmp,-1};
        }
        return null;
    }
}
