function getBook(url, book)
    local doc = docGet(url)
    local eTitles = doc:select("strong:matches((?<!T|t)全集下载)");
    if (eTitles:isEmpty()) then
        Log:i(LT, "title not find");
    else
        local s1 = eTitles:get(0):text()
        book:setTitle(s1);
    end
    local eImg = doc:select("img.picborder[src~=https://www\\.bxwx9\\.org/image/.+\\.jpg]");
    if (eImg:isEmpty()) then
        Log:i(LT, "imgurl and chapterlisturl not find");
    else
        book:setImgUrl(eImg:get(0):attr("src"));
        local eChapter = eImg:get(0):parent();
        if (eChapter:tagName() == "a") then
            book:setChapterListUrl(eChapter:attr("href"));
        else
            Log:i(LT, "chapterurl not find");
        end
        local eTrs = doc:select("tr[bgcolor][height]");
        if (eTrs:size() > 0) then
            local eTds = eTrs:get(0):select("td");
            book:setClassify(eTds:get(1):text());
            book:setAuthor(eTds:get(3):text());
        end
        if (eTrs:size() > 1) then
            local eTds = eTrs:get(1):select("td");
            book:setStatus(eTds:get(5):text());
        end
        local edp1 = doc:select("td:matchesOwn(【内容简介】)");
        if (edp1:isEmpty()) then
            Log:i(LT, "detail not find");
        else
            local edp2 = edp1:get(0):parent():nextElementSibling();
            book:setDetail(edp2:text());
        end
    end
end

function getChapterList(url, list)
    local doc = docGet(url)
    local chaptersDiv = doc:select("#TabCss"):get(0)
    local chaptersAs = chaptersDiv:select("a")
    for i = 0, chaptersAs:size() - 1 do
        local chapter = luajava.newInstance("com.wintersky.windyreader.data.Chapter")
        local e = chaptersAs:get(i)
        chapter:setTitle(e:ownText())
        local base = url
        base = string.match(base, "https?://.+/")
        chapter:setUrl(base .. e:attr("href"))
        list:add(chapter)
    end
end

function getChapter(url, chapter)
    local doc = docGet(url)
    chapter:setTitle(doc:select("#title"):get(0):ownText())
    local content = doc:select("#content"):get(0)
    content:select("br"):after("\\n")
    local contentStr = content:ownText()
    contentStr = string.gsub(contentStr, "\\n", "\n")
    chapter:setContent(contentStr)
end
