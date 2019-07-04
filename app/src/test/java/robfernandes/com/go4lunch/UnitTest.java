package robfernandes.com.go4lunch;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static robfernandes.com.go4lunch.utils.Utils.formatNumberOfStars;
import static robfernandes.com.go4lunch.utils.Utils.getTodaysWeekDay;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void formatNumberOfStars_raking5_0() {
        double rankin = 5.0;
        int stars = formatNumberOfStars(rankin);
        assertEquals(3, stars);
    }

    @Test
    public void formatNumberOfStars_raking4_5() {
        double rankin = 4.5;
        int stars = formatNumberOfStars(rankin);
        assertEquals(3, stars);
    }

    @Test
    public void formatNumberOfStars_raking4() {
        double rankin = 4.0;
        int stars = formatNumberOfStars(rankin);
        assertEquals(2, stars);
    }

    @Test
    public void formatNumberOfStars_raking2() {
        double rankin = 2.0;
        int stars = formatNumberOfStars(rankin);
        assertEquals(1, stars);
    }

    @Test
    public void formatNumberOfStars_raking0() {
        double rankin = 0;
        int stars = formatNumberOfStars(rankin);
        assertEquals(0, stars);
    }

    @Test
    public void getTodaysWeekDay_between0And6() {
        int todaysWeekDay = getTodaysWeekDay();
        assertTrue(todaysWeekDay>=0);
        assertTrue(todaysWeekDay<=6);
    }
}