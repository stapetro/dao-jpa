/*
 * DAO library based on JPA spec
 *
 * License: Apache License, Version 2.0, January 2004
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/>.
 *
 */

package com.github.stapetro;

import java.util.Collection;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Stanislav Petrov, GfK
 */
@Named
public class JpaBaseDao
        implements BaseDao {
    @PersistenceContext
    private EntityManager entityMgr;

    private CriteriaBuilder criteriaBuilder;


    @Override
    public <S> S get(final Object pId, final Class<S> pTargetEntityClass) {
        return getEntityMgr().find(pTargetEntityClass, pId);
    }


    @Override
    public <S> Collection<S> getAll(final Class<S> pTargetEntityClass) {
        CriteriaQuery<S> query = getCriteriaBuilder().createQuery(pTargetEntityClass);
        Root<S> root = query.from(pTargetEntityClass);
        query.select(root);
        return findEntities(query);
    }


    @Override
    public <S> S saveOrUpdate(final S pEntity, final Object pId) {
        if (pEntity == null) {
            return null;
        }
        if (pId == null) {
            return create(pEntity);
        } else {
            return update(pEntity);
        }
    }


    @Override
    public <S> void delete(final S pEntity) {
        getEntityMgr().remove(pEntity);
    }


    @Override
    public <S> void delete(final Object pId, final Class<S> pTargetEntityClass) {
        S entity = get(pId, pTargetEntityClass);
        if (entity == null) {
            return;
        }
        delete(entity);
    }


    @Override
    public <S> void delete(final Collection<S> pEntities) {
        throw new RuntimeException("Not impl yet!");
    }


    @Override
    public <S> int deleteAll(final Class<S> pTargetEntityClass) {
        throw new RuntimeException("Not impl yet!");
    }


    @Override
    public <S> int countAll(final Class<S> pTargetEntityClass) {
        throw new RuntimeException("Not impl yet!");
    }


    <S> S findEntity(CriteriaQuery<S> pCriteriaQuery) {
        TypedQuery<S> typedQuery = getEntityMgr().createQuery(pCriteriaQuery);
        return executeSingleResultQuery(typedQuery);
    }


    <S> Collection<S> findEntities(CriteriaQuery<S> pCriteriaQuery, Integer pMaxRowsNumber, Integer pFromRowNumber) {
        TypedQuery<S> typedQuery = getEntityMgr().createQuery(pCriteriaQuery);
        return executeResultListQuery(typedQuery, pMaxRowsNumber, pFromRowNumber);
    }


    <S> Collection<S> findEntities(CriteriaQuery<S> pCriteriaQuery) {
        return findEntities(pCriteriaQuery, null, null);
    }


    EntityManager getEntityMgr() {
        return entityMgr;
    }


    CriteriaBuilder getCriteriaBuilder() {
        if (criteriaBuilder == null) {
            criteriaBuilder = getEntityMgr().getCriteriaBuilder();
        }
        return criteriaBuilder;
    }


    <S> CriteriaQuery<S> addPredicateToQuery(CriteriaQuery<S> pQuery, Predicate pPredicate) {
        if (pPredicate != null) {
            pQuery.where(pPredicate);
        }
        return pQuery;
    }


    <S> CriteriaQuery<S> addPredicatesToQuery(CriteriaQuery<S> pQuery, Collection<Predicate> pPredicates) {
        if (pPredicates == null || pPredicates.isEmpty()) {
            return pQuery;
        }
        Predicate[] preds = new Predicate[pPredicates.size()];
        preds = pPredicates.toArray(preds);
        pQuery = pQuery.where(preds);
        return pQuery;
    }


    <S> CriteriaQuery<S> addOrdersToQuery(CriteriaQuery<S> pQuery, List<Order> pOrders) {
        if (pOrders == null || pOrders.isEmpty()) {
            return pQuery;
        }
        return pQuery.orderBy(pOrders);
    }


    <M, N> Join<M, N> toJoin(Fetch<M, N> pFetch) {
        if (pFetch instanceof Join<?, ?>) {
            return (Join<M, N>) pFetch;
        }
        return null;
    }


    <S> CriteriaQuery<S> createCriteriaQuery(Class<S> pEntityClass) {
        return getCriteriaBuilder().createQuery(pEntityClass);
    }


    private Query setResultSetLimitations(Query pQuery, Integer pMaxRowsNumber, Integer pFromRowNumber) {
        if (pMaxRowsNumber != null && pMaxRowsNumber > 0) {
            pQuery.setMaxResults(pMaxRowsNumber);
        }
        if (pFromRowNumber != null && pFromRowNumber > 0) {
            pQuery.setFirstResult(pFromRowNumber);
        }
        return pQuery;
    }


    private <S> Collection<S> executeResultListQuery(TypedQuery<S> pQuery, Integer pMaxRowsNumber, Integer pFromRowNumber) {
        setResultSetLimitations(pQuery, pMaxRowsNumber, pFromRowNumber);
        return pQuery.getResultList();
    }


    private <S> S executeSingleResultQuery(TypedQuery<S> pQuery) {
        try {
            return pQuery.getSingleResult();
        } catch (NoResultException nrex) {
            return null;
        }
    }


    protected <S> S create(final S pEntity) {
        getEntityMgr().persist(pEntity);
        return pEntity;
    }


    protected <S> S update(final S pEntity) {
        return getEntityMgr().merge(pEntity);
    }
}
