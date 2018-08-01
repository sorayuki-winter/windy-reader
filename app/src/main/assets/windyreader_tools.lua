local Android_Log = luajava.bindClass("android.util.Log")
local LT = "wintersky"
function log(sMsg)
    Android_Log:i(LT, sMsg)
end

local Jsoup = luajava.bindClass("org.jsoup.Jsoup")
function docGet(url)
    return Jsoup:connect(url):timeout(3000):get()
end

function Chapter()
    return luajava.newInstance("com.wintersky.windyreader.data.Chapter")
end

function Book()
    return luajava.newInstance("com.wintersky.windyreader.data.Book")
end
