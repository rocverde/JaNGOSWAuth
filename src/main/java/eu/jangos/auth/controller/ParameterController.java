package eu.jangos.auth.controller;

import eu.jangos.auth.dto.AuthParameterDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Parameter1;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Warkdev
 */
@Stateless
public class ParameterController {

    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;

    /**
     * Returns the parameter record matching the supplied id.
     * @param id The ID parameter to find.
     * @return The parameter corresponding to the given id.
     * @throws DatabaseIntegrityException If the parameter was not found.
     */
    public Parameter1 getParameter(int id) throws DatabaseIntegrityException {
        try {
            return (Parameter1) this.em.createNamedQuery("Parameter1.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("The parameter with id "+id+" does not exist");
        }
    }
    
    /**
     * Return the parameter record matching the supplied key.     
     * @param key The key for which the parameter must be returned.
     * @return The parameter corresponding to the given key.
     * @throws DatabaseIntegrityException if the parameter was not found.
     * @throws InvalidArgumentException if there was an issue during the validation of the parameter.
     */
    public Parameter1 getParameter(String key) throws DatabaseIntegrityException, InvalidArgumentException {
        if(key == null || key.isEmpty())
        {
            throw new InvalidArgumentException("The submitted key parameter is null or empty", "Check the key parameter sent");
        }
        
        try {
            return (Parameter1) this.em.createNamedQuery("Parameter1.findByParam").setParameter("param", key).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("The parameter with key "+key+" does not exist");
        }
    }
    
    /**
     * Return the parameter value matching the supplied key.     
     * @param key The key for which the parameter must be returned.
     * @return The value corresponding to the given key.
     * @throws DatabaseIntegrityException if the parameter was not found.
     * @throws InvalidArgumentException if there was an issue during the validation of the parameter.
     */
    public String getValue(String key) throws DatabaseIntegrityException, InvalidArgumentException {
        if(key == null || key.isEmpty())
        {
            throw new InvalidArgumentException("The submitted key parameter is null or empty", "Check the key parameter sent");
        }
        
        try {
            return ((Parameter1) this.em.createNamedQuery("Parameter1.findByParam").setParameter("param", key).getSingleResult()).getVal();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("The parameter with key "+key+" does not exist");
        }
    }
    
    /**
     * Return all the parameters stored in the database.
     * @return A List of Parameter.
     */
    public List<Parameter1> getAll() {
        return (List<Parameter1>) this.em.createNamedQuery("Parameter1.findAll").getResultList();
    }
    
    /**
     * Delete the parameter with the given ID.
     * @param id The ID of the parameter to be deleted.
     * @throws DatabaseIntegrityException If the parameter was not found in the database.
     */
    public void delete(int id) throws DatabaseIntegrityException {
        Parameter1 parameter = getParameter(id);
        
        this.em.remove(parameter);
    }
    
    /**
     * Update a parameter record based on the provided information.
     * @param parameterDTO The paramater record to update.
     * @return The updated parameter.
     * @throws InvalidArgumentException If there was an error during the validation.
     * @throws DatabaseIntegrityException If the parameter was not found in the database.
     */
    public Parameter1 update(AuthParameterDTO parameterDTO) throws InvalidArgumentException, DatabaseIntegrityException {
        if(parameterDTO == null)
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO.");
        }
        
        if(parameterDTO.getId() == null)
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO, ID value is null or empty.");
        }
        
        if(parameterDTO.getParam() == null || parameterDTO.getParam().isEmpty())
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO, param value is null or empty.");
        }
        
        if(parameterDTO.getVal()== null || parameterDTO.getVal().isEmpty())
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO, Value value is null or empty.");
        }
        
        Parameter1 parameter = getParameter(parameterDTO.getId());
        
        parameter.setParam(parameterDTO.getParam());
        parameter.setVal(parameterDTO.getVal());
        
        this.em.merge(parameter);
        
        return parameter;
    }
    
    /**
     * Create a parameter record based on the provided information.
     * @param parameterDTO The paramater record to update.
     * @return The updated parameter.
     * @throws InvalidArgumentException If there was an error during the validation.
     * @throws DatabaseIntegrityException If the parameter was not found in the database.
     * @throws EntityExistsException If the entity already exist in the database.
     */
    public Parameter1 create(AuthParameterDTO parameterDTO) throws InvalidArgumentException, DatabaseIntegrityException, EntityExistsException {
        if(parameterDTO == null)
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO.");
        }        
        
        if(parameterDTO.getParam() == null || parameterDTO.getParam().isEmpty())
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO, param value is null or empty.");
        }
        
        if(parameterDTO.getVal()== null || parameterDTO.getVal().isEmpty())
        {
            throw new InvalidArgumentException("The parameter submitted is invalid", "Please check your AuthParameterDTO, Value value is null or empty.");
        }

        try {
            getParameter(parameterDTO.getParam());
            throw new EntityExistsException("The parameter "+parameterDTO.getParam()+" already exists.");
        } catch (DatabaseIntegrityException die) {
            
        }
        Parameter1 parameter = new Parameter1();
        
        parameter.setParam(parameterDTO.getParam());
        parameter.setVal(parameterDTO.getVal());
        
        this.em.persist(parameter);
        
        return parameter;
    }
}
