package eu.jangos.auth.security.env;

import eu.jangos.auth.security.realm.JTokenRealm;
import eu.jangos.auth.security.realm.KeyApiTokenRealm;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDIEnvironmentLoaderListener is responsible to integrate Shiro objects into
 * the CDI container.
 *
 * @author Warkdev
 */
public class CDIEnvironmentLoaderListener extends EnvironmentLoaderListener {

    private static final Logger logger = LoggerFactory.getLogger(CDIEnvironmentLoaderListener.class);

    @Inject
    private JTokenRealm tokenRealm;

    @Inject
    private KeyApiTokenRealm keyRealm;

    public CDIEnvironmentLoaderListener() {
    }

    @Override
    protected WebEnvironment createEnvironment(ServletContext sc) {
        WebEnvironment webEnvironment  = super.createEnvironment(sc);
        
        logger.debug("Enter createEnvironment");
                
        RealmSecurityManager securityManager = (RealmSecurityManager) webEnvironment.getWebSecurityManager();
        Collection<Realm> listRealms = new ArrayList<>();
        listRealms.add(tokenRealm);
        listRealms.add(keyRealm);
        securityManager.setRealms(listRealms);   
        
        ((DefaultWebEnvironment) webEnvironment).setSecurityManager(securityManager);
        
        logger.debug("Exit createEnvironment");
        
        return webEnvironment;
    }     
    
    
}
