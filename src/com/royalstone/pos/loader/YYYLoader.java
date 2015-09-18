package com.royalstone.pos.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.PayModeList;
import com.royalstone.pos.common.YYYList;
import com.royalstone.pos.db.PosMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

public class YYYLoader implements ICommand {

	public YYYLoader() {
		// TODO Auto-generated constructor stub
	}
	
	public void download2Xml(String file) throws IOException {

		Connection con = null;
		try {
			
			con = DBConnection.getConnection("java:comp/env/dbpos");

			if (con != null) {
				YYYList list = PosMinister.getYYYList(con);
				XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
				outputter.setTextTrim(true);
				FileOutputStream out = new FileOutputStream(file);
				outputter.output(new Document(list.toElement()),out);
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			DBConnection.closeAll(null, null, con);
		}
	}

	public Object[] excute(Object[] values) {
		Object[] results = new Object[1];
		Connection con    = null;
		Response response = null;

		if ( values != null  ) {
			try {

				con = DBConnection.getConnection("java:comp/env/dbpos");
				
				if( con != null ){
					System.out.println("dbpos connected in OperatorCommand!");   
					PayModeList list = PosMinister.getPayModeList(con);
					response = new Response( 0, "OK", list );
				}else {
					response = new Response( -1, "Database connection failed in OperatorCommand." );
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
		}		return null;
	}
}
