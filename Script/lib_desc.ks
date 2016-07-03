@lazyglobal off.

run once lib_calc.
run once lib_math.
run once lib_log.

function desc_dist {
    parameter base.
    parameter dv.
    parameter an.

    local vv to ship:verticalspeed + dv*sinus(an).
    local vh to ship:velocity:orbit*heading(90, 0):vector + dv*cosin(an).

    local v to sqrt(vv^2+vh^2).
    local mu to ship:body:mu.
    local r to calc_abs_alt().

    local ap to desc_appe(v, vh, r, mu, 1).
    local pe to desc_appe(v, vh, r, mu, -1).

    local s to sign(vh).
    local a to sign(vv) * desc_ang(ap, pe, r).
    local b to desc_ang(ap, pe, ship:body:radius).
    local ba to desc_base_ang(base).

    local t to desc_fall_t(a, b, ap, pe, mu).

    return desc_dist_ang(s*desc_ang_dlt(a, b, s*ba, s*t)).
}

function desc_dist_atm {
    parameter base.
    parameter dv.

    local an to torad(VECTORANGLE(ship:velocity:surface, heading(90, 0):vector)). // TODO dv?
    set an to an*sign(ship:verticalspeed).

    return desc_dist(base, dv, an).
}

function desc_fall_t_simple {
    local vv to ship:verticalspeed.
    local vh to ship:velocity:orbit*heading(90, 0):vector.

    local v to sqrt(vv^2+vh^2).
    local mu to ship:body:mu.
    local r to calc_abs_alt().

    local ap to desc_appe(v, vh, r, mu, 1).
    local pe to desc_appe(v, vh, r, mu, -1).

    local a to sign(vv) * desc_ang(ap, pe, r).
    local b to desc_ang(ap, pe, ship:body:radius).

    return desc_fall_t(a, b, ap, pe, mu).
}

function desc_dist_ang {
    parameter ang_dlt.

    return ang_dlt * ship:body:radius / pi2.
}

function desc_rot_spd {
    return pi2 / ship:body:rotationperiod.
}

function desc_ang_dlt {
    parameter a. // Current ship angle
    parameter b. // Land ship angle
    parameter ba. // Base angle
    parameter t. // Time of fall

    local w to desc_rot_spd().
    return a + b - ba - w*t.
}

function desc_rot_vec {
    return  ship:velocity:orbit - ship:velocity:surface.
}

function desc_base_ang {
    parameter base.

    local a to torad(vang(base:position - ship:body:position, ship:position - ship:body:position)).
    local s to sign((base:position - ship:position)*heading(90, 0):vector).

    return a*s.
}

function desc_ang {
    parameter ap.
    parameter pe.
    parameter r.

    return acos2(ap*(r - pe) - pe*(ap-r), r*(ap - pe)).
}

function desc_appe {
    parameter v.
    parameter vh.
    parameter r.
    parameter mu.
    parameter aps. // 1/-1

    local mrv2 to 2*mu - r*v^2.
    return r*(mu + aps*sqrt(mu^2 - r*vh^2*mrv2))/mrv2.
}

function desc_fall_t {
    parameter a. // deg
    parameter b. // deg
    parameter ap.
    parameter pe.
    parameter mu.

    local n to desc_mean_motion(ap, pe, mu).
    local pi to constant:pi.
    local ec to desc_ecc(ap, pe).

    return (2*pi - desc_mean(desc_eccan(pi-a, ec), ec) - desc_mean(desc_eccan(pi-b, ec), ec))/n.
}

function desc_mean_motion {
    parameter ap.
    parameter pe.
    parameter mu.

    local a to (ap + pe)/2.

    return sqrt(mu/a^3).
}

function desc_ecc {
    parameter ap.
    parameter pe.

    return (ap - pe) / (ap + pe).
}

function desc_mean {
    parameter ea. // eccentric anomaly
    parameter ec. // eccentricity

    return ea - ec * (sinus((ea))).
}

function desc_eccan {
    parameter ta. // true anomaly
    parameter ec.

    return 2*atan2(sqrt(1-ec)*(sinus((ta/2))), sqrt(1+ec)*(cosin((ta/2)))).
}