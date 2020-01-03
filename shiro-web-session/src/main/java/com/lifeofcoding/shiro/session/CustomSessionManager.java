package com.lifeofcoding.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import javax.servlet.ServletRequest;
import java.io.Serializable;

public class CustomSessionManager extends DefaultWebSessionManager {

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException{
        //通过SessionKey获取SessionId
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        //通过SessionKey获取ServletRequest
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        //尝试从request中根据sessionId获取session
        if (null!=request && null!=sessionId){
            Session session = (Session) request.getAttribute(sessionId.toString());
            if (null!=session) {
                return session;
            }
        }
        /*如果request中没有session，则使用父类获取session，并保存到request中，
         父类DefaultWebSession是通过SessionDao获取session,在这里是从redis获取*/
        Session session = super.retrieveSession(sessionKey);
        if (null != request && null != sessionId){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}


