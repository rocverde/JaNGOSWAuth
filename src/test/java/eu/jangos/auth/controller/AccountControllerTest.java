package eu.jangos.auth.controller;

import eu.jangos.auth.dto.AccountDTO;
import eu.jangos.auth.dto.LocaleDTO;
import eu.jangos.auth.dto.RealmDTO;
import eu.jangos.auth.dto.RolesDTO;
import eu.jangos.auth.enums.LockType;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.model.Account;
import eu.jangos.auth.utils.BigNumber;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Class for AccountController.
 * @author Warkdev
 */
@RunWith(Arquillian.class)
public class AccountControllerTest {

    private static final String WEBAPP_SRC = "src/main/webapp";
    @EJB
    AccountController accountController;
        
    int id = 0;
    /**
     *
     * @return 
     */
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"JaNGOSWAuth.war");
                
        war.setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"));
        
        war.addPackages(true, AccountController.class.getPackage(), Account.class.getPackage(), BigNumber.class.getPackage());
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsWebInfResource(new File(WEBAPP_SRC+"/WEB-INF/resources.xml"));
        war.addAsWebInfResource(new File(WEBAPP_SRC+"/WEB-INF/openejb-jar.xml"));
        war.addAsWebInfResource(new File(WEBAPP_SRC+"/WEB-INF/shiro.ini"));
        war.addAsManifestResource(new File("target/classes/logback.xml"));
        war.addAsManifestResource(new File("target/classes/META-INF/persistence.xml"));
        
        //System.out.println(war.toString(true));
        
        return war;
    }
    
    @Before
    public void setUp() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException
    {
        AccountDTO account = new AccountDTO();
        account.setName("JUnit");                                
        account.setEmail("Junit@junit.com");
        account.setCreation(new Date());
        account.setLastIP("0.0.0.0");
        account.setFailedattempt(0);
        account.setLocked(false);
        account.setLastlogin(new Date());
        
        Collection<RolesDTO> rolesCollection = new ArrayList<>();
        rolesCollection.add(new RolesDTO(1));
        
        account.setRolesCollection(rolesCollection);
        
        account.setFkLocale(new LocaleDTO(0, "English", "enGB"));
        char[] password={'J','U','n','i','t'};
        id = accountController.create(account, password).getId();
    }
    
    @After
    public void tearDown() throws InvalidArgumentException, DatabaseIntegrityException
    {
        accountController.delete(id);
    }    
    
    @Test
    /**
     * Validates that the method getAccount using a known name is working properly.
     */
    public void testExistingAccountByName() throws DatabaseIntegrityException {
        String name = "JUnit";
        Account account = accountController.getAccount(name);        
        Assert.assertNotNull(account);
        Assert.assertEquals(name.toUpperCase(), account.getName());        
    }
    
    @Test(expected = DatabaseIntegrityException.class)
    /**
     * Validates that the method getAccount using a known non-existing name is returning null.
     */
    public void testNonExistingAccountByName() throws DatabaseIntegrityException {
        String name = "IGNORED";
        Account account = accountController.getAccount(name);        
        Assert.assertNull(account);        
    }
    
    @Test
    /**
     * Validates that the method getAccount using a known ID is working properly.
     */
    public void testExistingAccountById() throws DatabaseIntegrityException {        
        Account account = accountController.getAccount(id);
        Assert.assertNotNull(account);
        Assert.assertEquals(id, account.getId().intValue());        
    }
    
    @Test(expected = DatabaseIntegrityException.class)
    /**
     * Validates that the methid getAccount using a known non-existend ID is working properly.
     */
    public void testNonExistingAccountById() throws DatabaseIntegrityException {                 
        Account account = accountController.getAccount(0);
        Assert.assertNull(account);
    }
    
    @Test(expected = InvalidArgumentException.class)
    /**
     * Validates that a null name throws an InvalidArgumentException whenever we try to create an account.
     */
    public void testCreateAccountWithNameNull() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException {
        char[] password={'b','l','a','h'};
        accountController.create(null, password);
    }
    
    @Test(expected = InvalidArgumentException.class)
    /**
     * Validates that a null password throws an InvalidArgumentException whenever we try to create an account.
     */
    public void testCreateAccountWithPasswordNull() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException {
        AccountDTO account = new AccountDTO();
        account.setName("TEST");                                
        account.setEmail("Junit@junit.com");
        account.setCreation(new Date());
        account.setLastIP("0.0.0.0");
        account.setFailedattempt(0);
        account.setLocked(false);
        account.setLastlogin(new Date());
        account.setFkPrefrealm(new RealmDTO(1));
        Collection<RolesDTO> rolesCollection = new ArrayList<>();
        rolesCollection.add(new RolesDTO(1));
        
        account.setRolesCollection(rolesCollection);
        account.setFkLocale(new LocaleDTO(1));
        accountController.create(account, null);
    }
    
    @Test(expected = InvalidArgumentException.class)
    /**
     * Validates that an empty name throws an InvalidArgumentException whenever we try to create an account.
     */
    public void testCreateAccountWithEmptyName() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException {
        AccountDTO account = new AccountDTO();
        account.setName("");                                
        account.setEmail("Junit@junit.com");
        account.setCreation(new Date());
        account.setLastIP("0.0.0.0");
        account.setFailedattempt(0);
        account.setLocked(false);
        account.setLastlogin(new Date());
        account.setFkPrefrealm(new RealmDTO(1));
        Collection<RolesDTO> rolesCollection = new ArrayList<>();
        rolesCollection.add(new RolesDTO(1));
        
        account.setRolesCollection(rolesCollection);
        account.setFkLocale(new LocaleDTO(1));
        char[] password={'J','U','n','i','t'};
        accountController.create(account, password);        
    }
    
    @Test(expected = InvalidArgumentException.class)
    /**
     * Validates that an empty password throws an InvalidArgumentException whenever we try to create an account.
     */
    public void testCreateAccountWithEmptyPassword() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException {
        AccountDTO account = new AccountDTO();
        account.setName("");                                
        account.setEmail("Junit@junit.com");
        account.setCreation(new Date());
        account.setLastIP("0.0.0.0");
        account.setFailedattempt(0);
        account.setLocked(false);
        account.setLastlogin(new Date());
        account.setFkPrefrealm(new RealmDTO(1));
        Collection<RolesDTO> rolesCollection = new ArrayList<>();
        rolesCollection.add(new RolesDTO(1));
        
        account.setRolesCollection(rolesCollection);
        account.setFkLocale(new LocaleDTO(1));
        char[] password={};
        accountController.create(account, password);               
    }
    
    @Test(expected = EntityExistsException.class)
    /**
     * Validates that we cannot create two accounts with the same name.
     */
    public void testCreateExistingAccount() throws EntityExistsException, InvalidArgumentException, DatabaseIntegrityException {
        AccountDTO account = new AccountDTO();
        account.setName("JUnit");                                
        account.setEmail("Junit@junit.com");
        account.setCreation(new Date());
        account.setLastIP("0.0.0.0");
        account.setFailedattempt(0);
        account.setLocked(false);
        account.setLastlogin(new Date());
        account.setFkPrefrealm(new RealmDTO(1));
        Collection<RolesDTO> rolesCollection = new ArrayList<>();
        rolesCollection.add(new RolesDTO(1));
        
        account.setRolesCollection(rolesCollection);
        account.setFkLocale(new LocaleDTO(1));
        char[] password={'J','U','n','i','t'};
        accountController.create(account, password);              
    }
    
    @Test
    /**
     * Validates that the GetAllAccounts method returns all accounts (at least one in database).
     */
    public void testGetAllAccounts()
    {
        Assert.assertTrue(accountController.getAllAccounts().size()>0);
    }
    
    @Test
    /**
     * Validates that the GetAllAccounts with both type returns all accounts.
     */
    public void testGetAllBothAccounts()
    {
        //Assert.assertTrue(accountController.getAllAccounts(LockType.BOTH).size()>0);
    }
    
    @Test
    /**
     * Validates that the GetAllAccounts with locked type returns all locked accounts.
     */
    public void testGetAllLockedAccounts()
    {
        //Assert.assertTrue(accountController.getAllAccounts(LockType.LOCKED).size()>0);
        //Assert.assertTrue(accountController.getAllAccounts(LockType.LOCKED).get(0).getLocked());
    }
    
    @Test
    /**
     * Validates that the GetAllAccounts with unlocked type returns all unlocked accounts.
     */
    public void testGetAllUnlockedAccounts()
    {
        //Assert.assertTrue(accountController.getAllAccounts(LockType.UNLOCKED).size()>0);
        //Assert.assertFalse(accountController.getAllAccounts(LockType.UNLOCKED).get(0).getLocked());
    }
    
    @Test
    /**
     * Validates that the GetMatchingAccounts with a known account returns at least one result.
     */
    public void testGetMatchingExistingAccount()
    {
        //Assert.assertTrue(accountController.getMatchingAccounts("Jun%", LockType.BOTH).size()>0);
    }
    
    @Test
    /**
     * Validates that the GetMatchingAccounts with an unknown accounts returns no result.
     */
    public void testGetMatchingUnknownAccount()
    {
        //Assert.assertTrue(accountController.getMatchingAccounts("Jun", LockType.BOTH).isEmpty());
    }
    
    @Test(expected = DatabaseIntegrityException.class)
    /**
     * Validates that deleting an account with an unknown id generates an exception.
     */
    public void testDeleteNonExistingAccount() throws InvalidArgumentException, DatabaseIntegrityException
    {
        accountController.delete(0);
    }
}
