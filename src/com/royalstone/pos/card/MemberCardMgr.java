
package com.royalstone.pos.card;

import java.io.IOException;


/**
 * @author yaopoqing
 */
public interface MemberCardMgr {
	
	public  MemberCard query(String cardNo)throws IOException;
	public String updateCardInfo(MemberCardUpdate memberCard)throws IOException;
	// ���þ� Ч����� ���� ��ֵ
	public MemberCard activation(String cardNo, String secrety)throws IOException;	 
}
