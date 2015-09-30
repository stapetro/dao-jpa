/*
 * DAO library based on JPA spec
 *
 * License: Apache License, Version 2.0, January 2004
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/>.
 *
 */

package com.github.stapetro;

import java.util.Collection;

/**
 * Defines basic DAO contract.
 *
 * @author Stanislav Petrov
 */
public interface BaseDao {
    /**
     * Retrieves a persistent instance.
     *
     * @param pId                <tt>id</tt> of the persistent instance to retrieve.
     * @param pTargetEntityClass Target persistent entity class.
     * @return Resulting persistent instance or <tt>null</tt> in case the requested instance was not
     * found.
     */
    <S> S get(Object pId, Class<S> pTargetEntityClass);


    /**
     * Retrieves all persistent instances.
     *
     * @param pTargetEntityClass Target persistent entity class.
     * @return Resulting list of persistent instances.
     */
    <S> Collection<S> getAll(Class<S> pTargetEntityClass);


    /**
     * Persists a transient instance or updates a detached instance.
     *
     * @param pEntity Transient or detached instance to save or update.
     * @param pId     Creates entity if pId is null, or updates it otherwise.
     * @return Resulting persistent instance.
     */
    <S> S saveOrUpdate(S pEntity, Object pId);


    /**
     * Deletes a persistent instance.
     *
     * @param pEntity Persistent instance to delete.
     */
    <S> void delete(S pEntity);


    /**
     * Deletes a persistent instance.
     *
     * @param pId                <tt>id</tt> of the persistent instance to delete.
     * @param pTargetEntityClass Target persistent entity class.
     */
    <S> void delete(Object pId, Class<S> pTargetEntityClass);


    /**
     * Deletes all specified persistent instances.
     *
     * @param pEntities Entities to be deleted.
     */
    <S> void delete(Collection<S> pEntities);


    /**
     * Deletes all persistent instances.
     *
     * @param pTargetEntityClass Target persistent entity class.
     * @return Number of persistent instances deleted.
     */
    <S> int deleteAll(Class<S> pTargetEntityClass);


    /**
     * Returns the total number of all instances persisted within the database.
     *
     * @param pTargetEntityClass Target persistent entity class.
     * @return Total instance count.
     */
    <S> int countAll(Class<S> pTargetEntityClass);
}
