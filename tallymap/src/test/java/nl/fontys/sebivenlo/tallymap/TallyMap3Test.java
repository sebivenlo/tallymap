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

import nl.fontys.sebivenlo.tallymap.TallyMap3;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class TallyMap3Test extends TallyMapStringTestBase {
    
    @Override
    //@Test( expected = NullPointerException.class )
    public void testUnknownKeyValue() {
        System.out.println( "not supported in this tallymap impl" );
        //super.testUnknownKeyValue(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected TallyMap<String> createFromMap( String n,
            Map<String, String> hm ) {
        return new TallyMap3<>( hm ).named( n );
    }
    
    @Override
    protected TallyMap<String> createInstance( String n, Collection<String> keys ) {
        return new TallyMap3<>( keys ).named( n );
    }
    
    @Test( expected = NullPointerException.class )
    public void unmappedAddThowsNPE() {
        map1.addTallyForKey( "Z", 1 );
    }
    
    @Test
    public void testCopyCTor() {
        TallyMap<String> map3 = new TallyMap3<>( testSet1() );
        TallyMap<String> map4 = new TallyMap3( map3 );//<>( map3 );

    }

    // supperess test
    public void unmappedAddsKey() {
    }
    
    @Test
    public void defaultCtorEmpty() {
        TallyMap<String> map = new TallyMap3<>();
        assertEquals( 0L, map.grandTotal() );
    }
    
}
