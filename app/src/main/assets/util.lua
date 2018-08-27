function matchTag(doc, tag, attr)
    local s = doc:find(("< *%s *%s"):format(tag, attr))
    assert(s ~= nil, ("< *%s *%s not find"):format(tag, attr))
    local m = s
    local e = s + 1
    while m ~= nil and e ~= nil and m < e do
        m = doc:find(("< *%s"):format(tag), m + 1)
        _, e = doc:find(("< */ *%s *>"):format(tag), e + 1)
    end
    assert(e ~= nil, ("< *%s *%s not closed"):format(tag, attr))
    return doc:sub(s, e)
end

function toContent(str)
    return str:gsub(" *\r?\n *", ""):gsub(" *<br */?> *", "\n"):gsub(" *%b<> *", "")
              :gsub("\n+", "\n\n"):gsub("\"", "\\\""):gsub("\\", "\\\\")
              :gsub("&middot;", "·"):gsub("&nbsp;", " "):gsub("&times;", "×")
end
