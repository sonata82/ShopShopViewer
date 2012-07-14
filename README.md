ShopShopViewer
==============
This Android app shows shopping lists created with the iPhone/iPad app [ShopShop](http://nschum.de/apps/ShopShop/).


Contributing
------------
Before compiling create a file dropbox_keys.xml in res/values with the following content:
  <?xml version="1.0" encoding="utf-8"?>
    <resources>
      <string name="app_key">(application-key-from-dropbox)</string>
      <string name="db_scheme">db-(application-key-from-dropbox)</string>
      <string name="app_secret">(application-secret-from-dropbox)</string>
    </resources>

The application key and secret are generated by creating a dropbox application.

Currently the application can only be installed on a phone using Eclipse or adb.
    
License
-------
see LICENSE