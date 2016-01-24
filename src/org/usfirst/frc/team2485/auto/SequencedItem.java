package org.usfirst.frc.team2485.auto;


/**
 * Represents an item which can be sequenced. The item will be run at least once.
 * 
 * The item has a duration, which represents a period during which the item's
 * {@code run()} method will be invoked.
 * 
 * If the item should only be run once, the duration should be set to -1.
 * @author Bryce Matsumori
 * @author Patrick Wamsley
 *
 * @see Sequencer
 */
public interface SequencedItem {

    /**
     * The action to be invoked while the item is to be running.
     * 
     * This will be invoked when the {@code Sequencer}'s {@code run()} method
     * is invoked, meaning that this should be a non-blocking operation and be
     * able to exit quickly.
     * 
     * There is no guarantee that this method will be called consistently.
     * 
     * @see Sequencer
     */
    public void run();

    /**
     * Specifies the duration for which this item should run, in seconds.
     * @return the duration, in seconds
     */
    public double duration();
    
    /**
     * This method is called when an item finishes running. This can be used to prevent "phantom PID", 
     * make sure speed controllers are reset, for debugging, etc. It can also be left blank. 
     * 
     * In a <code>SequencedMultibleItem</code> each item calls its finish method when it expires inside the multiple item. 
     * 
     * @see Sequencer 
     */
    public void finish(); 
    
}
