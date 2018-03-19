require("jsoup")

local Android_Log = luajava.bindClass("android.util.Log")
local LT = "wintersky"
function log(sMsg)
    Android_Log:i(LT, sMsg)
end

function Chapter()
    return luajava.newInstance("com.wintersky.windyreader.data.Chapter")
end

function Book()
    return luajava.newInstance("com.wintersky.windyreader.data.Book")
end
