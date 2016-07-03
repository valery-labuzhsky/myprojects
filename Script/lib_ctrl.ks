@LAZYGLOBAL off.

local ctrl_throt to 0.
local ctrl_autostage to false.
local ctrl_as_autostage to false.

function set_throttle {
    parameter thr.
    if thr<0 set thr to 0.
    if thr>1 set thr to 1.
    set ctrl_throt to thr.
    lock throttle to ctrl_throt.
}

function release_throttle {
    set_throttle(0).
    unlock throttle.
}

function get_throttle {
    return ctrl_throt.
}

function auto_stage_start {
    if not ctrl_autostage {
        set ctrl_autostage to true.
        if not ctrl_as_autostage {
            set ctrl_as_autostage to true.
            when STAGE:LIQUIDFUEL <= 0.01 then {
                if ctrl_autostage {
                    log_log("Stage").
                    stage.
                    preserve.
                } else {
                    set ctrl_as_autostage to false.
                }
            }
        }
    }
}

function auto_stage_stop {
    set ctrl_autostage to false.
}

function warp_wait {
    parameter t.

    warp_stop().
    set warpmode to "RAILS".
    WARPTO(TIME:SECONDS + t).
    wait until warp = 0.
}

function warp_phys {
    parameter w.

    set warpmode to "PHYSICS".
    set warp to w.
}

function warp_stop {
    set warp to 0.
}