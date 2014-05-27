/**
 * 
 */
package fr.inria.lille.nopol.synth.collector;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author fav
 * 
 */
enum SubValuesCollectors implements SubValuesCollector {

	ARRAY {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			ValuesCollector.addValue(name + ".length", Array.getLength(value), mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object.getClass().isArray();
		}
	},

	COLLECTION {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			Collection<?> collection = (Collection<?>) value;
			ValuesCollector.addValue(name + ".size()", collection.size(), mapID);
			ValuesCollector.addValue(name + ".isEmpty()", collection.isEmpty(), mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Collection;
		}
	},

	CHAR_SEQUENCE {
		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			CharSequence string = (CharSequence) value;
			ValuesCollector.addValue(name + ".length()", string.length(), mapID);
			ValuesCollector.addValue(name + ".length()==0", string.length() == 0, mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof CharSequence;
		}
	},

	MAP {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			Map<?, ?> map = (Map<?, ?>) value;
			ValuesCollector.addValue(name + ".size()", map.size(),mapID);
			ValuesCollector.addValue(name + ".isEmpty()", map.isEmpty(),mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Map;
		}
	},

	ITERATOR {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			Iterator<?> iterator = (Iterator<?>) value;
			ValuesCollector.addValue(name + ".hasNext()", iterator.hasNext(), mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Iterator;
		}
	},

	ENUMERATION {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			Enumeration<?> iterator = (Enumeration<?>) value;
			ValuesCollector.addValue(name + ".hasMoreElements()", iterator.hasMoreElements(), mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Enumeration;
		}
	},

	DICTIONARY {

		/**
		 * @see fr.inria.lille.nopol.synth.collector.SubValuesCollector#addSubValues(java.lang.String, java.lang.Object)
		 */
		@Override
		public void addSubValues(final String name, final Object value, int mapID) {
			Dictionary<?, ?> dictionary = (Dictionary<?, ?>) value;
			ValuesCollector.addValue(name + ".size()", dictionary.size(),mapID);
			ValuesCollector.addValue(name + ".isEmpty()", dictionary.isEmpty(),mapID);
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Dictionary;
		}
	},

	NO_OP_COLLECTOR {

		@Override
		public void addSubValues(final String name, final Object value, int mapID) {/* NO-OP */}

		@Override
		boolean handles(final Object object) {
			return false;
		}
	};

	static void process(final String name, final Object value, int mapID) {
		for (SubValuesCollectors collector : values()) {
			if (collector.handles(value)) {
				collector.addSubValues(name, value,mapID);
			}
		}
	}

	abstract boolean handles(Object object);
}
