package eu.jangos.auth.security.filters;

import eu.jangos.auth.Constants;
import eu.jangos.auth.controller.ParameterController;
import eu.jangos.auth.security.token.JToken;
import javax.ejb.EJB;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JTokenAuthenticatingFilter is used to retrieve the token from the request.
 * @author Warkdev
 */
public class JTokenAuthenticatingFilter extends AuthenticatingFilter {

    private static final Logger logger = LoggerFactory.getLogger(JTokenAuthenticatingFilter.class);
    
    @EJB
    ParameterController pc;
    
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Enter createToken");
        logger.info("Creating the JToken from the header");
        String authorizationHeader = getAuthzHeader(request);
        
        // There is no AuthenticationToken for this request. Maybe a server component.
        if(authorizationHeader == null || authorizationHeader.isEmpty())
        {
            logger.info("No token found in the header, generating and empty token");
            return new JToken();
        }
        
        logger.info("Token found in the header");
  
        JToken token = new JToken(authorizationHeader);
        token.decrypt(pc.getParameter(Constants.SERVER_KEY_PARAM).getVal());
        logger.debug("Exit createToken");
        return token;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Enter onAccessDenied");
        String authorizationHeader = getAuthzHeader(request);
        
        logger.debug("Header value: "+authorizationHeader);
        
        if(authorizationHeader == null || authorizationHeader.isEmpty())
        {
            logger.debug("No suitable authorization header found");
            logger.debug("Exit onAccessDenied");
            return true;
        }
                        
        logger.debug("Exit onAccessDenied");
        return executeLogin(request, response); 
    }

    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }
    
}
