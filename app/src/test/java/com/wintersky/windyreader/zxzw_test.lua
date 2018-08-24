---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by WinterSky.
--- DateTime: 2018/8/21 19:33
---
package.path = package.path .. ";D:/WinterSky/Documents/AndroidStudio/WindyReader/app/src/main/assets/?.lua"
require("zxzw_com")
local http = require("socket.http")

local bookUrl = "http://zxzw.com/164718/"
local catalogUrl = "http://zxzw.com/164718/"
local chapterUrl = "http://zxzw.com/164718/14181340/"

local bookDoc = http.request(bookUrl)
local catalogDoc = http.request(catalogUrl)
local chapterDoc = http.request(chapterUrl)

local book = getBook(bookUrl, bookDoc)
print(book)

local catalog = getCatalog(catalogUrl, catalogDoc)
print(catalog)

local chapter = getChapter(chapterUrl, chapterDoc)
print(chapter)