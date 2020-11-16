'use strict';

self.addEventListener('install', (evt) => {
  console.log('[ServiceWorker] Install');
});

self.addEventListener('activate', (evt) => {
  console.log('[ServiceWorker] Activate');
});

self.addEventListener('fetch', (evt) => {});

// Respond to a server push with a user notification
self.addEventListener('push', function (event) {
    if ("granted" === Notification.permission) {
        var payload = event.data ? event.data.text() : 'no payload';
        const promiseChain = self.registration.showNotification('Cazadescuentos', {
            body: payload
//            icon: 'images/windows10/Square44x44Logo.scale-100.png'
        });
        // Ensure the toast notification is displayed before exiting this function
        event.waitUntil(promiseChain);
    }
});

// Respond to the user clicking the toast notification
self.addEventListener('notificationclick', function (event) {
    console.log('On notification click: ', event.notification.tag);
    event.notification.close();
});