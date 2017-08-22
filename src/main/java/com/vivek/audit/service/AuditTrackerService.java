/**
 * 
 */
package com.vivek.audit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vivek.audit.jpa.AuditTrackerRepository;
import com.vivek.audit.model.AuditLogRecord;

/**
 * @author vivek
 *
 */
@Service("auditTrackerService")
public class AuditTrackerService {

	@Autowired
	private AuditTrackerRepository auditTrackerRepository;

	public void addAuditTracker(AuditLogRecord tracker) {
		this.auditTrackerRepository.save(tracker);
	}

}
