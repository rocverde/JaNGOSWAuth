package eu.jangos.auth.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Warkdev
 */
@Entity
@Table(name = "account")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
    @NamedQuery(name = "Account.findById", query = "SELECT a FROM Account a WHERE a.id = :id"),
    @NamedQuery(name = "Account.findByName", query = "SELECT a FROM Account a WHERE a.name LIKE :name"),
    @NamedQuery(name = "Account.findByHashPass", query = "SELECT a FROM Account a WHERE a.hashPass = :hashPass"),
    @NamedQuery(name = "Account.findByToken", query = "SELECT a FROM Account a WHERE a.token = :token"),
    @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(name = "Account.findByCreation", query = "SELECT a FROM Account a WHERE a.creation = :creation"),
    @NamedQuery(name = "Account.findByLastIP", query = "SELECT a FROM Account a WHERE a.lastIP = :lastIP"),
    @NamedQuery(name = "Account.findByFailedattempt", query = "SELECT a FROM Account a WHERE a.failedattempt = :failedattempt"),
    @NamedQuery(name = "Account.findByLocked", query = "SELECT a FROM Account a WHERE a.locked = :locked"),
    @NamedQuery(name = "Account.findByLastlogin", query = "SELECT a FROM Account a WHERE a.lastlogin = :lastlogin"),
    @NamedQuery(name = "Account.findByNameLocked", query = "SELECT a FROM Account a WHERE a.name LIKE :name AND a.locked = TRUE"),
    @NamedQuery(name = "Account.findByNameUnlocked", query = "SELECT a FROM Account a WHERE a.name LIKE :name AND a.locked = FALSE"),
    @NamedQuery(name = "Account.findLoggedIn", query = "SELECT a FROM Account a WHERE a.name LIKE : name AND a.id = :id AND a.token LIKE :token")
})
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "hash_pass")
    private String hashPass;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "sessionkey")
    private String sessionkey;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "verifier")
    private String verifier;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "salt")
    private String salt;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "token")
    private String token;    
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "lastIP")
    private String lastIP;
    @Basic(optional = false)
    @NotNull
    @Column(name = "failedattempt")
    private int failedattempt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "locked")    
    private boolean locked;
    @Basic(optional = false)
    @NotNull
    @Column(name = "lastlogin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogin;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private Collection<RealmAccount> realmAccountCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkBannedBy")
    private Collection<BannedIP> bannedIPCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkBannedAccount")
    private Collection<BannedAccount> banRecordsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkBannedBy")
    private Collection<BannedAccount> accountBannedCollection;    
    @JoinColumn(name = "fk_locale", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Locale fkLocale;
    @JoinColumn(name = "fk_prefrealm", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Realm fkPrefrealm;
    @JoinTable(name = "account_roles", joinColumns = {
        @JoinColumn(name = "fk_account", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_roles", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Roles> rolesCollection;

    public Account() {
    }

    public Account(Integer id) {
        this.id = id;
    }

    public Account(Integer id, String name, String hashPass, Date creation, String lastIP, int failedattempt, boolean locked, Date lastlogin) {
        this.id = id;
        this.name = name;
        this.hashPass = hashPass;
        this.creation = creation;
        this.lastIP = lastIP;
        this.failedattempt = failedattempt;
        this.locked = locked;
        this.lastlogin = lastlogin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashPass() {
        return hashPass;
    }

    public void setHashPass(String hashPass) {
        this.hashPass = hashPass;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }   
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public int getFailedattempt() {
        return failedattempt;
    }

    public void setFailedattempt(int failedattempt) {
        this.failedattempt = failedattempt;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    @XmlTransient
    public Collection<RealmAccount> getRealmAccountCollection() {
        return realmAccountCollection;
    }

    public void setRealmAccountCollection(Collection<RealmAccount> realmAccountCollection) {
        this.realmAccountCollection = realmAccountCollection;
    }

    @XmlTransient
    public Collection<BannedIP> getBannedIPCollection() {
        return bannedIPCollection;
    }

    public void setBannedIPCollection(Collection<BannedIP> bannedIPCollection) {
        this.bannedIPCollection = bannedIPCollection;
    }

    @XmlTransient
    public Collection<BannedAccount> getBanRecordsCollection() {
        return banRecordsCollection;
    }

    public void setBanRecordsCollection(Collection<BannedAccount> banRecordsCollection) {
        this.banRecordsCollection = banRecordsCollection;
    }

    @XmlTransient
    public Collection<BannedAccount> getAccountBannedCollection() {
        return accountBannedCollection;
    }

    public void setAccountBannedCollection(Collection<BannedAccount> accountBannedCollection) {
        this.accountBannedCollection = accountBannedCollection;
    }

    public Locale getFkLocale() {
        return fkLocale;
    }

    public void setFkLocale(Locale fkLocale) {
        this.fkLocale = fkLocale;
    }

    public Realm getFkPrefrealm() {
        return fkPrefrealm;
    }

    public void setFkPrefrealm(Realm fkPrefrealm) {
        this.fkPrefrealm = fkPrefrealm;
    }

    public Collection<Roles> getRolesCollection() {
        return rolesCollection;
    }

    public void setRolesCollection(Collection<Roles> rolesCollection) {
        this.rolesCollection = rolesCollection;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.Account[ id=" + id + " ]";
    }
    
}
