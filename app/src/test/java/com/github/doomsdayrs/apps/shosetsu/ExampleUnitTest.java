package com.github.doomsdayrs.apps.shosetsu;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;

import org.junit.Test;

import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void format() {
        try {
            System.out.println((String) Database.deserialize("U3RyaW5n"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}