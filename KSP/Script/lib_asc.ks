@LAZYGLOBAL off.

set ship:control:neutralize to true.
set ship:control:PILOTMAINTHROTTLE to 0.
set ship:control:MAINTHROTTLE to 0.

run once lib_calc.
run once lib_sac.
run once lib_ctrl.
run once lib_gt.
run once lib_msmnt.
run once lib_math.

local switch_active to false.
local asc_autowarp to false.
local asc_speedup to false.

local base to LATLNG(-0.097208003409217, -74.5576396864113).
local start_time to 0.
//set sac_yaw_debug to true.

function asc_start {
    parameter th.

    if ship:altitude < 25000 {
        wait 5.
        from {local c is 5.} until c = 0 step {set c to c - 1.} do {
            HUDTEXT(c, 1, 2, 18, RGB(0.5,1.0,0.0), false).
            wait 1.
        }
    }

    sac_start(ship:facing).

    if ship:altitude < 25000 {
        set_throttle(1).
        stage.
        HUD_TEXT("Lift off").

        when ship:altitude > 25000 then {
            release_throttle().
            HUD_TEXT("Main engine cutoff").
        }
    }
    set start_time to time:seconds.
    if ship:altitude < 30000 {
        log_log("Gravity turn").
        if asc_speedup warp_phys(1).
        asc_grav_turn_a(30000).//30000

        set_throttle(0).
        stage.
        hud_text("Stage separation").
        set_throttle(0.01).
        wait 1.
        set_throttle(0.1).
        wait 1.
        set_throttle(1).
        if not switch_active hud_text("Second engine startup").

        log_log("Moving to transfer orbit").
    //if asc_speedup warp_phys(1).
        asc_inc_ap(th).
        if not switch_active hud_text("Second engine cutoff").

        if asc_speedup and not switch_active warp_phys(3).
        if asc_autowarp {
            log_log("Wait for warp").
            asc_wait_ap(th, 70000).
            log_log("Warp").
            asc_warp_2_ap(th).
        }

        if asc_speedup and not switch_active warp_stop().
    }

    log_log("Final maneuver").
    asc_burn_2_orb_ap(th).

    if not switch_active hud_text("Second engine cutoff (2)").
    if not switch_active hud_text("Orbit").

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

        set a to gt_nt_a(1.2*calc_max_twr(), 30000, a, 25, v:MAG, alt). // 70!
        sac_toggle(r(0, a, 0)).
    }
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

        local vv0 to calc_vvel(ap, pe, r).
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
            set_throttle(asc_thr_2_ap_wait(th)).
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
    local wt to calc_time_2_alt(tr) - calc_min_thr_time_2_orb(tr).
    if wt > 0 {
        warp_wait(wt).
    }

    sac_stop_following().
}

function asc_burn_2_orb_ap {
    parameter th.

    when time:seconds - start_time > 5*60 + 25 then {
        log_log(base:position:mag).
    }
    when time:seconds - start_time > 5*60 + 35 then {
        log_log(base:position:mag).
    }
    when time:seconds - start_time > 5*60 + 45 then {
        log_log(base:position:mag).
    }

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
                    if not switch_active hud_text("Second engine startup (2)").
                }
            }
            set thr to thr * tthr.

            // angle of attack
            local a to arctan2(vv, vh) - todeg(tta - ta).
            if thr > 1 {
                local t2 to t/2.
                local ac to 2*(tr0-r - calc_vvel_ta(ap, pe, r, ta)*t2)/t2^2 - calc_vacc(ap, pe, r).
                local na2 to mtwr^2 - ac^2*cos(a)^2.
                if na2 > 0 {// it's not worth it close to 0
                    local na to sqrt(na2) - ac*sin(a).
                    set a to arctan2(ac+na*sin(a), na*cos(a)).
                }
            }
            sac_toggle(r(0, -a, 0)).
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
