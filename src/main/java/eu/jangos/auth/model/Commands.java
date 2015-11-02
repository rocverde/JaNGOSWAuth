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
@Table(name = "commands")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Commands.findAll", query = "SELECT c FROM Commands c"),        
    @NamedQuery(name = "Commands.findById", query = "SELECT c FROM Commands c WHERE c.id = :id"),      
    @NamedQuery(name = "Commands.findByName", query = "SELECT c FROM Commands c WHERE c.name LIKE :name")    
})
public class Commands implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "description")
    private String description;

    public Commands() {
    }

    public Commands(Integer id) {
        this.id = id;
    }   
    
    public Commands(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof Commands)) {
            return false;
        }
        Commands other = (Commands) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eu.jangos.auth.model.Commands[ id=" + id + " ]";
    }
    
}
