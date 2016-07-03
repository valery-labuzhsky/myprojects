@LAZYGLOBAL off.

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
    return ship:ALTITUDE + ship:body:RADIUS.
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

function calc_ang_2_ap_pe {
    parameter tap.
    parameter tpe.

    local r to calc_abs_alt().
    local pe to calc_abs_pe().
    local ap to calc_abs_ap().

    local vv to calc_vvel(tap, tpe, r).
    local vh to calc_hvel(tap, tpe, r).
    local vv0 to calc_vvel(ap, pe, r).
    local vh0 to calc_hvel(ap, pe, r).

    return arctan2(vv-vv0, vh-vh0).
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

// Velocities

function calc_orb_vel {
    return sqrt(ship:body:mu/(ship:body:radius + ship:altitude)).
}

function calc_orb_dv {
    parameter ap.
    parameter pe.
    parameter tap.

    local mu to ship:body:mu.

    return sqrt(mu / ap) * (sqrt(2 - ap/tap) - sqrt(2*pe/(ap + pe))).
}

// Times

function calc_time_2_ap {
    return ship:obt:period * (1 - ship:obt:MEANANOMALYATEPOCH/180) / 2.
}

function calc_min_thr_time_2_orb {
    parameter tap.

    local mu to ship:body:mu.
    local pe to calc_abs_pe().
    local ap to calc_abs_ap().
    local dv to calc_orb_dv(ap, pe, tap).
    return dv / calc_max_twr().
}
