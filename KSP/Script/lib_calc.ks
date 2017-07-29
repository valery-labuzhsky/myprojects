@LAZYGLOBAL off.

run once lib_math.

// Simple functions x, v, a

function calc_abs {
    parameter x.
    return x + ship:body:radius.
}

function calc_abs_ap {
    return ship:obt:APOAPSIS + ship:body:RADIUS.
}

function calc_abs_pe {
    return ship:obt:PERIAPSIS + ship:body:RADIUS.
}

function calc_abs_alt {
    return ship:body:position:mag.//ship:ALTITUDE + ship:body:RADIUS.
}

function calc_hvel {
    parameter ap.
    parameter pe.
    parameter r.

    local mu to ship:body:mu.
    local v2 to 2 * mu * ap * pe / (r * r * (ap + pe)).
    if v2 < 0 set v2 to 0.
    return sqrt(v2).
}

function calc_vvel_ta {
    parameter ap.
    parameter pe.
    parameter r.
    parameter ta.

    local s to 1.
    if ta > pi set s to -1.
    return s*calc_vvel(ap, pe, r).
}

function calc_vvel {
    parameter ap.
    parameter pe.
    parameter r.

    local mu to ship:body:mu.
    local v2 to 2 * mu * (ap - r) * (r - pe) / (r * r * (ap + pe)).
    if v2 < 0 set v2 to 0.
    return sqrt(v2).
}

function calc_vel {
    parameter ap.
    parameter pe.
    parameter r.

    local mu to ship:body:mu.
    local v2 to 2 * mu * (ap + pe - r) / (r * (ap + pe)).
    if v2 < 0 set v2 to 0.
    return sqrt(v2).
}

function calc_vacc {
    parameter ap.
    parameter pe.
    parameter r.

    return ship:body:mu * (pe * (ap - r) - ap * (r - pe)) / (r^3 * (ap + pe)).
}

// Thrust

function calc_max_twr {
    return ship:maxthrust/ship:mass.
}

function calc_twr {
//log_log("t = "+get_throttle()).
//log_log("m = "+calc_max_twr()).
    return get_throttle() * calc_max_twr().
}

function calc_real_twr {
    local twr to 0.
    local engs to 0.
    LIST ENGINES IN engs.
    FOR eng IN engs {
        set twr to twr + eng:thrust.
    }
    return twr/ship:mass.
}

// Angles

function calc_ang {
    return arcsin(ship:VERTICALSPEED / ship:velocity:orbit:MAG).
}

function calc_srf_ang {
    local sina to ship:VERTICALSPEED / ship:velocity:surface:MAG.
    if sina > 1 set sina to 1.
    if sina < -1 set sina to -1.
    return arcsin(sina).
}

// TODO sort
function calc_true_ano {
    parameter ap.
    parameter pe.
    parameter r.

    return acos2(pe*(ap-r) - ap*(r - pe), r*(ap - pe)).
}

function calc_true_ano_ecc {
    parameter ea.
    parameter ec.

    return 2*atan2(sqrt(1 + ec)*sinus(ea/2), sqrt(1 - ec)*cosin(ea/2)).
}

function calc_time_2_mean_ano {
    parameter ma_from. // rad
    parameter ma_to. // rad
    parameter n.

    local da to ma_to - ma_from.
    //if da < 0 set da to pi2 + da.
    return da/n.
}

function calc_time_2_ecc_ano {
    parameter ea_from. // rad
    parameter ea_to. // rad
    parameter n.
    parameter ec.

    return calc_time_2_mean_ano(calc_mean_ano(ea_from, ec), calc_mean_ano(ea_to, ec), n).
}

function calc_time_2_tr_ano {
    parameter ta_from. // rad
    parameter ta_to. // rad
    parameter ap.
    parameter pe.
    parameter body.

    local n to calc_mean_motion(ap, pe, body).
    local ec to calc_ecc(ap, pe).

    return calc_time_2_ecc_ano(calc_ecc_ano(ta_from, ec), calc_ecc_ano(ta_to, ec), n, ec).
}

function calc_mean_ano {
    parameter ea. // eccentric anomaly
    parameter ec. // eccentricity

    return ea - ec*sinus(ea).
}

function calc_ecc_ano {
    parameter ta. // true anomaly
    parameter ec.

    return 2*atan2(sqrt(1 - ec)*sinus(ta/2), sqrt(1 + ec)*cosin(ta/2)).
}

function calc_mean_motion {
    parameter ap.
    parameter pe.
    parameter body.

    local a to (ap + pe)/2.

    if a < 0 {
        log_log("ap = "+ap).
        log_log("pe = "+pe).
    }
    return sqrt(body:mu/a)/a.
}

function calc_ecc {
    parameter ap.
    parameter pe.

    return (ap - pe) / (ap + pe).
}
// TODO sort end

function calc_ang_2_vacc {
    parameter ta.

    local ma to calc_twr().

    local a to 90.
    if -ta > ma {
        set a to -90.
    } else if ta < ma {
        set a to arcsin(ta / ma).
    }
    return a.
}

// Lengthes

function calc_gvt_pe_2_ap {
    parameter tap.

    local ap to calc_abs_ap().
    local pe to calc_abs_pe().
    local r to calc_abs_alt().

    local c2r to ap*pe/(ap+pe-r).
    local pe2 to c2r*(tap-r)/(tap-c2r).

    return pe2.
}

function calc_dv_2_ap_pe {
    parameter r.
    parameter ap.
    parameter pe.
    parameter tap.
    parameter tpe.
    parameter ret.

    local vv to calc_vvel(tap, tpe, r).
    local vh to calc_hvel(tap, tpe, r).
    local vv0 to calc_vvel(ap, pe, r).
    local vh0 to calc_hvel(ap, pe, r).

    return ret(vv-vv0, vh-vh0).
}

// Velocities

function calc_orb_vel {
    return sqrt(ship:body:mu/(ship:body:radius + ship:altitude)).
}

function calc_orb_dv {
    parameter ap.
    parameter pe.
    parameter tap.

    local dv to 0.
    calc_dv_2_ap_pe(tap, ap, pe, tap, tap,
            {parameter dvv. parameter dvh.
                set dv to sqrt(dvv^2 + dvh^2).
            }).
    return dv.
}

// Times

function calc_time_2_alt {
    parameter r.

    local ap to calc_abs_ap().
    local pe to calc_abs_pe().
    local ta to torad(ship:obt:TRUEANOMALY).

    local tta to calc_true_ano(ap, pe, r).
    return calc_time_2_tr_ano(ta, tta, ap, pe, ship:body).
}

function calc_min_thr_time_2_orb {
    parameter tap.

    local mu to ship:body:mu.
    local pe to calc_abs_pe().
    local ap to calc_abs_ap().
    local dv to calc_orb_dv(ap, pe, tap).
    return dv / calc_max_twr().
}
