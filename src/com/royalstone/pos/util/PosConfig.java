package com.royalstone.pos.util;

import java.util.HashMap;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
/**
   @version 1.0 2004.05.27
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosConfig
{
	public static void main(String[] args)
	{
		PosConfig con = PosConfig.getInstance();
	}

	public PosConfig()
	{
		map_config = new HashMap();
	}

	public static PosConfig getInstance()
	{
		if( config == null ){
			config = new PosConfig();
			config.fromXML( "pos.xml" );
		}
		return config;
	}

	public static PosConfig getNewInstance()
	{
		config = new PosConfig();
		config.fromXML( "pos.xml" );
		return config;
	}


	public void fromXML( String file )
	{
		
		try{
			Document doc = (new SAXBuilder()).build( file );
			Element  root = doc.getRootElement();
			Element  config = root.getChild( "config" );

			List parms = config.getChildren( "parameter" );

			for (int i = 0; i < parms.size(); i++)
			{
				Element parm_item = (Element) parms.get(i);
				Element name      = parm_item.getChild("name");
				Element value     = parm_item.getChild("value");
				this.add( name.getTextTrim(), value.getTextTrim() );
			}
//TODO 屏蔽油品信息   沧州富达 by fire  2005_5_11 			
//			Element  indicator = root.getChild( "indicator" );
//			List     dept_lst  = indicator.getChildren( "deptid" );
//			dept_indicators    = new String [ dept_lst.size() ];
//			for( int i=0; i < dept_lst.size(); i++ )
//			{
//				Element elm = (Element) dept_lst.get(i);
//				dept_indicators[i] = elm.getTextTrim();
//System.out.println( "Indicator[" + i + "] " + dept_indicators[i] );				
//			}
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
	}

	public void toXML( String file )
	{
		String[] names = (String[]) map_config.entrySet().toArray();
		for( int i=0; i<names.length; i++ ){
			String name  = names[i];
			String value = getString(name);
		}
	}
	
	public boolean isIndicatorDept( String deptid )
	{
		for( int i=0; i<dept_indicators.length; i++ ) 
			if( deptid.equals( dept_indicators[i] ) )return true;
		return false;
	}

	public void add( String name, Object value )
	{
		map_config.put( name, value );
	}

	public Object getObj( String name )
	{
		return map_config.get(name);
	}

	public String getString( String name )
	{
		return (String)(map_config.get(name)==null?"":map_config.get(name));
	}

	public int getInteger( String name )
	{
		int v;

		try {
			v = Integer.parseInt( (String) map_config.get(name) );
		} catch ( NumberFormatException e ) {
			v = 0;
		}
		return v;
	}

	private static PosConfig config = null;
	private HashMap map_config      = null;
	private String[] dept_indicators = null;
}
/*
class ConfigElement extends Element
{
	public ConfigElement( String name, int value, String note )
	{
		super( "parameter" );
		this.addChild( "name", name );
		this.addChild( "value", "" + value );
		this.addChild( "note", note );
	}

	public ConfigElement( String name, String value, String note )
	{
		super( "parameter" );
		this.addChild( "name", name );
		this.addChild( "value", value );
		this.addChild( "note", note );
	}

	private void addChild( String name, String value )
	{
		Element e = new Element( name );
		e.addContent( value );
		this.addContent( e );
	}
}*/
