<#if package??>package ${package};</#if>

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Producer for injectable EntityManager
 *
 */
public class EntityManagerProducer {

    @Produces
    @PersistenceContext(unitName = "${PU}")
    private EntityManager em;
}