package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.SQLException;

import com.royalstone.pos.common.Operator;
import com.royalstone.pos.common.RequestLogon;
import com.royalstone.pos.db.OperatorMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;


/**
 * @author root
 *
 */
public class ClerkCommand implements ICommand 
{

	public Object[] excute(Object[] values) {
		System.out.println( "ClerkCommand executed!" );

		Connection con    = null;
		Response response = null;
		RequestLogon req  = null;
			
		Object[] results = new Object[1];
		if ( values != null && values.length >1 ) {
			req = (RequestLogon) values[1];
			System.out.println( req );
			String posid = req.getPosid();
			String cashierid = req.getCashierid();
			String pin = req.getPlainPin();
			int    shiftid = req.getShiftid();


			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");
				
				OperatorMinister minister = new OperatorMinister( con );
                int listNO=minister.getListNO(posid);
				Operator op = minister.getOperator( cashierid );
				if( op == null ) response = new Response( -1, "失败！" );
				if( op != null && !op.checkPlainPin( pin ) ) response = new Response( -1, "密码不正确" );
				if( op != null && op.checkPlainPin( pin ) ) response = new Response( 0, "成功", op,listNO );
				
			} catch (SQLException e) {
				e.printStackTrace();
				response = new Response( -1, "数据库连接失败，登录未成功." );
			}finally{
				DBConnection.closeAll(null,null,con);
			}  
		}
		results[0] = response;
		return results;

	}
}



