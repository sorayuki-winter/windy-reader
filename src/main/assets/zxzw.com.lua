function getCatalog(url)
    local doc = docGet(url)
    local divCList = doc:select("div.chapters"):get(0)
    return divCList:select("a")
end

function getChapter(url)
    local doc = docGet(url)
    local CT = doc:select("div.text.t_c"):get(0):child(0):ownText()
    local CC = doc:select("div#content"):get(0):html()
    --CC = CC:substring(CC:indexOf("</div>")+8, CC:indexOf("<input"));
    --CC = CC:replaceAll("<br>", "");
    CC = CC:sub(CC:find("</div>") + 8, CC:find("<input") - 2)
    CC = CC:gsub("<br>", "")

    local chapter = Chapter()
    chapter:setUrl(url)
    chapter:setTitle(CT)
    chapter:setContent(CC)
    return chapter
end

