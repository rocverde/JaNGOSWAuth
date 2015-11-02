package eu.jangos.auth.controller;

import eu.jangos.auth.dto.AccountDTO;
import eu.jangos.auth.dto.RolesDTO;
import eu.jangos.auth.enums.DateType;
import eu.jangos.auth.model.Account;
import eu.jangos.auth.utils.BigNumber;
import eu.jangos.auth.enums.LockType;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Locale;
import eu.jangos.auth.model.Realm;
import eu.jangos.auth.model.Roles;
import eu.jangos.auth.utils.Utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Warkdev
 */
@Stateless
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    @PersistenceContext(name = "AuthPU")    
    private EntityManager em;
    
    @EJB
    private LocaleController ls;
    @EJB
    private RealmController rs;
    @EJB
    private RolesController rc;        
    
    private final MessageDigest md;
        
    public AccountController() throws NoSuchAlgorithmException{
        this.md = MessageDigest.getInstance("SHA1");
    }   
    
    /**
     * Create an account based on the given parameters.
     * @param accountDTO The account to be created.       
     * @param password The clear-text password of the account.
     * @return The generated account.
     * @throws EntityExistsException if the name is already taken.
     * @throws InvalidArgumentException if the submitted arguments are invalids.
     * @throws DatabaseIntegrityException If one of the foreign key constraint is not valid.
     */
    public Account create(AccountDTO accountDTO, char [] password) throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException
    {
        // Checking input.
        if(accountDTO == null || accountDTO.getName() == null || accountDTO.getName().isEmpty())
        {
            logger.debug("Account can't be created, inputs are null.");
            throw new InvalidArgumentException("Account name is null or empty.", "The name parameter of the submitted account is null or empty.");
        }
        
        if(password == null || password.length < 1)
        {
            logger.debug("Account can't be created, inputs are invalid.");
            throw new InvalidArgumentException("Password is null or empty.", "The password parameter is null or empty.");
        }
        
        try
        {
            getAccount(accountDTO.getName());
            throw new EntityExistsException("An account with this name already exists");
        } catch (DatabaseIntegrityException die) {
            
        }        
        
        String user = accountDTO.getName().toUpperCase() + ":" + new String(password).toUpperCase();
        
        this.md.update(user.getBytes());
        
        Account account = new Account();
                        
        account.setName(accountDTO.getName().toUpperCase());                        
        account.setHashPass(new BigNumber(this.md.digest()).toHexString());
        account.setCreation(new Date());
        account.setEmail(accountDTO.getEmail());
        account.setFailedattempt(0);
        account.setLastIP("0.0.0.0");
        account.setLastlogin(new Date());
        account.setLocked(false);                   
        
        Collection<Roles> rolesCollection = new ArrayList<>();
        
        for(RolesDTO r : accountDTO.getRolesCollection())
        {
            rolesCollection.add(this.rc.get(r.getId()));
        }
                    
        account.setRolesCollection(rolesCollection);
        
        account.setFkLocale(this.ls.getLocaleForString(accountDTO.getFkLocale().getLocaleString()));
        account.setFkPrefrealm(null);
                       
        this.em.persist(account);
        return account;
    }
    
    /**
     * Delete the account corresponding to the given ID.
     * @param id The ID of the account to be deleted.
     * @throws InvalidArgumentException if the submitted ID does not exists.
     * @throws DatabaseIntegrityException if none account with the given ID exists.
     */
    public void delete(int id) throws InvalidArgumentException, DatabaseIntegrityException
    {
        Account account = getAccount(id);
        
        this.em.remove(account);                
    }
    
    /**
     * Performs login update with the client information.     
     * @param name Name of the account to be logged in.
     * @param ip IP of the client who just logged in.
     * @param locale Locale of the client who just logged in.
     * @param session Hashed session key of the client who just logged in.
     * @return The updated account.
     * @throws InvalidArgumentException in case of error during parameters validation.
     * @throws eu.jangos.auth.exception.DatabaseIntegrityException
     */
    public Account login(String name, String ip, String locale, String session) throws InvalidArgumentException, DatabaseIntegrityException {                
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("Name is null or empty", "The submitted name "+name+" parameter is null or empty.");
        }
        
        if(locale == null || locale.isEmpty())
        {
            throw new InvalidArgumentException("Locale is null or empty", "The submitted locale "+locale+" parameter is null or empty.");
        }
        
        if(ip == null || ip.isEmpty() || !Utils.isValidIPv4(ip))
        {
            throw new InvalidArgumentException("IP is null, empty or invalid", "The submitted ip "+ip+" parameter is null, empty or in an invalid format.");
        }
        
        if(session == null || session.isEmpty())
        {
            throw new InvalidArgumentException("Session is null or empty", "The submitted session "+session+"parameter is null or empty.");
        }
        
        Account account = getAccount(name);                
        
        if(account == null)
        {
            throw new InvalidArgumentException("Unknown account", "The submitted accout is unknown, make sure that you have sent valid parameters.");
        }
        
        Locale loc = this.ls.getLocaleForString(locale);
        
        if(loc == null)
        {
            throw new InvalidArgumentException("Unknown locale", "The submitted locale is unknown, make sure that you have sent valid parameters.");
        }
        
        account.setFailedattempt(0);
        account.setLastIP(ip);
        account.setLastlogin(new Date());                
        account.setFkLocale(loc);
        account.setSessionkey(session);
        
        em.merge(account);
        
        logger.info("User "+account.getName() + " just logged in successfully from ip "+ip);
        return account;
    }        
    
    /**
     * Performs the login update to know into which realm the user has logged in.     
     * @param name Name of the account to be logged in.
     * @param realmString Realm used by the client to log in.
     * @return The updated account.
     * @throws DatabaseIntegrityException if the account or the realm has not be found into the database.
     * @throws InvalidArgumentException if the submitted parameters are invalid.
     */
    public Account loginRealm(String name, String realmString) throws DatabaseIntegrityException, InvalidArgumentException
    {
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("Name of the account is null or empty.", "The submitted account name is invalid.");
        }
        
        if(realmString == null || realmString.isEmpty())
        {
            throw new InvalidArgumentException("Name of the realm is null or empty.", "The submitted realm name is invalid.");
        }
        
        Account account = getAccount(name);                
        
        if(account == null)
        {
            throw new DatabaseIntegrityException("Account with name "+name+" not found.");
        }
        
        Realm realm = this.rs.get(realmString);
        
        if(realm == null)
        {
            throw new DatabaseIntegrityException("Realm with name "+name+" not found.");
        }
        
        account.setFkPrefrealm(realm);
        
        em.merge(account);
        
        logger.info("User "+account.getName()+ " just logged in realm "+account.getFkPrefrealm());
        return account;
    }
    
    /**
     * Provide the stored account in database.
     * @param id The id of the account to find.
     * @return The corresponding account or null if the account does not exist.
     * @throws DatabaseIntegrityException
     */
    public Account getAccount(int id) throws DatabaseIntegrityException
    {
        Account account = null;
        
        try{
            account = (Account) this.em.createNamedQuery("Account.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No account found with the id "+id);
        }
        
        return account;
    }
    
    /**
     * Provide the stored account in database.
     * @param name The name of the account to find.
     * @return The corresponding account or null if the account does not exist.
     * @throws eu.jangos.auth.exception.DatabaseIntegrityException
     */
    public Account getAccount(String name) throws DatabaseIntegrityException
    {
        Account account = null;                
                
        try{
            account = (Account) this.em.createNamedQuery("Account.findByName").setParameter("name", name).getSingleResult();
        } catch (NoResultException nre) {
            throw new DatabaseIntegrityException("No account found with the name "+name);
        }
        
        return account;
    }
    
    /**
     * Provide a list of accounts matching to the given name.
     * @param name The name of the account(s) to be found.
     * @param lock The type of lock to be found.
     * @param cdType The type of filter to be applied for the creation date.
     * @param creationFrom The starting date for the creation date.
     * @param creationTo The ending date for the creation date.
     * @param ldType The type of filter to be applied on the login date.
     * @param loginFrom The starting date for the login date.
     * @param loginTo The ending date for the login date.
     * @return A List of accounts matching the request.
     */
    public List<Account> getMatchingAccounts(String name, LockType lock, DateType cdType, Date creationFrom, Date creationTo, DateType ldType, Date loginFrom, Date loginTo)
    {        
        List<Account> listAccounts = null;       
        
        logger.debug("getMatchingAccounts entered.");
        logger.debug("Parameters: name="+name+", lock="+lock);                                     
        
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> r = cq.from(Account.class);
        cq.select(r);
        Predicate condition = cb.conjunction();             
        
        switch(lock)
        {
            case LOCKED:           
                condition = cb.and(condition, cb.isTrue(r.get("lock")));
                break;
            case UNLOCKED:
                condition = cb.and(condition, cb.isFalse(r.get("lock")));
                break;
            case BOTH:
                break;
            default:
                return listAccounts;                
        }                
               
        switch(cdType)
        {
            case AFTER:
                condition = cb.and(condition, cb.greaterThan(r.get("creation"), creationFrom));
                break;
            case BEFORE:
                condition = cb.and(condition, cb.lessThan(r.get("creation"), creationFrom));
                break;
            case BETWEEN:
                condition = cb.and(condition, cb.between(r.get("creation"), creationFrom, creationTo));
                break;
        }
        
        switch(ldType)
        {
            case AFTER:
                condition = cb.and(condition, cb.greaterThan(r.get("lastlogin"), loginFrom));
                break;
            case BEFORE:
                condition = cb.and(condition, cb.lessThan(r.get("lastlogin"), loginFrom));
                break;
            case BETWEEN:
                condition = cb.and(condition, cb.between(r.get("lastlogin"), loginFrom, loginTo));
                break;
        }
        
        try{                                  
            listAccounts = this.em.createQuery(cq.where(condition)).getResultList();
        } catch (Exception e) {                         
            e.printStackTrace();
            logger.debug("Exception raised while querying the database.");
        }
        
        return listAccounts;
    }
    
    /**
     * Returns the list of all accounts into the database.
     * @return A List of Accounts containing all accouts.
     */
    public List<Account> getAllAccounts(){
        List<Account> listAccounts = null;
        
        try{                       
            listAccounts = this.em.createNamedQuery("Account.findAll").getResultList();            
        } catch (Exception e) {                                    
            logger.debug("Exception raised while querying the database.");
        }
        
        return listAccounts;
    } 
    
    /**
     * Returns the list of all accounts into the database.
     * @param lock The type of lock to be found.
     * @param cdType The type of filter to be applied for the creation date.
     * @param creationFrom The starting date for the creation date.
     * @param creationTo The ending date for the creation date.
     * @param ldType The type of filter to be applied on the login date.
     * @param loginFrom The starting date for the login date.
     * @param loginTo The ending date for the login date.
     * @return A List of Accounts containing all accouts.
     */
    public List<Account> getAllAccounts(LockType lock, DateType cdType, Date creationFrom, Date creationTo, DateType ldType, Date loginFrom, Date loginTo){
        List<Account> listAccounts = null;
        
        logger.debug("getAllAccounts entered: ");
        logger.debug("Parameters: lock="+lock);
        
        try{                       
            listAccounts = getMatchingAccounts("%", lock, cdType, creationFrom, creationTo, ldType, loginFrom, loginTo);
        } catch (Exception e) {                                    
            logger.debug("Exception raised while querying the database.");
        }
        
        logger.debug("getAllAccounts exist");
        
        return listAccounts;
    }   
    
    /**
     * Update the account in the database.
     * @param accountDTO The new data for this account.
     * @return The updated account.
     * @throws InvalidArgumentException
     * @throws DatabaseIntegrityException
     */
    public Account update(AccountDTO accountDTO) throws InvalidArgumentException, DatabaseIntegrityException{
        if(accountDTO == null) {
            throw new InvalidArgumentException("The account to update is null", "");
        }        
        
        Account account = getAccount(accountDTO.getId());        
        
        account.setId(accountDTO.getId());
        account.setName(accountDTO.getName());
        account.setHashPass(accountDTO.getHashPass());
        account.setSessionkey(accountDTO.getSessionkey());
        account.setVerifier(accountDTO.getVerifier());
        account.setSalt(accountDTO.getSalt());
        account.setToken(accountDTO.getToken());
        account.setEmail(accountDTO.getEmail());
        account.setCreation(accountDTO.getCreation());
        account.setLastIP(accountDTO.getLastIP());
        account.setFailedattempt(accountDTO.getFailedattempt());
        account.setLocked(accountDTO.getLocked());
        account.setLastlogin(accountDTO.getLastlogin());
        account.setFkPrefrealm(this.rs.get(accountDTO.getFkPrefrealm().getId()));
        
        Collection<Roles> rolesCollection = new ArrayList<>();
        
        for(RolesDTO r : accountDTO.getRolesCollection())
        {
            rolesCollection.add(this.rc.get(r.getId()));
        }
                    
        account.setRolesCollection(rolesCollection);
        
        account.setFkLocale(this.ls.getLocaleForString(accountDTO.getFkLocale().getLocaleString()));
        
        this.em.merge(account);
        
        return account;
    }

    public Account update(Account account) throws InvalidArgumentException, DatabaseIntegrityException{
        if(account == null) {
            throw new InvalidArgumentException("The account to update is null", "");
        }  
        
        this.em.merge(account);
        
        return account;
    }
    
    /**
     * Lock the account corresponding to the given name.
     * @param name Name of the account to be locked.
     * @throws InvalidArgumentException if there is a problem during the validation of the parameters.
     * @throws DatabaseIntegrityException if no matching account was found in the database.
     * 
     */
    public void lock(String name) throws InvalidArgumentException, DatabaseIntegrityException {
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("The name parameter is empty or null", "");
        }
        
        Account account = getAccount(name);
        
        account.setLocked(true);
        
        this.em.merge(account);
    }
    
    /**
     * Unlock the account corresponding to the given name.
     * @param name Name of the account to be unlocked.
     * @throws InvalidArgumentException if there is a problem during the validation of the parameters.
     * @throws DatabaseIntegrityException if no matching account was found in the database.
     * 
     */
    public void unlock(String name) throws InvalidArgumentException, DatabaseIntegrityException {
        if(name == null || name.isEmpty())
        {
            throw new InvalidArgumentException("The name parameter is empty or null", "");
        }
        
        Account account = getAccount(name);
        
        account.setLocked(false);
        
        this.em.merge(account);
    }
}