package com.royalstone.pos.card.common;

import com.royalstone.pos.shell.pos;
import com.royalstone.pos.invoke.MarshalledValue;
import com.royalstone.pos.invoke.HttpInvoker;
import com.royalstone.pos.common.Accurate;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: fire
 * Date: 2005-5-27
 */
public class AccurateRemoteDAO implements AccurateDAO {
   private URL servlet;
   private HttpURLConnection conn;
   public AccurateRemoteDAO()throws Exception {
        try {
			servlet =
				new URL(
					"http://"
						+ pos.core.getPosContext().getServerip()
						+ ":"
						+ pos.core.getPosContext().getPort()
						+ "/pos41/DispatchServlet");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw(ex);
		}
    }
   public AccurateRemoteDAO(URL servlet){
       this.servlet=servlet;

    }
    public Accurate getAccurate(int cardLevel, int deptID)throws Exception {
       Accurate result = null;
		try {
			conn = (HttpURLConnection) servlet.openConnection();

			Object[] params = new Object[3];

			params[0] = "com.royalstone.pos.web.command.AccurateDAOCommand";
			params[1] = Integer.toString(cardLevel);
            params[2] = Integer.toString(deptID);

			MarshalledValue mvI = new MarshalledValue(params);
			MarshalledValue mvO = HttpInvoker.invoke(conn, mvI);

			Object[] results = null;

			if (mvO != null) {
				results = mvO.getValues();
			}

			if (results != null && results.length > 0) {
				result = (Accurate) results[0];
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw(ex);
		}

		return result;
    }
  public static void main(String[] args) {
      try {
          URL servlet =
				new URL(
					"http://localhost:9090/pos41/DispatchServlet");
          AccurateDAO aDao=new AccurateRemoteDAO(servlet);
          Accurate acc=aDao.getAccurate(0,10101);
          System.out.println("caa"+acc.getAccurate());
      } catch (Exception e) {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
  }



}
