package org.usfirst.frc.team2485.auto;

/**
 * Runs any number of <code>SequencedItems</code> in pararrel. 
 * Each Item stops running when its own duration finishes. <br>
 *
 * This class has an empty <code>finish()</code> method, however
 * each SequencedItem inside the <code>SequencedMultibleItem</code> will 
 * call its <code>finish()</code> method when its duration expires. 
 * 
 * @author Patrick Wamsley
 * @author Bryce Matsumori 
 */

public class SequencedMultipleItem implements SequencedItem {

	private SequencedItem[] items; 
	private long startTime;

	private boolean firstRun; 

	private String name; 
	
	public SequencedMultipleItem(String name, SequencedItem... items) {
		this.name 		= name; 
		this.items 		= items; 
		this.firstRun	= true; 
	}
	
	public SequencedMultipleItem(SequencedItem... items) {
		this(null, items); 
	}

	@Override
	public void run() {

		if (firstRun) {
			startTime = System.currentTimeMillis();
			firstRun = false; 
		}

		for (int currItemIndex = 0; currItemIndex < items.length; currItemIndex++) { 	
			
			SequencedItem curr = items[currItemIndex]; 

			if (curr == null)
				continue; 

			//hack to garentee the last item runs its finish method before the multi item times out
			if (currItemIndex == items.length - 1 && 
					System.currentTimeMillis() - startTime + 20 >= curr.duration() * 1000) { //one iritation before the multi item stops
				curr.finish();
				return; 
			}
			else if (System.currentTimeMillis() - startTime <= curr.duration() * 1000)
				curr.run(); 
			else {
				curr.finish(); 
				items[currItemIndex] = null;
			}
		}
	}

	@Override
	public double duration() {

		double max = Double.MIN_VALUE; 

		for (int i = 0; i < items.length; i++) {
			if (items[i] == null)
				continue; 
			if (max < items[i].duration())
				max = items[i].duration(); 
		}

		return max; 
	}

	/**
	 * This does nothing, however each SequencedItem inside will call its finish method when its duration expires. 
	 * If this SequencedMultibleItem is named, then a message will print telling that it has finished. This can be used for debugging.
	 */
	@Override
	public void finish() {
		if (name != null) 
			System.out.println("SequencedMultibleItem: " + name + " has finished.");
	}
}
