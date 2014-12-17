var parsePlugin = {
    initialize: function(appId, clientKey, uniqueId, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'initialize',
            [appId, clientKey, uniqueId]
        );
    },

    getInstallationId: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getInstallationId',
            []
        );
    },

    getInstallationObjectId: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getInstallationObjectId',
            []
        );
    },

    getSubscriptions: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getSubscriptions',
            []
        );
    },

    subscribe: function(channel, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'subscribe',
            [ channel ]
        );
    },

    unsubscribe: function(channel, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'unsubscribe',
            [ channel ]
        );
    },

    onopen: function(payload) {}
};
module.exports = parsePlugin;
