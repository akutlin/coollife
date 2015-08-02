package coollife.core.bio;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Biosphere {
	
	private Set<Animate> animatePool;
	private final Map<Integer, Set<? extends Animate>> history = new HashMap<>();
	private int time = -1;
	
	public int getTime() { return time; }
	
	public void updateAnimates( Animate a ) {
		synchronized(animatePool) {
			if ( animatePool.contains(a) )
				animatePool.remove(a);
			else 
				animatePool.add(a);
		}
	}

	public Biosphere( Animate... animate ) {
		animatePool = new HashSet<>();
		for (Animate a : animate) {
			updateAnimates(a);
		}
	}
	
	public synchronized void turn() {
		synchronized(animatePool) {
			updateHistory(animatePool);
			animatePool = new HashSet<>();
			for ( Animate e : history.get(history.size() - 1) ) {
				Set<? extends Animate> newborns = e.evolve(Collections.unmodifiableMap(history));
				if (e.isAlive()) animatePool.add(e);
				if (newborns != null) animatePool.addAll(newborns);
			}
		}
	}
	
	public Set<Animate> getAnimatePool() {
		synchronized(animatePool) {
			return Collections.unmodifiableSet(animatePool);
		}
	}
	
	private void updateHistory(Set<? extends Animate> pool) {
		synchronized(history) {
			time = history.size();
			history.put(time, pool);
		}
	}

}
