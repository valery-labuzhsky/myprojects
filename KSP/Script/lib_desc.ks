@lazyglobal off.

run once lib_calc.
run once lib_math.
run once lib_log.
run once lib_msmnt.

function desc_dist_self {
    parameter base.
    parameter dv.
    parameter an.

    local vv to 0.
    local vh to 0.
    local r to 0.
    local ba to 0.

    msmnt_measure({
        set vv to ship:verticalspeed.
        set vh to ship:velocity:orbit*heading(90, 0):vector.
        set r to calc_abs_alt().
        set ba to desc_base_ang(base).}).

    return desc_dist(base, dv, an, vv, vh, r, ba).
}

function desc_dist {
    parameter base.
    parameter dv.
    parameter an.
    parameter vv.
    parameter vh.
    parameter r.
    parameter ba.

    local d to 0.
    desc_dist_time(base, dv, an, vv, vh, r, ba, {
        parameter rd.
        parameter t.

        set d to rd.
    }).
    return d.
}

function desc_dist_time {
    parameter base.
    parameter dv.
    parameter an.
    parameter vv.
    parameter vh.
    parameter r.
    parameter ba.
    parameter ret.

    set vv to vv + dv*sinus(an).
    set vh to vh + dv*cosin(an).

    local v to sqrt(vv^2+vh^2).
    local body to ship:body.

    local ap to desc_appe(v, vh, r, body, 1).
    if ap < 0 {
        local fd to ship:body:radius*2*pi - ship:body:radius^2/ap.
        ret(fd, fd).
        return.
    }
    local pe to desc_appe(v, vh, r, body, -1).

    local s to sign(vh).
    local a to sign(vv)*calc_true_ano(ap, pe, r).
    local b to -calc_true_ano(ap, pe, ship:body:radius + base:TERRAINHEIGHT).

    local n to calc_mean_motion(ap, pe, body).
    local ec to calc_ecc(ap, pe).

    local eaa to calc_ecc_ano(a, ec).
    local eab to calc_ecc_ano(b, ec).

    local t to calc_time_2_ecc_ano(eaa, eab, n, ec).
    if a < 0 set a to pi2 + a.
    local d to desc_dist_ang(s*desc_ang_dlt(a, pi2+b, s*ba, s*t)).

    ret(d, t).
}

function desc_dist_atm_newton {
    parameter base.
    parameter dv.
    parameter ddv.
    parameter drag.
    parameter ret.

    local r to 0.
    local east to 0.
    local north to 0.
    local an to 0.
    local ba to 0.
    local v to 0.
    local bp to 0.

    msmnt_measure({
        set r to calc_abs_alt().
        set east to heading(90, 0):vector.
        set north to heading(0, 0):vector.
        set an to VECTORANGLE(ship:velocity:surface, east).
        set ba to desc_base_ang(base).
        set v to ship:velocity:orbit.
        set bp to base:position.
    }).

    local up to vectorcrossproduct(north, east).

    local vv to v*up.
    local vh to v*east.
    local vz to v*north.

    local z to -bp*north.

    set an to torad(an*sign(vv)).
    set vv to vv - drag*sinus(an).
    set vh to vh - drag*cosin(an).

    set an to an-pi/2.
    local d1 to desc_dist(base, dv-ddv/2, an, vv, vh, r, ba).
    local d2 to 0.
    local t to 0.
    desc_dist_time(base, dv+ddv/2, an, vv, vh, r, ba, {
        parameter rd.
        parameter rt.

        set d2 to rd.
        set t to rt.
    }).
    local dd to d2-d1.
    if dd<>0 {
        set ddv to -(d1+3)*ddv/(d2-d1). // TODO it's a little bit wrong
        set dv to dv+ddv.
    }

    ret(dv, ddv, -z/t-vz, t).
}

function desc_dist_ang {
    parameter ang_dlt.

    return ang_dlt * ship:body:radius.
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
    return b - a - ba - w*t.
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

function desc_appe {
    parameter v.
    parameter vh.
    parameter r.
    parameter body.
    parameter aps. // 1/-1

    local mrv2 to 2*body:mu - r*v^2.
    local smrvm to sqrt(body:mu^2 - r*vh^2*mrv2).
    if (aps>0) {
        return r*(body:mu + smrvm)/mrv2.
    } else {
        return r*(body:mu - smrvm)/mrv2.
    }
}

function calc_anomaly_wrong {
    parameter ap.
    parameter pe.
    parameter r.

    return acos2(ap*(r - pe) - pe*(ap-r), r*(ap - pe)).
}

function calc_time_2_ecc_ano_wrong {
    parameter ea_from. // rad
    parameter ea_to. // rad
    parameter n.
    parameter ec.

    return (2*pi - calc_mean_ano(ea_from, ec) - calc_mean_ano(ea_to, ec))/n.
}
