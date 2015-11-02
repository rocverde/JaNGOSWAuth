package eu.jangos.auth.security.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * KeyApiToken represents an authenticating filter for server components.
 * @author Warkdev
 */
public class KeyApiToken implements AuthenticationToken {
    private String name;
    private String key;

    public KeyApiToken() {
    }   
    
    public KeyApiToken(String name, String key) {
        this.name = name;
        this.key = key;
    }        

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }        
    
    @Override
    public Object getPrincipal() {
        return this.name;
    }

    @Override
    public Object getCredentials() {
        return this.key;
    }
    
}
