package eu.jangos.auth.controller;

import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Locale;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Warkdev
 */
@Stateless
public class LocaleController {  
    private static final Logger logger = LoggerFactory.getLogger(LocaleController.class);
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;     
    
    @EJB
    private ParameterController ps;
    
    /**
     * Return all locale available in the database.
     * @return A List of Locales.
     */
    public List<Locale> getAll()
    {                
        return this.em.createNamedQuery("Locale.findAll").getResultList();                
    }
    
    /**
     * Returns the locale matching to the provided ID
     * @param id The ID of the locale to be found.
     * @return A locale corresponding to the given ID.
     * @throws DatabaseIntegrityException if there was no result found.
     */
    public Locale getLocale(int id) throws DatabaseIntegrityException{
        
        try {
            return (Locale) this.em.createNamedQuery("Locale.findById").setParameter("id", id).getSingleResult();            
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("Not locale with the id "+id+" found");
        }
    }
    
    /**
     * Return the locale matching the provided string.
     * @param locale A String corresponding to the locale to be found.
     * @return A locale object corresponding to the String provided in parameter or the default Locale (set in the parameters) if that Locale does not exists.
     * @throws InvalidArgumentException in case of issue with parameter validation.
     */
    public Locale getLocaleForString(String locale) throws InvalidArgumentException, DatabaseIntegrityException{
        if(locale == null || locale.isEmpty())
        {
            throw new InvalidArgumentException(
                    "The locale parameter is empty",
                    "Please check your submitted parameters");
        }
        
        try{
            return (Locale) this.em.createNamedQuery("Locale.findByLocaleString").setParameter("localeString", locale).getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Locale not supported, providing default.");
            return (Locale) this.em.createNamedQuery("Locale.findByLocaleString").setParameter("localeString", this.ps.getValue("defaultLocale")).getSingleResult();            
        }
    }
}
