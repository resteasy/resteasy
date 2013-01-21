OAuth2 Login, Distributed SSO, Distributed Logout, and Token Grant AS7 Examples
===================================
The following examples requires JBoss AS7 and have been tested on version 7.1.1.  Here's the highlights of the examples
* Turning a jboss security domain into a remote authentication server
* Delegating authentication of a web app to the remote authentication server via OAuth 2 protocols
* Distributed Single-Sign-On and Single-Logout
* Transferring identity and role mappings via a special bearer token (Skeleton Key Token).
* Bearer token authentication and authorization of JAX-RS services
* Obtaining bearer tokens via the OAuth2 protocol

There are 6 WAR projects.  These all will run on the same jboss instance, but pretend each one is running on a different
machine on the network or Internet.
* **auth-server**: A WAR that turns a security domain into a remote login server and oauth token service
* **customer-app** A WAR applications that does remote login using OAUTH2 browser redirects with the auth server
* **product-app** A WAR applications that does remote login using OAUTH2 browser redirects with the auth server
* **database-service** JAX-RS services authenticated by bearer tokens only.  The customer and product app invoke on it
  to get data
* **third-party** Simple WAR that obtain a bearer token using OAuth2 using browser redirects to the auth-server.
* **client-grant** Simple WAR that obtains a token from the auth-server using a direct protocol and then uses the token
  to invoke on database services.

The UI of each of these applications is very crude and exists just to show our OAuth2 implementation in action.


Step 1: Make sure you've upgraded Resteasy
--------------------------------------
The first thing you is upgrade Resteasy within JBoss AS7

1. cd modules
2. unzip resteasy-jboss-modules-XXXX.zip

Go to the modules/ directory of your JBoss AS7 distribution and unzip the resteasy-jboss-modules-XXXX.zip file within
this directory.  This will upgrade existing resteasy modules and add a few more.

Step 2: Copy configuration to JBoss AS7
---------------------------------------
The OAUTH example comes with a configuration directory.  You must copy the contents to the standalone configuration of AS7

1. cd oauth2-as7-example/configuration/standalone
2. cp * $JBOSS_HOME/standalone/configuration

This sets up the security domain used by the examples, enables SSL and HTTPS, and also copies over all the keystore
files used by the distro.  This pretty much overrides any existing changes to standalone.xml, so you might want to
use a clean JBoss distro, or do a diff to see what you need to change (or look at our documentation on OAuth2).

Step 3: Boot AS7
---------------------------------------
Boot AS7 in 'standalone' mode.

Step 4: Build and deploy
---------------------------------------
next you must build and deploy

1. cd oauth2-as7-example
2. mvn clean install
3. mvn jboss-as:deploy

Step 5: Login and Observe Apps
---------------------------------------
Try going to the customer app and viewing customer data:

https://localhost:8443/customer-portal/customers/view.jsp

This should take you to the auth-server login screen.  Enter username: bburke@redhat.com and password: password.

If you click on the products link, you'll be take to the products app and show a product listing.  The redirects
are still happening, but the auth-server knows you are already logged in so the login is bypassed.

If you click on the logout link of either of the product or customer app, you'll be logged out of all the applications
and redirected to the central auth-server's login page.

Step 6: Traditional OAuth2 Example
----------------------------------
The customer and product apps are logins.  The third-party app is the traditional OAuth2 usecase of a client wanting
to get permission to access a user's data.  To run this example

https://localhost:8443/oauth-client

You'll notice that even if you've already logged into the central server, this example will always require you to
enter username and password.  Because this is an OAuth grant, we always want to ask the user if it is ok for somebody
to ask for grant permissions.  Look at the login.jsp of the auth-server.  You can see the logic there to see how
it determines whether this is a login or oauth grant.

Step 7: OAuth token via Basic Auth
---------------------------------
The client-grant example will obtain a bearer token from the auth-server.  It then invokes the bearer-token secured
database-service JAX-RS services.

https://localhost:8443/client-grant

Step 8: Admin logout interface
---------------------------------
Go to:

https://localhost:8443/auth-server

If you are not redirected to a login page, click the logout link on this page.  Relogin as admin:

username: admin password: password

Next go to:

https://localhost:8443/auth-server/admin/admin.html

You can see that it gives you options as admin to logout any user (or all users) if you want or need to.  The project
as a whole is meant to be a template.  For your own applications you might want to change the login.jsp and/or admin
pages to use any css tempaltes or anything like that you want.  You might also want to change the text or how
it appears to users.