function getBook(url, doc)
    local title = doc:match([[<h2 class="tit">《?(.-)》?</h2>]])
    return ([[{
    "url":"%s",
    "title":"%s",
    "catalogUrl":"%s"
}]]):format(url, title, url)
end

function getCatalog(url, doc)
    local catalog = "["
    local i = 0
    for u, t in doc:gmatch([[<a target="_blank" href="(/chapter/view%?id=%d+&amp;chapter_no=%d+)">([^%c]-)</a>]]) do
        u = "http://www.8wenku.com" .. u
        u = u:gsub("&amp;", "&")
        local chapter = ([[{"index":%d, "url":"%s", "title":"%s"}]]):format(i, u, t)
        i = i + 1
        catalog = catalog .. "\n\t" .. chapter .. ","
    end
    return catalog:sub(1, -2) .. "\n]"
end

function getChapter(url, doc)
    local title = doc:match("<div class=\"article%-title\">.-</div>")
    title = title:match("<h1>%s?([^%c]-)</h1>")
    local content = doc:match("<div class=\"article%-body\" role=\"article%-body\">.-</div>")
    content = content:gsub("\n", ""):gsub("<br */> *", "\n"):gsub("<.-> *", ""):gsub("&nbsp;", " ")

    return ([[{
    "url":"%s",
    "title":"%s",
    "content":"%s"
}]]):format(url, title, content)
end
