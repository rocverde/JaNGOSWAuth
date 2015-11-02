package eu.jangos.auth.security.realm;

import eu.jangos.auth.security.token.JToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JTokenRealm is a realm made to support the authenticating process with a token provided.
 * @author Warkdev
 */
public class JTokenRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(JTokenRealm.class);
    
    public JTokenRealm() {
        logger.debug("Enter JTokenRealm()");
        setAuthenticationTokenClass(JToken.class);
        logger.debug("Exit JTokenRealm()");
    }

    public JTokenRealm(CacheManager cacheManager) {
        super(cacheManager);
        logger.debug("Enter JTokenRealm(CacheManager)");
        setAuthenticationTokenClass(JToken.class);
        logger.debug("Exit JTokenRealm(CacheManager)");
    }

    public JTokenRealm(CredentialsMatcher matcher) {
        super(matcher);
        logger.debug("Enter JTokenRealm(CredentialsMatcher)");
        setAuthenticationTokenClass(JToken.class);
        logger.debug("Exit JTokenRealm(CredentialsMatcher)");
    }

    public JTokenRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
        logger.debug("Enter JTokenRealm(CacheManager, CredentialsMatcher)");
        setAuthenticationTokenClass(JToken.class);
        logger.debug("Exit JTokenRealm(CacheManager, CredentialsMatcher)");
    }              
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.debug("Enter doGetAuthorizationInfo");
                        
        logger.debug("Exit doGetAuthorizationInfo");
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        logger.debug("Enter doGetAuthenticationInfo");
        logger.debug("Exit doGetAuthenticationInfo");
        return null;
    }    
}
