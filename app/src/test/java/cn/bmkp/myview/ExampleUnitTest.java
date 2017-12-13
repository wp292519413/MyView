package cn.bmkp.myview;

import org.junit.Test;

import java.math.BigDecimal;

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
}