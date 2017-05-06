@LAZYGLOBAL off.

set config:ipu to 2000.

local sac_started to false.
local sac_stopped to true.

local sac_old_target to R(0, 0, 0).
local sac_cur_target to sac_old_target.
local sac_new_target to sac_cur_target.
local sac_pitch_arr to sac_init(50).
local sac_yaw_arr to sac_init(50).
local sac_roll_arr to sac_init(50).
local sac_t0 to time:seconds.
local sac_x0 to sac_cur_target.
local sac_v0 to ship:angularvel:vec.
local sac_tar_sp to V(0, 0, 0).
local sac_ltt to time:seconds.
local sac_ticks to 0.
local sac_ct to 0.25.
local sac_w to 2.
local sac_warpmode to WARPMODE.
local sac_warplevel to WARP.
local sac_warp to false.
local sac_roll_debug to false.

function sac_reset {
    set sac_old_target to sac_new_target.
    set sac_cur_target to sac_new_target.
    set sac_t0 to time:seconds.
    set sac_x0 to ship:facing:inverse * sac_cur_target.
    set sac_v0 to ship:angularvel:vec.
    set sac_tar_sp to V(0, 0, 0).
    set sac_ltt to time:seconds.
    set sac_ticks to 0.
}

function sac_start {
    parameter t.

    set sac_new_target to t.
    set sac_started to true.
    set sac_stopped to false.

    sac_reset().

    sac_log("sac started").

    when true then {
        local f to ship:facing:inverse * sac_cur_target.

        local v to ship:facing:inverse * ship:angularvel.

        set sac_ltt to time:seconds.
        set sac_ticks to sac_ticks + 1.

        local tt to time:seconds - sac_t0.
        if tt > sac_ct {
            local tune to true.
            if WARP > 0 {
                if WARPMODE = "PHYSICS" {
                    if WARP > sac_warplevel {
                        set sac_ct to sac_ct * (WARP - sac_warplevel + 1).
                        sac_reset().
                        set tune to false.
                    }
                } else {
                    sac_reset().
                    set tune to false.
                    set sac_warp to true.
                }
            } else if sac_ticks < 10 {
                set sac_ct to sac_ct * 12.5 / sac_ticks.
                sac_reset().
                set tune to false.
            }
            set sac_warpmode to WARPMODE.
            set sac_warplevel to WARP.
            if sac_ticks > 15 {
                set sac_ct to min(2, sac_ct * 12.5 / sac_ticks).
            }
            if sac_ct > 1 {
                sac_reset().
                set tune to false.
                set sac_warp to true.
            }

            if tune {
                set sac_warp to false.
                sac_tune(sac_pitch_arr, tt, -sac_x0:pitch, sac_v0:x, -f:pitch, v:x, sac_tar_sp:x, -SHIP:CONTROL:PITCH, false).
                sac_tune(sac_yaw_arr, tt, -sac_x0:yaw, sac_v0:y, -f:yaw, v:y, sac_tar_sp:y, SHIP:CONTROL:YAW, false).
                sac_tune(sac_roll_arr, tt, -sac_x0:roll, sac_v0:z, -f:roll, v:z, sac_tar_sp:z, -SHIP:CONTROL:ROLL, sac_roll_debug).

                set sac_t0 to time:seconds.
                set sac_x0 to f.
                set sac_v0 to v:vec.
                set sac_old_target to sac_cur_target.
                local nf to ship:facing:inverse * sac_new_target.
                set nf to R(normX(nf:pitch,0)/2, normX(nf:yaw,0)/2, normX(nf:roll,0)/2).
                set sac_cur_target to ship:facing * nf.

            //set sac_cur_target to sac_new_target.
            //set sac_tar_sp to sac_target_speed(f, ship:facing:inverse * sac_cur_target, sac_ct).
                set sac_tar_sp to sac_target_speed(f, nf, sac_ct).
            }
        //sac_log("sac_ct = " + sac_ct).
        //sac_log("sac_ticks = " + sac_ticks).
            set sac_ticks to 0.
        } else {
            local of to ship:facing:inverse * sac_old_target.
            local mf to sac_middle_target(of, f, tt/sac_ct).

            set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -mf:pitch, v:x, sac_tar_sp:x, -SHIP:CONTROL:PITCH, false).
            set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -mf:yaw, v:y, sac_tar_sp:y, SHIP:CONTROL:YAW, false).
            set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -mf:roll, v:z, sac_tar_sp:z, -SHIP:CONTROL:ROLL, sac_roll_debug).
        }

        if sac_started preserve.
        else set sac_stopped to true.
        if not sac_started sac_log("sac stoped").
    }
}

function sac_warping {
    return sac_warp.
}

function sac_target_speed {
    parameter of.
    parameter f.
    parameter t.

    return V(normX(f:pitch - of:pitch, 0)/t, normX(f:yaw - of:yaw, 0)/t, normX(f:roll - of:roll, 0)/t).
}

function sac_middle_target {
    parameter of.
    parameter f.
    parameter c.

    local bc to 1 - c.
    return R(normX(f:pitch, of:pitch) * c + of:pitch * bc, normX(f:yaw, of:yaw) * c + of:yaw * bc, normX(f:roll, of:roll) * c + of:roll * bc).
}

function sac_target {
    parameter t.
    set sac_new_target to t.
}

function sac_stop {
    set sac_started to false.
    wait until sac_stopped.
}

function sac_init {
    parameter ac.
    return list(0, ac, 0).
}

function sac_log {
    parameter text.

    log_log(text).
}

function sac_tune {
    parameter arr.
    parameter tt.
    parameter x0.
    parameter v0.
    parameter tx.
    parameter v.
    parameter zv.
    parameter c.
    parameter deb.

    local state to arr[0].
    local ac to arr[1].
    local za to arr[2].

    local t2 to time:seconds.
    local grad to 180 / constant():pi.
    local v2 to v * grad.
    set x0 to normX(x0, 0).
    set v0 to v0 * grad.
    local x to normX(tx, x0 + v0*tt).

    if state = 1 {
        set v0 to v0 - zv.
        local e0 to constant():e ^ (-sac_w * tt).

        local wt1 to 1 + sac_w * tt.
        local edx to x - (v0 * tt + wt1 * x0) * e0.
        local zx to edx / (1 - wt1 * e0).

        local c1 to v0 + sac_w * x0.
        local xt to (c1 * tt + x0) * e0.
        local dxw to x0 * tt * e0 - tt * xt.
        if abs(dxw) < 1 {
            if dxw < 0 set dxw to -1.
            else set dxw to 1.
        }
        local dw to (x - xt) / dxw.
        local nw to sac_w + dw.
        local acc to nw * nw / (sac_w * sac_w).
        if acc > 1.5 set acc to 1.5.
        if acc < 0.75 set acc to 0.75.
        local dac to (acc-1) * ac.
        set za to za - c * dac.
        set ac to ac * acc.
        set za to za + zx * sac_w * sac_w * 0.5.
    } else if state = 2 or state = 3 {
        local a to (v2 - v0) / tt.
        local da to a - c * ac - za.
        local dac to da * c * 0.5.
        if dac > 0.5 * ac set dac to 0.5 * ac.
        if dac < -0.25 * ac set dac to -0.25 * ac.
        set da to da - dac * c.
        set ac to ac + dac.
        set za to za + da.
        set state to 1.
    } else {
        set ac to ac * 2.
        set state to 1.
    }

    if deb sac_log("tune").

    set arr[0] to state.
    set arr[1] to ac.
    set arr[2] to za.
}

function sac_seek {
    parameter arr.
    parameter tx.
    parameter v.
    parameter zv.
    parameter c.
    parameter deb.

    local state to arr[0].
    local ac to arr[1].
    local za to arr[2].

    local tc to c.

    local v2 to v * 180 / constant():pi.

    local x to normX(tx, 0).

    if state > 0 {
        local ta to calcTA(x, v2 - zv).
        set tc to (ta - za)/ac.

        if deb {
        //sac_log("dt = " + dt).
            sac_log("dx = " + x).
            sac_log("v2 = " + v2).
            sac_log("zv = " + zv).
            sac_log("ac = " + ac).
            sac_log("c = " + c).
            sac_log("za = " + za).
            sac_log("ta = "+ta).
            sac_log(" ").
        }

        if tc >= 1 {
            if state = 1 set state to 2.
            else if state = 3 set state to 4.
            else if state = 5 set state to 6.
        } else if tc <= -1 {
            if state = 1 set state to 3.
            else if state = 2 set state to 5.
            else if state = 4 set state to 6.
        }
    } else {
        set state to 1.
    }

    set arr[0] to state.
    set arr[1] to ac.
    set arr[2] to za.

    return tc.
}

function calcTA {
    parameter x.
    parameter v.

    return -2 * v * sac_w - x * sac_w * sac_w.
}

function normX {
    parameter x.
    parameter ox.

    until x - ox < 180 {
        set x to x - 360.
    }
    until x - ox > -180 {
        set x to x + 360.
    }
    return x.
}
