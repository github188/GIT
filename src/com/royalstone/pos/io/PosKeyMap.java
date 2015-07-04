package com.royalstone.pos.io;
import java.awt.event.KeyEvent;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.PosFunction;

/** 
  * @version 1.0 2004.05.14
  * @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosKeyMap
{
	private static PosKeyMap instance=null;
	
	public static PosKeyMap getInstance(){
		
		if(instance==null){
			instance=new PosKeyMap();
			instance.fromXML("pos.xml");
		}
		return instance;
		
	}
	
	public PosKeyMap()
	{
		key_map = new PosInput[ SIZE ];
	}

	public void setFunction( int key_val, PosInput inp )
	{
		if( key_val < SIZE )
		key_map[ key_val ] = inp;
	}

	public void setFunction( int key_val, int key_fun )
	{
		if( key_val < SIZE )
		key_map[ key_val ] = new PosInput( key_fun );
	}

	public PosInput getFunction( int key_val )
	{
		if ( key_val >= SIZE ) return new PosInput (PosFunction.UNDEFINED);
		PosInput input = key_map[ key_val ];
		if ( input == null ) return new PosInput (PosFunction.UNDEFINED);
		return input;
	}
	
	public Element toElement()
	{
		Element elm_map = new Element( "keymap" );
		for( int i=0; i<SIZE; i++ ){
			if( getFunction(i).key() == PosFunction.UNDEFINED ) continue;
			Element elm_fun = new Element( "key_fun" );
			elm_fun.addContent( new Element( "value" ).addContent( "" + i ) );
			elm_fun.addContent( new Element( "fun" ).addContent( "" + getFunction( i ).key() ) );
			elm_fun.addContent( new Element( "note" ));
			elm_map.addContent( elm_fun );
		}
		return elm_map;
	}

	public void fromXML( String file )
	{
		
		this.setFunction( 38, PosFunction.UP);
		this.setFunction( 40, PosFunction.DOWN);
		
		try{
			Document  doc    = (new SAXBuilder()).build( file );
			Element   root   = doc.getRootElement();
			Element   config = root.getChild( "keymap" );

			List list = config.getChildren( "key_fun" );


			for (int i = 0; i < list.size(); i++)
			{
				Element item = (Element) list.get(i);
				Element elm_value     = item.getChild("value");
				Element elm_fun      = item.getChild("fun");

				int v = Integer.parseInt ( elm_value.getTextTrim() );
				int f = Integer.parseInt ( elm_fun.getTextTrim() );
				this.setFunction( v, f );
			}
		} catch ( Exception e ){
					e.printStackTrace();
		}

		this.setFunction( KeyEvent.VK_ESCAPE, PosFunction.EXIT );
		this.setFunction( 10, PosFunction.ENTER );
		this.setFunction( '\b', PosFunction.BACKSPACE);
		
		this.setFunction( '=', '=');
		this.setFunction( ';', ';');
		this.setFunction( '.', PosFunction.POINT);
		
	
		for (int i = '0'; i <= '9'; i++){
			this.setFunction( i, i);
		}
			
	}
	
	public int getKeyValue(PosInput posInput){
		if(posInput==null)return 0;
		for(int i=0;i<key_map.length;i++){
			if(key_map[i]!=null && posInput.getKey()==key_map[i].getKey()){
				return i;
			}
		}
		return 0;
	}

	final static public int SIZE = 256;
	private PosInput[] key_map;
}
