package com.github.doomsdayrs.apps.shosetsu;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.cleanString;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void test() {
        String a = "Abc123+++::::/ / `\"";
        System.out.println(a);
        a = cleanString(a);
        System.out.println(a);
    }
}
