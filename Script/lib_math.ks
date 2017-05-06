@LAZYGLOBAL off.

local e to constant:e.
local pi to constant:pi.
local pi_2 to pi/2.
local pi2 to pi*2.

function heading3 {
    parameter dir.
    parameter pitch.
    parameter roll.
    local h to heading(dir, pitch).
    return roll_dir(h, roll).
}

function roll_dir {
    parameter dir.
    parameter roll.
    local r to R(0, 0, roll).
    return dir * r.
}

function newton_init {
    parameter x1.
    parameter x0.

    return list(x1, x0).
}

function newton_next {
    parameter x.
    parameter f1.
    parameter f0.

    local x1 to x[0].
    local x0 to x[1].

    local dx to x1 - x0.
    local df to f1 - f0.
    local dxf to 1.
    if abs(dx) >= abs(df) {
        if dx * df < 0 set dxf to -1.
    } else {
        set dxf to dx / df.
    }

    local x2 to x1 - f1 * dxf.
    set x[0] to x2.
    set x[1] to x1.

    return x2.
}

function sinus {
    parameter a.
    return sin(constant:radtodeg * a).
}

function cosin {
    parameter a.
    return cos(constant:radtodeg * a).
}

function asin2 {
    parameter s.
    parameter h.

    if h = 0 return 0.
    if s > h return pi_2.
    if s < -h return -pi_2.
    return torad(arcsin(s/h)).
}

function acos2 {
    parameter s.
    parameter h.

    if h = 0 return 0.
    if s > h return 0.
    if s < -h return pi.

    return torad(arccos(s/h)).
}

function atan2 {
    parameter x.
    parameter y.

    return torad(arctan2(x, y)).
}

function amin {
    parameter a.
    parameter b.

    if abs(a) < abs(b) return a.
    else return b.
}

function todeg {
    parameter rad.

    return constant:radtodeg * rad.
}

function torad {
    parameter deg.

    return constant:degtorad * deg.
}

function sign {
    parameter x.

    if x < 0 return -1.
    else return 1.
}