package ${package};

<#if cdi>import jakarta.enterprise.context.Dependent;</#if><#if !cdi>import jakarta.ejb.Stateless;</#if>
<#if named>import jakarta.inject.Named;</#if>
import ${appPackage}${Authority_FQN};
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

<#if cdi>@Dependent</#if><if !cdi>@Stateless</#if>
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
