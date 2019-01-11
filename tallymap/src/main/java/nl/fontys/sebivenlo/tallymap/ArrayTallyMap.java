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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
public class ArrayTallyMap implements TallyMap<Integer> {

    private LongAdder[] map;
    private List<Map<Integer, Long>> snapList
            = new CopyOnWriteArrayList<>();

    public ArrayTallyMap( int capacity ) {
        map = new LongAdder[ capacity ];
        for ( int i = 0; i < map.length; i++ ) {
            addKey( i );
        }
    }

    public ArrayTallyMap( int capacity, Collection<Integer> keys ) {
        map = new LongAdder[ capacity ];
        for ( Integer key : keys ) {
            addKey( key );
        }
    }

    @Override
    public final TallyMap<Integer> addKey( Integer key ) {
        if ( key >= map.length ) {
            map = Arrays.copyOf( map, key + 1 );
        }
        map[ key ] = new LongAdder();
        return this;
    }

    @Override
    public void addTallyForKey( Integer k, long delta ) {
        map[ k ].add( delta );
    }

    @Override
    public void clearAll() {
        for ( LongAdder longAdder : map ) {
            longAdder.reset();
        }
    }

    @Override
    public List<Map<Integer, Long>> getSnapShots() {
        return snapList;
    }

    @Override
    public long getTallyForKey( Integer k ) {
        return map[ k ].sum();
    }

    @Override
    public long grandTotal() {
        long result = 0;
        for ( LongAdder longAdder : map ) {
            result += longAdder.sum();
        }
        return result;
    }

    @Override
    public Collection<Integer> keySet() {
        Set<Integer> result = new HashSet<>( map.length );
        for ( int i = 0; i < map.length; i++ ) {
            if ( null != map[ i ] ) {
                result.add( i );
            }
        }
        return result;
    }

    @Override
    public void snapShot() {
        snapList.add( takeSnapShot() );
    }

    @Override
    public String toString() {
        return TallyMap.asString( this );
    }

}
