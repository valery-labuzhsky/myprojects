@LAZYGLOBAL off.

set config:ipu to 2000.

run once lib_math.

local sac_started to false.
local sac_stopped to true.

local sac_cur_target to R(0, 0, 0).
local sac_new_target to sac_cur_target.
local sac_pitch_arr to sac_init(50).
local sac_yaw_arr to sac_init(50).
local sac_roll_arr to sac_init(50).
local sac_t0 to time:seconds.
local sac_tar_sp to V(0, 0, 0).
local sac_zero_sp to V(0, 0, 0).
local sac_zero_a to v(0, 0, 0).
local sac_ticks to 0.
local sac_ct to 0.25.
local sac_w to 0.5/sac_ct.
local sac_warpmode to WARPMODE.
local sac_warplevel to WARP.
local sac_warp to false.

local sac_pitch_debug to false.
local sac_yaw_debug to true.
local sac_roll_debug to false.

local sac_facing_old to R(0, 0, 0).
local sac_cturn to r(0, 0, 90).

local sac_ra to v(0, 0, 0).

// TODO check reset

log "poehali" to "stat.csv".
delete "stat.csv".

log "poehali" to "tune.csv".
delete "tune.csv".
log "dc, c, acc, dza, za, ac" to "tune.csv".

function sac_reset {
    set sac_cur_target to ship:facing.
    set sac_t0 to time:seconds.
    set sac_tar_sp to V(0, 0, 0).
    set sac_zero_sp to V(0, 0, 0).
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
        local av to todeg(ship:facing:inverse * ship:angularvel).

        set sac_ticks to sac_ticks + 1.

        local tt to time:seconds - sac_t0.
        //local mf to sac_dir_to_rotvec(sac_rotvec_to_dir(sac_zero_sp*tt):inverse*(sac_facing_old:inverse * ship:facing)).
        local mf to sac_dir_to_rotvec(sac_rotvec_to_dir(sac_zero_sp*tt + sac_zero_a*tt^2/2):inverse*(sac_facing_old:inverse * ship:facing)).

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
                set sac_warp to false.
                sac_tune(tt, mf).

                set sac_t0 to time:seconds.
                set sac_facing_old to ship:facing.

                local nf to sac_dir_to_rotvec(ship:facing:inverse * sac_new_target)/2.
                set sac_zero_sp to nf/sac_ct.

                set sac_tar_sp to sac_dir_to_rotvec(sac_cur_target:inverse * sac_new_target)/sac_ct.
                set sac_zero_sp to sac_speed_limit(nf, sac_zero_sp, av, sac_tar_sp).

                //set sac_v0 to av - sac_zero_sp.
                set sac_zero_a to (sac_zero_sp - av)/sac_ct.
                set sac_zero_sp to av:vec.

                set sac_cur_target to sac_new_target.
            }
            set sac_ticks to 0.
        } else {
            //local ta to sac_calc_ta(mf, av - sac_zero_sp).
            local ta to sac_calc_ta(mf, av - (sac_zero_sp + sac_zero_a*tt)) + sac_zero_a.
            local c to v(sac_seek_x(sac_pitch_arr, ta:x), sac_seek_x(sac_yaw_arr, ta:y), sac_seek_x(sac_roll_arr, ta:z)).
            set ship:control:rotation to -c*sac_cturn.
        }

        if sac_started preserve.
        else set sac_stopped to true.
        if not sac_started sac_log("sac stoped").
    }
}

function sac_rotvec_to_dir {
    parameter v.

    local a to v:mag.
    set v to v:NORMALIZED * sin(a/2).
    return q(v:x, v:y, v:z, cos(a/2)).
}

function sac_dir_to_rotvec {
    parameter d.

    local x to d:starvector.
    local y to d:topvector.
    local z to d:forevector.

    local vec to v(y:z, z:x, x:y) - v(z:y, x:z, y:x).
    local a to arctan2(vec:mag, x:x + y:y + z:z - 1).

    return vec:normalized * a.
}

function sac_speed_limit {
    parameter nt.
    parameter zv.
    parameter sp.
    parameter tv.
    return v(sac_speed_limit_x(-nt:x, zv:x, sp:x, tv:x, sac_pitch_arr),
            sac_speed_limit_x(-nt:y, zv:y, sp:y, tv:y, sac_yaw_arr),
            sac_speed_limit_x(-nt:z, zv:z, sp:z, tv:z, sac_roll_arr)).
}

function sac_speed_limit_x {
    parameter x.
    parameter zv.
    parameter sp.
    parameter tv.
    parameter arr.

    local ac to arr[0].
    local za to arr[1].

    local x2 to x + sp * sac_ct.
    if x * x2 < 0 set x to 0.
    else set x to x2.

    //sac_log("x = "+x).
    //sac_log("tv = "+tv).

    local s to sign(x).
    local lv to -s*sqrt(2*x*sac_max_a_x(arr, s)/4+tv^2). // TODO
    //local lv to -(x*sac_w^2+sac_max_a_x(arr, s)/2)/2.
    set zv to amin(zv, lv).

    local mdv to (1*ac + za)/(2*sac_w).
    local ndv to (-1*ac + za)/(2*sac_w).

    if zv - sp > mdv set zv to sp + mdv.
    else if zv - sp < ndv set zv to sp + ndv.

    //sac_log("sp = "+sp).
    //sac_log("ndv = "+ndv).
    //sac_log("mdv = "+mdv).

    //sac_log("lv = "+lv).
    //sac_log("ntv = "+tv).
    //sac_log(" ").

    return zv.
}

function sac_max_a_x {
    parameter arr.
    parameter sign.

    local ac to arr[0].
    local za to arr[1].
    local c to arr[3].

    local ba to sign * ac + za.

    set ac to ac + c*za.
    set za to za * (1 - c^2).

    set ba to sign*min(sign*ba, ac + sign*za).

    if ba * sign < 0 set ba to 0.

    return ba.
}

function sac_warping {
    return sac_warp.
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
    return list(ac, 0, 0, 0, 1, 0).
}

function sac_log {
    parameter text.

    log_log(text).
}

function sac_x {
    parameter v.

    return v:x.
}

function sac_y {
    parameter v.

    return v:y.
}

function sac_z {
    parameter v.

    return v:z.
}

function sac_stat {
    parameter x.
    parameter l.
    parameter f.

    local s to "".

    for i in l {
        set s to s + ", " + i.
    }

    log x+", "+v+", "+ac+", "+(tc*100)+", "+za+", "+ta to f.
}

function sac_tune {
    parameter tt.
    parameter tx.

    local e0 to constant():e ^ (-sac_w * tt).

    local wt1 to 1 + sac_w * tt.
    local zx to tx / (1 - wt1 * e0).

    local dza to zx * sac_w * sac_w/2.

    local ra to sac_zero_a + dza.
    local dra to ra - sac_ra.

    sac_tune_x(sac_pitch_arr, sac_zero_a:x, dza:x, dra:x, sac_pitch_debug).
    sac_tune_x(sac_yaw_arr, sac_zero_a:y, dza:y, dra:y, sac_yaw_debug).
    sac_tune_x(sac_roll_arr, sac_zero_a:z, dza:z, dra:z, sac_roll_debug).

    set sac_ra to ra.
}

function sac_tune_x {
    parameter arr.
    parameter ta.
    parameter dza.
    parameter dra.
    parameter deb.

    local ac to arr[0].
    local za to arr[1].
    local ra0 to arr[2].
    local c0 to arr[3].
    local mz to arr[4].
    local dza0 to arr[5].

    local c to (ta - za)/ac.

    local mzn to 1.
    if abs(c)<0.9 and dza * dza0 > 0 { // TODO change mz
        set mzn to mz * (1 + min(1, dza/dza0)).
    } else {
        set mz to 1.
    }

    local dc to c - c0.
    local acc to 1 + (dc*dra/4/ac - dc^2/4).//*mz^2.
    set acc to acc * (1 + dza * c * 0.5 / ac).
    if acc < 0.5 set acc to 0.5.
    else if acc > 2 set acc to 2.

    set ac to ac * acc.

    set za to (za - ta)*acc + ta.
    set za to za + dza.// * (0.25/sac_ct).

    if deb {
        log dc+", "+c+", "+acc+", "+dza+", "+za+", "+ac to "tune.csv".
    }

    set arr[0] to ac.
    set arr[1] to za.
    set arr[2] to ra0.
    set arr[3] to c.
    set arr[4] to mzn.
    set arr[5] to dza.
}

function sac_seek_x {
    parameter arr.
    parameter ta.

    local ac to arr[0].
    local za to arr[1].

    local tc to (ta - za)/ac.

    //if false {
    //sac_log("dt = " + dt).
    //sac_log("dx = " + x).
    //sac_log("v2 = " + v2).
    //sac_log("zv = " + zv).
    //sac_log("ac = " + ac).
    //sac_log("c = " + c).
    //sac_log("za = " + za).
    //sac_log("ta = "+ta).
    //sac_log(" ").

        //log x+", "+v+", "+ac+", "+(tc*100)+", "+za+", "+ta to "stat.csv".
    //}

    return tc.
}

function sac_calc_ta {
    parameter x.
    parameter v.

    return -2 * v * sac_w - x * sac_w * sac_w.
}