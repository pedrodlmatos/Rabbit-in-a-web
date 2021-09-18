package com.ua.riaw.security;

/**
 * This package includes all classes to deal with security and access to the API services
 *
 *  - Verifies if user is authenticated or if the provided JSON Web Token is still valid.
 *    - If it is valid, the request proceeds;
 *    - Otherwise, sends an error response informing the authentication has failed
 *
 * Defines the chain the list of endpoints that are public and that need authentication
 */