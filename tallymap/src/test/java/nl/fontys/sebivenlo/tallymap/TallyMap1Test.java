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

import nl.fontys.sebivenlo.tallymap.TallyMap1;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class TallyMap1Test extends TallyMapStringTestBase {

    @Override
    protected TallyMap<String> createFromMap( String name,Map<String, String> hm ) {
        return new TallyMap1<>( hm ).named( name);
    }

    /**
     * Make instance for the tests.
     *
     * @param keys to map
     * @return the sut.
     */
    @Override
    protected TallyMap<String> createInstance( String name, Collection<String> keys ) {
        return new TallyMap1<>( keys ).named( name);
    }

    @Test
    public void testCopyCTor() {
        TallyMap<String> map3 = new TallyMap1<>( testSet1() );
        TallyMap<String> map4 = new TallyMap1( map3 );//<>( map3 );
    }

    @Test
    public void unmappedAddsKey() {
        map1.addTallyForKey( "Z", 7L );
        assertEquals( 7L, map1.getTallyForKey( "Z" ) );
    }

    @Test
    public void defaultCtorEmpty() {
        TallyMap<String> map = new TallyMap1<>();
        assertEquals( 0L, map.grandTotal() );
    }

}
