package eu.jangos.servlet;

import io.swagger.jaxrs.config.BeanConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * BootStrap is used to initialize swagger.
 * @author Warkdev
 */
public class Bootstrap extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/Auth");
        beanConfig.setDescription("This REST-API is used to access the account management database, including realms management for JaNGOS.");
        beanConfig.setTitle("JaNGOS Auth API");        
        beanConfig.setLicense("https://www.gnu.org/licenses/gpl.html");
        beanConfig.setContact("https://www.getmangos.eu/members/talendrys/");
        beanConfig.setResourcePackage("eu.jangos.auth.service");                
        beanConfig.setScan(true);
    }
}
