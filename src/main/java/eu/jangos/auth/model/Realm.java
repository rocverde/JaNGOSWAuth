package eu.jangos.auth.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Warkdev
 */
@Entity
@Table(name = "realm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Realm.findAll", query = "SELECT r FROM Realm r"),
    @NamedQuery(name = "Realm.findById", query = "SELECT r FROM Realm r WHERE r.id = :id"),
    @NamedQuery(name = "Realm.findByName", query = "SELECT r FROM Realm r WHERE r.name = :name"),
    @NamedQuery(name = "Realm.findByAddress", query = "SELECT r FROM Realm r WHERE r.address = :address"),
    @NamedQuery(name = "Realm.findByPort", query = "SELECT r FROM Realm r WHERE r.port = :port"),
    @NamedQuery(name = "Realm.findByPopulation", query = "SELECT r FROM Realm r WHERE r.population = :population"),
    @NamedQuery(name = "Realm.findByMaxPlayers", query = "SELECT r FROM Realm r WHERE r.maxPlayers = :maxPlayers"),
    @NamedQuery(name = "Realm.findByCountPlayers", query = "SELECT r FROM Realm r WHERE r.countPlayers = :countPlayers")})
public class Realm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Column(name = "port")
    private int port;
    @Basic(optional = false)
    @NotNull
    @Column(name = "population")
    private float population;
    @Basic(optional = false)
    @NotNull
    @Column(name = "maxPlayers")
    private int maxPlayers;
    @Basic(optional = false)
    @NotNull
    @Column(name = "countPlayers")
    private int countPlayers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "realm")
    private Collection<RealmAccount> realmAccountCollection;
    @Basic(optional = false)
    @NotNull
    @Column(name = "invalid")
    private boolean invalid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "offline")
    private boolean offline;
    @Basic(optional = false)
    @NotNull
    @Column(name = "showversion")
    private boolean showversion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "newplayers")
    private boolean newplayers;
    @Basic(optional = false)
    @NotNull
    @Column(name = "recommended")
    private boolean recommended;    
    @JoinColumn(name = "fk_timezone", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RealmTimeZone fkTimezone;
    @JoinColumn(name = "fk_realmtype", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RealmType fkRealmtype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkPrefrealm")
    private Collection<Account> accountCollection;

    public Realm() {
    }

    public Realm(Integer id) {
        this.id = id;
    }

    public Realm(Integer id, String name, String address, int port, float population, int maxPlayers, int countPlayers, boolean invalid, boolean offline, boolean showversion, boolean newplayers, boolean recommended) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.port = port;
        this.population = population;
        this.maxPlayers = maxPlayers;
        this.countPlayers = countPlayers;
        this.invalid = invalid;
        this.offline = offline;
        this.showversion = showversion;
        this.newplayers = newplayers;
        this.recommended = recommended;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getPopulation() {
        return population;
    }

    public void setPopulation(float population) {
        this.population = population;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCountPlayers() {
        return countPlayers;
    }

    public void setCountPlayers(int countPlayers) {
        this.countPlayers = countPlayers;
    }

    @XmlTransient
    public Collection<RealmAccount> getRealmAccountCollection() {
        return realmAccountCollection;
    }

    public void setRealmAccountCollection(Collection<RealmAccount> realmAccountCollection) {
        this.realmAccountCollection = realmAccountCollection;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isShowversion() {
        return showversion;
    }

    public void setShowversion(boolean showversion) {
        this.showversion = showversion;
    }

    public boolean isNewplayers() {
        return newplayers;
    }

    public void setNewplayers(boolean newplayers) {
        this.newplayers = newplayers;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public RealmTimeZone getFkTimezone() {
        return fkTimezone;
    }

    public void setFkTimezone(RealmTimeZone fkTimezone) {
        this.fkTimezone = fkTimezone;
    }

    public RealmType getFkRealmtype() {
        return fkRealmtype;
    }

    public void setFkRealmtype(RealmType fkRealmtype) {
        this.fkRealmtype = fkRealmtype;
    }

    @XmlTransient
    public Collection<Account> getAccountCollection() {
        return accountCollection;
    }

    public void setAccountCollection(Collection<Account> accountCollection) {
        this.accountCollection = accountCollection;
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
        if (!(object instanceof Realm)) {
            return false;
        }
        Realm other = (Realm) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.Realm[ id=" + id + " ]";
    }
    
}
