package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.BusinessRoles;
import io.github.t3r1jj.develog.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AuthorizationAspect {

    private final UserService userService;

    @Autowired
    public AuthorizationAspect(UserService userService) {
        this.userService = userService;
    }

//    @Before("@annotation(io.github.t3r1jj.develog.model.domain.BusinessRoles) || within(@io.github.t3r1jj.develog.model.domain.BusinessRoles *)")
//    public void authorize(JoinPoint joinPoint) throws AccessDeniedException {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        BusinessRoles[] businessRolesAnnotations = getBusinessRoles(method);
//        for (BusinessRoles roles : businessRolesAnnotations) {
//            List<User.Role> roleValues = Arrays.asList(roles.values());
//            if (!roleValues.isEmpty() && !roleValues.contains(userService.getLoggedUser().getRole())) {
//                throw new AccessDeniedException("Unauthorized");
//            }
//        }
//    }

    private BusinessRoles[] getBusinessRoles(Method method) {
        BusinessRoles[] businessRolesAnnotations = method.getAnnotationsByType(BusinessRoles.class);
        if (businessRolesAnnotations.length == 0) {
            businessRolesAnnotations = method.getDeclaringClass().getAnnotationsByType(BusinessRoles.class);
        }
        return businessRolesAnnotations;
    }

    @Before("within(@org.springframework.stereotype.Controller *)")
    public void addUserInfoModel(JoinPoint joinPoint) {
        if (!userService.isUserAuthenticated()) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Model) {
                Model model = (Model) arg;
                model.addAttribute("loggedUser", userService.getLoggedUser());
                return;
            }
        }
    }

}
