/*
 * Filename: RSBaseServiceEventQueue.java
 * Created on 2004-1-28
 * @author wangcr
 */
package com.royalstone.pos.services;

import jpos.events.DataEvent;
import jpos.events.DirectIOEvent;
import jpos.events.ErrorEvent;
import jpos.events.JposEvent;
import jpos.events.OutputCompleteEvent;
import jpos.events.StatusUpdateEvent;



class RSBaseServiceEventQueue extends Thread
{
    class FIFO
    {

        public Object getElement()
        {
            if(num == 0)
            {
                return null;
            } else
            {
                Object obj = objs[0];
                objs[0] = null;
                System.arraycopy(((Object) (objs)), 1, ((Object) (objs)), 0, num);
                num--;
                objs[num] = null;
                return obj;
            }
        }

        public int getMax()
        {
            return dim;
        }

        public int getSize()
        {
            return num;
        }

        public boolean isFull()
        {
            return num >= dim - 1;
        }

        public Object peekElement(int i)
        {
            if(i < 0 || i >= num)
                throw new ArrayIndexOutOfBoundsException(i);
            else
                return objs[i];
        }

        public boolean putElement(Object obj)
        {
            if(num >= dim - 1)
            {
                return false;
            } else
            {
                num++;
                objs[num - 1] = obj;
                return true;
            }
        }

        public void removeAllElements()
        {
            num = 0;
            for(int i = 0; i < dim; i++)
                objs[i] = null;

        }

        public boolean removeElement(Object obj)
        {
            for(int i = 0; i < num; i++)
                if(obj == objs[i])
                {
                    objs[i] = null;
                    System.arraycopy(((Object) (objs)), i + 1, ((Object) (objs)), i, num);
                    num--;
                    objs[num] = null;
                    return true;
                }

            return false;
        }

        private Object objs[];
        private int num;
        private int dim;

        public FIFO(int i)
        {
            objs = null;
            objs = new Object[i];
            num = 0;
            dim = i;
        }
    }


    public RSBaseServiceEventQueue(RSBaseService RSBaseService)
    {
        eventObjects = new FIFO(50); 
        eventEvents = new FIFO(50);
        threadShouldFinish = false;
        baseService = null;
        eventObjects = new FIFO(50);
        eventEvents = new FIFO(50);
        baseService = RSBaseService;
    }

    public RSBaseServiceEventQueue(RSBaseService RSBaseService, int i)
    {
        eventObjects = new FIFO(50);
        eventEvents = new FIFO(50);
        threadShouldFinish = false;
        baseService = null;
        baseService = RSBaseService;
        eventObjects = new FIFO(i);
        eventEvents = new FIFO(i);
    }

    public void run()
    {
        while(!threadShouldFinish) 
        {
            if(!baseService.freezeEvents && baseService.deviceEnabled)
            {
                Object obj = null;
                JposEvent jposevent = null;
                if(baseService.dataEventEnabled)
                    synchronized(eventObjects)
                    {
                        obj = eventObjects.getElement();
                        jposevent = (JposEvent)eventEvents.getElement();
                    }
                else
                    synchronized(eventObjects)
                    {
                        boolean flag4 = false;
                        int j = eventObjects.getSize();
                        for(int l = 0; l < j; l++)
                        {
                            obj = eventObjects.peekElement(l);
                            jposevent = (JposEvent)eventEvents.peekElement(l);
                            if(!(jposevent instanceof StatusUpdateEvent) && !(jposevent instanceof OutputCompleteEvent) && (!(jposevent instanceof ErrorEvent) || ((ErrorEvent)jposevent).getErrorLocus() != 1))
                                continue;
                            flag4 = true;
                            eventObjects.removeElement(obj);
                            eventEvents.removeElement(jposevent);
                            break;
                        }

                        if(!flag4)
                        {
                            jposevent = null;
                            obj = null;
                        }
                    }
                if(jposevent != null)
                {
                    if(jposevent instanceof DataEvent)
                    {
                        DataEvent dataevent = (DataEvent)jposevent;
                        boolean flag = baseService.preDataEvent(obj, dataevent);
						if(flag)
						{
							baseService.callbacks.fireDataEvent(dataevent);
							baseService.postDataEvent(obj, dataevent);
						}                        
 
                    } else
                    if(jposevent instanceof ErrorEvent)
                    {
                        ErrorEvent errorevent = (ErrorEvent)jposevent;
                        boolean flag1 = baseService.preErrorEvent(obj, errorevent);
                        if(flag1)
                        {
							baseService.callbacks.fireErrorEvent(errorevent);
                            baseService.postErrorEvent(obj, errorevent);
                        }
                    } else
                    if(jposevent instanceof DirectIOEvent)
                    {
                        DirectIOEvent directioevent = (DirectIOEvent)jposevent;
                        baseService.callbacks.fireDirectIOEvent(directioevent);
                        
                    } else
                    if(jposevent instanceof OutputCompleteEvent)
                    {
                        OutputCompleteEvent outputcompleteevent = (OutputCompleteEvent)jposevent;
                        boolean flag2 = baseService.preOutputCompleteEvent(obj, outputcompleteevent);
                        if(flag2)
                        {
                        
                            baseService.callbacks.fireOutputCompleteEvent(outputcompleteevent);
                        
                            baseService.postOutputCompleteEvent(obj, outputcompleteevent);
                        }
                    } else
                    if(jposevent instanceof StatusUpdateEvent)
                    {
                        StatusUpdateEvent statusupdateevent = (StatusUpdateEvent)jposevent;
                        boolean flag3 = baseService.preStatusUpdateEvent(obj, statusupdateevent);
                        if(flag3)
                        {
  
                            baseService.callbacks.fireStatusUpdateEvent(statusupdateevent);
  
                            baseService.postStatusUpdateEvent(obj, statusupdateevent);
                        }
                    }
                    continue;
                }
            }
            synchronized(this)
            {
                try
                {
                    wait();
                }
                catch(InterruptedException _ex) { }
            }
        }
    }

    private static final int eventMAX = 50;
    FIFO eventObjects;
    FIFO eventEvents;
    protected boolean threadShouldFinish;
    private RSBaseService baseService;
}