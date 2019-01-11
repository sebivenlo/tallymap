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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Tally map is a Thread safe concurrent map to count (tally) things. The Key
 * can be of any type suitable as a key in a Map.
 * <p>
 * TallyMap has a snapshot facility, which takes a copy of the accumulated
 * count and saves it as a unmodifiable map.
 * <p>
 * Typical usage is in tests.
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 * @param <K> type of keys.
 */
public interface TallyMap<K> {

    /**
     * Add a counter. Counter is added only when not yet in map.
     *
     * @param key to add
     *
     * @return this map
     */
    TallyMap<K> addKey( K key );

    /**
     * Add to count through key. The counter(k) is incremented with delta.
     * <p>
     * Trying to add to a non existing counter will add the counter and set it
     * to delta.
     *
     * @param k     key in the map
     * @param delta amount to add
     */
    void addTallyForKey( K k, long delta );

    /**
     * Clear all counters.
     * <p>
     * This map iterates (concurrently) over all keys and
     * set the associated counters to 0. There is no guarantee that all counters
     * are 0 at any same moment, unless no updates of the counters occur while
     * this clearing takes place. The user is advised to use this method between
     * tests or create a new map for a new test. Postcondition: all counters in
     * the map have been set to 0.
     */
    void clearAll();

    /**
     * Get the list of snapshots.
     *
     * @return unmodifiable list of snapshots.
     */
    List<Map<K, Long>> getSnapShots();

    /**
     * Get the count through the key. Trying to get from a non existing counter
     * will return 0l;
     *
     * @param k the key
     *
     * @return the counter value
     */
    long getTallyForKey( K k );

    /**
     * Return the sum of all values mapped in this TallyMap1.
     *
     * @return the sum
     */
    long grandTotal();

    /**
     * Increment a counter by key. Trying to add to a non existing counter will
     * loose the increment.
     *
     * @param k the key
     */
    /**
     * Increment a counter by key. Trying to add to a non existing counter will
     * loose the increment.
     *
     * @param k the key
     */
    default void incrementForKey( K k ) {
        addTallyForKey( k, 1 );
    }

    /**
     * Decrement a counter by key. Trying to add to a non existing counter will
     * loose the increment.
     *
     * @param k the key
     */
    default void decrementForKey( K k ) {
        addTallyForKey( k, -1 );
    }

    /**
     * Take a snapshot and add it to the list.
     */
    void snapShot();

    /**
     * Calculates difference of two maps and represents it as a String.
     *
     * @param other map to compare
     *
     * @return string. Empty string if maps are equal.
     */
    default String snapShotDiff( TallyMap<K> other ) {

        // do the trivial self comparison first.
        if ( this == other ) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        // take the snapshots.
        Map<K, Long> mySnapShot = takeSnapShot();
        Map<K, Long> otherSnapShot = other.takeSnapShot();
        Set<K> leftDiffSet = new TreeSet<>( mySnapShot.keySet() );
        Set<K> rightDiffSet = new TreeSet<>( otherSnapShot.keySet() );
        leftDiffSet.removeAll( otherSnapShot.keySet() );
        rightDiffSet.removeAll( mySnapShot.keySet() );

        leftDiffSet.stream().forEach( ( k ) -> {
            result.append( "\n\tthis  " )
                    .append( k.toString() )
                    .append( " => " )
                    .append( mySnapShot.get( k ) )
                    .append( ", other missing" );
        } );
        rightDiffSet.stream().forEach( ( k ) -> {
            result.append( "\n\tother " )
                    .append( k.toString() )
                    .append( " => " )
                    .append( otherSnapShot.get( k ) )
                    .append( ", this missing" );
        } );
        // now compare values but only for keys that are available in
        // both maps
        Set<K> minSet = new TreeSet<>( mySnapShot.keySet() );
        minSet.retainAll( otherSnapShot.keySet() );
        minSet.stream().forEach( ( k ) -> {
            long leftValue = mySnapShot.get( k );
            long rightValue = otherSnapShot.get( k );
            if ( leftValue != rightValue ) {
                result.append( "\n\tthis  " )
                        .append( k.toString() )
                        .append( " => " )
                        .append( leftValue )
                        .append( ", other " )
                        .append( k.toString() )
                        .append( " => " )
                        .append( rightValue );
            }
        } );
        return result.toString();
    }

    /**
     * Compare two snapshots.
     *
     * @param other Tally
     *
     * @return true is all keys are in both maps and all counters are equal.
     */
    //boolean snapShotEquals( TallyMap<K> other );
    default boolean snapShotEquals( TallyMap<K> other ) {
        boolean result = true; // start optimisc

        // do the trivial self comparison first.
        if ( this == other ) {
            return true;
        }

        // take the snapshots.
        Map<K, Long> mySnapshot = takeSnapShot();
        Map<K, Long> otherSnapShot = other.takeSnapShot();

        // test keyset equality.
        Set<K> myKeySet = mySnapshot.keySet();
        Set<K> otherKeySet = otherSnapShot.keySet();

        // does this contain all others keys?
        if ( !myKeySet.containsAll( otherKeySet ) ) {
            return false;
        }

        // does other contain all this.keys?
        if ( !otherKeySet.containsAll( myKeySet ) ) {
            return false;
        }

        // element by element comparison.
        if ( !mySnapshot.keySet().stream().noneMatch( ( k )
                -> ( !mySnapshot.get( k ).equals( otherSnapShot.get( k ) ) ) ) ) {
            return false;
        }
        return result;
    }

    /**
     * Get a immutable snapshot of all Tallies.
     *
     * @return immutable {@code map<K,Integer>}
     */
    // Map<K, Long> takeSnapShot();
    default Map<K, Long> takeSnapShot() {
        Map<K, Long> result = new HashMap<>();
        for ( K k : keySet() ) {
            result.put( k, get( k ) );
        }
        return Collections.unmodifiableMap( result );
    }

    /**
     * Get the string rep. Note that the string is stable only when NO threads
     * are modifying the map. The order is in key order, natural ordering.
     *
     * @return a string representation.
     */
    @Override
    String toString();

    /**
     * Get the keys of this map.
     *
     * @return all keys.
     */
    Collection<K> keySet();

    /**
     * ToString helper, keeping implementations dry.
     *
     * @param <U> type of key in map
     * @param map the tallymap
     *
     * @return a string represenation
     */
    static <U> String asString( final TallyMap<U> map ) {
        StringBuilder sb = new StringBuilder();
        // put stuff in SortedSet
        SortedSet<U> keySet = new TreeSet<>( map.keySet() );
        keySet.stream().forEach( ( k ) -> {
            long l = map.get( k );
            sb.append( String.format( "{%s => %2d}\n", k.toString(), l ) );
        } );
        return sb.toString();

    }

    /**
     * Get the value associated to a key.
     *
     * @param k for this mapping
     *
     * @return the long value
     */
    default long get( K k ) {
        return getTallyForKey( k );
    }
}
