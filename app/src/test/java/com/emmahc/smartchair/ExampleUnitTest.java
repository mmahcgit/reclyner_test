package com.emmahc.smartchair;

import com.emmahc.smartchair.utils.CalendarUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    //중복제거 유닛테스트
    public ArrayList<Integer> arr(ArrayList<Integer> a){
        Collections.sort(a);

        ArrayList<Integer> arr1 = new ArrayList<Integer>();

        for(int i=0;i<a.size()-1;i++){

            if(a.get(i) == a.get(i+1)){
                a.set(i,0);
            }

        }
//        System.out.println(a);

        for(int i=0;i<a.size();i++){
            if(a.get(i) != 0){

                arr1.add(a.get(i));
            }
        }

//        System.out.println(arr1);
        return arr1;
    }

    @Test
    public void voidDayFinder(){
        ArrayList<Integer> resultArray = new ArrayList<Integer>();

        ArrayList<Integer> a = new ArrayList<Integer>();
        a.add(24);
        a.add(21);
        a.add(24);
        a.add(25);
        a.add(24);
        a.add(26);
        a.add(24);
        a.add(22);
        a.add(31);

        long time = System.currentTimeMillis();
        //1. 중복제거 후, 배열 내림차순 Sorting.
        a = arr(a);


        //2. sort된 배열의 0번째까지 빈 숫자 채워넣기.
        //   당연 1보다 커야 빈숫자를 채우니까 0번배열이 1보다 큰지 체크
        if(a.get(0)>1){
            //2-1 공백숫자 채워놓은 후,
            for(int i=1; i<a.get(0); i++)
                resultArray.add(i);
            //2-2 배열 통째로 집어넣고,
            resultArray.addAll(a);
        }
        //3. 비워진 숫자 채워진거 확인.
//        System.out.println(resultArray);

        try {
            //4. 띄엄띄엄 없는숫자 찾아서 배열에 넣기
            int[] missing = {0, 0};
            while (missing[1] != -1){
//                missing = findMissingNumber(a,missing[0]);
                resultArray.add(missing[1]);
            }

        }catch (IndexOutOfBoundsException e){
            //java.lang.IndexOutOfBoundsException: Index: 7, Size: 7
            e.printStackTrace();
        }
        //4-1 없는숫자 넣어지면 한번더 내림차순정렬
        Collections.sort(resultArray);

//        System.out.println("실행 시간 : " + (System.currentTimeMillis() - time)/100.0 +"초");
//
//        System.out.println(resultArray);
    }
    @Test
    public void findMissingNumber() throws IndexOutOfBoundsException{
        ArrayList<Integer> tmp = new ArrayList<Integer>();

        ArrayList<Integer> a = new ArrayList<Integer>();
        a.add(24);
        a.add(21);
        a.add(24);
        a.add(25);
        a.add(24);
        a.add(26);
        a.add(24);
        a.add(22);
        a.add(31);
        arr(a);

        if(a.get(0)>1){
            //2-1 공백숫자 채워놓은 후,
            for(int i=1; i<a.get(0); i++)
                tmp.add(i);
            //2-2 배열 통째로 집어넣고,
            tmp.addAll(a);
        }
        //3. 비워진 숫자 채워진거 확인.
//        System.out.println(tmp);

        int numberIndex=1;
        for(int i=0;i<a.size();) {
            while(numberIndex<a.get(i)){
                tmp.add(numberIndex++);
            }
            if(tmp.size()>0&&tmp.get(0)+1==a.get(i+1))
                numberIndex++;
            i++;
        }
    }
}