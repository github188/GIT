package com.royalstone.pos.common;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.io.*;

/**	Operator 模拟系统中的操作人员,包括收银员,收银组长等角色.
 * @version 1.0 2004.05.25
 * @author  Mengluoyi, Royalstone Co., Ltd.
 */
public class Operator implements Serializable
{
	/**
	 * @param id	操作员ID
	 * @param pin	登录口令(密文)
	 * @param name	操作员名字
	 * @param native_privileges		自有操作权限
	 */
	public Operator( String id, String pin, String name, int[] native_privileges )
	{
		this.id   = id;
		this.pin  = pin;
		this.name = name;
		this.native_privileges = new int[ native_privileges.length ];
		System.arraycopy( native_privileges, 0, this.native_privileges, 0, native_privileges.length );
		Arrays.sort( this.native_privileges );
	}

        /**
         * @param id	操作员ID
         * @param pin	登录口令(密文)
         * @param name	操作员名字
         * @param max_disc 操作员最大的打折界限
         * @param native_privileges		自有操作权限
         */
        public Operator( String id, String pin, String name,int max_disc, int[] native_privileges )
        {
                this.id   = id;
                this.pin  = pin;
                this.name = name;
                this.max_disc = max_disc;
                this.native_privileges = new int[ native_privileges.length ];
                System.arraycopy( native_privileges, 0, this.native_privileges, 0, native_privileges.length );
                Arrays.sort( this.native_privileges );
        }




	/**
	 * @param id	操作员ID
	 */
	public Operator( String id )
	{
		this.id = id;
		this.pin = "";
		this.name = "";
	}

	/**
	 * @return	操作员ID
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return	操作员名字
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return		操作员的密码密文
	 */
	public String getPinEncrypted()
	{
		return pin;
	}

        /**
         *
         * @return 此操作员最大的折扣界限
         */
        public int getMax_Disc(){
          return max_disc;
        }

	/**	此函数将对传入的ID和密码进行校验,主要用于收银员登录和授权操作.
	 * @param id	操作员ID
	 * @param pin	操作员输入的密码(明文)
	 * @return		true	密码校验通过;<br/>
	 * 				false	密码校验失败.
	 */
	public boolean check( String id, String pin )
	{
		return ( this.id.equals( id ) && this.pin.equals( encryptPin( id, pin ) ) );
	}

	/**	校验密码
	 * @param pin	密码明文
	 * @return		true	校验成功;<br/>
	 * 				false	校验失败.
	 */
	public boolean checkPlainPin( String pin )
	{
		return ( this.pin.equals( encryptPin( this.id, pin ) ) );
	}

	/**	此函数用于修改操作员的密码.
	 * @param pin_plain		密码明文
	 */
	public void setPlainPin( String pin_plain )
	{
		this.pin = encryptPin( id, pin_plain );
	}

	/**	此函数检查操作员的操作权限.
	 * @param fun	操作代码
	 * @return		true	权限检查通过<br/>
	 * 				false	权限检查失败.操作员无权进行指定的操作.
	 */
	public boolean hasPrivilege( int fun )
	{
		if( native_privileges != null
		  && Arrays.binarySearch( native_privileges, fun ) >= 0 ) return true;
		if( added_privileges != null
		  && Arrays.binarySearch( added_privileges, fun ) >= 0 ) return true;
		return false;
	}

	/**
	 * 重置操作员权限. 调用此函数后,操作员将失去因授权而获得的权限,只拥有自有权限.
	 */
	public void resetPrivilege()
	{
		added_privileges = null;
		authorizer	 = "";
	}

	/**	授权函数. 调用此函数后, 调用者将获得supervisor 的操作权限.
	 * @param supervisor	授权人员
	 */
	public void addPrivilege( Operator supervisor )
	{
		int[] au_right = supervisor.native_privileges;
		native_privileges = supervisor.native_privileges;	// zhouzhou add 20070302 权限值
		int   len      = au_right.length;
		this.added_privileges = new int[ len ];
		System.arraycopy( au_right, 0, added_privileges, 0, len );
		Arrays.sort( this.added_privileges );

		this.authorizer = supervisor.getId();
	}

	/**	加密登录口令
	 * @param id		登录ID
	 * @param pin_plain	登录口令明文
	 * @return			登录口令的密文
	 */
	public String encryptPin( String id, String pin_plain )
	{
		long k = 123456789;

		for( int i=0; i < id.length(); i++){
			long	a = ( (int)id.charAt(i) ) % 13 + 1;
			k = ( k * a ) % 9999999 + 1;
		}

		k = k % 98989898 + 99;
		for(int i=0; i<  pin_plain.length(); i++){
			long	a = ( (int)pin_plain.charAt(i) ) % 17 + 1;
			k = ( k % 9876543 + 1 ) * a;
		}

		DecimalFormat df = new DecimalFormat( "00000000" );
		return df.format( k % 100000000 );
	}

	/**
	 * for debug use.
	 */
	public String toString()
	{
		String s = " [ ";
		if( native_privileges.length >0 ) s += native_privileges[0];
		for (int i=1; i<=native_privileges.length-1; i++ )
			s += ", " + native_privileges[i];
		s += " ] ";
		return id + ":" + pin + ":" + name + s;
	}

	/**
	 * <code>id</code>		操作员ID
	 */
	private String id;
	/**
	 * <code>pin</code>		操作员的密码(密文)
	 */
	private String pin;		// encrypted pin.
	/**
	 * <code>name</code>	操作员名字
	 */
	private String name;

      /**
      * <code>max_disc</code> 操作员能进行折扣的最大界限,默认为0.
      */
       private int max_disc=0;

	/**
	 * <code>native_privileges</code>	操作员自己所拥有的权限<br/>
	 * <code>added_privileges</code>	因授权操作而获得的权限
	 */
	private int[]  native_privileges = null, added_privileges  = null;

	/**
	 * <code>authorizer</code>			授权人员
	 */
	private String authorizer = "";
}
