package com.royalstone.pos.web.command;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.royalstone.pos.common.GoodsList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * œ¬‘ÿGoods–≈œ¢
 * @author HuangXuean
 */
public class PriceCommand implements ICommand{

	public PriceCommand (){
	}

	public Object[] excute(Object[] values) {
		Object[] results = new Object[1];
		Connection con    = null;
		Response response = null;
		Context ctx       = null;
		DataSource ds     = null;

		if ( values != null  ) {
			try {
				ctx = new InitialContext();
				if( ctx != null ) ds = (DataSource)ctx.lookup( "java:comp/env/dbpos" );
				if( ds  != null ) con = ds.getConnection();  
				
				if( con != null ){
					System.out.println("dbpos connected in PriceCommand!");   
					GoodsList goodslist = PosMinister.getGoodsList( con );
					response = new Response( 0, "OK", goodslist );
				}else {
					response = new Response( -1, "Database connection failed in PriceCommand." );
				}

				results[0] = response;
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				results[0] = new Response( -1, "Failed." );
			}finally{
				DBConnection.closeAll(null,null,con);
				return results;
			}
		}

		return null;
	}
}

