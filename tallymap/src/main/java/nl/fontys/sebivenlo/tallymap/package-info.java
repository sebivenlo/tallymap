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
/**
 * A set of implementations of a TallyMap.
 * A TallyMap is a set of counters, mapping some key to a long accumulator.
 * The implementations strive to be thread safe and performant under heavy concurrent conditions.
 * TallyMap is the interface that is implemented by 4 implementing classes:
 * <ol>
 * <li>TallyMap1 is the classic, and using AtomicLong as the
 * accumulator-elements and a ConcurrentHashMap as mapping structure.</li>
 * <li>TallyMap2 is a modernized version, using new Java8 features such as the
 * computeIfAbsent method in ConcurrentMap and the
 * {@link java.util.concurrent.atomic.LongAdder LongAdder}, whose combination
 * is advertised as the exact match for things such as TallyMaps.</li>
 * <li>TallyMap3 deviates from the contract of TallyMap, which the TallyMap1 and
 * TallyMap2 adhere to, to be able to speed ups things a little.
 * The deviation is not allowing to tally for a key that has not been announced
 * previously.
 * If you want a key counter mapping to be
 * used, you must make sure that this mapping exists before it is used. This
 * allows the map NOT to check (or compute a value) before the adding is done.
 * It is quicker or less obtrusive, but has this trait, so you have been warned.
 * </li>
 * <li>EnumTallyMap is what its name says, and easily plays out its performance
 * card, because and EnumMap and friends are dead fast. This is bought with the
 * need to define and use an enum type in your application. From measurements
 * this is the quickest. Note that all enums are mapped from the start.
 * </li>
 * <li>ArrayTallyMap uses and Integer to Long mapping and internally uses an
 * array for O(1) access to the accumulators. My bet that this implementation
 * would be the quickest did not really pay off, probably because of the auto
 * boxing that may take place. Lesson learned: If you need an array with normal {@code int}
 * access, then roll your own. In this case the "TallyMap" is a no-brainer
 * anyway.
 * <p>
 * </li>
 * </ol>
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * Customer( Set<Integer> menuNumbers ) {
 *    orderedMap = new TallyMap<Integer>( menuNumbers );
 *    servedMap = new TallyMap<Integer>( menuNumbers );
 * }
 * .
 * .
 *    orderedMap.addTallyForKey( mealNr, servings );
 * .
 *    servedMap.addTallyForKey( meal.getNumber(), 1 );
 * .
 *    // test. In unit tests use assertTrue(m1.snapshotEquals(m2)) and diff
 *    return "You asked \"" + question + "\" " + ( orderedMap.snapShotEquals(
 *              servedMap )
 *               ? "Yes, very. Thank you"
 *              : "No I missed some: " + orderedMap.snapShotDiff( servedMap ) );
 * }
 * </pre>
 * <p>
 * Have fun using it.</p>
 */
package nl.fontys.sebivenlo.tallymap;
