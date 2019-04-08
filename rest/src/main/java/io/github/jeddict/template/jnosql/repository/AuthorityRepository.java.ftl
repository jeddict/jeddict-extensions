package ${package};

<#if !cdi>import javax.ejb.Stateless;</#if>
<#if named>import javax.inject.Named;</#if>
import ${appPackage}${Authority_FQN};
import javax.inject.Inject;
import javax.persistence.EntityManager;

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
