/**
 * 
 */
package com.vivek.audit.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author vivek
 *
 */
@Component
public class SpringContextInitializer implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
}
