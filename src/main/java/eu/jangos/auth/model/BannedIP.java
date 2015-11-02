/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Cedri
 */
@Entity
@Table(name = "bannedip")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BannedIP.findAll", query = "SELECT b FROM BannedIP b"),
    @NamedQuery(name = "BannedIP.findById", query = "SELECT b FROM BannedIP b WHERE b.id = :id"),
    @NamedQuery(name = "BannedIP.findByIp", query = "SELECT b FROM BannedIP b WHERE b.ip = :ip"),
    @NamedQuery(name = "BannedIP.findByDate", query = "SELECT b FROM BannedIP b WHERE b.date = :date"),
    @NamedQuery(name = "BannedIP.findByUnban", query = "SELECT b FROM BannedIP b WHERE b.unban = :unban"),
    @NamedQuery(name = "BannedIP.findByReason", query = "SELECT b FROM BannedIP b WHERE b.reason = :reason"),
    @NamedQuery(name = "BannedIP.findByActive", query = "SELECT b FROM BannedIP b WHERE b.active = :active"),
    @NamedQuery(name = "BannedIP.findAllInactiveBan", query = "SELECT b FROM BannedIP b WHERE b.active = FALSE"),
    @NamedQuery(name = "BannedIP.findActiveBan", query = "SELECT b FROM BannedIP b WHERE b.ip = :ip AND b.active = TRUE"),
    @NamedQuery(name = "BannedIP.findAllActiveBan", query = "SELECT b FROM BannedIP b WHERE b.active = TRUE")
})
public class BannedIP implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "ip")
    private String ip;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
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
    @JoinColumn(name = "fk_bannedby", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Account fkBannedBy;

    public BannedIP() {
    }

    public BannedIP(Integer id) {
        this.id = id;
    }

    public BannedIP(Integer id, String ip, Date date, String reason, boolean active) {
        this.id = id;
        this.ip = ip;
        this.date = date;
        this.reason = reason;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        if (!(object instanceof BannedIP)) {
            return false;
        }
        BannedIP other = (BannedIP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.BannedIP[ id=" + id + " ]";
    }
    
}
