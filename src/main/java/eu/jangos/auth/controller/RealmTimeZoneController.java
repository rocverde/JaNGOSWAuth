package eu.jangos.auth.controller;

import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.model.RealmTimeZone;
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
public class RealmTimeZoneController {
    private static final Logger logger = LoggerFactory.getLogger(RealmTimeZoneController.class);
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;
    
    /**
     * Returns the realm time zone corresponding to the given id.
     * @param id The ID of the realm time zone to find.
     * @return A RealmTimeZone object corresponding to the realm time zone.
     * @throws DatabaseIntegrityException If none realm time zone with this id was found.
     */
    public RealmTimeZone get(int id) throws DatabaseIntegrityException {
        try {
            return (RealmTimeZone) this.em.createNamedQuery("RealmTimeZone.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("Time Zone with id "+id+" not found");
        }
    }
    
    /**
     * Returns all the realm time zones stored in the database.
     * @return A List of RealmTimeZone objects.
     */
    public List<RealmTimeZone> getAll() {
        return this.em.createNamedQuery("RealmTimeZone.findAll").getResultList();
    }
}
