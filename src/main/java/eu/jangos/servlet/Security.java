package eu.jangos.servlet;

import eu.jangos.auth.Constants;
import eu.jangos.auth.controller.ParameterController;
import eu.jangos.auth.dto.AuthParameterDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Parameter1;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is used to initialize security parameters on servlet startup.
 *
 * @author Warkdev
 */
public class Security extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Security.class);

    @EJB
    ParameterController pc;

    @Override
    public void init() throws ServletException {
        boolean generate = false;
        Parameter1 param = null;
        
        // First, we check that the server key exists.
        try {
            param = pc.getParameter(Constants.SERVER_KEY_PARAM);
        } catch (DatabaseIntegrityException ex) {
            generate = true;
        } catch (InvalidArgumentException ex) {
            logger.error(ex.getMessage());
        }

        // Then we check if the parameter which specify to generate a new key is set in database and what is the value.
        try {
            generate = Boolean.parseBoolean(pc.getParameter(Constants.GENERATE_NEW_KEY_PARAM).getVal());
        } catch (DatabaseIntegrityException | InvalidArgumentException ex) {
            logger.info("Missing parameter " + Constants.GENERATE_NEW_KEY_PARAM + " in database.");
        }

        if (!generate) {
            logger.info("Server is configured to not generate a new key on startup");
            return;
        }               

        try {        
            logger.info("Initialization of server security, deleting the old server key.");  
            logger.info("Generating server key");            
            AesCipherService aes = new AesCipherService();
            aes.setKeySize(Integer.parseInt(pc.getParameter(Constants.SERVER_KEY_LENGTH).getVal()));                
            logger.debug("Key Algorithm: AES");
            logger.debug("Key Size: "+aes.getKeySize());                        
            AuthParameterDTO parameter = new AuthParameterDTO();            
            parameter.setParam(Constants.SERVER_KEY_PARAM);
            parameter.setVal(Base64.encodeToString(aes.generateNewKey().getEncoded()));            
            if(param != null)
            {
                parameter.setId(param.getId());
                pc.update(parameter);
            } else {
                pc.create(parameter);
            }                        
        } catch (InvalidArgumentException | DatabaseIntegrityException | EntityExistsException ex) {
            logger.error("Exception during the creation of the security key, the API won't be usable.");
        }
    }

}
