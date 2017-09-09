<#if package??>package ${package};</#if>

import ${SecurityUtils_FQN};
import java.time.Instant;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Entity listener class for audit info
 */
public class AuditListner {

    <#assign wf = serverFamily == "WILDFLY_FAMILY" >
    <#if wf>//Issue : https://issues.jboss.org/browse/WFLY-2387</#if>
    <#if wf>//</#if>@Inject
    <#if wf>//</#if>private SecurityUtils securityUtils;

    @PrePersist
    void onCreate(AbstractAuditingEntity entity) {
        entity.setCreatedDate(Instant.now());
        <#if wf>//</#if>entity.setCreatedBy(securityUtils.getCurrentUserLogin());
    }

    @PreUpdate
    void onUpdate(AbstractAuditingEntity entity) {
        entity.setLastModifiedDate(Instant.now());
        <#if wf>//</#if>entity.setLastModifiedBy(securityUtils.getCurrentUserLogin());
    }
}
