package org.springframework.security.authentication;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * @author Luke Taylor
 */
public class DefaultAuthenticationEventPublisherTests {
    DefaultAuthenticationEventPublisher publisher;

    @Test
    public void expectedDefaultMappingsAreSatisfied() throws Exception {
        publisher = new DefaultAuthenticationEventPublisher();
        ApplicationEventPublisher appPublisher = mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        Authentication a = mock(Authentication.class);

        Exception cause = new Exception();
        Object extraInfo = new Object();
        publisher.publishAuthenticationFailure(new BadCredentialsException(""), a);
        publisher.publishAuthenticationFailure(new BadCredentialsException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new BadCredentialsException("", cause), a);
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureBadCredentialsEvent.class));
        reset(appPublisher);
        publisher.publishAuthenticationFailure(new UsernameNotFoundException(""), a);
        publisher.publishAuthenticationFailure(new UsernameNotFoundException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new UsernameNotFoundException("", cause), a);
        publisher.publishAuthenticationFailure(new AccountExpiredException(""), a);
        publisher.publishAuthenticationFailure(new AccountExpiredException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new AccountExpiredException("", cause), a);
        publisher.publishAuthenticationFailure(new ProviderNotFoundException(""), a);
        publisher.publishAuthenticationFailure(new DisabledException(""), a);
        publisher.publishAuthenticationFailure(new DisabledException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new DisabledException("", cause), a);
        publisher.publishAuthenticationFailure(new LockedException(""), a);
        publisher.publishAuthenticationFailure(new LockedException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new LockedException("", cause), a);
        publisher.publishAuthenticationFailure(new AuthenticationServiceException(""), a);
        publisher.publishAuthenticationFailure(new AuthenticationServiceException("",cause), a);
        publisher.publishAuthenticationFailure(new CredentialsExpiredException(""), a);
        publisher.publishAuthenticationFailure(new CredentialsExpiredException("", extraInfo), a);
        publisher.publishAuthenticationFailure(new CredentialsExpiredException("", cause), a);
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureBadCredentialsEvent.class));
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureExpiredEvent.class));
        verify(appPublisher).publishEvent(isA(AuthenticationFailureProviderNotFoundEvent.class));
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureDisabledEvent.class));
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureLockedEvent.class));
        verify(appPublisher, times(2)).publishEvent(isA(AuthenticationFailureServiceExceptionEvent.class));
        verify(appPublisher, times(3)).publishEvent(isA(AuthenticationFailureCredentialsExpiredEvent.class));
        verifyNoMoreInteractions(appPublisher);
    }

    @Test
    public void authenticationSuccessIsPublished() {
        publisher = new DefaultAuthenticationEventPublisher();
        ApplicationEventPublisher appPublisher = mock(ApplicationEventPublisher.class);
        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationSuccess(mock(Authentication.class));
        verify(appPublisher).publishEvent(isA(AuthenticationSuccessEvent.class));

        publisher.setApplicationEventPublisher(null);
        // Should be ignored with null app publisher
        publisher.publishAuthenticationSuccess(mock(Authentication.class));
    }

    @Test
    public void additionalExceptionMappingsAreSupported() {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(MockAuthenticationException.class.getName(), AuthenticationFailureDisabledEvent.class.getName());
        publisher.setAdditionalExceptionMappings(p);
        ApplicationEventPublisher appPublisher = mock(ApplicationEventPublisher.class);

        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationFailure(new MockAuthenticationException("test"), mock(Authentication.class));
        verify(appPublisher).publishEvent(isA(AuthenticationFailureDisabledEvent.class));
    }

    @Test(expected=RuntimeException.class)
    public void missingEventClassExceptionCausesException() {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(MockAuthenticationException.class.getName(), "NoSuchClass");
        publisher.setAdditionalExceptionMappings(p);
    }

    @Test
    public void unknownFailureExceptionIsIgnored() throws Exception {
        publisher = new DefaultAuthenticationEventPublisher();
        Properties p = new Properties();
        p.put(MockAuthenticationException.class.getName(), AuthenticationFailureDisabledEvent.class.getName());
        publisher.setAdditionalExceptionMappings(p);
        ApplicationEventPublisher appPublisher = mock(ApplicationEventPublisher.class);

        publisher.setApplicationEventPublisher(appPublisher);
        publisher.publishAuthenticationFailure(new AuthenticationException("") {}, mock(Authentication.class));
        verifyZeroInteractions(appPublisher);
    }

    private static final class MockAuthenticationException extends AuthenticationException {
        public MockAuthenticationException(String msg) {
            super(msg);
        }
    }

}
