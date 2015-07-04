package com.royalstone.pos.card.common;

/**
 * Created by IntelliJ IDEA.
 * User: yaopoqing
 * Date: 2005-5-27
 * Time: 12:31:14
 * To change this template use Options | File Templates.
 */
public abstract class CardDAOFactory {
    public static final int LOCAL = 1;
    public static final int REMOTE = 2;

    public abstract AccurateDAO getAccurateDAO()throws Exception;

    public static CardDAOFactory getCardDAOFactory(int whichFactory) {

        switch (whichFactory) {
            case LOCAL:
                return new CardDAOLocalFactory();
            case REMOTE:
                return new CardDAORemoteFactory();
            default :
                return null;
        }
    }

}
