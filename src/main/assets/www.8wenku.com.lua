function search()
    local doc = jsoup:connect(searchUrl):data("keyword", keyWord):post()
    local lis = doc:select("li.inline")
    for i = 0, lis:size() - 1 do
        local li = lis:get(i)
        local img = li:select("img.J_scoll_load"):get(0)
        local bk = Book()
        bk:setTitle(img:attr("alt"))
        bk:setImgUrl(img:attr("src"))
        local a = img:parent()
        local host = string.match(searchUrl, "https?://[^/]+")
        bk:setUrl(host .. a:attr("href"))
        list:add(bk)
    end
end

function getBook()
    local doc = jsoup:connect(bookUrl):post()
    local tH2 = doc:select("h2.tit"):get(0)
    local sT = tH2:text()
    local _, _, sT1 = string.find(sT, "《(.+)》")
    book:setTitle(sT1)
    local img = doc:select("img[width][height][alt][src~=.*\\.jpg]"):get(0)
    book:setImgUrl(img:attr("src"))
    book:setChapterListUrl(bookUrl)
    local atr = doc:select("span.author"):get(0)
    local sAtr = atr:text()
    _, _, sAtr = string.find(sAtr, "作者：(.*)")
    if string.len(sAtr) > 0 then
        book:setAuthor(sAtr)
    else
        book:setAuthor(" ")
    end
    book:setStatus(" ")
    book:setClassify(" ")
    local desc = doc:select("p.desc"):get(0)
    book:setDetail(desc:ownText())
end

function getChapterList()
    local doc = jsoup:connect(chapterListUrl):post()
    local jrs = doc:select("div.hd.clearfix")
    for i = 0, jrs:size() - 1 do
        local jr = jrs:get(i)
        local st1 = jr:text()
        local divCl = jr:nextElementSibling()
        local aCl = divCl:select("a[target][href]")
        for j = 0, aCl:size() - 1 do
            local aC = aCl:get(j)
            local jc = Chapter()
            jc:setTitle(st1 .. " " .. aC:ownText())
            local burl = string.match(chapterListUrl, "https?://[^/]+")
            jc:setUrl(burl .. aC:attr("href"))
            list:add(jc)
        end
    end
end

function getChapter()
    local doc = jsoup:connect(chapterUrl):post()
    local t1 = doc:select(".article-title"):get(0)
    local t2 = t1:select("h1"):get(0)
    chapter:setTitle(t2:ownText())
    local c1 = doc:select(".article-body"):get(0)
    local c2 = c1:select("p"):get(0)
    c2:select("br"):after("\\n")
    local c3 = c2:ownText()
    c3 = string.gsub(c3, "\\n", "\n")
    chapter:setContent(c3)
end