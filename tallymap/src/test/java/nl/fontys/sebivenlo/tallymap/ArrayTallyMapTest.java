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

import nl.fontys.sebivenlo.tallymap.ArrayTallyMap;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class ArrayTallyMapTest extends TallyMapTestBase<Integer> {

    /**
     * Ignores hm.
     *
     * @param hm the map
     * @return
     */
    @Override
    protected TallyMap<Integer> createFromMap( Map<Integer, String> hm ) {
        return new ArrayTallyMap( hm.size() );
    }

    @Override
    protected TallyMap<Integer> createInstance( Collection<Integer> keys ) {
        int maxMapped = keys.stream().max( ( a, b ) -> a.compareTo( b ) ).get()
                .intValue();
        return new ArrayTallyMap( maxMapped + 1, keys );

    }

    @Override
    protected Integer getTestValue( int i ) {
        return i;
    }

    static List<Integer> l1;
    static List<Integer> l2;

    {
        l1 = Arrays.asList( new Integer[]{ 0, 1, 2, 3, 4, 5 } );
        l2 = Arrays.asList( new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8 } );
    }

    @Override
    protected List<Integer> testSet1() {
        return l1;
    }

    @Override
    protected List<Integer> testSet2() {
        return l2;
    }

    @Override
    //@Test( expected = NullPointerException.class )
    public void testUnknownKeyValue() {
        System.out.println( "not supported in this tallymap impl" );
        //super.testUnknownKeyValue(); //To change body of generated methods, choose Tools | Templates.
    }

    @Test
    public void mapCanHaveHoles() {
        map2 = new ArrayTallyMap( 5 );
        map2.addKey( 8 );
        assertEquals( 5 + 1, map2.keySet().size() );
        assertFalse( map2.keySet().contains( 6 ) );
        assertFalse( map2.keySet().contains( 7 ) );
        assertTrue( map2.keySet().contains( 8 ) );
    }
}
