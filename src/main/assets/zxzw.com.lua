function getBook(url, key, list)
end

function getChapterList(url, callback)
    local doc = docGet(url)
    local divCList = doc:select("div.chapters"):get(0);
    local aCList = divCList:select("a");
    for i = 0, aCList:size() - 1 do
        local chapter = Chapter();
        local aC = aCList:get(i);
        chapter:setId(i + 1);
        chapter:setTitle(aC:attr("title"));
        chapter:setUrl(aC:absUrl("href"));
        callback:onLoading(chapter);
    end
end

function getChapter(url, chapter)
    local doc = docGet(url)
    local CT = doc:select("div.text.t_c"):get(0):child(0):ownText();
    local CC = doc:select("div#content"):get(0):html();
    --CC = CC:substring(CC:indexOf("</div>")+8, CC:indexOf("<input"));
    --CC = CC:replaceAll("<br>", "");
    CC = CC:sub(CC:find("</div>") + 8, CC:find("<input") - 2)
    CC = CC:gsub("<br>", "")
    local CL = doc:select("a#prevLink"):get(0):absUrl("href");
    local CN = doc:select("a#nextLink"):get(0):absUrl("href");

    chapter:setTitle(CT)
    chapter:setContent(CC)
    chapter:setPrev(CL)
    chapter:setNext(CN)
end

