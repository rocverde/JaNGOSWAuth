package eu.jangos.auth.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Warkdev
 */
@Entity
@Table(name = "bannedaccount")
@XmlRootElement
@NamedQueries({  
    @NamedQuery(name = "BannedAccount.findAll", query = "SELECT b FROM BannedAccount b"),
    @NamedQuery(name = "BannedAccount.findById", query = "SELECT b FROM BannedAccount b WHERE b.id = :id"),
    @NamedQuery(name = "BannedAccount.findByBandate", query = "SELECT b FROM BannedAccount b WHERE b.bandate = :bandate"),
    @NamedQuery(name = "BannedAccount.findByUnban", query = "SELECT b FROM BannedAccount b WHERE b.unban = :unban"),
    @NamedQuery(name = "BannedAccount.findByReason", query = "SELECT b FROM BannedAccount b WHERE b.reason = :reason"),
    @NamedQuery(name = "BannedAccount.findByActive", query = "SELECT b FROM BannedAccount b WHERE b.active = :active"),
    @NamedQuery(name = "BannedAccount.findActiveBan", query = "SELECT b FROM BannedAccount b WHERE b.fkBannedAccount = :account AND b.active = TRUE"),
    @NamedQuery(name = "BannedAccount.findActiveBanByID", query = "SELECT b FROM BannedAccount b WHERE b.fkBannedAccount.id = :id AND b.active = TRUE")
})
public class BannedAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "bandate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date banDate;
    @Column(name = "unban")
    @Temporal(TemporalType.TIMESTAMP)
    private Date unban;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "reason")
    private String reason;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @JoinColumn(name = "fk_bannedaccount", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Account fkBannedAccount;
    @JoinColumn(name = "fk_bannedby", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Account fkBannedBy;

    public BannedAccount() {
    }

    public BannedAccount(Integer id) {
        this.id = id;
    }

    public BannedAccount(Integer id, Date banDate, String reason, boolean active) {
        this.id = id;
        this.banDate = banDate;
        this.reason = reason;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getBanDate() {
        return banDate;
    }

    public void setBanDate(Date banDate) {
        this.banDate = banDate;
    }

    public Date getUnban() {
        return unban;
    }

    public void setUnban(Date unban) {
        this.unban = unban;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Account getFkBannedAccount() {
        return fkBannedAccount;
    }

    public void setFkBannedAccount(Account fkBannedAccount) {
        this.fkBannedAccount = fkBannedAccount;
    }

    public Account getFkBannedBy() {
        return fkBannedBy;
    }

    public void setFkBannedBy(Account fkBannedBy) {
        this.fkBannedBy = fkBannedBy;
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
        if (!(object instanceof BannedAccount)) {
            return false;
        }
        BannedAccount other = (BannedAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.BannedAccount[ id=" + id + " ]";
    }

}
