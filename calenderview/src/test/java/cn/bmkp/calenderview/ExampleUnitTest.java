package cn.bmkp.calenderview;

import org.junit.Test;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test1(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 31);
        //int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //int week = calendar.get(Calendar.DAY_OF_WEEK);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        //int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //System.out.println(i);
        System.out.println(i);
    }

}