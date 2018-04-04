package cn.bmkp.myview;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test1() throws Exception {
        double d = 2.2345;
        double keep = keep(d, 3);
        System.out.println("keep: " + keep);
    }

    private static double keep(Double value, int digit) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Test
    public void test2(){
        /*String s = "123";
        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
        System.out.println(split.length);
        List<String> list = Arrays.asList(split);*/
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        System.out.println(year + "-" + (month + 1) + "-" + day);
    }
}