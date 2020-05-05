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

import nl.fontys.sebivenlo.tallymap.LoadTesterApp.TallyIncrementer;
import nl.fontys.sebivenlo.tallymap.IncrementerResult;
import nl.fontys.sebivenlo.tallymap.TallyMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
class LoadTester<U> implements Runnable {

    final TallyMap<U> map2;
    final TallyMap<U> map1;
    final List<U> keys1;

    LoadTester( TallyMap<U> map2, TallyMap<U> map1, List<U> keys1 ) {
        this.map2 = map2;
        this.map1 = map1;
        this.keys1 = keys1;
        map1.clearAll();
        map2.clearAll();
    }

    LoadTester( Runnable work, TallyMap<U> map2, TallyMap<U> map1,
            List<U> keys1 ) {
        this( map2, map1, keys1 );
        this.work = work;
    }
    int tasks = 480; // make multiple of three
    int rounds = 1000; //000;
    Runnable work = Thread::yield;

    public LoadTester<U> withRounds( int rounds ) {
        this.rounds = rounds;
        return this;
    }

    public LoadTester<U> withTasks( int tasks ) {
        this.tasks = tasks;
        return this;
    }

    @Override
    public void run() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        for ( U s : keys1 ) {
            map2.addTallyForKey( s, 2 * tasks * rounds );
        }
        ExecutorService executor = Executors.newFixedThreadPool( 100 );
        System.out.println( "Starting Load test for"
                + map2.getClass().getSimpleName() );
        CompletionService<IncrementerResult> ecs
                = new ExecutorCompletionService<>( executor );
        long before = System.currentTimeMillis();
        for ( int i = 0; i < tasks;
                i += 3 ) {
            System.out.println( "Sumbit task " + i );
            ecs.submit( new TallyIncrementer<>( work, map1, keys1,
                    new int[]{ 1, 2, 3 }, rounds ) );
            ecs.submit( new TallyIncrementer<>( work, map1, keys1,
                    new int[]{ 3, 1, 2 }, rounds ) );
            ecs.submit( new TallyIncrementer<>( work, map1, keys1,
                    new int[]{ 2, 3, 1 }, rounds ) );
        }
        long expectedTotals = 0;
        for ( int i = 0; i < tasks;
                i++ ) {
            try {
                Future<IncrementerResult> fut = ecs.take();
                IncrementerResult taskResult = fut.get();
                expectedTotals += taskResult.getResult();
                System.out.println( "Completed " + taskResult );
            } catch ( ExecutionException | InterruptedException ex ) {
                Logger.getLogger( TallyMapTestBase.class.getName() ).log(
                        Level.SEVERE,
                        null, ex );
            }
        }
        long after = System.currentTimeMillis();
        long totals = map1.grandTotal();
        Assert.assertEquals( expectedTotals, totals );
        System.out.println( "map1:\n" + map1 );
        System.out.println( "map2:\n" + map2 );
        System.out.println( "totals=" + totals );
        System.out.println( map2.getClass().getSimpleName() + " does " + tasks
                * rounds + " (tasks*rounds) in " + ( after - before )
                + " milli seconds for " + work.toString() );
        Assert.assertTrue( "expected equal maps" + map1,
                map2.snapShotEquals( map1 ) );
        System.out.println( "Load Test completed succesfully" );
    }

    static Runnable sleepyCat = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep( 0 );
            } catch ( InterruptedException ignored ) {
            }
        }

        @Override
        public String toString() {
            return "sleepy cat";
        }
    };

    static Runnable lazyBones = new Runnable() {
        @Override
        public void run() {
        }

        @Override
        public String toString() {
            return "lazy bones";
        }
    };

    static Runnable yielder = new Runnable() {
        @Override
        public void run() {
            Thread.yield();
        }

        @Override
        public String toString() {
            return "yielder";
        }
    };

}
