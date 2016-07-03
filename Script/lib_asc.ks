@LAZYGLOBAL off.

run once lib_calc.
run once lib_sac.
run once lib_ctrl.

local asc_autowarp to true.
local asc_speedup to false.

local st to false.

function asc_start {
    parameter th.

    set_throttle(1).
    stage.

    sac_start(HEADING3(90, 90, -90)).
//auto_stage_start().

    set_throttle(1).

    if false {
    //when ship:altitude > 25000 then {
        set_throttle(0).
        stage.
        set st to true.
    }
    if false {
    //when st and stage:ready then {
        stage.
        set_throttle(1).
        set st to false.
    }

    when ship:altitude > 25000 then release_throttle().
    log_log("Gravity turn").
//if asc_speedup
    //warp_phys(1).
    asc_grav_turn_a(30000).//30000

    set_throttle(0).
    stage.
    //close_terminal().
//wait until stage:ready.
    wait 4.
//stage.
//wait 1.
    set_throttle(1).

    log_log("Increase apoapsis").
    if asc_speedup warp_phys(2).
    asc_inc_ap(th).

    if asc_speedup warp_phys(3).
    if asc_autowarp {
        log_log("Wait to warp").
        asc_wait_ap(th, 70000).

        log_log("Warp").
        asc_warp_2_ap(th).
    }

    log_log("Wait to burn").
    asc_wait_ap(th, th).

    if asc_speedup warp_stop().
    log_log("Burn to apoapsis").
    asc_burn_2_orb_ap(th).

    log_log("Burn to orbit").
    asc_burn_2_orb(th).

    log_log("Orbit").

    sac_target(heading3(90, 0, -90)).
    set_throttle(0).
}

// mc for 80000 = 1.3
function asc_grav_turn_a {
    parameter sal.

    local a to 0.
    until ship:altitude > sal {
        set a to gt_nt_a(1.2*calc_max_twr(), 30000, a, 30 ). // 70!
        sac_target(HEADING3(90, 90-a, -90)).
    }
}

function asc_grav_turn_p {
    parameter tal.

    until ship:altitude > tal {
        sac_target(HEADING3(90, calc_srf_ang(), -90)).
    }
}

function asc_inc_ap {
    parameter tal.

    local tpes to asc_init_tgt_pe(tal).
    local tap to calc_abs(tal).

    until ship:obt:APOAPSIS > tal-1000 {
        local tpe to asc_tgt_pe(tap, tpes).
        local a to calc_ang_2_ap_pe(tap, tpe).
        sac_target(heading3(90, a, -90)).
    }
}

function asc_init_tgt_pe {
    parameter th.

    local pe to calc_gvt_pe_2_ap(calc_abs(th)).

    return newton_init(pe, pe * 0.9 + 0.1 * ship:body:RADIUS).
}

function asc_tgt_pe{
    parameter tap.
    parameter nlist.

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

function asc_wait_ap {
    parameter th.
    parameter sal.

    set_throttle(0).
    local next to false.
    local tap to calc_abs(th).
    until next or ship:altitude > sal {
        if calc_min_thr_time_2_orb(tap) + 2 > asc_ap_burn_time(th) {
            sac_target(heading3(90, 0, -90)).
            set_throttle(0).
            if calc_min_thr_time_2_orb(tap) > asc_ap_burn_time(th) set next to true.
        } else {
            set_throttle(asc_thr_2_ap_wait(th)).
            sac_target(roll_dir(ship:prograde, -90)).
        }
    }
}

function asc_ap_burn_time {
    parameter th.

    local mu to ship:body:mu.
    local x0 to ship:ALTITUDE + ship:body:RADIUS.
    set th to th + ship:body:RADIUS.

    local vv to ship:VERTICALSPEED.

    local dth to th - x0.
    if vv < 1 return 0.
    return 2 * dth / vv.
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

    set_throttle(0).
    local wt to calc_time_2_ap() - calc_min_thr_time_2_orb(calc_abs(th))*2.
    warp_wait(wt).
    until not sac_warping() {
        sac_target(roll_dir(ship:prograde, -90)).
    }
    set wt to time:seconds.
    until time:seconds - wt > 3 {
        sac_target(roll_dir(ship:prograde, -90)).
    }
}

function asc_burn_2_orb_ap {
    parameter th.

    set_throttle(1).
    local next to false.
    until next {
        local a to asc_ang_2_ap(th).
        sac_target(heading3(90, a, -90)).
        set_throttle(asc_thr_2_ap_burn(th)).
        if th - ship:altitude < 1 or ship:velocity:orbit:MAG > calc_orb_vel() set next to true.
    }
}

function asc_ang_2_ap {
    parameter th.

    local mu to ship:body:mu.
    local ap to ship:obt:APOAPSIS + ship:body:RADIUS.
    local r to ship:ALTITUDE + ship:body:RADIUS.
    local v to ship:velocity:orbit:MAG.

    local ta to 180 - ship:obt:TRUEANOMALY.
    local sa to ship:VERTICALSPEED/v.
    local ca to sqrt(1 - sa ^ 2).
    local san to sa * cos(ta) + ca * sin(ta).

    local vv to v * san.
    local vh to v * ca.

    local x0 to (ship:body:RADIUS + ship:ALTITUDE) * cos(ta).

    local dth to ship:body:RADIUS + th - x0.
    local dap to ap - x0.

    local a to -ta.
    if dth>1 and dap>1 and vv>0 {
        return a + calc_ang_2_vacc(vv * vv * (((ship:body:RADIUS + th - ap)/dap)/dth)/2).
    } else {
        return a.
    }
}

function asc_thr_2_ap_burn {
    parameter th.

    local tat to asc_ap_burn_time(th).
    if tat > 1 {
        return (calc_min_thr_time_2_orb(calc_abs(th)) / tat) * 10 - 9.
    } else {
        local tt to calc_min_thr_time_2_orb(calc_abs(th))/4.
        if tt < 0 return 0.
        return sqrt(tt).
    }
}

function asc_burn_2_orb {
    parameter th.

    until ship:velocity:orbit:MAG > calc_orb_vel() {
        local a to asc_seek_ap(th).
        set_throttle(asc_thr_2_ap_burn(th)).
        sac_target(heading3(90, a, -90)).
    }
}

function asc_seek_ap {
    parameter th.

    local w to 0.5 * get_throttle().
    local r to ship:ALTITUDE + ship:body:RADIUS.
    local vv to ship:VERTICALSPEED.
    local v2 to ship:velocity:orbit:SQRMAGNITUDE.
    local vh2 to v2 - vv ^ 2.
    local a0 to ship:body:mu / (r ^ 2) - vh2 / r.

    local ap to calc_abs_ap().
    local pe to calc_abs_pe().
    set vv to calc_vvel(ap, pe, r).
    if ship:obt:MEANANOMALYATEPOCH > 180 set vv to -vv.
    set a0 to -calc_vacc(ap, pe, r).

    return calc_ang_2_vacc(a0 - 2 * w * vv - w * w * (ship:ALTITUDE - th)).
}
