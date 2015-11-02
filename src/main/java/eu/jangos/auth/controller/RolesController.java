package eu.jangos.auth.controller;

import eu.jangos.auth.dto.RolesDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Roles;
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
public class RolesController {

    private static final Logger logger = LoggerFactory.getLogger(RolesController.class);

    @PersistenceContext(name = "AuthPU")
    private EntityManager em;
    
    /**
     * Get the role associated to the given id.
     * @param id The id of the role to look for.
     * @return The role corresponding to the given ID.
     * @throws DatabaseIntegrityException if no roles with the given ID is found.
     */
    public Roles get(int id) throws DatabaseIntegrityException
    {
        Roles role = null;
        
        try{
            role = (Roles) this.em.createNamedQuery("Roles.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No role found with the id "+id);
        }
        
        return role;
    }
    
    /**
     * Get the role associated to the name given in parameter.
     * @param name The name of the role to be found.
     * @return The role corresponding to the given name.
     * @throws DatabaseIntegrityException If no roles with the given name is found.
     * @throws InvalidArgumentException If the supplied name is null or empty.
     */
    public Roles get(String name) throws DatabaseIntegrityException, InvalidArgumentException
    {
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("The name parameter is null or empty", "");
        }
        
        Roles role = null;
        
        try{
            role = (Roles) this.em.createNamedQuery("Roles.findByName").setParameter("name", name).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No role found with the name "+name);
        }
        
        return role;
    }
     
    /**
     * Get all the roles available in the database.
     * @return All the roles available in the database.     
     */
    public List<Roles> getAll()
    {
        List<Roles> listRoles = this.em.createNamedQuery("Roles.findAll").getResultList();
        
        return listRoles;
    }
    
    /**
     * Create a new role in the database.
     * @param roleDTO The role to be inserted.
     * @return The created role.
     * @throws InvalidArgumentException If there was an error during the input validation.
     * @throws EntityExistsException If the role already exist.
     */
    public Roles create(RolesDTO roleDTO) throws InvalidArgumentException, EntityExistsException
    {
        if(roleDTO == null)
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role DTO is null");
        }
        
        if(roleDTO.getName() == null || roleDTO.getName().isEmpty())
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role name is empty");
        }
        
        if(roleDTO.getDescription() == null || roleDTO.getDescription().isEmpty())
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role description is empty");
        }
        
        Roles role = new Roles();
        
        try{
            get(roleDTO.getName());
            throw new EntityExistsException("The role already exist");
        } catch (DatabaseIntegrityException die) {
            
        }
        
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        this.em.persist(role);
        return role;
    }
    
    /**
     * Delete the role corresponding to the given ID.
     * @param id The ID of the role to be deleted.
     * @throws DatabaseIntegrityException If the ID does not match to any role.
     */
    public void delete(int id) throws DatabaseIntegrityException
    {
        Roles role = get(id);
        
        this.em.remove(role);
    }
    
    /**
     * Update a role in the database.
     * @param roleDTO The role to be updated.
     * @return The updated role.
     * @throws InvalidArgumentException If there was an error during the input validation.
     * @throws DatabaseIntegrityException If the role to update is not found in the database.
     */
    public Roles update(RolesDTO roleDTO) throws InvalidArgumentException, DatabaseIntegrityException
    {
        if(roleDTO == null)
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role DTO is null");
        }
        
        if(roleDTO.getName() == null || roleDTO.getName().isEmpty())
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role name is empty");
        }
        
        if(roleDTO.getDescription() == null || roleDTO.getDescription().isEmpty())
        {
            throw new InvalidArgumentException("The data provided are uncomplete", "The provided role description is empty");
        }
        
        Roles role = get(roleDTO.getName());            
        
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        this.em.merge(role);
        return role;
    }
}
