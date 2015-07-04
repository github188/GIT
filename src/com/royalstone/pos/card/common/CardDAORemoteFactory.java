package com.royalstone.pos.card.common;

/**
 * Created by IntelliJ IDEA.
 * User: fire
 * Date: 2005-5-27
 */
public class CardDAORemoteFactory extends CardDAOFactory{
    public AccurateDAO getAccurateDAO() throws Exception{
        return new AccurateRemoteDAO();
    }
}
