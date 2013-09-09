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
		public void addSubValues(final String name, final Object value) {
			ValuesCollector.addValue(name + ".length", Array.getLength(value));
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
		public void addSubValues(final String name, final Object value) {
			Collection<?> collection = (Collection<?>) value;
			ValuesCollector.addValue(name + ".size()", collection.size());
			ValuesCollector.addValue(name + ".isEmpty()", collection.isEmpty());
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
		public void addSubValues(final String name, final Object value) {
			CharSequence string = (CharSequence) value;
			ValuesCollector.addValue(name + ".length()", string.length());
			ValuesCollector.addValue(name + ".length()==0", string.length() == 0);
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
		public void addSubValues(final String name, final Object value) {
			Map<?, ?> map = (Map<?, ?>) value;
			ValuesCollector.addValue(name + ".size()", map.size());
			ValuesCollector.addValue(name + ".isEmpty()", map.isEmpty());
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
		public void addSubValues(final String name, final Object value) {
			Iterator<?> iterator = (Iterator<?>) value;
			ValuesCollector.addValue(name + ".hasNext()", iterator.hasNext());
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
		public void addSubValues(final String name, final Object value) {
			Enumeration<?> iterator = (Enumeration<?>) value;
			ValuesCollector.addValue(name + ".hasMoreElements()", iterator.hasMoreElements());
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
		public void addSubValues(final String name, final Object value) {
			Dictionary<?, ?> dictionary = (Dictionary<?, ?>) value;
			ValuesCollector.addValue(name + ".size()", dictionary.size());
			ValuesCollector.addValue(name + ".isEmpty()", dictionary.isEmpty());
		}

		@Override
		boolean handles(final Object object) {
			return object instanceof Dictionary;
		}
	},

	NO_OP_COLLECTOR {

		@Override
		public void addSubValues(final String name, final Object value) {/* NO-OP */}

		@Override
		boolean handles(final Object object) {
			return false;
		}
	};

	static void process(final String name, final Object value) {
		for (SubValuesCollectors collector : values()) {
			if (collector.handles(value)) {
				collector.addSubValues(name, value);
			}
		}
	}

	abstract boolean handles(Object object);
}
