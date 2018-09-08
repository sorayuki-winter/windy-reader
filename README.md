# windy-reader
online reader, build with `mvp` and `dagger-android`

## Use
It's easy to add new book and enjoy reading. You can design your own library support with lua.

#### Add New Book
Just find and open your target book page in browser, and share it to this app.

#### Library Support
Design library support with lua, the lua file usually is named from website, e.g. www_google_com.lua from www.google.com. A library support file should be a module which contains the following functions:
* `getBook`(url, doc)
* `getCatalog`(url, doc)
* `getContent`(doc)

There are two internal library support:
* `www_8wenku_com.lua` support http://www.8wenku.com
* `zxzw_com.lua` support http://zxzw.com
