@LAZYGLOBAL off.

local ctrl_throt to 0.
local ctrl_autostage to false.
local ctrl_as_autostage to false.

local ctrl_udv to 0.
local ctrl_dv0 to 0.
local ctrl_dv to 0.
local ctrl_dv_started to false.
local ctrl_dv_dt to 0.04.
local ctrl_dv_t0 to 0.

local ctrl_state to 0.

local ctrl_min_thrust to 0.00001.

function start_throttle_dv {
    set ctrl_dv_started to true.
    set ctrl_dv_t0 to time:seconds.
    set ctrl_state to 0.
    set ctrl_dv to 0.
    when true then {
        set ctrl_dv_dt to time:seconds - ctrl_dv_t0.
        set ctrl_dv_t0 to time:seconds.
        local twr to calc_max_twr().
        if twr=0 {
            set ship:control:mainthrottle to ctrl_throt.
        } else if ctrl_dv <> 0 {
            local cdv to ctrl_dv.
            local tt to cdv/twr.
            local th to tt/ctrl_dv_dt/8. // 16
            if th > ctrl_throt {
                set th to ctrl_throt.
                set ctrl_state to 0.
            } else {
                set ctrl_state to 1.
            }
            set ship:control:mainthrottle to th.
            set ctrl_dv to ctrl_dv - th*twr*ctrl_dv_dt.
        }
        if ctrl_dv_started preserve.
        else set ship:control:mainthrottle to ctrl_throt.
    }
}

function set_throttle_dv {
    parameter dv.

    set ctrl_dv to dv - ctrl_dv0 + ctrl_dv.
    local fin to false.
    if ctrl_state = 1 and ctrl_udv < dv {
        set fin to true.
    }
    set ctrl_dv0 to ctrl_dv.
    set ctrl_udv to dv.
    return fin.
}

function stop_throttle_dv {
    set ctrl_dv_started to false.
    set ship:control:mainthrottle to ctrl_throt.
}

function set_throttle {
    parameter thr.
    if thr<0 set thr to 0.
    if thr>1 set thr to 1.
    set ctrl_throt to thr.
    if not ctrl_dv_started {
        set ship:control:mainthrottle to ctrl_throt.
        set ship:control:pilotmainthrottle to ctrl_throt.
        //lock throttle to ctrl_throt.
    }
}

function release_throttle {
    set_throttle(0).
    //unlock throttle.
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
    wait t.
    wait until warp = 0.
    wait until kuniverse:timewarp:issettled.
}

function warp_phys {
    parameter w.

    set warpmode to "PHYSICS".
    set warp to w.
}

function warp_stop {
    set warp to 0.
}