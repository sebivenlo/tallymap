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

/**
 *
 * @author Pieter van den Hombergh (p dot vandenhombergh at fontys dot nl)
 */
class IncrementerResult {
    
    private final int serial;
    private final Long result;

    public IncrementerResult( int serial, Long result ) {
        this.serial = serial;
        this.result = result;
    }

    @Override
    public String toString() {
        return "IncrementerResult{" + "serial=" + serial + ", result=" + result +
            '}';
    }

    public Long getResult() {
        return result;
    }
    
}
