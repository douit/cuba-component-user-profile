/*
 * Copyright (c) 2017 Nexbit di Paolo Furini
 */

package it.nexbit.cuba.security.userprofile.web.companions;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.sys.WebUserSessionSource;
import com.vaadin.server.VaadinSession;
import it.nexbit.cuba.security.userprofile.UserEditProfile;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class UserEditProfileCompanion implements UserEditProfile.Companion {

    @Override
    public Boolean isLoggedInWithExternalAuth() {
        UserSession userSession = AppBeans.get(UserSession.class);
        // IMPL NOTE: this check is the same as the one performed in SettingsWindow#init() on line 152
        return ExternalUserCredentials.isLoggedInWithExternalAuth(userSession);
    }

    @Override
    public void pushUserSessionUpdate(UserSession userSession) {
        // IMPL NOTE: this code has been extracted (and adapted) from the WebUserSessionSource
        //            class.  The original logic is for reading the current UserSession, the
        //            following one is the same (reversed) logic to force an update.
        if (App.isBound()) {
            VaadinSession.getCurrent().setAttribute(UserSession.class, userSession);
        } else {
            SecurityContext securityContext = AppContext.getSecurityContextNN();
            if (securityContext.getSession() == null) {
                HttpServletRequest request = null;
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                if (requestAttributes instanceof ServletRequestAttributes) {
                    request = ((ServletRequestAttributes) requestAttributes).getRequest();
                }
                if (request != null && request.getAttribute(WebUserSessionSource.REQUEST_ATTR) != null) {
                    request.setAttribute(WebUserSessionSource.REQUEST_ATTR, userSession);
                }
            }
        }
    }
}
