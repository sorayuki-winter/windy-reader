local Android_Log = luajava.bindClass("android.util.Log")
local LT = "wintersky"
function WS(sMsg)
    Android_Log:i(LT, sMsg)
end
