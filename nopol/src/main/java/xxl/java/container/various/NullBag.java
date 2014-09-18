package xxl.java.container.various;

import static xxl.java.container.classic.MetaMap.newHashMap;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class NullBag<T> extends Bag<T> {

	@SuppressWarnings("unchecked")
	protected static <T> NullBag<T> instance() {
		if (instance == null) {
			Map<?, Integer> empty = newHashMap();
			instance = new NullBag(empty);
		}
		return instance;
	}
	
	private NullBag(Map<T, Integer> emptyMap) {
		super(emptyMap);
	}

	private static NullBag instance;
}
