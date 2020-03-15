package ${package};

<#if !cdi>import javax.ejb.Stateless;</#if>
<#if named>import javax.inject.Named;</#if>
import javax.persistence.EntityManager;
import javax.inject.Inject;
import ${EntityClass_FQN};
<#if AbstractRepository_FQN!="">import ${appPackage}${AbstractRepository_FQN};</#if>
<#if EntityPKClass_FQN!="">import ${EntityPKClass_FQN};</#if>

<#if !cdi>@Stateless</#if>
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
