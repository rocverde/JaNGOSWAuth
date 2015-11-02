package eu.jangos.auth.controller;

import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Commands;
import java.util.List;
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
public class CommandsController {
    private static final Logger logger = LoggerFactory.getLogger(CommandsController.class);
    
    @PersistenceContext(name = "AuthPU")    
    private EntityManager em;
    
    /**
     * Get the command associated to the given id.
     * @param id The id of the command to look for.
     * @return The command corresponding to the given ID.
     * @throws DatabaseIntegrityException if no commands with the given ID is found.
     */
    public Commands get(int id) throws DatabaseIntegrityException
    {
        Commands command = null;
        
        try{
            command = (Commands) this.em.createNamedQuery("Commands.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No account found with the id "+id);
        }
        
        return command;
    }
    
    /**
     * Get the command associated to the name given in parameter.
     * @param name The name of the command to be found.
     * @return The command corresponding to the given name.
     * @throws DatabaseIntegrityException If no commands with the given name is found.
     * @throws InvalidArgumentException If the supplied name is null or empty.
     */
    public Commands get(String name) throws DatabaseIntegrityException, InvalidArgumentException
    {
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("The name parameter is null or empty", "");
        }
        
        Commands command = null;
        
        try{
            command = (Commands) this.em.createNamedQuery("Commands.findByName").setParameter("name", name).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No command found with the name "+name);
        }
        
        return command;
    }
     
    /**
     * Get all the commands available in the database.
     * @return All the commands available in the database.     
     */
    public List<Commands> getAll()
    {
        List<Commands> listCommands = this.em.createNamedQuery("Commands.findAll").getResultList();
        
        return listCommands;
    }
}
