package eu.jangos.auth.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Cedri
 */
@Embeddable
public class RealmAccountPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_account")
    private int fkAccount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_realm")
    private int fkRealm;

    public RealmAccountPK() {
    }

    public RealmAccountPK(int fkAccount, int fkRealm) {
        this.fkAccount = fkAccount;
        this.fkRealm = fkRealm;
    }

    public int getFkAccount() {
        return fkAccount;
    }

    public void setFkAccount(int fkAccount) {
        this.fkAccount = fkAccount;
    }

    public int getFkRealm() {
        return fkRealm;
    }

    public void setFkRealm(int fkRealm) {
        this.fkRealm = fkRealm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkAccount;
        hash += (int) fkRealm;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RealmAccountPK)) {
            return false;
        }
        RealmAccountPK other = (RealmAccountPK) object;
        if (this.fkAccount != other.fkAccount) {
            return false;
        }
        if (this.fkRealm != other.fkRealm) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.RealmAccountPK[ fkAccount=" + fkAccount + ", fkRealm=" + fkRealm + " ]";
    }
    
}
