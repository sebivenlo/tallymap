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

import nl.fontys.sebivenlo.tallymap.TallyMap2;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class TallyMap2Test extends TallyMapStringTestBase {
    
    @Override
    protected TallyMap<String> createFromMap( String name, Map<String, String> hm ) {
        return new TallyMap2<String>( hm ).named( name );
    }
    
    @Override
    protected TallyMap<String> createInstance( String n, Collection<String> keys ) {
        return new TallyMap2<>( keys ).named( n );
    }
    
    @Test
    public void testCopyCTor() {
        TallyMap<String> map3 = new TallyMap2<>( testSet1() );
        TallyMap<String> map4 = new TallyMap2( map3 );//<>( map3 );
    }
    
    @Test
    public void unmappedAddsKey() {
        map1.addTallyForKey( "Z", 7L );
        assertEquals( 7L, map1.getTallyForKey( "Z" ) );
    }
    
    @Test
    public void defaultCtorEmpty() {
        TallyMap<String> map = new TallyMap2<>();
        assertEquals( 0L, map.grandTotal() );
    }
    
}
