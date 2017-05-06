@LAZYGLOBAL off.

run once lib_calc.
run once lib_sac.
run once lib_ctrl.
run once lib_gt.
run once lib_msmnt.
run once lib_math.

local asc_autowarp to true.
local asc_speedup to true.

local st to false.

function asc_start {
    parameter th.

    sac_start(ship:facing).
    sac_follow({return heading(0, 90):vector.}).
    sac_start_following().
    sac_start_following_toggle().

    if ship:altitude < 25000 {
        set_throttle(1).
        stage.

        when ship:altitude > 25000 then {
            release_throttle().
            log_log("Main engine cut off").
        }
        log_log("Gravity turn").
        if asc_speedup warp_phys(1).
    }
    asc_grav_turn_a(30000).//30000
    sac_stop_following().

    set_throttle(0).
    stage.
    log_log("Stage separation").
    set_throttle(0.01).
    wait 1.
    set_throttle(0.1).
    wait 1.
    //close_terminal().
//wait until stage:ready.
    //wait 4.
//stage.
//wait 1.
    set_throttle(1).

    log_log("Moving to transfer orbit").
    //if asc_speedup warp_phys(1).
    asc_inc_ap(th).
    log_log("Transfer orbit").

    if asc_speedup warp_phys(3).
    if asc_autowarp {
        log_log("Wait for warp").
        asc_wait_ap(th, 70000).

        log_log("Warp").
        asc_warp_2_ap(th).
    }

    log_log("Wait for ignition").
    asc_wait_ap(th, th).

    if asc_speedup warp_stop().
    log_log("Burn to apoapsis").
    asc_burn_2_orb_ap4(th).

    log_log("Burn to orbit").
    asc_burn_2_orb(th).

    log_log("Orbit").

    sac_target(heading3(90, 0, -90)).
    set_throttle(0).
}

// mc for 80000 = 1.3
function asc_grav_turn_a {
    parameter sal.

    //local a to acos2(HEADING(0, 90):vector * ship:facing:vector, 1).
    local a to 0.
    local v to ship:velocity:surface.
    local alt to ship:altitude.
    until alt > sal {
        msmnt_measure({
            set v to ship:velocity:surface.
            set alt to ship:altitude.
        }).

        set a to gt_nt_a(1.2*calc_max_twr(), 30000, a, 25, v:MAG, alt). // 70!
        //sac_target(HEADING3(90, 90-a, -90)).
        //sac_target(lookdirup(heading(0, 90):vector*r(0, -a, 0), heading(180, 0):vector)).
        sac_toggle(r(0, a, 0)).
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

function asc_burn_2_orb_ap2 {
    parameter th.

    set_throttle(1).
    local next to false.
    until next {
        local r to 0.
        local ap to 0.
        local pe to 0.
        local vv to 0.
        msmnt_measure({
            set r to calc_abs_alt().
            set ap to calc_abs_ap().
            set pe to calc_abs_pe().
            set vv to ship:verticalspeed.
        }).

        local dvv to 0.
        local dvh to 0.
        local ath to calc_abs(th).
        local tap to ath.
        local tpe to r.

        if tap < tpe {
            set tpe to tap.
            set tap to r.
        }
        calc_dv_2_ap_pe(r, ap, pe, tap, tpe, {
            parameter rdvv.
            parameter rdvh.

            set dvv to rdvv.
            set dvh to rdvh.
        }).
        local t to 0.//calc_time_2_tr_ano(ca, ta, ap, pe, ship:body).
        if vv < 0 {
            set dvv to -dvv.
            if r < ath set t to 1.
            else {
                local ca to -calc_true_ano(ap, pe, r).
                local ta to calc_true_ano(ap, pe, ath).

                set t to calc_time_2_tr_ano(ca, ta, ap, pe, ship:body).
            }
        } else {
            local ca to calc_true_ano(ap, pe, r).
            local ta to 0.
            if ath < ap set ta to calc_true_ano(ap, pe, ath).
            else set ta to pi.

            set t to calc_time_2_tr_ano(ca, ta, ap, pe, ship:body).
        }
        local dv to sqrt(dvv^2 + dvh^2).

        local a to arctan2(dvv, dvh).
        //local a to asc_ang_2_ap(th).
        sac_target(heading3(90, a, -90)).
        set_throttle(dv/t/calc_max_twr()).
        //set_throttle(asc_thr_2_ap_burn(th)).
        //if th - ship:altitude < 1 or ship:velocity:orbit:MAG > calc_orb_vel() set next to true.
        if abs(ap-tap)<0.5 and abs(pe-tpe)<0.5 set next to true.
    }
}

function asc_burn_2_orb_ap3 {
    parameter th.

    local ath to calc_abs(th).

    local tap to calc_abs_ap().
    local tpe to calc_abs_pe().
    local dtap to 1.
    local dtpe to 1.

    start_throttle_dv().
    set_throttle(1).
    local next to false.
    until next {
        local r to 0.
        local ap to 0.
        local pe to 0.
        local vv to 0.
        msmnt_measure({
            set r to calc_abs_alt().
            set ap to calc_abs_ap().
            set pe to calc_abs_pe().
            set vv to ship:verticalspeed.
        }).
        log_log(" ").
        log_log("ap = "+ship:obt:APOAPSIS).
        log_log("pe = "+ship:obt:PERIAPSIS).
        log_log("tap = "+(tap-ship:body:RADIUS)).
        log_log("tpe = "+(tpe-ship:body:RADIUS)).

        asc_burn_2_orb_ap3_newton2(ap, pe, r, sign(vv), ath, tap, tpe, dtap, dtpe, {
            parameter ntap.
            parameter ntpe.
            parameter ndtap.
            parameter ndtpe.

            set tap to ntap.
            set tpe to ntpe.
            set dtap to ndtap.
            set dtpe to ndtpe.
        }).

        if tpe > pe {

        if tpe > ath set tpe to ath.
        if tap < ath set tap to ath.

        local vv0 to vv.
        local vh0 to calc_hvel(ap, pe, r).

        local vv1 to calc_vvel(tap, tpe, r).
        local vh1 to calc_hvel(tap, tpe, r).

        if r > ath set vv1 to -vv1.

        local dvv to vv1 - vv0.
        local dvh to vh1 - vh0.

            log_log("dvv = "+(dvv)).

        local a to arctan2(dvv, dvh).
        sac_target(heading3(90, a, -90)).
        //set_throttle(asc_thr_2_ap_burn(th)).
            set_throttle_dv(sqrt(dvv^2+dvh^2)).
        } else {
            set_throttle_dv(0).
        }
        if th - ship:altitude < 1 or ship:velocity:orbit:MAG > calc_orb_vel() set next to true.
    }
    stop_throttle_dv().
}

function asc_burn_2_orb_ap3_newton {
    parameter ap.
    parameter pe.
    parameter r.
    parameter vvs.
    parameter tr.

    parameter tap.
    parameter tpe.
    parameter dtap.
    parameter dtpe.

    parameter ret.

    local a to calc_max_twr().

    function tf {
        parameter tap.
        parameter tpe.

        return (asc_burn_2_orb_ap3_time(tap, tpe, r, tr)) - (asc_burn_2_orb_ap3_dv(ap, pe, r, vvs, tap, tpe, tr)/a).
    }

    log_log("dap = "+dtap).
    log_log("dpe = "+dtpe).

    if false {
        local tf0 to tf(tap, tpe).
        local tfap to tf(tap+dtap, tpe).
        log_log("tfap = "+tfap).
        log_log("tf0 = "+tf0).
        local ap1 to tap - tfap*dtap/(tfap - tf0).

        local tfpe to tf(tap, tpe+dtpe).
        local pe1 to tpe - tfpe*dtpe/(tfpe - tf0).
    }

    local tf0 to tf(tap, tpe).
    local ap1 to 0.
    nt_zero({
        parameter tap.
        return tf(tap, tpe).
    }, tap, tf0, dtap, {
        parameter nx.
        parameter ndx.

        set ap1 to nx.
    }).
    local pe1 to 0.
    nt_zero({
        parameter tpe.
        return tf(tap, tpe).
    }, tpe, tf0, dtpe, {
        parameter nx.
        parameter ndx.

        set pe1 to nx.
    }).

    function dvf {
        parameter tap.
        parameter tpe.

        return asc_burn_2_orb_ap3_dv(ap, pe, r, vvs, tap, tpe, tr).
    }

    if false {
        local dvf1 to dvf(tap, tpe).
        local dvfap0 to dvf(tap-dtap, tpe).
        local dvfap2 to dvf(tap+dtap, tpe).
        local ddvdap02 to (dvfap2 - dvfap0)/2/dtap.
        local ddvdap01 to (dvf1 - dvfap0)/dtap.
        log_log("ddvdap02 = "+ddvdap02).
        log_log("ddvdap01 = "+ddvdap01).
        local ap2 to tap - ddvdap02*dtap/2/(ddvdap02 - ddvdap01).

        local dvfpe0 to dvf(tap, tpe-dtpe).
        local dvfpe2 to dvf(tap, tpe+dtpe).
        local ddvdpe02 to (dvfpe2 - dvfpe0)/2/dtpe.
        local ddvdpe01 to (dvf1 - dvfpe0)/dtpe.
        log_log("ddvdpe02 = "+ddvdpe02).
        log_log("ddvdpe01 = "+ddvdpe01).
        local pe2 to tpe - ddvdpe02*dtpe/2/(ddvdpe02 - ddvdpe01).
    }

    local dvf1 to dvf(tap, tpe).
    local ap2 to 0.
    nt_max({
        parameter tap.

        return dvf(tap, tpe).
    }, tap, dvf1, dtap, {
        parameter nx.
        parameter ndx.

        set ap2 to nx.
    }).
    local pe2 to 0.
    nt_max({
        parameter tpe.

        return dvf(tap, tpe).
    }, tpe, dvf1, dtpe, {
        parameter nx.
        parameter ndx.

        set pe2 to nx.
    }).

    log_log("ap1 = "+(ap1-ship:body:radius)).
    log_log("pe1 = "+(pe1-ship:body:radius)).
    log_log("ap2 = "+(ap2-ship:body:radius)).
    log_log("pe2 = "+(pe2-ship:body:radius)).

    local a1 to tap - ap1.
    local b1 to pe1 - tpe.
    local c1 to pe1*ap1 - tpe*tap.

    local a2 to tap - ap2.
    local b2 to pe2 - tpe.
    local c2 to pe2*ap2 - tpe*tap.

    local d to b2*a1 - b1*a2.

    local nap to (c2*a1 - c1*a2)/d. // ap
    local npe to (c2*b1 - c1*b2)/d. // pe

    ret(nap, npe, nap - tap, npe - tpe).
}


function asc_burn_2_orb_ap3_newton2 {
    parameter ap.
    parameter pe.
    parameter r.
    parameter vvs.
    parameter tr.

    parameter tap.
    parameter tpe.
    parameter dtap.
    parameter dtpe.

    parameter ret.

    local a to calc_max_twr().

    function tf {
        parameter tap.
        parameter tpe.

        return (asc_burn_2_orb_ap3_time(tap, tpe, r, tr)) - (asc_burn_2_orb_ap3_dv(ap, pe, r, vvs, tap, tpe, tr)/a).
    }

    log_log("dap = "+dtap).
    log_log("dpe = "+dtpe).


    local tf0 to tf(tap, tpe).
    local tfap to tf(tap+dtap, tpe).
    local tfpe to tf(tap, tpe+dtpe).

    local dtap to (tfap-tf0)/dtap.
    local dtpe to (tfpe-tf0)/dtpe.

    function dvf {
        parameter tap.
        parameter tpe.

        return asc_burn_2_orb_ap3_dv(ap, pe, r, vvs, tap, tpe, tr).
    }

    local dt2 to dtap^2 + dtpe^2.

    function xap {
        parameter x.

        return (dtpe*x - dtap)*tf0/dt2.
    }

    function xpe {
        parameter x.

        return (-dtap*x - dtpe)*tf0/dt2.
    }

    function dvfm {
        parameter x.

        local tap to xap(x).
        local tpe to xpe(x).
        return dvf(tap, tpe).
    }

    nt_max(dvfm@, 0, dvfm(0), 1, {
        parameter nx.
        parameter ndx.

        local nap to xap(nx).
        local npe to xpe(nx).
        if nap < tr {
            log_log("zero").
            nt_zero({
                parameter tpe.
                return tf(tap, tpe).
            }, tpe, tf0, dtpe, {
                parameter nx.
                parameter ndx.

                ret(tr, nx, 1, nx - tpe).
            }).
        } else {
            log_log("max").
            ret(nap, npe, nap - tap, npe - tpe).
        }
    }).
}

function nt_zero {
    parameter f.
    parameter x0.
    parameter f0.
    parameter dx.
    parameter ret.

    if dx = 0 set dx to 1.

    local f1 to f(x0 + dx).
    local df to f1 - f0.
    if df=0 nt_zero(f, x0, f0, dx*2, ret).
    else ret(x0 - f1*dx/df, dx).
}

function nt_max {
    parameter f.
    parameter x1.
    parameter f1.
    parameter dx.
    parameter ret.

    if dx = 0 set dx to 1.

    local f0 to f(x1-dx).
    local f2 to f(x1+dx).
    local df02 to (f2 - f0)/2/dx.
    local df01 to (f1 - f0)/dx.
    local ddf to df02 - df01.

    if ddf=0 nt_max(f, x1, f1, dx*2, ret).
    else ret(x1 - df02*dx/2/ddf, dx).
}

function asc_burn_2_orb_ap3_time {
    parameter ap.
    parameter pe.
    parameter r.
    parameter tr.

    local ca to calc_true_ano(ap, pe, r).
    local ta to calc_true_ano(ap, pe, tr).
    if ca > ta {
        set ca to -ca.
        set ta to -ta.
    }

    return calc_time_2_tr_ano(ca, ta, ap, pe, ship:body).
}

function asc_burn_2_orb_ap3_dv {
    parameter ap.
    parameter pe.
    parameter r.
    parameter vvs. // to 1.//-1
    parameter tap.
    parameter tpe.
    parameter tr.

    local vv0 to vvs*calc_vvel(ap, pe, r).
    local vh0 to calc_hvel(ap, pe, r).

    local vv to calc_vvel(tap, tpe, r).
    local vh to calc_hvel(tap, tpe, r).

    if r > tr set vv to -vv.

    local dv to sqrt((vv-vv0)^2+(vh-vh0)^2).

    set vv0 to calc_vvel(tap, tpe, tr).
    set vh0 to calc_hvel(tap, tpe, tr).

    set vh to calc_hvel(tr, tr, tr).
    set dv to dv + sqrt(vv0^2+(vh-vh0)^2).

    return dv.
}

function asc_burn_2_orb_ap4 {
    parameter th.

    set_throttle(1).
    local next to false.
    until next {
        local a to asc_ang_2_ap4(th).
        //log_log("a = "+a).
        sac_target(heading3(90, a, -90)).
        set_throttle(asc_thr_2_ap_burn(th)).
        if th - ship:altitude < 1 or ship:velocity:orbit:MAG > calc_orb_vel() set next to true.
    }
}

function asc_ang_2_ap4 {
    parameter th.

    local ta to ship:obt:TRUEANOMALY.
    local ap to calc_abs_ap().
    local pe to calc_abs_pe().
    local r to calc_abs_alt().

    local tr to calc_abs(th).
    local tta to todeg(calc_true_ano(ap, pe, tr)).

    local vv0 to calc_vvel(ap, pe, tr).
    local vh0 to calc_hvel(ap, pe, tr).

    local vv to 0.
    local vh to calc_hvel(tr, tr, tr).

    local an to arctan2(vv-vv0, vh-vh0).
    //log_log("an = "+an).
    //log_log("ta = "+ta).
    //log_log("tta = "+tta).
    return an - tta + ta.
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
        return a + calc_ang_2_vacc(vv * vv * (ship:body:RADIUS + th - ap)/dap/dth/2).
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
