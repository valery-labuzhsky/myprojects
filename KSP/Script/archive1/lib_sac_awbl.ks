@LAZYGLOBAL off.

set config:ipu to 2000.

run once lib_math.

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
local sac_w to 0.5/sac_ct.
local sac_warpmode to WARPMODE.
local sac_warplevel to WARP.
local sac_warp to false.

local sac_pitch_debug to false.
local sac_yaw_debug to false.
local sac_roll_debug to false.

local sac_facing_old to R(0, 0, 0).

local sac_last_v to v(0, 0, 0).
local sac_last_ta to v(0, 0, 0).
local sac_last_ra to v(0, 0, 0).
local sac_max_ta to v(0, 0, 0).
local sac_max_ta_n to v(0, 0, 0).
local sac_max_ra to v(0, 0, 0).
local sac_max_ra_n to v(0, 0, 0).

log "poehali" to "stat.csv".
delete "stat.csv".

log "poehali" to "tune.csv".
delete "tune.csv".
log "dc, c, dac, acc" to "tune.csv".

function sac_reset {
    set sac_old_target to ship:facing.
    set sac_cur_target to ship:facing.
    set sac_t0 to time:seconds.
    set sac_x0 to ship:facing:inverse * sac_cur_target.
    set sac_v0 to ship:angularvel:vec.
    set sac_tar_sp to V(0, 0, 0).
    set sac_ltt to time:seconds.
    set sac_ticks to 0.

    set sac_facing_old to ship:facing.
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

        local av to ship:facing:inverse * ship:angularvel. // TODO to deg

        local dt to time:seconds - sac_ltt.
        set sac_ltt to time:seconds.
        set sac_ticks to sac_ticks + 1.

        local tt to time:seconds - sac_t0.

        local ta to v(0, 0, 0).
        set ta:x to sac_target_a_x(sac_pitch_arr, -SHIP:CONTROL:PITCH).
        set ta:y to sac_target_a_x(sac_yaw_arr, SHIP:CONTROL:YAW).
        set ta:z to sac_target_a_x(sac_roll_arr, -SHIP:CONTROL:ROLL).
        local ra to todeg(av - sac_last_v)/dt.
        set sac_last_v to av:vec.
        sac_save_max(sac_last_ta, ta, ra, sac_max_ra, sac_max_ra_n).
        sac_save_max(sac_last_ra, ra, ta, sac_max_ta, sac_max_ta_n).
        set sac_last_ta to ta.
        set sac_last_ra to ra.

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
            set sac_w to 0.5/sac_ct.

            if tune {
                sac_detect_wobble().

                local ff to sac_facing_old:inverse * ship:facing.
                local fo to sac_facing_old:inverse * sac_cur_target.

                set sac_warp to false.
                sac_tune(sac_pitch_arr, tt, -sac_x0:pitch, sac_v0:x, ff:pitch-fo:pitch, av:x, sac_tar_sp:x, sac_pitch_debug).
                sac_tune(sac_yaw_arr, tt, -sac_x0:yaw, sac_v0:y, ff:yaw-fo:yaw, av:y, sac_tar_sp:y, sac_yaw_debug).
                sac_tune(sac_roll_arr, tt, -sac_x0:roll, sac_v0:z, ff:roll-fo:roll, av:z, sac_tar_sp:z, sac_roll_debug).

                set sac_t0 to time:seconds.
                set sac_x0 to r(0, 0, 0).
                set sac_v0 to av:vec.
                set sac_facing_old to ship:facing.
                set sac_old_target to ship:facing.

                local nf to ship:facing:inverse * sac_new_target.
                set nf to R(normX(nf:pitch,0)/2, normX(nf:yaw,0)/2, normX(nf:roll,0)/2).

                set sac_tar_sp to sac_target_speed(sac_x0, nf, sac_ct).
                set sac_tar_sp to sac_speed_limit(nf, sac_tar_sp, av).
                set sac_cur_target to ship:facing * sac_calc_new_target(sac_x0, sac_tar_sp, sac_ct).
            }
            set sac_ticks to 0.
        } else
                {
            local of to ship:facing:inverse * sac_old_target.
            local mf to sac_middle_target(of, f, tt/sac_ct).

            set SHIP:CONTROL:PITCH to -sac_seek(sac_pitch_arr, -mf:pitch, av:x, sac_tar_sp:x, -SHIP:CONTROL:PITCH, ra:x, sac_pitch_debug, dt).
            set SHIP:CONTROL:YAW to sac_seek(sac_yaw_arr, -mf:yaw, av:y, sac_tar_sp:y, SHIP:CONTROL:YAW, ra:y, sac_yaw_debug, dt).
            set SHIP:CONTROL:ROLL to -sac_seek(sac_roll_arr, -mf:roll, av:z, sac_tar_sp:z, -SHIP:CONTROL:ROLL, ra:z, sac_roll_debug, dt).
        }

        if sac_started preserve.
        else set sac_stopped to true.
        if not sac_started sac_log("sac stoped").
    }
}

function sac_detect_wobble {
    sac_detect_wobble_x(sac_max_ra_n:x, sac_max_ta_n:x, sac_max_ra:x, sac_max_ta:x, sac_pitch_arr).
    sac_detect_wobble_x(sac_max_ra_n:y, sac_max_ta_n:y, sac_max_ra:y, sac_max_ta:y, sac_yaw_arr).
    sac_detect_wobble_x(sac_max_ra_n:z, sac_max_ta_n:z, sac_max_ra:z, sac_max_ta:z, sac_roll_arr).
    set sac_max_ta to v(0, 0, 0).
    set sac_max_ta_n to v(0, 0, 0).
    set sac_max_ra to v(0, 0, 0).
    set sac_max_ra_n to v(0, 0, 0).
}

function sac_detect_wobble_x {
    parameter ra_n.
    parameter ta_n.
    parameter ra.
    parameter ta.
    parameter arr.

    if ra_n > 1 and ta_n > 1 and abs(ra_n - ta_n) < 2 and ra > ta {
        //sac_log("wobble").
        local ac to arr[1].
        local acc to 1+(min(2, ra/ta)-1)*min(1, ta/ac).
        set arr[1] to ac * acc.
    }
}

function sac_save_max {
    parameter sac_last_ta.
    parameter ta.
    parameter ra.
    parameter sac_max_ra.
    parameter sac_max_ra_n.

    if sac_last_ta:x * ta:x < 0 {
        local n to sac_max_ra_n:x.
        set sac_max_ra:x to (sac_max_ra:x * n + abs(ra:x))/(n+1).
        set sac_max_ra_n:x to n + 1.
    }
    if sac_last_ta:y * ta:y < 0 {
        local n to sac_max_ra_n:y.
        set sac_max_ra:y to (sac_max_ra:y * n + abs(ra:y))/(n+1).
        set sac_max_ra_n:y to n + 1.
    }
    if sac_last_ta:z * ta:z < 0 {
        local n to sac_max_ra_n:z.
        set sac_max_ra:z to (sac_max_ra:z * n + abs(ra:z))/(n+1).
        set sac_max_ra_n:z to n + 1.
    }
}

function sac_detect_wobble2 {
    //sac_save_max(sac_last_ta, ta, ra, sac_max_ra, sac_max_ra_n).
    //sac_save_max(sac_last_ra, ra, ta, sac_max_ta, sac_max_ta_n).
    parameter ta.
    parameter ra.
    if sac_last_ta:y * ta:y < 0 {
    }
}

function sac_target_a_x {
    parameter arr.
    parameter c.

    local ac to arr[1].
    local za to arr[2].
    return ac*c+za.
}

function sac_calc_new_target {
    parameter x0.
    parameter vv.
    parameter t.

    return r(x0:pitch + vv:x*t, x0:yaw + vv:y*t, x0:roll + vv:z*t).
}

function sac_speed_limit {
    parameter nt.
    parameter tv.
    parameter sp.
    return v(sac_speed_limit_x(-nt:pitch, tv:x, sp:x, sac_pitch_arr),
            sac_speed_limit_x(-nt:yaw, tv:y, sp:y, sac_yaw_arr),
            sac_speed_limit_x(-nt:roll, tv:z, sp:z, sac_roll_arr)).
}

function sac_speed_limit_x {
    parameter x.
    parameter tv.
    parameter sp.
    parameter arr.

    local ac to arr[1].
    local za to arr[2].

    set x to normX(x, 0).
    set sp to todeg(sp).
    local x2 to x + sp * sac_ct.
    if x * x2 < 0 set x to 0.
    else set x to x2.

    //sac_log("x = "+x).
    //sac_log("tv = "+tv).

    local s to sign(x).
    local lv to -s*sqrt(2*x*sac_max_a_x(arr, s)/4). // TODO
    //local lv to -(x*sac_w^2+sac_max_a_x(arr, s)/2)/2.
    set tv to amin(tv, lv).

    local mdv to (1*ac + za)/(2*sac_w).
    local ndv to (-1*ac + za)/(2*sac_w).

    if tv - sp > mdv set tv to sp + mdv.
    else if tv - sp < ndv set tv to sp + ndv.

    //sac_log("sp = "+sp).
    //sac_log("ndv = "+ndv).
    //sac_log("mdv = "+mdv).

    //sac_log("lv = "+lv).
    //sac_log("ntv = "+tv).
    //sac_log(" ").

    return tv.
}

function sac_max_a_x {
    parameter arr.
    parameter sign.

    local ac to arr[1].
    local za to arr[2].
    local c to arr[4].

    //sac_log("c = "+c).

    local ba to sign * ac + za.

    //sac_log("ba = "+(sign*ba)).
    //sac_log("za = "+za).
    //sac_log("ac = "+ac).

    set ac to ac + c*za.
    set za to za * (1 - c^2).

    set ba to sign*min(sign*ba, ac + sign*za).

    //sac_log("ba* = "+(ac + sign*za)).
    //sac_log("za* = "+za).
    //sac_log("ac* = "+ac).
    //sac_log("ma = "+ba).

    if ba * sign < 0 set ba to 0.

    return ba.
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
    return list(1, ac, 0, 0, 0, 1, 0, 0, 0).
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
    parameter deb.

    local state to arr[0].
    local ac to arr[1].
    local za to arr[2].
    local ra0 to arr[3].
    local c0 to arr[4].
    local mz to arr[5].
    local dza0 to arr[6].

    local t2 to time:seconds.
    local grad to 180 / constant():pi.
    set v to v * grad.
    set x0 to normX(x0, 0).
    set v0 to v0 * grad.
    local x to normX(tx, x0 + v0*tt).

    set v0 to v0 - zv.
    local v2 to v - zv.
    local e0 to constant():e ^ (-sac_w * tt).

    local wt1 to 1 + sac_w * tt.
    local edx to x - (v0 * tt + wt1 * x0) * e0.
    local zx to edx / (1 - wt1 * e0).

    local dza to zx * sac_w * sac_w/2.

    local ta to calcTA(x0, v0).
    local c to (ta - za)/ac.

    local mzn to 1.
    if abs(c)<0.9 and dza * dza0 > 0 {
        set mzn to mz * (1 + min(1, dza/dza0)).
    } else {
        set mz to 1.
    }

    local ra to ta + dza.
    local dc to c - c0.
    local dra to ra - ra0.
    local acc to 1 + (dc*dra/4/ac - dc^2/4)*mz^2.
    set acc to acc * (1 + dza * c * 0.5*mz^2 / ac).
    if acc < 0.5 set acc to 0.5.
    else if acc > 2 set acc to 2.

    set ac to ac * acc.

    set za to (za - ta)*acc + ta.
    set za to za + dza.// * (0.25/sac_ct).

    set ra0 to ra.
    set c0 to c.
    set mz to mzn.
    set state to 1.

    if deb {
        log dc+", "+c+", "+acc to "tune.csv".
    }

    set arr[0] to state.
    set arr[1] to ac.
    set arr[2] to za.
    set arr[3] to ra0.
    set arr[4] to c0.
    set arr[5] to mz.
    set arr[6] to dza.

    set arr[8] to v.
}

function sac_seek {
    parameter arr.
    parameter tx.
    parameter v.
    parameter zv.
    parameter c.
    parameter ra.
    parameter deb.

    parameter dt.

    local state to arr[0].
    local ac to arr[1].
    local za to arr[2].

    local lta to arr[7].
    local cv to arr[8].
    set cv to cv + lta*dt.

    local tc to c.

    local v2 to v * 180 / constant():pi.
    set cv to cv - (cv-v2)/10.

    local x to normX(tx, 0).

    //local ta to calcTA(x, (v2+cv)/2 - zv).
    local ta to calcTA(x, v2 - zv).
    set tc to (ta - za)/ac.

    if deb {
    //sac_log("dt = " + dt).
    //sac_log("dx = " + x).
    //sac_log("v2 = " + v2).
    //sac_log("zv = " + zv).
    //sac_log("ac = " + ac).
    //sac_log("c = " + c).
    //sac_log("za = " + za).
    //sac_log("ta = "+ta).
    //sac_log(" ").

        log x+", "+cv+", "+v2+", "+zv+", "+ac+", "+(c*100)+", "+za+", "+ta+", "+ra to "stat.csv".
    }


    if tc >= 1 {
        if state = 1 set state to 2.
        else if state = 3 set state to 4.
        else if state = 5 {
            set ac to ac * 2.
            set state to 4.
        }
        set tc to 1.
    } else if tc <= -1 {
        if state = 1 set state to 3.
        else if state = 2 set state to 5.
        else if state = 4 {
            set ac to ac * 2.
            set state to 5.
        }
        set tc to -1.
    }

    set arr[0] to state.
    set arr[1] to ac.
    set arr[2] to za.

    set arr[7] to ta.
    set arr[8] to cv.

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
