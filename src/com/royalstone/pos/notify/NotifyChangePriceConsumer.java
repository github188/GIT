package com.royalstone.pos.notify;

import java.util.ArrayList;

import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.shell.pos;

/**
 * @author liangxinbiao
 */

public class NotifyChangePriceConsumer {
	
	private static NotifyChangePriceConsumer instance=new NotifyChangePriceConsumer();
	
	public static NotifyChangePriceConsumer getInstance(){
		return instance;
	}

	public void consume() {
		ArrayList goodsNoList = new ArrayList();
		if (NotifyQueue.getInstance().getNotifyCount() > 0) {
			ArrayList notifyList =
				NotifyQueue.getInstance().getNotify(Notify.CHANGE_PRICE);
			for (int i = 0; i < notifyList.size(); i++) {
				Notify notify =
					(Notify) notifyList.get(i);
				ArrayList notifyItemList = notify.getNotifyItemList();
				for (int j = 0; j < notifyItemList.size(); j++) {
					NotifyItem notifyItem = (NotifyItem) notifyItemList.get(j);
					if (!goodsNoList.contains(notifyItem.getValue1())) {
						goodsNoList.add(notifyItem.getValue1());
					}
				}
			}

			try {
				ArrayList goodsUpdateList =
					RealTime.getInstance().getGoodsUpdateList(goodsNoList);
				
				pos.core.goodsUpdate(goodsUpdateList);

			} catch (RealTimeException e) {
				e.printStackTrace();
			}
			
			
		}
	}

}
