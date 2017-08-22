/**
 * 
 */
package com.vivek.audit.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vivek.audit.model.AuditLogRecord;

/**
 * @author vivek Performs CRUD operation on AuditTable
 */
@Repository
public interface AuditTrackerRepository extends CrudRepository<AuditLogRecord, Integer> {

}
