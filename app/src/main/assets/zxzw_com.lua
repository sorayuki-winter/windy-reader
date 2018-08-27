-- zxzw.com
require("util")

local M = {}

function M.getBook(url, doc)
    local title = doc:match("<div class=\"text t_c\">.-</div>"):match("<a href=.->(.-)</a>")
    return ([[{
    "url":"%s",
    "title":"%s",
    "catalogUrl":"%s"
}]]):format(url, title, url)
end

function M.getCatalog(url, doc)
    local catalog = "["
    local i = 0
    for div in doc:gmatch("<div class=\"chapter\">.-</div>") do
        local u, t = div:match(("<a href=\"([^%c]-)\" title=\"([^%c]-)\">.-</a>"))
        u = "http://zxzw.com" .. u
        local chapter = ([[{"index":%d,"url":"%s","title":"%s"}]]):format(i, u, t)
        catalog = catalog .. "\n\t" .. chapter .. ","
        i = i + 1
    end
    return catalog:sub(1, -2) .. "\n]"
end

function M.getChapter(url, doc)
    local title = doc:match("<div class=\"text t_c\"><h1>([^%c]-)</h1></div>")
    local content = matchTag(doc, "div", "id=\"content\"")
    content = toContent(content);
    return ([[{
    "url":"%s",
    "title":"%s",
    "content":"%s"
}]]):format(url, title, content)
end

return M
