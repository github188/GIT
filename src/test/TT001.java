/*
 * Created on 2004-6-26
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TT001 {

	public static void main(String[] args) {
		int auth = 0;
		boolean isPowerfull = false;
		String curOp = "not null";
		String cashierid = "0002";		
		
		if(auth==1){
			isPowerfull = true;
			System.out.println( "True!" );
		}else if( curOp.equals(null)||curOp.equals(cashierid)){
			isPowerfull = true;
			System.out.println( "Also True!" );
		}else{}
System.out.println( isPowerfull );
	}
}
