package service.facade;

import ${EntityClass_FQN};
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Named("${entityInstance}")
public class ${EntityFacade} extends ${AbstractFacade}<${EntityClass}> {

    @PersistenceContext(unitName = "${PU}")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${EntityFacade}() {
        super(${EntityClass}.class);
    }
    
}
