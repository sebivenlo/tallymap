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

import nl.fontys.sebivenlo.tallymap.EnumTallyMap;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nl.fontys.sebivenlo.tallymap.EnumTallyMapTest.ABC;
import static nl.fontys.sebivenlo.tallymap.EnumTallyMapTest.ABC.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class EnumTallyMapTest extends TallyMapTestBase<ABC> {

    enum ABC {
        A, B, C, D, E, F, G, H, I, J, K, L, M
    };

    /**
     * Ignores hm.
     *
     * @param hm the map
     * @return
     */
    @Override
    protected TallyMap<ABC> createFromMap( Map<ABC, String> hm ) {
        return new EnumTallyMap<>( ABC.class, hm.keySet() );
    }

    @Override
    protected TallyMap<ABC> createInstance( Collection<ABC> keys ) {
        return new EnumTallyMap<>( ABC.class, keys );

    }

    @Override
    protected ABC getTestValue( int i ) {
        return ABC.values()[ i ];
    }

    static List<ABC> l1;
    static List<ABC> l2;

    {
        l1 = Arrays.asList( new ABC[]{ A, B, C, D, E } );
        l2 = Arrays.asList( new ABC[]{ A, B, C, D, E, F, G } );
    }

    @Override
    protected List<ABC> testSet1() {
        return l1;
    }

    @Override
    protected List<ABC> testSet2() {
        return l2;
    }

    @Override
    //@Test( expected = NullPointerException.class )
    public void testUnknownKeyValue() {
        System.out.println( "not supported in this tallymap impl" );
        //super.testUnknownKeyValue(); //To change body of generated methods, choose Tools | Templates.
    }

    @Test
    public void defaultMapHasUniverse() {
        TallyMap<ABC> tm = new EnumTallyMap( ABC.class );
        for ( ABC abc : ABC.values() ) {
            tm.addTallyForKey( abc, 1 );
        }
        assertEquals( ABC.values().length, tm.grandTotal() );

    }

}
