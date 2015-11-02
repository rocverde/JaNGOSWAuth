package eu.jangos.auth.controller;

import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.model.RealmType;
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
public class RealmTypeController {
    private static final Logger logger = LoggerFactory.getLogger(RealmTypeController.class);
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;
    
    /**
     * Returns the realm type corresponding to the given id.
     * @param id The ID of the realm type to find.
     * @return A RealmType object corresponding to the realm type.
     * @throws DatabaseIntegrityException If none realm type with this id was found.
     */
    public RealmType get(int id) throws DatabaseIntegrityException {
        try {
            return (RealmType) this.em.createNamedQuery("RealmType.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("Realmtype with id "+id+" not found");
        }
    }
    
    /**
     * Returns all the realm types stored in the database.
     * @return A List of RealmType objects.
     */
    public List<RealmType> getAll() {
        return this.em.createNamedQuery("RealmType.findAll").getResultList();
    }
}
