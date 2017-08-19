<#if package??>package ${package};</#if>

import java.io.Serializable;
import java.time.Instant;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.EntityListeners;

/**
 * Base abstract class for entities which will hold definitions for created,
 * last modified by and created, last modified by date.
 */
@MappedSuperclass
@EntityListeners(AuditListner.class)
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonbTransient
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @JsonbTransient
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @JsonbTransient
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @JsonbTransient
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
