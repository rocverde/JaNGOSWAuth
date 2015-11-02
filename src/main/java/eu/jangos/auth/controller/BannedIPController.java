package eu.jangos.auth.controller;

import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Account;
import eu.jangos.auth.model.BannedIP;
import eu.jangos.auth.utils.Utils;
import java.util.Date;
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
public class BannedIPController {
    
    private static final Logger logger = LoggerFactory.getLogger(BannedIPController.class);
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;        

    /**
     * Returns the list of all active banned IP's.
     * @return A List of all banned IP's.
     */
    public List<BannedIP> getAllActiveBannedIPs()
    {
        List<BannedIP> listIP = null;
        
        try
        {
            listIP = this.em.createNamedQuery("BannedIP.findAllActiveBan").getResultList();
        } catch (NoResultException nre) {
            logger.debug("Exception raised while querying the database.");
        }
        
        return listIP;
    }
    
    /**
     * Returns the list of all inactive banned IP's.
     * @return A List of all banned IP's.
     */
    public List<BannedIP> getAllInactiveBannedIPs()
    {
        List<BannedIP> listIP = null;
        
        try
        {
            listIP = this.em.createNamedQuery("BannedIP.findAllInactiveBan").getResultList();
        } catch (NoResultException nre) {
            logger.debug("Exception raised while querying the database.");
        }
        
        return listIP;
    }
    
    
    
    /**
     * Returns the list of all banned IP's records
     * @return A list of all records.
     */
    public List<BannedIP> getAll()
    {
        List<BannedIP> listIP = null;
        
        try
        {
            listIP = this.em.createNamedQuery("BannedIP.findAll").getResultList();
        } catch (NoResultException nre) {
            logger.debug("Exception raised while querying the database.");
        }
        
        return listIP;
    }
    
    /**
     * Ban the IP provided in parameter.
     * @param ip The IP address to be ban.
     * @param bannedBy The banner account.
     * @param reason The reason of the ban.
     * @param days The duration of the ban.
     */
    public void banIP(String ip, Account bannedBy, String reason, int days)
    {
        BannedIP bannedIP = null;
        
        try{
            bannedIP = (BannedIP) this.em.createNamedQuery("BannedIP.findActiveBan").setParameter("ip",ip).getSingleResult();
        } catch (Exception e) {
            bannedIP = new BannedIP();
        }
            
        bannedIP.setIp(ip);
        bannedIP.setActive(true);
        bannedIP.setDate(new Date());
        bannedIP.setFkBannedBy(bannedBy);
        if (days == 0) {
            bannedIP.setUnban(null);
        } else {
            bannedIP.setUnban(Utils.addDays(new Date(), days));
        }
        bannedIP.setReason(reason);
        
        this.em.persist(bannedIP);
    }
    
    /**
     * Unban the given ip address.
     * @param ip The ip address to be unban.
     */
    public void unbanIP(String ip) throws Exception
    {
        BannedIP bannedIP = null;
        
        try{
            bannedIP = (BannedIP) this.em.createNamedQuery("BannedIP.findActiveBan").setParameter("ip",ip).getSingleResult();
        } catch (Exception e) {
            throw e;
        }
                    
        bannedIP.setActive(false);               
        this.em.merge(bannedIP);
    }
    
    /**
     * Check whether an IP is banned or not.
     * @param ip The IP to be checked.
     * @return True if the IP is banned, false otherwise.
     * @throws InvalidArgumentException If the IP does not have the right format.
     */
    public boolean isIPBanned(String ip) throws InvalidArgumentException {                       
        if(ip == null || ip.isEmpty() || !Utils.isValidIPv4(ip))
        {
            throw new InvalidArgumentException("There was an error during the validation of the parameters"
                    , "The format of the given IP "+ip+" is not a valid IPv4 format.");
        }
        
        try{
            this.em.createNamedQuery("BannedIP.findActiveBan").setParameter("ip", ip).getSingleResult();            
        } catch(NoResultException nre) {
            return false;
        }
                
        return true;
    }
}
