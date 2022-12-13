package ${package};

<#if cdi>import jakarta.enterprise.context.Dependent;</#if><#if !cdi>import jakarta.ejb.Stateless;</#if>
<#if named>import jakarta.inject.Named;</#if>
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import ${EntityClass_FQN};
<#if AbstractRepository_FQN!="">import ${appPackage}${AbstractRepository_FQN};</#if>
<#if EntityPKClass_FQN!="">import ${EntityPKClass_FQN};</#if>

<#if cdi>@Dependent</#if><#if !cdi>@Stateless</#if>
<#if named>@Named("${entityInstance}")</#if>
public class ${EntityRepository} extends ${AbstractRepository}<${EntityClass}, ${EntityPKClass}> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${EntityRepository}() {
        super(${EntityClass}.class);
    }
    
}
