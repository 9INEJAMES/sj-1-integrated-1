import { reactive } from 'vue'
import { PublicClientApplication } from '@azure/msal-browser'

export const msalConfig = {
    auth: {
        clientId: import.meta.env.VITE_CLIENT_ID,
        authority: 'https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a',
        redirectUri: '/', // Must be registered as a SPA redirectURI on your app registration
        postLogoutRedirectUri: '/', // Must be registered as a SPA redirectURI on your app registration
    },
    cache: {
        cacheLocation: 'localStorage',
    },
}

// Add here scopes for id token to be used at MS Identity Platform endpoints.
export const graphScopes = {
    scopes: ['User.Read'],
}

export const state = reactive({
    isAuthenticated: false,
    user: null,
})

export const msalInstance = new PublicClientApplication(msalConfig)

// Add here the endpoints for MS Graph API services you would like to use.
// export const graphConfig = {
//     graphMeEndpoint: 'https://graph.microsoft.com/v1.0/me',
// }
