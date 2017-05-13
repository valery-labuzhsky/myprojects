@LAZYGLOBAL off.

function open_terminal {
    core:part:getmodule("kOSProcessor"):doevent("Open Terminal").
}

function close_terminal {
    open_terminal().
    log_log(core:part:getmodule("kOSProcessor"):ALLACTIONNAMES).
    core:part:getmodule("kOSProcessor"):doaction("Close Terminal", true).
}

function default {
    parameter par.
    parameter def.

    if par = "$<argstart>" return def.
    return par.
}

function omit {
    parameter par.

    return par = "$<argstart>".
}

function hud_text {
    parameter text.

    HUDTEXT(text, 3, 2, 18, RGB(0.5,1.0,0.0), false).
}
