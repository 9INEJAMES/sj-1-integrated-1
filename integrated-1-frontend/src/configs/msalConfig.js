import { reactive } from 'vue'
import { PublicClientApplication } from '@azure/msal-browser'

// Get the base URL of the application
const baseUrl = window.location.origin

export const msalConfig = {
    auth: {
        clientId: import.meta.env.VITE_CLIENT_ID,
        authority: 'https://login.microsoftonline.com/79845616-9df0-43e0-8842-e300feb2642a',
        redirectUri: `${baseUrl}/`, // Use full URL for redirect
        postLogoutRedirectUri: `${baseUrl}/`, // Use full URL for logout redirect
        navigateToLoginRequestUrl: true
    },
    cache: {
        cacheLocation: 'localStorage',
        storeAuthStateInCookie: true // This might help with redirect issues
    },
    // system: {
    //     loggerOptions: {
    //         loggerCallback: (level, message, containsPii) => {
    //             if (containsPii) {
    //                 return;
    //             }
    //             switch (level) {
    //                 case 0:
    //                     console.error(message);
    //                     return;
    //                 case 1:
    //                     console.warn(message);
    //                     return;
    //                 case 2:
    //                     console.info(message);
    //                     return;
    //                 case 3:
    //                     console.debug(message);
    //                     return;
    //                 default:
    //                     console.log(message);
    //                     return;
    //             }
    //         },
    //         logLevel: 3 // Enable all logging when debugging
    //     }
    // }
}

// Scopes for initial login - includes both ID token and MS Graph scopes
export const loginRequest = {
    scopes: [
        'openid',
        'profile',
        'email',
        'User.Read',
        'User.ReadBasic.All'
    ]
}

// Separate scopes for MS Graph API calls
export const graphScopes = {
    scopes: ['User.Read', 'User.ReadBasic.All']
}

// MS Graph endpoints
export const graphConfig = {
    graphMeEndpoint: 'https://graph.microsoft.com/v1.0/me'
}

export const state = reactive({
    isAuthenticated: false,
    user: null,
})

// Initialize MSAL instance
export const msalInstance = new PublicClientApplication(msalConfig)
