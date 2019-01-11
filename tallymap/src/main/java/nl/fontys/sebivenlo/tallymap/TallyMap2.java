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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;
//import net.jcip.annotations.ThreadSafe;

/**
 * Tally map is a Thread safe concurrent map to count (tally) things. The Key
 * can be of any type suitable as a key in a Map.
 * <p>
 * The TallyMap2 has a snapshot facility, which takes a copy of the accumulated
 * count and saves it as a unmodifiable map.
 * <p>
 * Typical usage is in tests.
 *
 * @param <K> Key type
 *
 * @author hom
 */
//@ThreadSafe
public class TallyMap2<K> implements TallyMap<K> {

    private final ConcurrentHashMap<K, LongAdder> map;

    /**
     * Set up TallyMap with keySet.
     *
     * @param keySet the keys to use for the Tally.
     */
    public TallyMap2( Collection<K> keySet ) {
        map = new ConcurrentHashMap<>( keySet.size() );
        for ( K k : keySet ) {
            map.put( k, new LongAdder() );
        }
    }

    /**
     * Add a counter. Counter is added only when not yet in map.
     *
     * @param key to add
     *
     * @return this map.
     */
    @Override
    public final TallyMap<K> addKey( K key ) {
        map.putIfAbsent( key, new LongAdder() );
        return this;
    }

    /**
     * Set up TallyMap with keys from a Map.
     *
     * @param map to provide keySet
     */
    public TallyMap2( Map<K, ?> map ) {
        this( map.keySet() );
    }

    /**
     * Create a TallyMap from other TallyMap with equal keyset, starting at all
     * zeros.
     *
     * @param tm copy constructor
     */
    public TallyMap2( TallyMap<K> tm ) {
        this( tm.keySet() );
    }

    /**
     * Create a map with an empty set.
     */
    public TallyMap2() {
        this( new HashSet<>() );
    }

    /**
     * Get the count through the key. Trying to get from a non existing counter
     * will return 0l;
     *
     * @param k the key
     *
     * @return the counter value
     */
    public long getTallyForKey( K k ) {
        long result = 0;
        try {
            result = map.get( k ).sum();
        } catch ( NullPointerException ignored ) {
        }
        return result;

    }

    /**
     * Add to count through key. The counter(k) is incremented with delta.
     * <p>
     * Trying to add to a non existing counter will add the counter and set it
     * to delta.
     *
     * @param k     key
     * @param delta increment
     */
    public void addTallyForKey( K k, long delta ) {
        LongAdder x = map.computeIfAbsent( k, v -> new LongAdder() );
        x.add( delta );
    }

    /**
     * Clear all counters.
     * <p>
     * This map iterates (concurrently) over all keys and set the associated
     * counters to 0. There is no guarantee that all counters are 0 at any same
     * moment, unless no updates of the counters occur while this clearing takes
     * place. The user is advised to use this method between tests or create a
     * new map for a new test. Postcondition: all counters in the map have been
     * set to 0.
     */
    public void clearAll() {
        for ( K k : map.keySet() ) {
            map.get( k ).reset();
        }

    }

    @Override
    public Collection<K> keySet() {
        return map.keySet();
    }

    /**
     * Take a snapshot.
     *
     * @return
     */
    private List<Map<K, Long>> snapList
                                       = new CopyOnWriteArrayList<Map<K, Long>>();

    /**
     * Take a snapshot and add it to the list.
     */
    @Override
    public void snapShot() {
        snapList.add( takeSnapShot() );
    }

    /**
     * Get the list of snapshots.
     *
     * @return unmodifiable list of snapshots.
     */
    @Override
    public List getSnapShots() {
        return Collections.unmodifiableList( snapList );
    }

    /**
     * Get the string rep. Note that the string is stable only when NO threads
     * are modifying the map. The order is in key order, natural ordering.
     *
     * @return a string representation.
     */
    @Override
    public String toString() {
        return TallyMap.asString( this );
    }

    /**
     * Return the sum of all values mapped in this TallyMap2.
     *
     * @return the sum
     */
    @Override
    public long grandTotal() {
        return map.reduceValuesToLong( 1L, LongAdder::sum, 0L, Long::sum );
    }
}
