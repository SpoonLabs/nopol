/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under GNU General Public License 2.0 (http://www.gnu.org/licenses/gpl.html)
 */
package sacha.finder.filters.impl;

import sacha.finder.filters.ClassFilter;

public class AcceptAllFilter implements ClassFilter {

	public boolean acceptClassName(String className) {
		return true;
	}

	public boolean acceptInnerClass() {
		return true;
	}

	public boolean acceptClass(Class<?> clazz) {
		return true;
	}

	public boolean searchInJars() {
		return true;
	}

}