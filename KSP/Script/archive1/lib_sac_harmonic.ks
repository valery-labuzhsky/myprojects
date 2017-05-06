@LAZYGLOBAL off.

set config:ipu to 2000.

run once lib_math.
run once lib_msmnt.

local sac_started to false.
local sac_stopped to true.

local sac_cur_target to ship:facing.
local sac_new_target to sac_cur_target.
local sac_cur_follow to sac_cur_target.
local sac_new_follow to sac_cur_target.
local sac_pitch_arr to sac_init(50).
local sac_yaw_arr to sac_init(50).
local sac_roll_arr to sac_init(50).
local sac_t0 to time:seconds.
local sac_x0 to v(0, 0, 0).
local sac_v0 to todeg(ship:facing:inverse * ship:angularvel).
local sac_tar_sp to V(0, 0, 0).
local sac_zero_x to r(0, 0, 0).
local sac_zero_v to V(0, 0, 0).
local sac_zero_a to v(0, 0, 0).
local sac_ct0 to 0.25.
local sac_ct to 0.25.
local sac_w to 0.5/sac_ct.
//local sac_w to 0.25/sac_ct. // TODO experimental
local sac_warp to false.

local sac_pitch_debug to false.
local sac_yaw_debug to false.
local sac_roll_debug to false.

local sac_facing_old to R(0, 0, 0).
local sac_cturn to r(0, 0, 90).
local sac_avc to v(0, 0, 0).
local sac_t to 0.

local sac_ra to v(0, 0, 0).

// TODO check reset

local sac_w_av0 to v(0, 0, 0).
local sac_w_an to 0.
local sac_w_ph to 0.
local sac_w_t0 to 0.
local sac_w_w to 0.
local sac_w_wa0 to v(0, 0, 0).
local sac_w_ta0 to v(0, 0, 0).

log "poehali" to "stat.csv".
deletepath("stat.csv").
log "x, v, a, ac, za, c" to "stat.csv".

log "poehali" to "tune.csv".
deletepath("tune.csv").
log "ac, za, c, acc, dza, dc, mz" to "tune.csv".

function sac_reset {
    set sac_cur_target to ship:facing.
    set sac_t0 to time:seconds.
    set sac_tar_sp to V(0, 0, 0).
    set sac_zero_v to V(0, 0, 0).

    set sac_facing_old to ship:facing.
}

function sac_start {
    parameter t.

    set sac_new_target to t.
    set sac_new_follow to t.
    set sac_started to true.
    set sac_stopped to false.

    sac_reset().

    sac_log("sac started").

    when true then {
        local t to 0.
        local facing to 0.
        local angularvel to 0.
        local control to 0.

        msmnt_measure({
            set t to time:seconds.
            set facing to ship:facing.
            set angularvel to ship:angularvel.
            set control to ship:control.
        }).

        local av to todeg(facing:inverse * angularvel).

        local dt to t - sac_t.
        set sac_t to t.
        set sac_avc to sac_avc-control:rotation*sac_cturn:inverse*dt.

        local tt to t - sac_t0.
        local mf to sac_dir_to_rotvec(sac_rotvec_to_dir(sac_zero_x + (sac_zero_v + sac_zero_a*tt/2)*tt):inverse*(sac_facing_old:inverse * facing)).

        local wa to (av - sac_w_av0)/dt.
        set sac_w_av0 to av.

        if tt > sac_ct {
            local tune to true.
            if sac_warp {
                if warp = 0 and kuniverse:timewarp:issettled {
                    set sac_warp to false.
                    sac_reset().
                } else {
                    set tune to false.
                }
            } else {
                if (WARPMODE = "RAILS") {
                    if warp <> 0 or not kuniverse:timewarp:issettled {
                        set sac_warp to true.
                        set tune to false.
                    }
                }
            }
            set sac_ct to sac_ct0 * kuniverse:timewarp:rate.
            set sac_w to 0.5/sac_ct.

            if tune {
                set sac_avc to sac_avc/tt.
                sac_tune(tt, mf, sac_x0, sac_v0).

                set sac_t0 to t.
                set sac_facing_old to facing.

                sac_follow_new_target().
                set sac_tar_sp to sac_dir_to_rotvec(sac_cur_follow:inverse * sac_new_follow)/sac_ct.

                //local fin_v to sac_tar_sp.
                //local fin_x to sac_dir_to_rotvec(ship:facing:inverse * sac_new_target).
                //set sac_zero_a to v(0, 0, 0).
                //set sac_zero_v to fin_v - sac_zero_a * sac_ct.
                //set sac_zero_x to fin_x -(sac_zero_v + sac_zero_a*sac_ct/2)*sac_ct.
                //set sac_x0 to -sac_zero_x.
                //set sac_v0 to av - sac_zero_v.

                set sac_zero_v to sac_tar_sp.
                set sac_zero_x to sac_dir_to_rotvec(facing:inverse * sac_new_target) - sac_zero_v*sac_ct.
                set sac_x0 to -sac_zero_x.
                set sac_v0 to av - sac_zero_v.

                //local ta to sac_calc_ta(sac_x0, sac_v0).
                set sac_zero_a to sac_limit_a(sac_x0, sac_v0).
                sac_nulify(sac_x0, sac_zero_a).
                sac_nulify(sac_v0, sac_zero_a).
                set sac_zero_x to -sac_x0.
                set sac_zero_v to av - sac_v0.

                set sac_cur_target to sac_new_target.
                set sac_cur_follow to sac_new_follow.
                set sac_avc to v(0, 0, 0).
            }
        } else {
            //local ta to sac_calc_ta(mf, av - sac_zero_sp).
            local tv to av - (sac_zero_v + sac_zero_a*tt).
            local ta to sac_calc_ta(mf, tv) + sac_zero_a.
            local c to v(sac_seek_x(sac_pitch_arr, ta:x), sac_seek_x(sac_yaw_arr, ta:y), sac_seek_x(sac_roll_arr, ta:z)).

            // TODO this can be used to fully control ac and za
            local wan to arctan2((wa-sac_w_wa0):y, (ta-sac_w_ta0):y).
            set sac_w_wa0 to wa.
            set sac_w_ta0 to ta.
            local dwan to wan-sac_w_an.
            if dwan > 180 set dwan to dwan - 360.
            else if dwan < -180 set dwan to dwan + 360.
            local resw to dwan/dt.
            set sac_w_an to wan.

            if dwan < 0 {
                set sac_w_w to 0.
                set sac_w_ph to 0.
                set sac_w_t0 to t.
            } else {
                set sac_w_ph to sac_w_ph + dwan.
                if sac_w_ph > 360 {
                    set sac_w_w to sac_w_ph/360/(t - sac_w_t0).
                    local newc to sac_w_w/2/sac_w.
                    if newc > 1
                    //set sac_yaw_arr[0] to newc*sac_yaw_arr[0]. // TODO highly experimental feature

                    set sac_w_ph to 0.
                    set sac_w_t0 to t.
                }
            }

            if sac_yaw_debug {
                log mf:y+", "+tv:y+", "+ta:y+", "+sac_yaw_arr[0]+", "+sac_yaw_arr[1]+", "+c:y+
                        ", " + wa:y + ", " + wan + ", " + sac_w_ph + ", " + sac_w_w
                        to "stat.csv".
            }

            set ship:control:rotation to -c*sac_cturn.
        }

        if sac_started preserve.
        else set sac_stopped to true.
        if not sac_started sac_log("sac stoped").
    }
}

function sac_nulify {
    parameter v.
    parameter a.

    if a:x <> 0 set v:x to 0.
    if a:y <> 0 set v:y to 0.
    if a:z <> 0 set v:z to 0.
}

function sac_limit_a {
    parameter x0.
    parameter v0.

    local ta to sac_calc_ta(x0, v0).
    local c1 to v0+x0*sac_w.

    return v(sac_limit_a_x(ta:x, c1:x, x0:x, v0:x, sac_pitch_arr, sac_pitch_debug),
            sac_limit_a_x(ta:y, c1:y, x0:y, v0:y, sac_yaw_arr, sac_yaw_debug),
            sac_limit_a_x(ta:z, c1:z, x0:z, v0:z, sac_roll_arr, sac_roll_debug)).
}

function sac_limit_a_x {
    parameter ta.
    parameter c1.
    parameter c2.
    parameter v0.
    parameter arr.
    parameter deb.

    local ac to arr[0].
    local za to arr[1].

    local da to 0.
    if c1<>0 and v0*ta>0 {
        local t to 3/sac_w - c2/c1.
        if t>0 {
            local ba to sac_w*c1*constant():e^(-t*sac_w).
            local s to sign(ba).
            local ma to sac_max_a_x(arr, s)/2.
            if ma=0 { // Shouldn't be really used as it never return 0 anymore
                set s to -sign(v0).
                return s * ac + za.
            }
            local xa to ba/ma.
            if xa > 1 {
                local w to sac_w/xa.

                set ta to -2*v0*w - c2*w^2.
                set da to ta.
            }
        }
    }

    set ta to ta - za.
    local c to sign(ta).
    local ma to c * ac.

    if ta*c > ma*c { // it's needed to limit ta to maximum possible
        set da to ma + za.
    }
    return da.
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

function sac_max_a_x {
    parameter arr.
    parameter s.

    local ac to arr[0].
    local za to arr[1].
    local c to arr[3].

    local ba to s * ac + za.

    local ac2 to ac + c*za.
    set za to za * (1 - c^2).

    set ba to s*min(s*ba, ac2 + s*za).

    if ba * s < ac/2 set ba to s*ac/2. // 0. It doesn't allow control to leave limit position when it's 0

    return ba.
}

function sac_warping {
    return sac_warp.
}

function sac_target {
    parameter t.
    set sac_new_target to t.
    set sac_new_follow to t.
}

function sac_target_follow {
    parameter t.
    parameter f.
    set sac_new_target to t.
    set sac_new_follow to f.
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
    parameter x0.
    parameter v0.

    local e0 to constant():e ^ (-sac_w * tt).

    local wt1 to 1 + sac_w * tt.
    local edx to tx - (v0 * tt + wt1 * x0) * e0.
    local zx to edx / (1 - wt1 * e0).
    local dza to zx * sac_w * sac_w/2.

    local ta to sac_calc_ta(x0, v0) + sac_zero_a.
    local ra to ta + dza.
    local dra to ra - sac_ra.

    sac_tune_x(sac_pitch_arr, sac_avc:x,  ta:x, dza:x, dra:x, sac_pitch_debug).
    sac_tune_x(sac_yaw_arr, sac_avc:y, ta:y, dza:y, dra:y, sac_yaw_debug).
    sac_tune_x(sac_roll_arr, sac_avc:z, ta:z, dza:z, dra:z, sac_roll_debug).

    set sac_ra to ra.
}

function sac_tune_x {
    parameter arr.
    parameter c.
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
    local dc to c - c0.

    local mzn to 1.
    if abs(c)<0.9 and dza * dza0 > 0 { // TODO change mz
        set mzn to mz * (1 + min(1, dza/dza0)).
        if mzn*dc^2 > 4 set mzn to 4/dc^2.
    } else {
        set mz to 1.
    }

    local acc to 1 + (dc*dra/4/ac - dc^2/4)*mz. // 4 = max(dc)^2
    //set acc to acc * (1 + dza * c * 0.5 / ac).
    if acc < 0.5 set acc to 0.5.
    else if acc > 2 set acc to 2.

    set ac to ac * acc.

    set za to (za - ta)*acc + ta.
    set za to za + dza.// * (0.25/sac_ct).

    if deb {
        log ac+", "+za+", "+(c*100)+", "+(acc*100)+", "+dza+", "+(dc*100)+", "+mz to "tune.csv".
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

local sac_following to false.
local sac_follow_func to {return ship:facing:vector.}.
local sac_follow_up to heading(180, 0):vector.
local sac_delta to r(0, 0, 0).
local sac_follow_delta to false.

function sac_start_following {
    set sac_following to true.
    sac_stop_following_toggle().

    when false then {
        local f to lookdirup(sac_follow_func(), sac_follow_up).
        if sac_follow_delta {
            sac_target(f*sac_delta).
        } else {
            sac_target_follow(f*sac_delta, f).
        }

        if sac_following preserve.
    }
}

function sac_follow_new_target {
    if sac_following {
        local f to lookdirup(sac_follow_func(), sac_follow_up).
        if sac_follow_delta {
            sac_target(f*sac_delta).
        } else {
            sac_target_follow(f*sac_delta, f).
        }
    }
}

function sac_stop_following {
    set sac_following to false.
}

function sac_start_following_toggle {
    set sac_follow_delta to true.
}

function sac_stop_following_toggle {
    set sac_follow_delta to false.
}

function sac_follow {
    parameter func.

    set sac_follow_func to func.
}

function sac_toggle {
    parameter delta.

    set sac_delta to delta.
}