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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 * @param <K> type of key
 */
public class EnumTallyMap<K extends Enum<K>> implements TallyMap<K> {

    private final Class<K> keyType;
    private final EnumMap<K, LongAdder> map;
    private String name="this tallymap";

    public EnumTallyMap( Class<K> keyType ) {
        this.keyType = keyType;
        EnumSet<K> keySet = EnumSet.allOf( keyType );
        map = new EnumMap<>( this.keyType );

        for ( K e : keySet ) {
            map.put( e, new LongAdder() );
        }
    }

    public EnumTallyMap( Class<K> keyType, Collection<K> keys ) {
        this.keyType = keyType;
        map = new EnumMap<>( this.keyType );
        for ( K e : keys ) {
            map.put( e, new LongAdder() );
        }
    }

    @Override
    public TallyMap<K> addKey( K key ) {
        map.putIfAbsent( key, new LongAdder() );
        return this;
    }

    /**
     * Add to a mapped tally.
     *
     * @param k     key of mapping
     * @param delta value to add
     *
     * @throws NullPointerException when mapping does not exist before calling
     *                              this method
     */
    @Override
    public void addTallyForKey( K k, long delta ) {
        LongAdder a = map.get( k );
        a.add( delta );
    }

    @Override
    public void clearAll() {
        for ( K k : map.keySet() ) {
            map.get( k ).reset();
        }
    }

    /**
     * List of snapshots.
     */
    private List<Map<K, Long>> snapList
            = new CopyOnWriteArrayList<Map<K, Long>>();

    @Override
    public List<Map<K, Long>> getSnapShots() {
        return snapList;
    }

    @Override
    public long getTallyForKey( K k ) {
        return map.get( k ).sum();

    }

    @Override
    public long grandTotal() {
        long result = 0;

        for ( K k : map.keySet() ) {
            result += map.get( k ).sum();
        }
        return result;
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public void snapShot() {

        snapList.add( takeSnapShot() );
    }

    @Override
    public Map<K, Long> takeSnapShot() {
        Map<K, Long> snap = new HashMap<>();
        for ( K k : map.keySet() ) {
            snap.put( k, map.get( k ).sum() );
        }
        return Collections.unmodifiableMap( snap );
    }

    @Override
    public String toString() {
        return TallyMap.asString( this );

    }


    @Override
    public EnumTallyMap named( String name ) {
        this.name=name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    
}
