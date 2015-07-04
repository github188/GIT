
package com.royalstone.pos.card;

import java.io.IOException;


/**
 * @author yaopoqing
 */
public interface MemberCardMgr {
	
	public  MemberCard query(String cardNo)throws IOException;
	public String updateCardInfo(MemberCardUpdate memberCard)throws IOException;
	// 抵用卷 效验过程 卡号 卷值
	public MemberCard activation(String cardNo, String secrety)throws IOException;	 
}
