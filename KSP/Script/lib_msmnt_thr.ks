@lazyglobal off.

run once lib_calc.
run once lib_log.

local msmnt_thr_started to false.

local msmnt_log to true.

local msmnt_pos to 0.
local msmnt_alt to 0.
local msmnt_ap to 0.
local msmnt_pe to 0.
local msmnt_m to 0.
local msmnt_t to 0.
local msmnt_v to 0.
local msmnt_up to 0.
local msmnt_fcng to 0.
local msmnt_q to 0.
local msmnt_mthr to 0.

local msmnt_t0 to 0.
local msmnt_v0 to 0.

function msmnt_thr_start {
    set msmnt_thr_started to true.

    msmnt_thr_measure().
    msmnt_thr_save().

    when msmnt_thr_started then {
        msmnt_thr_measure().

        local a to (msmnt_v - msmnt_v0)/(msmnt_t - msmnt_t0).
        local g to msmnt_up*calc_vacc(msmnt_ap, msmnt_pe, msmnt_pos:mag).
        local thr to ship:maxthrust*msmnt_fcng.

        local fg to g*msmnt_m.
        local dr to (a - g)*msmnt_m - thr.
        local drk to dr:mag/msmnt_q.
        local drq to dr:mag/msmnt_v:mag^2.
        local mv to sqrt(fg:mag/drq).

        if msmnt_log {
            stat_log("measure",
                    {return list("h", "fg", "drq", "v", "mv").},
                    list(msmnt_alt, fg:mag, drq, msmnt_v:mag, mv)).
        }

        msmnt_thr_save().

        preserve.
    }
}

function msmnt_acc_stop {
    set msmnt_thr_started to false.
}

function msmnt_thr_measure {
    msmnt_measure({
        set msmnt_pos to ship:body:position.
        set msmnt_alt to ship:ALTITUDE.
        set msmnt_ap to calc_abs_ap().
        set msmnt_pe to calc_abs_pe().
        set msmnt_m to ship:mass.
        set msmnt_v to ship:velocity:surface.
        set msmnt_t to time:seconds.
        set msmnt_up to heading(0, 90):vector.
        set msmnt_fcng to ship:facing:vector.
        set msmnt_q to ship:q.
        set msmnt_mthr to ship:maxthrust.
    }).
}

function msmnt_thr_save {
    set msmnt_v0 to msmnt_v.
    set msmnt_t0 to msmnt_t.
}

function msmnt_measure {
    parameter f.

    local t to 0.
    until t = time:seconds {
        set t to time:seconds.
        f().
    }
}