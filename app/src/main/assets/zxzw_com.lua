-- zxzw.com
require("util")

local M = {}

function M.getBook(url, doc)
    local title = doc:match("<div class=\"text t_c\">.-</div>"):match("<a href=.->(.-)</a>")
    return bookJson(url, title, url)
end

function M.getCatalog(url, doc)
    local catalog = "["
    local i = 0
    for div in doc:gmatch("<div class=\"chapter\">.-</div>") do
        local u, t = div:match(("<a href=\"([^%c]-)\" title=\"([^%c]-)\">.-</a>"))
        u = "http://zxzw.com" .. u
        catalog = catalog .. "\n\t" .. chapterJson(u, i, t, url) .. ","
        i = i + 1
    end
    return catalog:sub(1, -2) .. "\n]"
end

function M.getContent(url, doc)
    local content = matchTag(doc, "div", "id=\"content\"")
    return outStr(content)
end

return M