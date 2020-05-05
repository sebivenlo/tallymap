/*
 * (C) Pieter van den Hombergh
 * (C) Fontys Hogeschool voor Techniek en Logistiek
 * Hulsterweg 2-6 5912 PL Venlo The Netherlands.
 *
 * email: P dot vandenHombergh at fontys dot nl
 * website http://javabits.fontysvenlo.org
 *
 * This code is distributed under the artistic license version 2.
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 * Before your start using this code, make sure you understand that license.
 */
package nl.fontys.sebivenlo.tallymap;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
abstract class TallyMapStringTestBase extends TallyMapTestBase<String> {

    String[] testVAK = new String[]{ "A", "B",
        "C", "D",
        "E",
        "F",
        "G", "H",
        "I", "J",
        "K" };

    @Override
    protected String getTestValue( int i ) {
        return testVAK[ i ];
    }

    @Override
    protected List<String> testSet1() {
        return Arrays.asList( new String[]{ "A", "B",
            "C", "D",
            "E",
            "F",
            "G", "H" } );
    }

    @Override
    protected List<String> testSet2() {
        return Arrays.asList( testVAK );
    }

    @Test
    public void nonMappedReturnsZero() {
        assertEquals( 0L, map1.getTallyForKey( "Y" ) );
    }

}
