
package com.royalstone.pos.io;
import  com.royalstone.pos.common.*;
import com.royalstone.pos.util.Volume;

public class PosInputGoods extends PosInput 
{

	public PosInputGoods ( String code )
	{
		super ( PosFunction.GOODS );
		goodscode = code;
		orgcode   = code;
	}

	public PosInputGoods ( String code, double amount )
	{
		super ( PosFunction.GOODS );
		goodscode = code;
		orgcode   = code;
		qty = (int) amount;	
		volume = new Volume( (int)(1000 * amount) );
		this.amount=amount;
	}

	public PosInputGoods ( String code, int q, int vcents, int gtype, String ocode )
	{
		super ( PosFunction.GOODS );
		goodscode = code;
		orgcode   = ocode;
		type      = gtype;
		qty       = q;
		cents     = vcents;
	}
	

	
	public int getGoodsType()
	{
		return type;
	}
	
	public void setGoodsType( int type )
	{
		this.type = type;
	}

	public int getQty()
	{
		return qty;
	}
	
	public Volume getVolume()
	{
		return volume;
	}
	
	public double getAmount()
	{
		return volume.getVolume();
	}
	
	public int getMilliVolume()
	{
		return volume.getMilliVolume();
	}

	public void setMilliVolume( int milli )
	{
		volume = new Volume ( milli );
	}

	public void setVolume( double v )
	{
		volume.setVolume( v );
		qty = (int) v;
	}

	public void setQty( int q )
	{
		qty = q;
	}
	
	public void setCode(String value)
	{
		this.goodscode=value;
	}


	public String getCode()
	{
		return goodscode;
	}

	public String getOrgCode()
	{
		return orgcode;
	}

	public long getCents()
	{
		return cents;
	}
	
	public void setCents( long cents )
	{
		this.cents = cents;
	}

	public void setOrgCode( String orgcode )
	{
		this.orgcode = orgcode; 
	}
	
	public void setColorSize( String colorsize )
	{
		this.colorsize = colorsize;
	}
	
	public String getColorSize()
	{
		return colorsize;
	}
	
	public boolean hasOddment()
	{
		return !( volume.oddment() == 0 );
	}
	
	public void setDeptid(String deptid){
		this.deptid = deptid;
	}
	
	public String getDeptid(){
		return deptid;
	}

	public String toString()
	{
		return "{ " + goodscode + " * " + qty + " @ " + cents + " cents }";
	}
	
	public double getAmountEx(){
		return amount;
	}

	private String deptid ;
	private int type  = Goods.GENERAL;
	private String orgcode   = "";
	private String goodscode = "";
	private String colorsize = "";
	private int qty   = 0;
	private long cents = 0;
	private Volume volume = new Volume();
	private double amount=0;
}
