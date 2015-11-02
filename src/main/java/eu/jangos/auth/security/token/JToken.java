package eu.jangos.auth.security.token;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.AesCipherService;

/**
 * JaNGOSToken is the token used by JaNGOS to propagate client information.
 * @author Warkdev
 */
public class JToken implements AuthenticationToken {
    private String value;
    private int id;
    private String name;    

    
    public JToken() {
        
    }
    
    public JToken(String value) {
        this.value = value;                     
    }

    public void encrypt(String key) {
        byte[] toEncrypt = CodecSupport.toBytes(this.value);
        
        AesCipherService aes = new AesCipherService();
        
        this.value = aes.encrypt(toEncrypt, Base64.decode(key)).toBase64();
    }
    
    public void decrypt(String key) {
        AesCipherService aes = new AesCipherService();
        String decrypted = CodecSupport.toString(aes.decrypt(Base64.decode(this.value), Base64.decode(key)).getBytes());
                        
        try
        {
            String [] decryptArr = decrypted.split(":");
            if(decryptArr.length < 2){
                throw new AuthenticationException("Token is invalid");
            }
            this.id = Integer.parseInt(decryptArr[0]);
            this.name = decryptArr[1];
        } catch (AuthenticationException | NumberFormatException e) {
            throw new AuthenticationException("Token is invalid.");
        }
    }            
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }  

    @Override
    public Object getPrincipal() {
        return this.id;
    }

    @Override
    public Object getCredentials() {
        return this.name;
    }
}
