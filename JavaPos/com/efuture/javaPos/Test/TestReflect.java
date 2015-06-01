package com.efuture.javaPos.Test;

import java.util.Vector;

public class TestReflect extends DebugBase
{
	public String aaaa = "123";
	public String bbbb = "234";
	protected double cccc = 112312.12434;
	protected Vector vc = new Vector();
	public GoodsDef gd = new GoodsDef();
	
	public static void main(String[] args) 
    {
		TestReflect tr = new TestReflect();
		tr.gd.code = "1112";
		GoodsDef gd1 = new GoodsDef();gd1.code = "GD1";
		GoodsDef gd2 = new GoodsDef();gd2.code = "GD2";
		GoodsDef gd3 = new GoodsDef();gd3.code = "GD3";
		GoodsDef gd4 = new GoodsDef();gd4.code = "GD4";
		GoodsDef gd5 = new GoodsDef();gd5.code = "GD5";
		tr.vc.add(gd1);
		tr.vc.add(gd2);
		tr.vc.add(gd3);
		tr.vc.add(gd4);
		tr.vc.add(gd5);
		
		DebugForm shf = new DebugForm();
		shf.open(tr,"tr");
		//tr.Debug();
		
    	Vector vec = new Vector();
    	vec.add(String.valueOf("1"));
    	vec.add(String.valueOf("2"));
    	vec.add(String.valueOf("3"));
    	
    	Vector vec1 = new Vector();
    	vec1.add(vec.elementAt(0));
    }
}
