package nopol_example;


public class NopolExample {
	

	
	
	/*
	 * Return the index char of s 
	 * or the last if index > s.length
	 * or the first if index < 0			
	 */
	public char charAt(String s, int index){
		
		if ( index == 0 ){//		if ( ( false && enable ) || ( !enable && (index==0)) )//if ( index == 0) // Fix index <= 0
			return s.charAt(0);
		}

		
		if ( index < s.length() ){
				return s.charAt(index);
		}		
		
		
		return s.charAt(s.length()-1);
	}
//	public char charAt(java.lang.String s, int index) {
//		
//		boolean ConditionValueHolderValue = true;
//		boolean[] enable={false,false,true,false};
//		
//        if ((ConditionValueHolderValue && enable[2]) || (!enable[2] && (index == 0)))
//            return s.charAt(0);
//
//        if ((ConditionValueHolderValue && enable[3]) || (!enable[3] && (index < (s.length())))) {
//
//            if (ConditionValueHolderValue || !enable[0])
//                return s.charAt(index);
//            
//        } else {
//        }
//        return s.charAt(((s.length()) - 1));
//    }
}
