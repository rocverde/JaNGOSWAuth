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
@Table(name = "locale")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Locale.findAll", query = "SELECT l FROM Locale l"),
    @NamedQuery(name = "Locale.findById", query = "SELECT l FROM Locale l WHERE l.id = :id"),
    @NamedQuery(name = "Locale.findByLocale", query = "SELECT l FROM Locale l WHERE l.locale = :locale"),
    @NamedQuery(name = "Locale.findByLocaleString", query = "SELECT l FROM Locale l WHERE l.localeString = :localeString")})
public class Locale implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "locale")
    private String locale;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "localeString")
    private String localeString;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fkLocale")
    private Collection<Account> accountCollection;

    public Locale() {
    }

    public Locale(Integer id) {
        this.id = id;
    }

    public Locale(Integer id, String locale, String localeString) {
        this.id = id;
        this.locale = locale;
        this.localeString = localeString;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocaleString() {
        return localeString;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
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
        if (!(object instanceof Locale)) {
            return false;
        }
        Locale other = (Locale) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.je4w.auth.model.Locale[ id=" + id + " ]";
    }
    
}
