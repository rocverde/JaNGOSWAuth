package eu.jangos.auth.security.filters;

import eu.jangos.auth.security.token.KeyApiToken;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KeyApiAuthenticatingFilter is used to retrieve the key api from the request.
 *
 * @author Warkdev
 */
public class KeyApiAuthenticatingFilter extends AuthenticatingFilter {

    private static final Logger logger = LoggerFactory.getLogger(KeyApiAuthenticatingFilter.class);

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        logger.debug("Enter createToken");

        String key = (String) request.getParameter("api_key");
        String name = (String) request.getParameter("name");

        if (key == null || key.isEmpty() || name == null || name.isEmpty()) {
            return new KeyApiToken("", "");
        }

        logger.debug("Exit createToken");
        return new KeyApiToken(name, key);
    }   

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Enter onAccessDenied");
        String key = (String) request.getParameter("api_key");
        String name = (String) request.getParameter("name");

        logger.debug("Key value: "+key);
        logger.debug("Name value: "+name);
        
        if(key == null || key.isEmpty() || name == null || name.isEmpty()){
            logger.debug("No suitable token found in the request.");
            logger.debug("Exit onAccessDenied");
            return true;
        }
        
        logger.debug("Exit onAccessDenied");
        
        return executeLogin(request, response);                                
    }

}
