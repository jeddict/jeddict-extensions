<#if package??>package ${package};</#if>

import java.util.Date;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import ${SecurityUtils_FQN};

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
        entity.setCreatedDate(new Date());
        <#if wf>//</#if>entity.setCreatedBy(securityUtils.getCurrentUserLogin());
    }

    @PreUpdate
    void onUpdate(AbstractAuditingEntity entity) {
        entity.setLastModifiedDate(new Date());
        <#if wf>//</#if>entity.setLastModifiedBy(securityUtils.getCurrentUserLogin());
    }
}
