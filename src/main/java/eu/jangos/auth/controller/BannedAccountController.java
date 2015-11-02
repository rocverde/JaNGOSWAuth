package eu.jangos.auth.controller;

import eu.jangos.auth.model.Account;
import eu.jangos.auth.model.BannedAccount;
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
public class BannedAccountController {

    private static final Logger logger = LoggerFactory.getLogger(BannedAccountController.class);

    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;

    /**
     * This method checks whether an account is banned or not into the database.
     *
     * @param id The ID of the account to be checked.
     * @return true if the account is banned, false otherwise.
     */
    public boolean isAccountBanned(int id) {                
        try {
            this.em.createNamedQuery("BannedAccount.findActiveBanByID").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("Account with ID " + id + " is not banned.");
            return false;
        }

        return true;
    }
    
    /**
     * This method checks whether an account is banned or not into the database.
     *
     * @param account The account to be checked.
     * @return true if the account is banned, false otherwise.
     */
    public boolean isAccountBanned(Account account) {
        if (account == null) {
            logger.debug("Account parameter is null. ban = true.");
            return true;
        }

        try {
            this.em.createNamedQuery("BannedAccount.findActiveBan").setParameter("account", account).getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("Account " + account.getName() + " is not banned.");
            return false;
        }

        logger.error("Account " + account.getName() + " tried to log in but is banned.");
        return true;
    }

    /**
     * This methid will ban the account given in parameter.
     *
     * @param account The account to be ban.
     * @param bannedBy The account of the banisher.
     * @param reason The reason why this account is ban.
     * @param days The number of days before this account will be unban.
     */
    public void banAccount(Account account, Account bannedBy, String reason, int days) {
        BannedAccount bannedAccount = null;

        // If an account is already banned, the duration is renewed.
        try{
            bannedAccount = (BannedAccount) this.em.createNamedQuery("BannedAccount.findActiveBan").setParameter("account", account).getSingleResult();
        } catch (Exception e) {
            bannedAccount = new BannedAccount();
        }
        
        bannedAccount.setActive(true);
        bannedAccount.setBanDate(new Date());
        if (days == 0) {
            bannedAccount.setUnban(null);
        } else {
            bannedAccount.setUnban(Utils.addDays(new Date(), days));
        }
        bannedAccount.setFkBannedBy(bannedBy);
        bannedAccount.setFkBannedAccount(account);
        bannedAccount.setReason(reason);
        
        this.em.persist(bannedAccount);
    }

    /**
     * This method will unban the account given in parameter.
     *
     * @param account The account to be unban.
     */
    public void unbanAccount(Account account) throws Exception {
        BannedAccount bannedAccount = null;

        try {
            bannedAccount = (BannedAccount) this.em.createNamedQuery("BannedAccount.findActiveBan").setParameter("account", account).getSingleResult();
        } catch (Exception e) {
            logger.debug("Exception raised while querying the database");
            throw e;
        }

        bannedAccount.setActive(false);
        this.em.merge(bannedAccount);
    }

    /**
     * Return the banned account corresponding to the given ID.
     *
     * @param id
     * @return
     */
    public BannedAccount getBannedAccount(int id) {
        BannedAccount bannedAccount = null;
        try {
            bannedAccount = (BannedAccount) this.em.createNamedQuery("BannedAccount.findById").setParameter("id", id).getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("Exception raised while querying the database");
        }
        return bannedAccount;
    }

    /**
     * Return the list of all active banned accounts in the database.
     *
     * @return All the banned accounts.
     */
    public List<BannedAccount> getAllBannedAccounts() {
        List<BannedAccount> listBannedAccounts = null;

        try {
            listBannedAccounts = this.em.createNamedQuery("BannedAccount.findAllActiveBans").getResultList();
        } catch (Exception e) {
            logger.debug("Exception raised while querying the database");
        }

        return listBannedAccounts;
    }

}
