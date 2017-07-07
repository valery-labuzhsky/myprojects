@LAZYGLOBAL off.

// 25000, 25, 0.34, 32.32
// 24000, 25, 5.7
// 24000, 26, 4.66
// 24000, 28, 5.15
// 24000, 35, crash
// 24000, 34, crash
// 24000, 33, 0.03
// 24000, 32, 2.0, 35.78
// 23000, 32, 8.71
// 23000, 35, 4.0
// 23000, 36, 3.72
// 23000, 37, 1.41, 37.44
// 22000, 40, 4.73
// 22000, 41, 2.75
// 22000, 42, 1.44, 38.92, falls
// 14000, 42, didn't make it to 30000
// 18000, 42, 35.92, falls
// 18000, 57, xxx, 1st stage didn't make it to landing zone, 2nd stage burnt in atmosphere
// 18000, 50, 0,
// 18000, 49, xxx, 1st stage didn't land, 2nd stage burnt in atmosphere
// 18000, 45, ???, 27.26
// 18000, 43, ???, 27.02
// 18000, 40, ???, 27.13
// 21000, 45, ???, 38.75
// 21000, 46, 0, 39.40
// follow prograde after cut off
// 21000, 40, 2.88
// 21000, 41, 0.99, 39.83
// stage at 25000 and 29000
// 21000, 41, it spins

set ship:control:neutralize to true.
set ship:control:PILOTMAINTHROTTLE to 0.
set ship:control:MAINTHROTTLE to 0.

run once lib_calc.
run once lib_sac.
run once lib_ctrl.
run once lib_gt.
run once lib_msmnt.
run once lib_math.
run once conf(true).

local cutoff_alt to 21000.
local target_angle to 40.

//set sac_yaw_debug to true.

function asc_start {
    parameter th.

    //if false {
    if ship:altitude < cutoff_alt {
        wait 5.
        from {local c is 5.} until c = 0 step {set c to c - 1.} do {
            HUDTEXT(c, 1, 2, 18, RGB(0.5,1.0,0.0), false).
            wait 1.
        }
    }

    sac_start(ship:facing).

    if ship:altitude < cutoff_alt {
        set_throttle(1).
        stage.
        HUD_TEXT("Lift off").

        when ship:altitude > cutoff_alt then {
            if conf_speedup warp_stop().
            release_throttle().
            HUD_TEXT("Main engine cutoff").
        }
    }
    if ship:altitude < stage_alt {
        log_log("Gravity turn").
        if conf_speedup warp_phys(1).
        asc_grav_turn_a(cutoff_alt).//30000
        asc_follow_prograde(stage_alt).

        set_throttle(0).
        stage.
        hud_text("Stage separation").
        set_throttle(0.01).
        wait 1.
        set_throttle(0.1).
        wait 1.
        set_throttle(1).
        if not conf_switch_active hud_text("Second engine startup").

        conf_stage().

        log_log("Moving to transfer orbit").
    //if asc_speedup warp_phys(1).
        asc_inc_ap(th).
        if not conf_switch_active hud_text("Second engine cutoff").

        if conf_speedup and not conf_switch_active warp_phys(3).
        if conf_autowarp {
            log_log("Wait for warp").
            asc_wait_ap(th, 70000).
            log_log("Warp").
            asc_warp_2_ap(th).
        } else {
            asc_wait_ap(th, 99000).
        }

        if conf_speedup and not conf_switch_active warp_stop().
    }

    log_log("Final maneuver").
    asc_burn_2_orb_ap2(th).

    if not conf_switch_active hud_text("Second engine cutoff (2)").
    if not conf_switch_active hud_text("Orbit").

    sac_target(heading3(90, 0, -90)).
    set_throttle(0).
}

// mc for 80000 = 1.3
function asc_grav_turn_a {
    parameter sal.

    sac_follow({return heading(0, 90):vector.}).
    sac_start_following().
    sac_start_following_toggle().

    local a to 0.
    local v to ship:velocity:surface.
    local alt to ship:altitude.
    local q to 0.
    local q0 to 0.
    local mq to false.
    until alt > sal {
        msmnt_measure({
            set v to ship:velocity:surface.
            set alt to ship:altitude.
            set q to ship:q.
        }).
        if not mq {
            if q < q0 and v:mag > 10 {
                hud_text("Max Q").
                set mq to true.
            } else {
                set q0 to q.
            }
        }

        set a to gt_nt_a(1.2*calc_max_twr(), sal, a, target_angle, v:MAG, alt). // 70!
        sac_toggle(r(0, a, 0)).
    }
}

function asc_follow_prograde {
    parameter sal.

    sac_follow({return ship:velocity:surface.}).
    sac_toggle(r(0, 0, 0)).

    wait until ship:altitude > sal.
}

function asc_inc_ap {
    parameter tal.

    sac_follow({return heading(90, 0):vector.}).

    local tpes to asc_init_tgt_pe(tal).
    local tap to calc_abs(tal).

    start_throttle_dv().
    set_throttle(1).
    until ship:obt:APOAPSIS > tal - 1 { // TODO remove -1000
        local tpe to asc_tgt_pe(tap, tpes).

        local r to calc_abs_alt().
        local pe to calc_abs_pe().
        local ap to calc_abs_ap().

        local vv0 to sign(ship:verticalspeed)*calc_vvel(ap, pe, r).
        local vh0 to calc_hvel(ap, pe, r).
        local vv to calc_vvel(tap, tpe, r)-vv0.
        local vh to calc_hvel(tap, tpe, r)-vh0.

        local a to arctan2(vv, vh).
        sac_toggle(r(0, -a, 0)).
        set_throttle_dv(sqrt(vv^2 + vh^2)).
    }
    set_throttle(0).
    stop_throttle_dv().

    sac_stop_following().
}

function asc_init_tgt_pe {
    parameter th.

    local pe to calc_gvt_pe_2_ap(calc_abs(th)).

    return newton_init(pe, pe * 0.9 + 0.1 * ship:body:RADIUS).
}

function asc_tgt_pe{
    parameter tap.
    parameter nlist.

    local ap to calc_abs_ap().
    local pe to calc_abs_pe().
    local r to calc_abs_alt().

    return newton_next(nlist, asc_dv_dpe(tap, nlist[0]), asc_dv_dpe(tap, nlist[1])).
}

function asc_dv_dpe {
    parameter tap.
    parameter tpe.

    local mu to ship:body:mu.
    local r to ship:ALTITUDE + ship:body:RADIUS.
    local ap to calc_abs_ap().
    local pe to calc_abs_pe().

    local vv0 to calc_vvel(ap, pe, r).
    local vh0 to calc_hvel(ap, pe, r).
    local vv1 to calc_vvel(tap, tpe, r).
    local vh1 to calc_hvel(tap, tpe, r).

    local dvh1dr to tap * vh1/(2 * tpe).
    local dvv1dr to -(tap + r)*vv1/(2 * (r - tpe)).
    local dv1 to sqrt((vv1 - vv0)^2 + (vh1 - vh0)^2).
    local dv1dr to ((vv1 - vv0)*dvv1dr + (vh1 - vh0)*dvh1dr) / dv1.
    local dv2dr to -sqrt(mu * tap / (2 * tpe * (tap + tpe))).

    return dv1dr + dv2dr.
}

function asc_wait_ap { // TODO sac follow, new thrust, new directions
    parameter th.
    parameter sal.

    set_throttle(0).
    local next to false.
    local tap to calc_abs(th).
    until next or ship:altitude > sal {
        if calc_min_thr_time_2_orb(tap) + 2 > calc_time_2_alt(tap) {
            sac_target(heading3(90, 0, -90)).
            set_throttle(0).
            if calc_min_thr_time_2_orb(tap) > calc_time_2_alt(tap) set next to true.
        } else {
            //set_throttle(asc_thr_2_ap_wait(th)). // TODO uncomment me
            sac_target(roll_dir(ship:prograde, -90)).
        }
    }
}

function asc_thr_2_ap_wait {
    parameter th.

    local r to calc_abs_alt().
    set th to th + ship:body:radius.

    local pe2 to calc_gvt_pe_2_ap(th).

    local dv to calc_vel(th, pe2, r) - ship:velocity:orbit:mag.
    return dv/calc_max_twr().
}

function asc_warp_2_ap {
    parameter th.

    sac_follow({return ship:prograde:vector.}).
    sac_start_following().

    set_throttle(0).
    local tr to calc_abs(th).
    local wt to calc_time_2_alt(tr) - calc_min_thr_time_2_orb(tr) - 10.
    if wt > 0 {
        warp_wait(wt).
    }

    sac_stop_following().
}

function asc_burn_2_orb_ap {
    parameter th.

    sac_follow({return heading(90, 0):vector.}).
    sac_start_following().
    sac_start_following_toggle().

    local state to 0.

    set_throttle(0).
    start_throttle_dv().
    until state = 3 {
        // TODO run measure
        local ta to torad(ship:obt:TRUEANOMALY).
        local ap to calc_abs_ap().
        local pe to calc_abs_pe().
        local r to calc_abs_alt().
        local mtwr to calc_max_twr().

        // target altitude
        local tr to calc_abs(th).
        local tr0 to tr.

        // target anomaly
        local tta to ta.
        if state < 2 {
            set tta to calc_true_ano(ap, pe, tr).
            if tta < ta set tta to pi2 - tta.
            if tta < ta {
                set state to 2.
                set tta to ta.
            }
        }

        if state = 2 {
            set tr to r.
        } else if ap < tr {
            set tr to ap.
        }

        // dv
        local vv0 to calc_vvel_ta(ap, pe, tr, tta).
        local vh0 to calc_hvel(ap, pe, tr).
        local vv to 0-vv0.
        local vh to calc_hvel(tr, tr, tr)-vh0.
        local dv to sqrt(vv^2 + vh^2).

        // time of burning
        local t to dv/mtwr.
        // ending of burning
        local pd to 10.
        local thr to sqrt(2*t/pd).
        if thr > 1 {
            set thr to 1.
            set t to t + pd/2.
        } else {
            set t to thr * pd.
        }

        if state < 2 {
            // time to target
            local p to calc_time_2_tr_ano(ta, tta, ap, pe, ship:body).
            local tthr to (t/p/2)^2.
            if state = 0 {
                if tthr >= 1 {
                    set state to 1.
                    if not conf_switch_active hud_text("Second engine startup (2)").
                }
            }
            set thr to thr * tthr.

            // angle of attack
            local a to arctan2(vv, vh) - todeg(tta - ta).
            if thr > 1 { // TODO
                local t2 to t/2.
                local ac to 2*(tr0-r - calc_vvel_ta(ap, pe, r, ta)*t2)/t2^2 - calc_vacc(ap, pe, r).
                local na2 to mtwr^2 - ac^2*cos(a)^2.
                if na2 > 0 {// it's not worth it close to 0
                    local na to sqrt(na2) - ac*sin(a).
                    set a to arctan2(ac+na*sin(a), na*cos(a)).
                }
            }
            sac_toggle(r(0, -a, 0)).

            //stat_log("orb",
                    //{return list("a", "state", "thr").},
                    //list(a, state, thr)).
        }

        // set thrust
        if state > 0 {
            set_throttle(thr).
            set_throttle_dv(dv).
        }
        if ta < torad(90) or ta > torad(270) or pe > tr0 set state to 3. // Maybe angle(dv, facing) is better but it's fine
    }

    set_throttle(0).
    stop_throttle_dv().
    sac_stop_following().
}

local asc_thr_start_time to 1.
local asc_thr_end_time to 10.

function asc_thr_stage {
    parameter mtwr.
    parameter dv0.
    parameter dv1.

    if dv1 < dv0 {
        if dv0 > dv1 + mtwr*asc_thr_start_time/2 {
            return 0. // min = /'''\ , max = '''\
        } else {
            return 1. // min = /\ , max = '\
        }
    } else {
        return 2. // min = /\, max = \
    }
}

function asc_max_thr_dx_1 {
    parameter mtwr.
    parameter dv0.
    parameter dv1.
    parameter dx1.

    return dx1 + dv0^2/2/mtwr - dv1^2/2/mtwr.
}

function asc_max_thr_dx_2 {
    parameter mtwr.
    parameter t.

    return mtwr*t^3/6/asc_thr_end_time.
}

function asc_time_to_stop {
    parameter mtwr.
    parameter dv0.

    return sqrt(2*dv0*asc_thr_end_time/mtwr).
}

function asc_min_thr_dx_1 {
    parameter mdx.
    parameter mtwr.
    parameter dv0.

    return mdx + dv0*asc_thr_start_time/2 - mtwr*asc_thr_start_time^2/24.
}

function asc_min_thr_dx_2 {
    parameter mdx. // result of asc_max_thr_dx_2

    local st to asc_thr_start_time.
    local et to asc_thr_end_time.
    return mdx*(et + 2*st)/sqrt((et + st)*et).
}

function asc_burn_2_orb_ap2 {
    parameter th.

    sac_follow({return heading(90, 0):vector.}).
    sac_start_following().
    sac_start_following_toggle().

    local state to 1. // TODO 0 is not needed

    local tr0 to calc_abs(th).
    local tr to tr0.

    set_throttle(0).
    start_throttle_dv().
    until state = 3 {
    // TODO run measure
        local ta to torad(ship:obt:TRUEANOMALY).
        local ap to calc_abs_ap().
        local pe to calc_abs_pe().
        local r to calc_abs_alt().
        local mtwr to calc_max_twr().

    // target anomaly
        local tta to ta.
        //if false {
        if state < 2 {
            set tta to calc_true_ano(ap, pe, tr).
            if tta < ta set tta to pi2 - tta.
            //if false {
            if tta < ta {
                set state to 2.
                set tta to ta.
            }
        }

        if state = 2 {
            set tr to r.
            //set tta to pi.
        } else if ap < tr {
            set tr to ap.
        }

    // dv
        local vv0 to calc_vvel_ta(ap, pe, tr, tta).
        local vh0 to calc_hvel(ap, pe, tr).
        local vv to 0-vv0.
        local vh to calc_hvel(tr, tr, tr)-vh0.
        local dv to sqrt(vv^2 + vh^2).
        local p to calc_time_2_tr_ano(ta, tta, ap, pe, ship:body).
        //if p > pi set p to p - pi2. TODO

        local a to -todeg(tta - ta). // TODO it doesn't work in state 2
        if false {
        //if state = 2 { // TODO hack
            set a to  a + arctan2(vv, vh).
            //set a to -todeg(pi-ta).
        }

        local thr to 0. // todo check usage

        local st to 1. // TODO set
        local et to 10.
        local dx0 to dv*p.
        local dv0 to dv.
        local dv1 to mtwr*et/2.
        local dx1 to mtwr*et^2/6.

        local stg to asc_thr_stage(mtwr, dv0, dv1).

        local mdx to 0.
        local tts to et.
        if stg < 2 {
            set mdx to asc_max_thr_dx_1(mtwr, dv0, dv1, dx1).
        } else {
            set tts to asc_time_to_stop(mtwr, dv0).
            set mdx to asc_max_thr_dx_2(mtwr, tts).
        }

        set thr to tts/et.

        local dt to 0.
        if dx0 > mdx {
            local zdx to 0.
            if stg = 0 {
                set zdx to asc_min_thr_dx_1(mdx, mtwr, dv0).
            } else if stg = 1 {
                set zdx to asc_min_thr_dx_2((asc_max_thr_dx_2(mtwr, (asc_time_to_stop(mtwr, dv0))))).
            } else {
                set zdx to asc_min_thr_dx_2(mdx).
            }

            if dx0 > zdx {
                set thr to 0.
            } else {
                set thr to thr * (zdx - dx0)/(zdx - mdx).
            }
        } else { // overshoot
            set dt to mdx/dv - p.
        }

        local dr to 0.
        if dt <> 0 or tr = ap {
            local n to calc_mean_motion(ap, pe, ship:body).
            local dma to dt*n.
            local ec to calc_ecc(ap, pe).
            local ea0 to calc_ecc_ano(tta, ec).
            local ea to calc_ecc_ano_dt(dma, ea0, ec).
            set dr to calc_alt_ecc_ano(ea, ec, (ap+pe)/2) - tr0.

            if dt <> 0 { // TODO replace it with a hint to next cycle
                local ntr to tr0 + dr.
                set vv0 to calc_vvel_ta(ap, pe, ntr, ea).
                set vh0 to calc_hvel(ap, pe, ntr).
                set vv to 0-vv0.
                set vh to calc_hvel(ntr, ntr, ntr)-vh0.

                local ntta to calc_true_ano_ecc(ea, ec).
                set a to -todeg(ntta - ta).
            }
            log_log("dt = "+dt).
            log_log("dma = "+dma).
            log_log("ec = "+ec).
            log_log("ea0 = "+ea0).
            log_log("ea = "+ea).
            log_log("dr = "+dr).
        }

        if state < 2 {
            set a to  a + arctan2(vv, vh).

            if dr <> 0 {
                local t to (p + dt)*2.
                local acc to -4*dr/t^2.

                set a to a + arctan2(acc, mtwr).
                set thr to thr*sqrt(mtwr^2 + acc^2)/mtwr.
            }

            log_log("a = "+a).
            log_log("thr = "+thr).
            log_log(" ").

            //sac_toggle(r(0, -a, 0)). // TODO it may cause little incorect orbit, but don't jiggle
        }

        sac_toggle(r(0, -a, 0)).

        stat_log("orb",
                {return list("a", "state", "thr", "ta", "tta", "dr", "dt", "ap", "pe", "alt", "stg", "dx0", "mdx").},
                list(a, state, thr, ta, tta, dr, dt, (ap-ship:body:RADIUS), (pe-ship:body:RADIUS), (r-ship:body:RADIUS), stg, dx0, mdx)).

    // set thrust
        set_throttle(thr).
        set_throttle_dv(dv).
        if ta < torad(90) or ta > torad(270) or pe > tr0 set state to 3. // Maybe angle(dv, facing) is better but it's fine
    }

    set_throttle(0).
    stop_throttle_dv().
    sac_stop_following().
}

function calc_ecc_ano_dt { // approximately
    parameter dma. // mean anomaly after ea (dt/n)
    parameter ea. // eccentric anomaly
    parameter ec. // eccentricity

    return ea + dma/(1 - ec*cosin(ea)).
}

function calc_alt_ecc_ano {
    parameter ea.
    parameter ec. // calculate?
    parameter a. // semimajor axis (ap + pe)/2

    return a*(1 - ec*cosin(ea)).
}


