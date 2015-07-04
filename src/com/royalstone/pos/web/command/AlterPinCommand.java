package com.royalstone.pos.web.command;

import java.sql.Connection;
import java.sql.SQLException;

import com.royalstone.pos.common.Operator;
import com.royalstone.pos.common.RequestAlterPin;
import com.royalstone.pos.db.OperatorMinister;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author root
 *
 */
public class AlterPinCommand implements ICommand {

	public Object[] excute(Object[] values) {
		System.out.println("alterPinCommand executed!");

		Connection con = null;
		Response response = null;
		RequestAlterPin req = null;

		Object[] results = new Object[1];
		if (values != null && values.length > 1) {
			req = (RequestAlterPin) values[1];
			System.out.println(req);
			String posid = req.getPosid();
			String cashierid = req.getCashierid();
			String pin_old = req.getPinOld();
			String pin_new = req.getPinNew();

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");
				OperatorMinister minister = new OperatorMinister(con);
				Operator op = minister.getOperator(cashierid);
				if (op == null)
					response = new Response(-1, "ʧ�ܣ�");
				if (op != null && !op.checkPlainPin(pin_old))
					response = new Response(-1, "���벻��ȷ");
				if (op != null && op.checkPlainPin(pin_old)) {
					op.setPlainPin(pin_new);
					minister.alterOperator(op);
					response = new Response(0, "�ɹ�", op);
				}

			} catch (SQLException e) {
				e.printStackTrace();
				response = new Response(-1, "���ݿ�����ʧ�ܣ���¼δ�ɹ�.");
			} finally {
				DBConnection.closeAll(null, null, con);
			}
		}
		results[0] = response;
		return results;

	}
}
