-- www.8wenku.com
require("util")

local M = {}

function M.getBook(url, doc)
    local title = doc:match([[<h2 class="tit">《?(.-)》?</h2>]])
    return bookJson(url, title, url)
end

function M.getCatalog(url, doc)
    local catalog = "["
    local i = 0
    for u, t in doc:gmatch([[<a target="_blank" href="(/chapter/view%?id=%d+&amp;chapter_no=%d+)">([^%c]-)</a>]]) do
        u = "http://www.8wenku.com" .. u
        u = u:gsub("&amp;", "&")
        catalog = catalog .. "\n" .. chapterJson(u, i, t, url) .. ","
        i = i + 1
    end
    return catalog:sub(1, -2) .. "\n]"
end

function M.getContent(url, doc)
    local content = matchTag(doc, "div", [[class="article%-body" role="article%-body"]])
    return outStr(content)
end

return M