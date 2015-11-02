package eu.jangos.auth.security.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UnauthenticatedFilter will send an HTTP error 401 to the client meaning that authentication is required.
 * @author Warkdev
 */
public class UnauthenticatedFilter extends AuthenticatingFilter {
    private static final Logger logger = LoggerFactory.getLogger(UnauthenticatedFilter.class);

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Enter createToken");
        logger.debug("Exit createToken");
        return null;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("Enter onAccessDenied");
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);        
        logger.debug("Exit onAccessDenied");
        return false;
    }
    
    
}
