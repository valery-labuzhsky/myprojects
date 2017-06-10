@LAZYGLOBAL off.

set config:ipu to 2000.
run once lib_math.
run once lib_msmnt.
run once lib_stat.

local sac_started to false.
local sac_stopped to true.

local sac_cturn to r(0, 0, 90).

local sac_facing_old to ship:facing.
local sac_new_target to ship:facing.
local sac_cur_follow to ship:facing.
local sac_new_follow to ship:facing.
local sac_pitch_arr to sac_init(50).
local sac_yaw_arr to sac_init(50).
local sac_roll_arr to sac_init(50).
local sac_t to time:seconds.
local sac_t0 to time:seconds.
local sac_zero_x to r(0, 0, 0).
local sac_zero_v to V(0, 0, 0).
local sac_zero_a to v(0, 0, 0).
local sac_ct0 to 0.25.
local sac_ct to 0.25.
local sac_w to 0.5/sac_ct.
local sac_warp to false.

local sac_pitch_debug to false.
local sac_yaw_debug to false.
local sac_roll_debug to false.

local sac_stat_av0 to v(0, 0, 0).
local sac_stat_ta0 to v(0, 0, 0).
local sac_stat_ra0 to v(0, 0, 0).

local sac_stat_n to 0.
local sac_stat_ar to v(0, 0, 0).
local sac_stat_at to v(0, 0, 0).
local sac_stat_ar2 to v(0, 0, 0).
local sac_stat_at2 to v(0, 0, 0).
local sac_stat_art to v(0, 0, 0).
local sac_stat_ar0 to v(0, 0, 0).

log "poehali" to "stat.csv".
deletepath("stat.csv").
log "x, v, a0, a, ac, za, c, ra" to "stat.csv".

// TODO check reset
function sac_reset {
    set sac_t to time:seconds.
    set sac_t0 to sac_t.
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

    log_log("sac started").

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

        local tt to t - sac_t0.
        local mf to sac_dir_to_rotvec(sac_rotvec_to_dir(sac_zero_x + (sac_zero_v + sac_zero_a*tt/2)*tt):inverse*(sac_facing_old:inverse * facing)).

        local ra to facing:inverse * todeg(angularvel - sac_stat_av0)/dt.
        local dra to ra - sac_stat_ta0.
        local ra1 to angularvel - sac_stat_av0.
        local ra2 to angularvel.
        set sac_stat_av0 to angularvel.

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
                sac_tune().

                set sac_t0 to t.
                set sac_facing_old to facing.

                sac_follow_new_target().
                local tar_sp to sac_dir_to_rotvec(sac_cur_follow:inverse * sac_new_follow)/sac_ct.

                set sac_zero_v to tar_sp.
                set sac_zero_x to sac_dir_to_rotvec(facing:inverse * sac_new_target) - sac_zero_v*sac_ct.
                local x0 to -sac_zero_x.
                local v0 to av - sac_zero_v.

                set sac_zero_a to sac_limit_a(x0, v0).
                sac_nulify(x0, sac_zero_a).
                sac_nulify(v0, sac_zero_a).
                set sac_zero_x to -x0.
                set sac_zero_v to av - v0.

                set sac_cur_follow to sac_new_follow.
            }
        } else {
            local tv to av - (sac_zero_v + sac_zero_a*tt).
            local ta to sac_calc_ta(mf, tv) + sac_zero_a.
            local c to v(sac_seek_x(sac_pitch_arr, ta:x), sac_seek_x(sac_yaw_arr, ta:y), sac_seek_x(sac_roll_arr, ta:z)).

            local ta2 to ta + dra*dt*sac_w. // *2 // TODO looks good but why 2 times less? dta = ta0 + dra*k - (ta0 - dra*k) => dta = 2*dta*k?
            set sac_stat_ta0 to ta2.
            //set ta2 to ta2 + dra*dt^2*sac_w^2/2. // TODO didn't change anything?

            set sac_stat_n to sac_stat_n + 1.
            set sac_stat_ar to sac_stat_ar + ra.
            set sac_stat_at to sac_stat_at + ta2.
            sac_stat_a2(sac_stat_ar2, ra).
            sac_stat_a2(sac_stat_at2, ta2).
            //sac_stat_ab(sac_stat_art, ra, ta2).
            sac_stat_ab(sac_stat_art, ra, sac_stat_ra0).
            set sac_stat_ra0 to ra. // TODO for corr ai and ai+1

            // TODO keep ac in sence limits => za shouldn't be much more then ra when |c| ~ 1

            if sac_yaw_debug {
                log mf:y+", "+tv:y+", "+sac_zero_a:y+", "+ta:y+", "+sac_yaw_arr[0]+", "+sac_yaw_arr[1]+", "+c:y+", "+ ra:y+", "+ ta2:y to "stat.csv".
            }

            set ship:control:rotation to -c*sac_cturn.
        }

        if sac_started preserve.
        else {
            set sac_stopped to true.
            set ship:control:rotation to v(0, 0, 0).
        }
        if not sac_started log_log("sac stoped").
    }
}

function sac_stat_a2 {
    parameter sa.
    parameter a.

    set sa:x to sa:x + a:x^2.
    set sa:y to sa:y + a:y^2.
    set sa:z to sa:z + a:z^2.
}

function sac_stat_ab {
    parameter sa.
    parameter a.
    parameter b.

    set sa:x to sa:x + a:x*b:x.
    set sa:y to sa:y + a:y*b:y.
    set sa:z to sa:z + a:z*b:z.
}

function sac_tune {
    local mra to sac_stat_ar/sac_stat_n.
    local mta to sac_stat_at/sac_stat_n.

    sac_tune_x(sac_pitch_arr, mra:x, mta:x, sac_stat_ar0:x, sac_stat_at2:x, sac_stat_ar2:x, sac_stat_art:x, sac_pitch_debug).
    sac_tune_x(sac_yaw_arr, mra:y, mta:y, sac_stat_ar0:y, sac_stat_at2:y, sac_stat_ar2:y, sac_stat_art:y, sac_yaw_debug).
    sac_tune_x(sac_roll_arr, mra:z, mta:z, sac_stat_ar0:z, sac_stat_at2:z, sac_stat_ar2:z, sac_stat_art:z, sac_roll_debug).

    set sac_stat_ar0 to mra.
    set sac_stat_n to 0.
    set sac_stat_ar to v(0, 0, 0).
    set sac_stat_ar2 to v(0, 0, 0).
    set sac_stat_at to v(0, 0, 0).
    set sac_stat_at2 to v(0, 0, 0).
    set sac_stat_art to v(0, 0, 0).
}

function sac_tune_x {
    parameter arr.
    parameter mra.
    parameter mta.
    parameter ar0.
    parameter at2.
    parameter ar2.
    parameter art.
    parameter debug.

    local ac0 to arr[0].
    local za0 to arr[1].
    local c0 to arr[2].

    local c1 to (mta - za0)/ac0.
    local dc to c1 - c0.

    local sta2 to at2/sac_stat_n - mta^2.
    local sra2 to ar2/sac_stat_n - mra^2. // It was S1/S2
    if (sra2<0 or sta2<0) {
        log_log(sra2).
        log_log(sta2).
    }
    local sart to sqrt(sra2*sta2).
    //set sta2 to sta2 * corr^2.
    //set sra2 to sra2 * corr^2.
    //set sart to sart * corr.

    //set sra2 to sta2 + (sra2 - sta2)*corr^2. // TODO it should be without squares, but just to try
    //set sart to sqrt(sra2*sta2).

    local dck to dc*ac0.
    local acck to 1/(sta2 + dck^2). // TODO move 4 here somehow (probably +3*ac0)
    local maacc to (sart-sta2)*acck/4 + 1.

    local dcacc to mra - ar0.
    set dcacc to (dcacc*dck-dck^2)*acck/4 + 1.

    local acc to maacc*dcacc.

    if acc < 0.5 set acc to 0.5.
    else if acc > 2 set acc to 2.

    local za to mra + (za0 - mta)*acc.
    set za to za - mra/2 + mta/2.

    //if false {
    if debug {
        //local corr to (art/sac_stat_n - mra*mta)/sart.
        local corr to (art/sac_stat_n - mra^2)/sra2.
        //local corr to 0.
        stat_log("tune",
        {return list("mra", "mta", "sra", "sta", "corr").},
                list(mra, mta, sqrt(sra2), sqrt(sta2), corr)).
    }

    set arr[0] to ac0 * acc.
    set arr[1] to za.
    set arr[2] to c1.
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
    //local c to arr[3].

    local ba to s * ac + za.

    //local ac2 to ac + c*za.
    //set za to za * (1 - c^2).
    //set ba to s*min(s*ba, ac2 + s*za). // There is no need to be too careful - coeffecients are real enough now

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
    return list(ac, 0, 0).
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