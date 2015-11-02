package eu.jangos.auth.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Warkdev
 */
@Entity
@Table(name = "servers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Servers.findAll", query = "SELECT s FROM Servers s"),
    @NamedQuery(name = "Servers.findById", query = "SELECT s FROM Servers s WHERE s.id = :id"),
    @NamedQuery(name = "Servers.findByName", query = "SELECT s FROM Servers s WHERE s.name LIKE :name"),
    @NamedQuery(name = "Servers.findByKey", query = "SELECT s FROM Servers s WHERE s.key LIKE :key"),
    @NamedQuery(name = "Servers.findByRevoked", query = "SELECT s FROM Servers s WHERE s.revoked = :revoked"),
    @NamedQuery(name = "Servers.findByToken", query = "SELECT s FROM Servers s WHERE s.name LIKE :name AND s.key LIKE :key")
})
public class Servers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @NotNull
    @Lob
    @Size(max = 2147483647)
    @Column(name = "api_key")
    private String key;
    @Basic(optional = false)
    @NotNull
    @Column(name = "revoked")    
    private boolean revoked;
    @JoinTable(name = "servers_roles", joinColumns = {
        @JoinColumn(name = "fk_server", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "fk_roles", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Roles> rolesCollection;
    
    public Servers() {
    }

    public Servers(Integer id) {
        this.id = id;
    }

    public Servers(Integer id, String name, String key, boolean revoked) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.revoked = revoked;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
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
        if (!(object instanceof Servers)) {
            return false;
        }
        Servers other = (Servers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.Servers[ id=" + id + " ]";
    }
    
}
