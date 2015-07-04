/*
 *POS Version 4 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.common;

import com.royalstone.pos.gui.Authorization;
import com.royalstone.pos.gui.DialogInfo;
import com.royalstone.pos.gui.MainUI;
import com.royalstone.pos.io.PosDevIn;
import com.royalstone.pos.io.PosDevOut;
import com.royalstone.pos.io.PosInput;
import com.royalstone.pos.io.PosInputLogon;
import com.royalstone.pos.managment.ClerkAdm;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.util.UserCancelException;

/**
 * 授权设置类
 */
public class Privilege {
	
	private PosDevOut out ;
	private PosDevIn in ;
	private PosContext context;
	private Operator cashier;
	
	public Privilege(){	
	}
	
	public Privilege(Operator operator){
		out = PosDevOut.getInstance();
		in = PosDevIn.getInstance();
		context = PosContext.getInstance();
		cashier = operator;
	} 	
	

	public boolean checkPrivilege(int fun) throws UserCancelException {

		return checkPrivilege(fun, null);
	}

	private boolean checkPrivilege(int fun, GetAuthor author)
		throws UserCancelException {
		if (!cashier.hasPrivilege(fun)) {
			getAuthority(author);
		} else {
			if (author != null) {
				author.setid(cashier.getId());
			}
		}

		if (!cashier.hasPrivilege(fun)) {
			notice("授权失败");
		}

		return cashier.hasPrivilege(fun);
	}
		
	private void getAuthority(GetAuthor author) throws UserCancelException {
		Authorization authrization = new Authorization();
		MainUI oldMainUI = out.getMainUI();
		out.setMainUI(authrization);
		authrization.show();
		try {
			
			PosInput inp;
			inp = in.getInputAuthority("请输入授权主管的编号和密码");
			out.setMainUI(oldMainUI);
			authrization.dispose();

			if (inp == null || !(inp instanceof PosInputLogon)) {
				System.out.println("Canceled!");
						return;
			}

			PosInputLogon input = (PosInputLogon) inp;

			String id = input.getID();
			String pin = input.getPIN();

			if (author != null) {
				author.setid(id);
			}

			Operator op = null;

			if (context.isOnLine()) {
				ClerkAdm adm =
					new ClerkAdm(context.getServerip(), context.getPort());
				Response r = adm.getClerk(context.getPosid(), id, pin);
				if (r != null) {
					op = (Operator) r.getObject();
				}
			} else {
				OperatorList lst = new OperatorList();
				lst.load("operator.lst");
				op = lst.get(id, pin);
			}

			if (op != null) {
				cashier.addPrivilege(op);
				context.setAuthorizerid(op.getId());
			}
		} catch (UserCancelException e) {
			out.setMainUI(oldMainUI);
			authrization.dispose();
			throw new UserCancelException("User Cancel!");
		}

	}
	
	private void notice(String note) {
		DialogInfo notice = new DialogInfo();
		notice.setMessage(note);
		notice.show();
	}


	public static void main(String[] args) {
	}
}
