package com.qualrole.backend.auth.security;

import com.qualrole.backend.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidJwtToken_SetsAuthentication() throws Exception {
        String jwtToken = "validToken";
        String userId = "123";
        String username = "testUser";
        String userRole = "ADMIN";
        String authHeader = "Bearer " + jwtToken;

        Claims claims = mock(Claims.class);
        when(claims.get("role", String.class)).thenReturn(userRole);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        when(jwtUtil.extractUserId(jwtToken)).thenReturn(userId);
        when(jwtUtil.extractClaims(jwtToken)).thenReturn(claims);
        when(jwtUtil.isTokenValid(jwtToken, userId)).thenReturn(true);
        when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidJwtToken_ReturnsUnauthorized() throws Exception {
        String jwtToken = "invalidToken";
        String authHeader = "Bearer " + jwtToken;

        when(jwtUtil.extractUserId(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(response.getWriter()).thenReturn(writer);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(response).setStatus(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(401);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\": \"Token inválido\"}");
        verifyNoInteractions(filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void testDoFilterInternal_MissingAuthorizationHeader_ContinuesChain() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderWithoutBearer_SkipsAuthentication() throws Exception {
        String authHeader = "InvalidHeaderFormat";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(authHeader);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }
}