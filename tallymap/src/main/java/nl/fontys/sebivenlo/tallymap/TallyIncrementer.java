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

import java.util.List;
import java.util.concurrent.Callable;
import nl.fontys.sebivenlo.tallymap.IncrementerResult;
import nl.fontys.sebivenlo.tallymap.TallyMap;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
class TallyIncrementer<T> implements Callable<IncrementerResult> {

    private final List<T> counterLists;
    private final int[] deltas;
    private final int rounds;
    private final TallyMap<T> map;
    private static int seq = 0;
    private final int serial = seq++;
    private final Runnable work;

    @Override
    @SuppressWarnings( value = "CallToThreadYield" )
    public IncrementerResult call() throws Exception {
        long myCount = 0;
        for ( int r = 0; r < rounds;
            r++ ) {
            for ( int i = 0; i < counterLists.size();
                i++ ) {
                map.addTallyForKey( counterLists.get( i ),
                    deltas[ i % deltas.length ] );
                //Thread.sleep( 1 );
                myCount += deltas[ i % deltas.length ];
                work.run();//Thread.yield();
            }
        }
        return new IncrementerResult( serial, myCount );
    }

    TallyIncrementer( Runnable work, TallyMap<T> map, final List<T> list,
        final int[] deltas,
        final int rounds ) {
        this.work = work;
        this.map = map;
        counterLists = list;
        this.deltas = deltas;
        this.rounds = rounds;
    }

    static final Runnable defaultWorker = () -> {
    };

    TallyIncrementer( TallyMap<T> map, final List<T> list, final int[] deltas,
        final int rounds ) {
        this( defaultWorker, map, list, deltas, rounds );
    }
}
