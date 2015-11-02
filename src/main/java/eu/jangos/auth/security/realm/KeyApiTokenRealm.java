package eu.jangos.auth.security.realm;

import eu.jangos.auth.controller.ServersController;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Commands;
import eu.jangos.auth.model.Roles;
import eu.jangos.auth.model.Servers;
import eu.jangos.auth.security.token.KeyApiToken;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.EJB;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KeyApiTokenRealm is a realm made to support the authenticating process with an api key provided.
 * @author Warkdev
 */
public class KeyApiTokenRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(KeyApiTokenRealm.class);
   
    @EJB
    private ServersController sc;        
    
    public KeyApiTokenRealm() {
        logger.debug("Enter KeyApiTokenRealm()");
        setAuthenticationTokenClass(KeyApiToken.class);
        logger.debug("Exit KeyApiTokenRealm()");
    }

    public KeyApiTokenRealm(CacheManager cacheManager) {
        super(cacheManager);
        logger.debug("Enter KeyApiTokenRealm(CacheManager)");
        setAuthenticationTokenClass(KeyApiToken.class);
        logger.debug("Exit KeyApiTokenRealm(CacheManager)");
    }

    public KeyApiTokenRealm(CredentialsMatcher matcher) {
        super(matcher);
        logger.debug("Enter KeyApiTokenRealm(CredentialsMatcher)");
        setAuthenticationTokenClass(KeyApiToken.class);
        logger.debug("Exit KeyApiTokenRealm(CredentialsMatcher)");
    }

    public KeyApiTokenRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
        logger.debug("Enter KeyApiTokenRealm(CacheManager, CredentialsMatcher)");
        setAuthenticationTokenClass(KeyApiToken.class);
        logger.debug("Exit KeyApiTokenRealm(CacheManager, CredentialsMatcher)");
    }    
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.debug("Enter doGetAuthorizationInfo");
        
        if(principals == null)
        {
            logger.debug("Exit doGetAuthorizationInfo");        
            return null;
        }
        
        logger.debug("Principals realm: "+principals.getRealmNames());
        
        AuthorizationInfo info = new SimpleAuthorizationInfo();                
        
        try {
            Servers server = sc.get(Integer.parseInt(principals.getPrimaryPrincipal().toString()));
            Set<String> listRoles = new HashSet<>();
            Set<String> listCommands = new HashSet<>();
            
            server.getRolesCollection().stream().map((r) -> {
                listRoles.add(r.getName());
                return r;
            }).map((r) -> {
                logger.debug("Adding role "+r.getName());
                return r;
            }).forEach((r) -> {
                r.getCommandsCollection().stream().map((c) -> {
                    listCommands.add(c.getName());
                    return c;
                }).forEach((c) -> {
                    logger.debug("Adding command "+c.getName());
                });
            });                        
            
            ((SimpleAuthorizationInfo) info).setRoles(listRoles);
            ((SimpleAuthorizationInfo) info).setStringPermissions(listCommands);
            logger.debug("Exit doGetAuthorizationInfo");        
            return info;
        } catch (DatabaseIntegrityException ex) {
            logger.debug("Exit doGetAuthorizationInfo");        
            throw new AuthorizationException(ex.getMessage());
        }                                        
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        logger.debug("Enter doGetAuthenticationInfo");
                
        if(token == null)
        {
            return null;
        }
        
        KeyApiToken kToken = (KeyApiToken) token;                                   
        
        try {
             Servers server = sc.getByToken(kToken.getName(), kToken.getKey());                          
             
             logger.debug("Exit doGetAuthenticationInfo");
             return new SimpleAuthenticationInfo(server.getId(), server.getKey(), "");
        } catch (InvalidArgumentException | DatabaseIntegrityException ex) {
            logger.debug("Exit doGetAuthenticationInfo");
            throw new AuthenticationException(ex.getMessage());
        }                        
    }    
}
