package com.royalstone.pos.common;

import java.io.*;
import java.util.Vector;

/**
   @version 1.0 2004.05.25
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class OperatorList implements Serializable
{
	public static void main(String[] args)
	{
		if( args.length == 0 ){
			int[] right0 = { PosFunction.CORRECT, PosFunction.QUICKCORRECT, PosFunction.WITHDRAW,
					 PosFunction.DELETE };
			int[] right1 = { PosFunction.CORRECT, PosFunction.QUICKCORRECT };
			int[] right2 = { PosFunction.WITHDRAW };
			OperatorList lst = new OperatorList();
			Operator op;
			op = new Operator( "0000", "0000", "root", right0 );
			lst.add( op );

			op = new Operator( "0001", "0001", "mengly", right1 );
			lst.add( op );

			op = new Operator( "0002", "0002", "liangxb", right2 );
			lst.add( op );
			System.out.println( op );
			lst.dump( "operator.lst" );
		} else {
			OperatorList lst = new OperatorList();
			Operator op;
			lst.load( "operator.lst" );
			for( int i=0; i<lst.size(); i++ ) System.out.println( (Operator) lst.get(i) );

		}
	}


	public OperatorList()
	{
		operator_lst = new Vector();
	}

	public Operator get( int i )
	{
		if( operator_lst.size() > i ){
			Operator op = (Operator) operator_lst.get(i);
			return op;
		}

		return null;
	}

	public Operator get( String id, String pin )
	{
		for( int i = 0; i < operator_lst.size(); i++ )
		{
			Operator op = (Operator) operator_lst.get(i);
			if( op.check( id, pin ) )
				return op;
		}

		return null;
	}

	public void add( Operator op )
	{
		operator_lst.add(op);
	}

	public int size()
	{
		return operator_lst.size();
	}

	public void dump( String file )
	{
		try{
			ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( file ) );
			for( int i=0; i<operator_lst.size(); i++ ) out.writeObject( operator_lst.get(i) );
			out.close();
		}
		catch( Exception e ){
			e.printStackTrace();
		}
	}

	public void load( String file )
	{
		try{
			System.out.println( "Loading start ... "  );
			ObjectInputStream in = new ObjectInputStream ( new FileInputStream( file ) );
			while( true ){
				this.add( (Operator) in.readObject() );
			}
		}
		catch( java.io.EOFException e )
		{
			System.out.println( "Loading ended." );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private Vector operator_lst;
}
