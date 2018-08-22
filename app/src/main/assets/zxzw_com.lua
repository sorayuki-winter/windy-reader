function getBook(url, doc)
    local title = doc:match("<div class=\"text t_c\">.-</div>"):match("<a href=.->(.-)</a>")
    return ([[{
    "url":"%s",
    "title":"%s",
    "catalogUrl":"%s"
}]]):format(url, title, url)
end

function getCatalog(url, doc)
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

function getChapter(url, doc)
    local title = doc:match("<div class=\"text t_c\"><h1>([^%c]-)</h1></div>")
    local s, _ = doc:find("<div id=\"content\">")
    local m, _ = doc:find("<div", s + 1)
    local _, e = doc:find("</div>", s)
    while m < e do
        m, _ = doc:find("<div", m + 1)
        _, e = doc:find("</div>", e + 1)
    end
    local content = doc:sub(s, e):gsub("\r?\n", ""):gsub("<br/>", "\n"):gsub("<.->", ""):gsub("\"", "\\\"")
    return ([[{
    "url":"%s",
    "title":"%s",
    "content":"%s"
}]]):format(url, title, content)
end
