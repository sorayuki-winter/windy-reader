function getBook(url)
    local doc = docGet(url)
    local BT = doc:select("div.text.t_c"):get(0):select("a"):get(0):ownText();
    local book = Book()
    book:setUrl(url)
    book:setTitle(BT)
    book:setCatalogUrl("http://zxzw.com/26133/")
    return book
end

function getCatalog(url)
    local doc = docGet(url)
    local divCList = doc:select("div.chapters"):get(0)
    return divCList:select("a")
end

function getChapter(url)
    local doc = docGet(url)
    local CT = doc:select("div.text.t_c"):get(0):child(0):ownText()
    local CC = doc:select("div#content"):get(0):html()
    CC = CC:sub(CC:find("</div>") + 6, CC:find("<input") - 1)
    CC = CC:gsub("<br>", "")
    while CC:match("^\n") do
        CC = CC:gsub("\n", "", 1)
    end
    while CC:match("\n$") do
        CC = CC:reverse():gsub("\n", "", 1):reverse()
    end
    local chapter = Chapter()
    chapter:setUrl(url)
    chapter:setTitle(CT)
    chapter:setContent(CC)
    return chapter
end

