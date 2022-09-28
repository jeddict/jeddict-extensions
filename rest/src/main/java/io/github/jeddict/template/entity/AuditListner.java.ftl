package ${package};

import ${appPackage}${SecurityHelper_FQN};
import java.time.Instant;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Entity listener class for audit info
 */
public class AuditListner {

    <#assign wf = (runtime == "WILDFLY_SERVER" || runtime == "WILDFLY_SWARM") >
    <#if wf>//Issue : https://issues.jboss.org/browse/WFLY-2387</#if>
    <#if wf>//</#if>@Inject
    <#if wf>//</#if>private SecurityHelper securityHelper;

    @PrePersist
    void onCreate(AbstractAuditingEntity entity) {
        entity.setCreatedDate(Instant.now());
        <#if wf>//</#if>entity.setCreatedBy(securityHelper.getCurrentUserLogin());
    }

    @PreUpdate
    void onUpdate(AbstractAuditingEntity entity) {
        entity.setLastModifiedDate(Instant.now());
        <#if wf>//</#if>entity.setLastModifiedBy(securityHelper.getCurrentUserLogin());
    }
}
