package eu.jangos.auth.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Cedri
 */
@Entity
@Table(name = "parameter1")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Parameter1.findAll", query = "SELECT p FROM Parameter1 p"),
    @NamedQuery(name = "Parameter1.findById", query = "SELECT p FROM Parameter1 p WHERE p.id = :id"),
    @NamedQuery(name = "Parameter1.findByParam", query = "SELECT p FROM Parameter1 p WHERE p.param = :param"),
    @NamedQuery(name = "Parameter1.findByVal", query = "SELECT p FROM Parameter1 p WHERE p.val = :val")})
public class Parameter1 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "param")
    private String param;
    @NotNull
    @Lob
    @Size(max = 2147483647)
    @Column(name = "val")
    private String val;

    public Parameter1() {
    }

    public Parameter1(Integer id) {
        this.id = id;
    }

    public Parameter1(Integer id, String param, String val) {
        this.id = id;
        this.param = param;
        this.val = val;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
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
        if (!(object instanceof Parameter1)) {
            return false;
        }
        Parameter1 other = (Parameter1) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.Parameter1[ id=" + id + " ]";
    }
    
}
