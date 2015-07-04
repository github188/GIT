package com.royalstone.pos.io;

import com.royalstone.pos.common.PosFunction;
/**
 * ÃÜÂë°ü×°Àà
 * */
public class PosInputPin extends PosInput{
	
	public PosInputPin ( String pin ){
		super( PosFunction.PIN );
		Passwd   = pin;
	}

	public String getPIN(){
		return Passwd;
	}

	private String Passwd    = "";
}
