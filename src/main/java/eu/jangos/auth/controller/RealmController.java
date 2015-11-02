package eu.jangos.auth.controller;

import eu.jangos.auth.dto.AccountDTO;
import eu.jangos.auth.dto.RealmDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Account;
import eu.jangos.auth.model.Realm;
import eu.jangos.auth.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Realm Service provides all services related to realm information.
 * 
 * @author Warkdev
 */
@Stateless
public class RealmController {
    private static final Logger logger = LoggerFactory.getLogger(RealmController.class);
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;     
    
    @EJB
    private RealmTypeController rtc;
    
    @EJB
    private RealmTimeZoneController rtzc;
    
    @EJB
    private AccountController ac;
    
    /**
     * Return The realm corresponding to this ID.
     * @param id The realm ID.
     * @return A Realm object corresponding to the given id.
     * @throws DatabaseIntegrityException if no realm with the ID has been found.
     */
    public Realm get(int id) throws DatabaseIntegrityException{
        Realm r;
        
        try {
            r = (Realm) this.em.createNamedQuery("Realm.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No realm with id "+id+" found");
        }
        
        return r;
    }
    
    /**
     * Return the realm corresponding to the name.
     * @param name The name of the realm.
     * @return A Realm object corresponding to the given name, null otherwise.
     * @throws InvalidArgumentException If there was a problem during input validation.
     * @throws DatabaseIntegrityException If no realm with that name has been found.
     */
    public Realm get(String name) throws InvalidArgumentException, DatabaseIntegrityException{
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("The name parameter is null or empty.", "The submitted name is null or empty");
        }
        
        Realm r;
        
        try {
            r = (Realm) this.em.createNamedQuery("Realm.findByName").setParameter("name", name).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No realm with name "+name+" found");
        }
        
        return r;
    }
    
    /**
     * Return all available realms in database.
     * @return A List of Realm corresponding to the available realms in database.
     */
    public List<Realm> getAllRealms(){
        return (List<Realm>) this.em.createNamedQuery("Realm.findAll").getResultList();
    }
    
    /**
     * Create a new realm based on the realm DTO provided in argument.
     * @param r The RealmDTO object to insert in the database.
     * @return The newly created realm.
     * @throws InvalidArgumentException If there is a validation error.    
     * @throws EntityExistsException If a realm with the same name already exist.
     */
    public Realm create(RealmDTO r) throws InvalidArgumentException, DatabaseIntegrityException, EntityExistsException
    {
        if(r == null)
        {
            throw new InvalidArgumentException("The realm is invalid", "The realm object is null");
        }
        
        if(r.getAddress() == null || r.getAddress().isEmpty() || !Utils.isValidIPv4(r.getAddress()))
        {
            throw new InvalidArgumentException("The realm address is invalid", "The realm address is null, empty or has an invalid format");
        }
        
        if(r.getName() == null || r.getName().isEmpty())
        {
            throw new InvalidArgumentException("The realm name is invalid", "The realm name is null or empty");
        }
                
        try {
            get(r.getName());
            throw new EntityExistsException("The realm with name "+r.getName()+" already exists");
        } catch (DatabaseIntegrityException ex) {        
        }
        
        Realm realm = new Realm();
        
        realm.setAddress(r.getAddress());
        realm.setPort(r.getPort());
        realm.setName(r.getName());
        realm.setCountPlayers(0);
        realm.setPopulation(0);
        realm.setInvalid(r.isInvalid());
        realm.setMaxPlayers(r.getMaxPlayers());        
        realm.setNewplayers(r.isNewplayers());
        realm.setOffline(r.isOffline());
        realm.setRecommended(r.isRecommended());        
        realm.setShowversion(r.isShowversion());
        realm.setFkRealmtype(rtc.get(r.getFkRealmTypeDTO().getId()));
        realm.setFkTimezone(rtzc.get(r.getFkTimezoneDTO().getId()));
        
        this.em.persist(realm);
        
        return realm;
    }
    
    /**
     * Update a realm based on the realm DTO provided in argument.
     * @param r The RealmDTO object to update in the database.
     * @return The updated realm.
     * @throws InvalidArgumentException If there is a validation error.
     * @throws DatabaseIntegrityException If no realm with that id already exists.
     */
    public Realm update(RealmDTO r) throws InvalidArgumentException, DatabaseIntegrityException
    {
        if(r == null)
        {
            throw new InvalidArgumentException("The realm is invalid", "The realm object is null");
        }
        
        if(r.getAddress() == null || r.getAddress().isEmpty() || !Utils.isValidIPv4(r.getAddress()))
        {
            throw new InvalidArgumentException("The realm address is invalid", "The realm address is null, empty or has an invalid format");
        }
        
        if(r.getName() == null || r.getName().isEmpty())
        {
            throw new InvalidArgumentException("The realm name is invalid", "The realm name is null or empty");
        }
                
        Realm realm = get(r.getId());                                
        
        realm.setAddress(r.getAddress());
        realm.setPort(r.getPort());
        realm.setName(r.getName());
        realm.setCountPlayers(0);
        realm.setPopulation(0);
        realm.setInvalid(r.isInvalid());
        realm.setMaxPlayers(r.getMaxPlayers());        
        realm.setNewplayers(r.isNewplayers());
        realm.setOffline(r.isOffline());
        realm.setRecommended(r.isRecommended());        
        realm.setShowversion(r.isShowversion());
        realm.setFkRealmtype(rtc.get(r.getFkRealmTypeDTO().getId()));
        realm.setFkTimezone(rtzc.get(r.getFkTimezoneDTO().getId()));
        
        Collection<Account> listAccounts = new ArrayList<>();
        
        for(AccountDTO a : r.getAccountCollection())
        {
            listAccounts.add(ac.getAccount(a.getId()));
        }
        
        realm.setAccountCollection(listAccounts);
        
        this.em.merge(realm);
        
        return realm;
    }
    
    /**
     * Delete a realm with the given ID.
     * @param id The id of the realm to be deleted.
     * @throws DatabaseIntegrityException If no realm with that ID exists in the database.
     */
    public void delete(int id) throws DatabaseIntegrityException
    {
        Realm realm = get(id);
        
        this.em.remove(realm);
    }
}
