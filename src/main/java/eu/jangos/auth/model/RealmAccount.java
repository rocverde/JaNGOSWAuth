package eu.jangos.auth.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Warkdev
 */
@Entity
@Table(name = "realm_account")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RealmAccount.findAll", query = "SELECT r FROM RealmAccount r"),
    @NamedQuery(name = "RealmAccount.findByFkAccount", query = "SELECT r FROM RealmAccount r WHERE r.realmAccountPK.fkAccount = :fkAccount"),
    @NamedQuery(name = "RealmAccount.findByFkRealm", query = "SELECT r FROM RealmAccount r WHERE r.realmAccountPK.fkRealm = :fkRealm"),
    @NamedQuery(name = "RealmAccount.findByNumChars", query = "SELECT r FROM RealmAccount r WHERE r.numChars = :numChars")})
public class RealmAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId    
    protected RealmAccountPK realmAccountPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "numChars")
    private int numChars;
    @JoinColumn(name = "fk_account", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Account account;
    @JoinColumn(name = "fk_realm", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Realm realm;

    public RealmAccount() {
    }

    public RealmAccount(RealmAccountPK realmAccountPK) {
        this.realmAccountPK = realmAccountPK;
    }

    public RealmAccount(RealmAccountPK realmAccountPK, int numChars) {
        this.realmAccountPK = realmAccountPK;
        this.numChars = numChars;
    }

    public RealmAccount(int fkAccount, int fkRealm) {
        this.realmAccountPK = new RealmAccountPK(fkAccount, fkRealm);
    }

    public RealmAccountPK getRealmAccountPK() {
        return realmAccountPK;
    }

    public void setRealmAccountPK(RealmAccountPK realmAccountPK) {
        this.realmAccountPK = realmAccountPK;
    }

    public int getNumChars() {
        return numChars;
    }

    public void setNumChars(int numChars) {
        this.numChars = numChars;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (realmAccountPK != null ? realmAccountPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RealmAccount)) {
            return false;
        }
        RealmAccount other = (RealmAccount) object;
        if ((this.realmAccountPK == null && other.realmAccountPK != null) || (this.realmAccountPK != null && !this.realmAccountPK.equals(other.realmAccountPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.RealmAccount[ realmAccountPK=" + realmAccountPK + " ]";
    }
    
}
