<#if package??>package ${package};</#if>

import ${Authority_FQN};
<#if !cdi>import javax.ejb.Stateless;</#if>
<#if named>import javax.inject.Named;</#if>
import javax.persistence.EntityManager;
import javax.inject.Inject;

<#if !cdi>@Stateless</#if>
<#if named>@Named("authority")</#if>
public class ${AuthorityRepository} extends ${AbstractRepository}<Authority, String> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${AuthorityRepository}() {
        super(Authority.class);
    }
}
