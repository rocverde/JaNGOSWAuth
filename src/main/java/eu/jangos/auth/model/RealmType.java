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
@Table(name = "realmtype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RealmType.findAll", query = "SELECT r FROM RealmType r"),
    @NamedQuery(name = "RealmType.findById", query = "SELECT r FROM RealmType r WHERE r.id = :id"),
    @NamedQuery(name = "RealmType.findByType", query = "SELECT r FROM RealmType r WHERE r.type = :type")})
public class RealmType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 30)
    @Column(name = "type")
    private String type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkRealmtype")
    private Collection<Realm> realmCollection;

    public RealmType() {
    }

    public RealmType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(object instanceof RealmType)) {
            return false;
        }
        RealmType other = (RealmType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.RealmType[ id=" + id + " ]";
    }
    
}
