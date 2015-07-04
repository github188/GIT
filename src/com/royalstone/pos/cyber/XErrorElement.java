/*
 * Created on 2004-6-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.cyber;

import org.jdom.Element;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XErrorElement extends Element
{
	public XErrorElement( int code, String note )
	{
		super( "xerror" );
		Element xerr = new Element( "xerror" );
		Element elm_code = new Element( "code" ).addContent( "" + code );
		Element elm_note = new Element( "note" ).addContent( note );
		
		this.addContent( elm_code );
		this.addContent( elm_note );
	}
}
