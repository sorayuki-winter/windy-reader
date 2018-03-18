---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by tiandong.
--- DateTime: 18-3-14 下午5:45
---
local M = {}
document = M

M.doc = nil

function M:new(doc)
    local o = {}
    self.__index = self
    setmetatable(o, self)
    o.doc = doc
    return o
end

function M:select(sQuery)
    local es = self.doc:select(sQuery)
    return elements:new(es)
end

function M:toString()
    return self.doc:toString()
end

return document