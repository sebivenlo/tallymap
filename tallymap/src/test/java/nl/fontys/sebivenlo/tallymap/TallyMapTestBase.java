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

import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author hom
 * @param <T>
 */
public abstract class TallyMapTestBase<T> {
    
    protected abstract TallyMap<T> createFromMap( Map<T, String> hm );
    
    protected abstract TallyMap<T> createInstance( Collection<T> keys );

    /**
     * Get a test value from a collection or array. The mapping between i and
     * the returned value should be constant or a constant function.
     *
     * @param i index
     * @return a test value.
     */
    protected abstract T getTestValue( int i );
    
    protected abstract List<T> testSet1();
    
    protected abstract List<T> testSet2();
    
    protected TallyMap<T> map1;
    protected TallyMap<T> map2;
    protected List<T> keys1;
    protected List<T> keys2;
    
    public TallyMapTestBase() {
    }
    
    @Before
    public void setUp() {
        keys1 = testSet1();
        keys2 = testSet2();
        map1 = createInstance( keys1 );
        map2 = createInstance( keys1 );
    }
    
    @After
    public void tearDown() {
        map1 = null;
        map2 = null;
    }

    /**
     * Test of setupCounter method, of class TallyMap2.
     */
    @Test
    public void testSetupCounter() {
        System.out.println( "setupCounter" );
        
        for ( T k : keys1 ) {
            assertEquals( 0, map1.getTallyForKey( k ) );
        }
    }

    /**
     * Test of incrementByKey method, of class TallyMap2.
     */
    @Test
    public void testIncrementByKey() {
        System.out.println( "incrementByKey" );
        T k = getTestValue( 0 );
        int expResult = 1;
        map1.incrementForKey( k );
        assertEquals( expResult, map1.get( k ) );
    }

    /**
     * Test of getTallyByKey method, of class TallyMap2.
     */
    @Test
    public void testgetTallyByKey() {
        System.out.println( "getTallyByKey" );
        T b = getTestValue( 1 );
        map1.incrementForKey( b );
        map1.incrementForKey( b );
        
        long expResult = 2;
        long result = map1.getTallyForKey( b );
        assertEquals( expResult, result );
        expResult = 0;
        T a = getTestValue( 0 );
        result = map1.getTallyForKey( a );
        assertEquals( expResult, result );
    }

    /**
     * Test of getTallies method, of class TallyMap2.
     */
    @Test
    public void testGetTallies() {
        System.out.println( "getTallies" );
        Map<T, Long> expResult = new HashMap<>();
        for ( T key : map1.keySet() ) {
            expResult.put( key, 0L );
        }
        T a = getTestValue( 0 );
        T b = getTestValue( 1 );
        T c = getTestValue( 2 );
        expResult.put( a, 0L );
        expResult.put( b, 0L );
        expResult.put( c, 2L );
        //expResult.put( "Z", 5L );
        map1.incrementForKey( c );
        map1.incrementForKey( c );
        //      map1.addTallyForKey( "Z", 5 );
        Map<T, Long> result = map1.takeSnapShot();
        assertEquals( expResult, result );
    }

    /**
     * Test of snapshotEquals method, of class TallyMap2.
     */
    @Test
    public void testSnapshotEquals() {
        System.out.println( "snapshotEquals" );
        TallyMap<T> other = createInstance( keys2 );
        boolean expResult = false;
        assertFalse( map1.snapShotEquals( other ) );
        assertFalse( other.snapShotEquals( map1 ) );
        
        other = map2;
        assertTrue( map1.snapShotEquals( other ) );
        T a = getTestValue( 0 );
        
        other.incrementForKey( a );
        assertFalse( map1.snapShotEquals( other ) );
        
        map1.incrementForKey( a );
        assertTrue( map1.snapShotEquals( other ) );

        // test trivial equals, coverage
        Map<T, Long> map3 = map2.takeSnapShot();
        Map<T, Long> map4 = map3;
        assertTrue( map2.snapShotEquals( map2 ) );
    }
    
    @Test
    public void testAddCountByKey() {
        System.out.println( "getTallyByKey" );
        T k = getTestValue( 1 );
        map1.incrementForKey( k );
        map1.incrementForKey( k );
        int delta = 4;
        map1.addTallyForKey( k, delta );
        long expResult = 6;
        long result = map1.getTallyForKey( k );
        assertEquals( expResult, result );
        expResult = 0;
        T a = getTestValue( 0 );
        result = map1.getTallyForKey( a );
        assertEquals( expResult, result );
        
    }
    
    @Test
    public void testDecrement() {
        map1.decrementForKey( getTestValue( 0 ) );
        assertEquals( "negative values allowed", -1, map1.getTallyForKey(
                      getTestValue( 0 ) ) );
    }
    
    @Test
    public void testSnapShots() {
        map1.snapShot();
        assertEquals( map1.getSnapShots().get( 0 ), map2.takeSnapShot() );
        T a = getTestValue( 0 );
        map1.incrementForKey( a );
        map1.snapShot();
        map2.incrementForKey( a );
        assertEquals( map1.getSnapShots().get( 1 ), map2.takeSnapShot() );
        
    }
    
    @Test
    public void testDiff() {
        
        assertEquals( "equal maps ", 0, map2.snapShotDiff( map1 ).length() );
        T a = getTestValue( 0 );
        T b = getTestValue( 1 );
        map2 = createInstance( keys2 );
        map2.addTallyForKey( b, 3 );
        assertEquals( "same map ", 0, map1.snapShotDiff( map1 ).length() );
        System.out.println( "diff map1 and map2:" + map1.snapShotDiff( map2 ) );
        assertFalse( "other map12 ", 0 == map1.snapShotDiff( map2 ).length() );
        System.out.println( "diff map2 and map1:" + map2.snapShotDiff( map1 ) );
        assertFalse( "other map21 ", 0 == map2.snapShotDiff( map1 ).length() );
        map2 = createInstance( keys2 );
        map2.addTallyForKey( a, 2 );
        System.out.println( "diff map2 and map1:" + map2.snapShotDiff( map1 ) );
        assertFalse( "other map22 ", 0 == map2.snapShotDiff( map1 ).length() );
        
    }
    
    @Test
    public void fromMap() {
        HashMap<T, String> hm = new HashMap<>();
        for ( T k : keys1 ) {
            hm.put( k, k.toString() );
        }
        T a = getTestValue( 0 );
        
        TallyMap<T> tm1 = createFromMap( hm );
        TallyMap<T> tm2 = createInstance( tm1.keySet() );
        tm1.incrementForKey( a );
        tm2.incrementForKey( a );
        assertTrue( "maps tm1 and tm2 should be equal", tm1
                    .snapShotEquals( tm2 ) );
    }
    
    @Test
    public void testUnderLoad() {
        new LoadTester<>( LoadTester.yielder, map2, map1, keys1 ).withRounds(
                1000 ).withTasks( 480 ).run();
        new LoadTester<>( LoadTester.lazyBones, map2, map1, keys1 ).run();
        new LoadTester<>( LoadTester.sleepyCat, map2, map1, keys1 ).run();
    }
    
    @Test
    public void testClearAll() {
        T a = getTestValue( 0 );
        T b = getTestValue( 1 );
        T c = getTestValue( 2 );
        T[] testA = ( T[] ) new Object[ 3 ];
        testA[ 0 ] = a;
        testA[ 1 ] = b;
        testA[ 2 ] = c;
        map2.addTallyForKey( a, 2 );
        map2.addTallyForKey( b, 2 );
        map2.addTallyForKey( c, 3 );
        for ( T s : testA ) {
            assertFalse( 0L == map2.getTallyForKey( s ) );
        }
        
        map2.clearAll();
        for ( T s : testA ) {
            assertTrue( 0L == map2.getTallyForKey( s ) );
        }
    }
    
    @Test
    public void testGrandTotal() {
        T a = getTestValue( 0 );
        T b = getTestValue( 1 );
        T c = getTestValue( 2 );
        map2.addTallyForKey( a, 2 );
        map2.addTallyForKey( b, 2 );
        map2.addTallyForKey( c, 3 );
        assertEquals( 7L, map2.grandTotal() );
    }
    
    @Test
    public void testUnknownKeyValue() {
        assertEquals( 0L, map2.getTallyForKey( getTestValue( 6 ) ) );
    }
    
    @Test
    public void testAddKey() {
        T z = getTestValue( 7 );
        TallyMap<T> m = map2.addKey( z );
        assertSame( map2, m );
        assertEquals( 0L, map2.getTallyForKey( z ) );
        map2.addTallyForKey( z, 6 );
        assertEquals( 6L, map2.getTallyForKey( z ) );
    }
    
}
