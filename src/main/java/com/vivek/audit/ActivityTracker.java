/**
 * 
 */
package com.vivek.audit;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import com.vivek.audit.config.SpringContextInitializer;
import com.vivek.audit.model.Audit;
import com.vivek.audit.model.AuditLogRecord;
import com.vivek.audit.model.NoAudit;
import com.vivek.audit.service.AuditTrackerService;

/**
 * @author vivek
 * 		  The Interceptor interface provides callbacks from the session
 *         to the application, allowing the application to inspect and/or
 *         manipulate properties of a persistent object before it is saved,
 *         updated, deleted or loaded.
 */
public class ActivityTracker extends EmptyInterceptor {

	@Transient
	private List<AuditLogRecord> auditTrail = new ArrayList<>();
	// static final Logger logger =
	// LoggerFactory.getLogger(ActivityTracker.class);
	private static final String SYSTEM = "SYSTEM";

	public enum AuditLogEventType {

		UPDATE("Update"), INSERT("Insert"), DELETE("Delete");

		String description;

		AuditLogEventType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

	};

	// called when record deleted.
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (auditObject(entity)) {
			try {
				logChanges(entity, id, null, state, propertyNames, types, AuditLogEventType.DELETE);
			} catch (Exception e) {
				// logger.error("Error while audit trail for delete action
				// "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (auditObject(entity)) {
			try {
				logChanges(entity, id, state, null, propertyNames, types, AuditLogEventType.INSERT);
			} catch (Exception e) {
				// logger.error("Error while audit trail for insert action
				// "+e.getMessage());
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	// called when record updated
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		if (auditObject(entity)) {
			try {
				logChanges(entity, id, currentState, previousState, propertyNames, types, AuditLogEventType.UPDATE);
			} catch (Exception e) {
				// logger.error("Error while audit trail for update action
				// "+e.getMessage());
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	/**
	 * Logs changes to persistent data
	 *
	 * @param session
	 *            the current session (currently optional, so null is OK)
	 * @param object
	 *            the object being saved, updated or deleted
	 * @param existingObject
	 *            the existing object in the database. Used only for updates
	 * @param event
	 *            the type of event being logged. Valid values are "update",
	 *            "delete", "save"
	 * @param className
	 *            the name of the class being logged. Used as a reference in the
	 *            auditLogRecord
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */

	public void logChanges(Object object, Serializable id, Object[] currentState, Object[] previousState,
			String[] properties, Type[] types, AuditLogEventType event) throws Exception {

		HashMap<String, Integer> propertyMap = new HashMap<String, Integer>();

		for (int i = 0; i < properties.length; i++)
			propertyMap.put(properties[i], i);

		Field[] fields = getAllFields(object.getClass(), null);
		Object currentValue = null;
		Object previousValue = null;
		// Iterate through all the fields in the object

		for (Field field : fields) {

			// ignore annotated 'no-audit' fields

			if (field.isAnnotationPresent(NoAudit.class))
				continue;

			Integer index = propertyMap.get(field.getName());

			if (index == null)
				continue;

			// if the current field is static, transient or final then don't log
			// it as these modifiers are unlikely to be part of the data model

			if (Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers())
					|| Modifier.isStatic(field.getModifiers()))
				continue;

			if (currentState != null) {
				currentValue = currentState[index];
			}
			if (previousState != null)

			{
				previousValue = previousState[index];
			}
			fillLogRecord(object, id, currentValue, previousValue, event, field, types[index]);
		}
	}

	public void fillLogRecord(Object object, Serializable id, Object currentValue, Object previousValue,
			AuditLogEventType event, Field field, Type type) throws Exception {

		AuditLogRecord logRecord = getAuditLogRecord(object, id, event, field);
		if (event == AuditLogEventType.DELETE) {
			if (field.isAnnotationPresent(Id.class) || previousValue == null)
				return;
			logRecord.setPreviousValue(previousValue.toString());
		} else if (event == AuditLogEventType.INSERT) {
			if (currentValue == null)
				return;
			logRecord.setCurrentValue(currentValue.toString());
		} else if (event == AuditLogEventType.UPDATE) {
			String currentValueStringified = "";
			String previousValueStringified = "";
			if (null != previousValue)
				previousValueStringified = previousValue.toString();
			if (null != currentValue)
				currentValueStringified = currentValue.toString();
			if (previousValueStringified.equals(currentValueStringified))
				return;

			logRecord.setCurrentValue(currentValueStringified);
			logRecord.setPreviousValue(previousValueStringified);
		}
		auditTrail.add(logRecord);

	}

	private boolean auditObject(Object obj) {

		if (!(obj.getClass().isAnnotationPresent(Entity.class)))
			return false;

		if (!(obj.getClass().isAnnotationPresent(Audit.class)))
			return false;

		return true;

	}

	/**
	 * Returns an array of all fields used by this object from it's class and
	 * all superclasses.
	 *
	 * @param objectClass
	 *            the class
	 * @param fields
	 *            the current field list
	 * @return an array of fields
	 */

	protected Field[] getAllFields(Class<?> clazz, Field[] fields) {

		Field[] newFields = clazz.getDeclaredFields();

		int fieldsSize = 0;
		int newFieldsSize = 0;

		if (fields != null) {
			fieldsSize = fields.length;
		}

		if (newFields != null) {
			newFieldsSize = newFields.length;
		}

		Field[] totalFields = new Field[fieldsSize + newFieldsSize];

		if (fieldsSize > 0) {
			System.arraycopy(fields, 0, totalFields, 0, fieldsSize);
		}

		if (newFieldsSize > 0) {
			System.arraycopy(newFields, 0, totalFields, fieldsSize, newFieldsSize);
		}

		Class superClass = clazz.getSuperclass();

		Field[] finalFieldsArray;

		if (superClass != null && !superClass.getName().equals("java.lang.Object")) {

			finalFieldsArray = getAllFields(superClass, totalFields);

		} else {

			finalFieldsArray = totalFields;

		}

		return finalFieldsArray;

	}

	private AuditLogRecord getAuditLogRecord(Object object, Serializable id, AuditLogEventType event, Field field) {

		AuditLogRecord logRecord = new AuditLogRecord();

		logRecord.setEntity(object.getClass().getSimpleName());
		logRecord.setEntityId((long) id);
		logRecord.setCurrentValue("");
		logRecord.setPreviousValue("");
		logRecord.setAction(event.getDescription());
		logRecord.setField(field.getName());
		logRecord.setDate(new Date());
		return logRecord;

	}

	@Override
	public void afterTransactionCompletion(Transaction userTx) {

		if (auditTrail.isEmpty())
			return;
		try {
			AuditTrackerService auditService = SpringContextInitializer.getBean(AuditTrackerService.class);

			Iterator<AuditLogRecord> it = auditTrail.iterator();

			while (it.hasNext()) {
				AuditLogRecord auditRecord = it.next();
				auditRecord.setUser(getCurrentUser());
				auditService.addAuditTracker(auditRecord);
				it.remove();
			}
		} catch (Exception e) {
			// logger.error("Exception while persisting the audit trail data " +
			// e.getMessage());
			e.printStackTrace();
		}
	}

	public String getCurrentUser() {
		return SYSTEM;
	};

}
