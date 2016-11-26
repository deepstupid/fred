package freenet.support;

import freenet.support.Logger.LogLevel;

import java.util.*;

/**
 * An LRU map from K to V. That is, when a mapping is added, it is 
 * pushed to the top of the queue, even if it was already present, and 
 * pop/peek operate from the bottom of the queue i.e. the least recently 
 * pushed. The caller must implement any size limit needed.
 * FIXME most callers should be switched to LinkedHashMap.
 * Does not support null keys.
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class LRUMap<K, V> {
	private static volatile boolean logMINOR;

	static {
		Logger.registerLogThresholdCallback(new LogThresholdCallback(){
			@Override
			public void shouldUpdate(){
				logMINOR = Logger.shouldLog(LogLevel.MINOR, this);
			}
		});
	}

	/** We use our own DoublyLinkedList implementation because it improves 
	 * performance to be able to inherit from and refer to QItem's directly. */
	private final DoublyLinkedListImpl<QItem<K, V>> list = new DoublyLinkedListImpl<>();
    private final Map<K, QItem<K, V>> hash;
    
    public LRUMap() {
    	hash = new HashMap<K, QItem<K, V>>();
    }
    
    /** Takes an arbitrary map */
    private LRUMap(Map<K, QItem<K, V>> map) {
    	hash = map;
    }
    
    /** Create a LRUMap that is safe to use with keys that can be
     * controlled by an attacker. Meaning one based on a TreeMap, not a 
     * HashMap (think hash collision DoS's). */
    public static<K extends Comparable<K>,V> LRUMap<K,V> createSafeMap() {
    	return new LRUMap<K,V>(new TreeMap<K, QItem<K,V>>());
    }
    
    /** Create a LRUMap that is safe to use with keys that can be
     * controlled by an attacker. Meaning one based on a TreeMap, not a 
     * HashMap (think hash collision DoS's). */
    public static<K,V> LRUMap<K,V> createSafeMap(Comparator<K> comparator) {
    	return new LRUMap<K,V>(new TreeMap<K, QItem<K,V>>(comparator));
    }
    
    /**
     *       push()ing an object that is already in
     *       the queue moves that object to the most
     *       recently used position, but doesn't add
     *       a duplicate entry in the queue.
     */
    public final V push(K key, V value) {
		if(key == null)
    		throw new NullPointerException();
    	V old = null;
		QItem<K, V> insert;
    	synchronized(this.hash) {
			insert = this.hash.computeIfAbsent(key, k -> new QItem<K, V>(key, value));
		}
		if (insert!=null) {
			old = insert.value;
			insert.value = value;
			synchronized(list) {
				list.remove(insert);
			}
		}

        if(logMINOR)
        	Logger.minor(this, "Pushed "+insert+" ( "+key+ ' ' +value+" )");

        list.unshift(insert);
        return old;
    } 

    /**
     *  @return Least recently pushed key.
     */
    public final K popKey() {
    	synchronized(list) {

			if (list.size() > 0) {
				QItem<K, V> popped = list.pop();
				synchronized(hash) {
					return hash.remove(popped.obj).obj;
				}
			}
		}

		return null;
    }

    /**
     * @return Least recently pushed value.
     */
    public final V popValue() {
		synchronized(list) {
			if (!list.isEmpty()) {
				QItem<K, V> popped = list.pop();
				synchronized (hash) {
					return hash.remove(popped.obj).value;
				}
			}
		}
		return null;
    }
    
	public final V peekValue() {
    	synchronized (list) {
			if (list.size() > 0) {
				QItem<K, V> tail = list.tail();
				synchronized(hash) {
					return hash.get(tail.obj).value;
				}
			} else {
				return null;
			}
		}
	}

	public final synchronized K peekKey() {
        if ( list.size() > 0 ) {
			return hash.get(list.tail().obj).obj;
        } else {
            return null;
        }
	}

    public final int size() {
        return list.size();
    }
    
    public final boolean removeKey(K key) {
    	if(key == null)
    		throw new NullPointerException();
    	synchronized(hash) {
			QItem<K, V> i = (hash.remove(key));
			if (i != null) {
				synchronized(list) {
					list.remove(i);
				}
				return true;
			} else {
				return false;
			}
		}
    }
    
    /**
     * Check if this queue contains obj
     * @param obj Object to match
     * @return true if this queue contains obj.
     */
    public final synchronized boolean containsKey(K key) {
    	if(key == null)
    		throw new NullPointerException();
        return hash.containsKey(key);
    }
    
    /**
     * Note that this does not automatically promote the key. You have
     * to do that by hand with push(key, value).
     */
    public final synchronized V get(K key) {
    	if(key == null)
    		throw new NullPointerException();
    	QItem<K,V> q = hash.get(key);
    	if(q == null) return null;
    	return q.value;
    }
    
	public Enumeration<K> keys() {
        return new ItemEnumeration();
    }
    
	public Enumeration<V> values() {
    	return new ValuesEnumeration();
    }

	private class ItemEnumeration implements Enumeration<K> {
		private Enumeration<QItem<K, V>> source = list.reverseElements();
       
        @Override
        public boolean hasMoreElements() {
        	synchronized(LRUMap.this) {
        		return source.hasMoreElements();
        	}
        }

		@Override
		public K nextElement() {
        	synchronized(LRUMap.this) {
        		return source.nextElement().obj;
        	}
        }
    }

	private class ValuesEnumeration implements Enumeration<V> {
		private final Enumeration<QItem<K, V>> source = list.reverseElements();
       
        @Override
        public boolean hasMoreElements() {
        	synchronized(LRUMap.this) {
        		return source.hasMoreElements();
        	}
        }

		@Override
		public V nextElement() {
        	synchronized(LRUMap.this) {
        		return source.nextElement().value;
        	}
        }
    }

	public static class QItem<K, V> extends DoublyLinkedListImpl.Item<QItem<K, V>> {
        public final K obj;
        public V value;

        public QItem(K key, V val) {
            this.obj = key;
            this.value = val;
        }
        
		@Override
        public String toString() {
        	return super.toString()+": "+obj+ ' ' +value;
        }
    }

	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Note that unlike the java.util versions, this will not reallocate (hence it doesn't return), 
	 * so pass in an appropriately big array, and make sure you hold the lock!
	 * @param entries
	 * @return
	 */
	public synchronized void valuesToArray(V[] entries) {
		Enumeration<V> values = values();
		int i=0;
		while(values.hasMoreElements())
			entries[i++] = values.nextElement();
	}

	public synchronized void clear() {
		list.clear();
		hash.clear();
	}
}
