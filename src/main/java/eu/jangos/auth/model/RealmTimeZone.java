package eu.jangos.auth.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
 * @author Cedri
 */
@Entity
@Table(name = "realmtimezone")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RealmTimeZone.findAll", query = "SELECT r FROM RealmTimeZone r"),
    @NamedQuery(name = "RealmTimeZone.findById", query = "SELECT r FROM RealmTimeZone r WHERE r.id = :id"),
    @NamedQuery(name = "RealmTimeZone.findByName", query = "SELECT r FROM RealmTimeZone r WHERE r.name = :name")})
public class RealmTimeZone implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 25)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkTimezone")
    private Collection<Realm> realmCollection;

    public RealmTimeZone() {
    }

    public RealmTimeZone(Integer id) {
        this.id = id;
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

    @XmlTransient
    public Collection<Realm> getRealmCollection() {
        return realmCollection;
    }

    public void setRealmCollection(Collection<Realm> realmCollection) {
        this.realmCollection = realmCollection;
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
        if (!(object instanceof RealmTimeZone)) {
            return false;
        }
        RealmTimeZone other = (RealmTimeZone) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.RealmTimeZone[ id=" + id + " ]";
    }
    
}
