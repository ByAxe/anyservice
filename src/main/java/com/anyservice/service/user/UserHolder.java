package com.anyservice.service.user;

import com.anyservice.dto.user.UserDetailed;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Singleton that holds a request bean
 */
@Component
public class UserHolder implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public UserDetailed getUser() {
        UserHolderDelegate userHolderDelegate = getUserHolderDelegate();
        if (userHolderDelegate != null)
            return userHolderDelegate.getUser();

        return null;
    }

    public void setUser(UserDetailed user) {
        UserHolderDelegate userHolderDelegate = getUserHolderDelegate();
        if (userHolderDelegate != null)
            userHolderDelegate.setUser(user);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private UserHolderDelegate getUserHolderDelegate() {
        try {
            return applicationContext.getBean("userHolderDelegate", UserHolderDelegate.class);
        } catch (Throwable ignored) {
            return null;
        }
    }
}

