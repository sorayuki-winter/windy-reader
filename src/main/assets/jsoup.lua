local M = {}
jsoup = M

local Jsoup = luajava.bindClass("org.jsoup.Jsoup")

function M:connect(sUrl)
    return Jsoup:connect(sUrl)
end

return jsoup