<#if package!="">package ${package};</#if>

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.inject.Inject;
import ${EntityClass_FQN};
<#if AbstractFacade_FQN!="">import ${AbstractFacade_FQN};</#if>
<#if EntityPKClass_FQN!="">import ${EntityPKClass_FQN};</#if>

@Stateless
@Named("${entityInstance}")
public class ${EntityFacade} extends ${AbstractFacade}<${EntityClass}, ${EntityPKClass}> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${EntityFacade}() {
        super(${EntityClass}.class);
    }
    
}
