package com.royalstone.pos.notify;

import java.util.ArrayList;

/**
 * @author liangxinbiao
 */

public class NotifyQueue {

	private static NotifyQueue instance = new NotifyQueue();
	private Object lock = new Object();
	private ArrayList queue = new ArrayList();

	private NotifyQueue() {
		
	}

	public static NotifyQueue getInstance() {
		return instance;
	}

	public void addNotify(Notify notify) {
		synchronized (lock) {
			queue.add(notify);
		}
	}

	public int getNotifyCount() {
		synchronized (lock) {
			return queue.size();
		}
	}

	public ArrayList getNotify(int type) {
		ArrayList msgList = new ArrayList();
		ArrayList leftList = new ArrayList();
		synchronized (lock) {
			for (int i = 0; i < queue.size(); i++) {
				if (((Notify) queue.get(i)).getNofityType() == type) {
					msgList.add(queue.get(i));
				} else {
					leftList.add(queue.get(i));
				}
			}
			queue = leftList;
		}
		return msgList;
	}

	public Notify getNotify() {
		Notify message = null;
		synchronized (lock) {
			if (queue.size() > 0) {
				message = (Notify) queue.get(0);
				queue.remove(0);
			}
		}
		return message;
	}
	
}
