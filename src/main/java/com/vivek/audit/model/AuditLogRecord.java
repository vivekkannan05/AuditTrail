package com.vivek.audit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author vivek Audit Entity
 */
@Entity
@Table(name = "AUDIT_LOG")
public class AuditLogRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	private int id;
	@Column(name = "field", unique = false, updatable = false, nullable = false)
	private String field;
	@Column(name = "action", unique = false, updatable = false, nullable = false)
	private String action;
	@Column(name = "prev_val", unique = false, updatable = false, nullable = true)
	private String previousValue;
	@Column(name = "cur_val", unique = false, updatable = false, nullable = true)
	private String currentValue;
	@Column(name = "entity_name", unique = false, updatable = false, nullable = false)
	private String entity;
	@Column(name = "entity_id", nullable = true)
	private long entityId;
	@Column(name = "modified_date", unique = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	@Column(name = "modified_user", unique = false, updatable = false, nullable = false)
	private String user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("--------------------------------------------------------------------------\n");
		strBuild.append(
				"TABLE  | TABLE_ID | FIELD | ACTION | CURRENTVALUE | PREVIOUSVALUE | Changed Date | Changed User \n");
		strBuild.append("--------------------------------------------------------------------------\n");
		strBuild.append(this.entity + " | " + this.entityId + " | " + this.field + " | " + this.action + " | "
				+ this.currentValue + " | " + this.previousValue + " | " + this.date + " | " + this.user + "\n");
		strBuild.append("--------------------------------------------------------------------------\n");
		return strBuild.toString();
	}
}
