@lazyglobal off.

run once lib_desc.
run once lib_math_.

run once lib_math.

run once lib_calc.

function desc_ang_ {
    parameter ap.
    parameter ap_.
    parameter pe.
    parameter pe_.
    parameter r.
    parameter r_.

    return acos2_(ap*(r - pe) - pe*(ap - r), ap_*(r - pe) + ap*(r_ - pe_) - pe_*(ap - r) - pe*(ap_ - r_), r*(ap - pe), r_*(ap - pe) + r*(ap_ - pe_)).
}

function desc_ang__ {
    parameter ap.
    parameter ap_.
    parameter ap__.
    parameter pe.
    parameter pe_.
    parameter pe__.
    parameter r.
    parameter r_.
    parameter r__.

    return acos2__(ap*(r - pe) - pe*(ap - r), ap_*(r - pe) + ap*(r_ - pe_) - pe_*(ap - r) - pe*(ap_ - r_), ap__*(r - pe) + 2*ap_*(r_ - pe_) + ap*(r__ - pe__) - pe__*(ap - r) - 2*pe_*(ap_ - r_) - pe*(ap__ - r__), r*(ap - pe), r_*(ap - pe) + r*(ap_ - pe_), r__*(ap - pe) + 2*r_*(ap_ - pe_) + r*(ap__ - pe__)).
}

function desc_ang_dlt_ {
    parameter a_.
    parameter b_.
    parameter ba_.
    parameter t_.

    local w to desc_rot_spd().
    return a_ + b_ - ba_ - w*t_.
}

function desc_ang_dlt__ {
    parameter a__.
    parameter b__.
    parameter ba__.
    parameter t__.

    local w to desc_rot_spd().
    return a__ + b__ - ba__ - w*t__.
}

function desc_appe_ {
    parameter v.
    parameter v_.
    parameter vh.
    parameter vh_.
    parameter r.
    parameter r_.
    parameter body.
    parameter aps.

    local mrv2 to 2*body:mu - r*v^2.
    local smrvm to sqrt(body:mu^2 - r*vh^2*mrv2).
    local mrv2_ to -r_*v^2 - 2*r*v_*v.
    local smrvm_ to (-r_*vh^2*mrv2 - 2*r*vh_*vh*mrv2 - r*vh^2*mrv2_)/2/sqrt(body:mu^2 - r*vh^2*mrv2).
    if (aps>0) {
        return r_*(body:mu + smrvm)/mrv2 + r*smrvm_/mrv2 - r*(body:mu + smrvm)*mrv2_/mrv2^2.
    }
    else {
        return r_*(body:mu - smrvm)/mrv2 - r*smrvm_/mrv2 - r*(body:mu - smrvm)*mrv2_/mrv2^2.
    }
}

function desc_appe__ {
    parameter v.
    parameter v_.
    parameter v__.
    parameter vh.
    parameter vh_.
    parameter vh__.
    parameter r.
    parameter r_.
    parameter r__.
    parameter body.
    parameter aps.

    local mrv2 to 2*body:mu - r*v^2.
    local smrvm to sqrt(body:mu^2 - r*vh^2*mrv2).
    local mrv2_ to -r_*v^2 - 2*r*v_*v.
    local smrvm_ to (-r_*vh^2*mrv2 - 2*r*vh_*vh*mrv2 - r*vh^2*mrv2_)/2/sqrt(body:mu^2 - r*vh^2*mrv2).
    local mrv2__ to -r__*v^2 - 4*r_*v_*v - 2*r*v__*v - 2*r*v_^2.
    local smrvm__ to (-r__*vh^2*mrv2 - 4*r_*vh_*vh*mrv2 - 2*r*vh__*vh*mrv2 - 2*r*vh_^2*mrv2 - 2*r_*vh^2*mrv2_ - 4*r*vh_*vh*mrv2_ - r*vh^2*mrv2__)/2/sqrt(body:mu^2 - r*vh^2*mrv2) - (-r_*vh^2*mrv2 - 2*r*vh_*vh*mrv2 - r*vh^2*mrv2_)^2/4/sqrt(body:mu^2 - r*vh^2*mrv2)^3.
    if (aps>0) {
        return r__*(body:mu + smrvm)/mrv2 + 2*r_*smrvm_/mrv2 + r*smrvm__/mrv2 - 2*r_*(body:mu + smrvm)*mrv2_/mrv2^2 - 2*r*smrvm_*mrv2_/mrv2^2 - r*(body:mu + smrvm)*mrv2__/mrv2^2 + 2*r*(body:mu + smrvm)*mrv2_^2/mrv2^3.
    }
    else {
        return r__*(body:mu - smrvm)/mrv2 - 2*r_*smrvm_/mrv2 - r*smrvm__/mrv2 - 2*r_*(body:mu - smrvm)*mrv2_/mrv2^2 + 2*r*smrvm_*mrv2_/mrv2^2 - r*(body:mu - smrvm)*mrv2__/mrv2^2 + 2*r*(body:mu - smrvm)*mrv2_^2/mrv2^3.
    }
}

function desc_dist_newton3 {
    parameter base.
    parameter dv.
    parameter an.
    parameter ret.

    local vv to ship:verticalspeed + dv*sinus(an).
    local vh to ship:velocity:orbit*heading(90, 0):vector + dv*cosin(an).
    local r to calc_abs_alt().
    local ba to desc_base_ang(base).
    local vz to ship:velocity:orbit*heading(0, 0):vector.
    local xz to (ship:position - base:position)*heading(0, 0):vector.

    local v to sqrt(vv^2 + vh^2).
    local body to ship:body.
    local ap to desc_appe(v, vh, r, body, 1).
    local pe to desc_appe(v, vh, r, body, -1).
    local s to sign(vh).
    local a to sign(vv)*calc_anomaly_wrong(ap, pe, r).
    local b to (calc_anomaly_wrong((ap), (pe), (ship:body:radius + base:TERRAINHEIGHT))).
    local n to calc_mean_motion(ap, pe, body).
    local ec to calc_ecc(ap, pe).
    local eaa to calc_ecc_ano(pi - a, ec).
    local eab to calc_ecc_ano(pi - b, ec).
    local t to calc_time_2_ecc_ano_wrong(eaa, eab, n, ec).
    local f to desc_dist_ang(s*desc_ang_dlt(-a, b, s*ba, s*t)).

    local vv_v to sinus(an).
    local vh_v to cosin(an).
    local v_v to (vv_v*vv + vh_v*vh)/sqrt(vv^2 + vh^2).
    local ap_v to desc_appe_(v, v_v, vh, vh_v, r, 0, body, 1).
    local pe_v to desc_appe_(v, v_v, vh, vh_v, r, 0, body, -1).
    local a_v to sign(vv)*desc_ang_(ap, ap_v, pe, pe_v, r, 0).
    local b_v to desc_ang_(ap, ap_v, pe, pe_v, ship:body:radius + base:TERRAINHEIGHT, 0).
    local n_v to desc_mean_motion_(ap, ap_v, pe, pe_v, body).
    local ec_v to desc_ecc_(ap, ap_v, pe, pe_v).
    local eaa_v to desc_eccan_(pi - a, -a_v, ec, ec_v).
    local eab_v to desc_eccan_(pi - b, -b_v, ec, ec_v).
    local t_v to desc_fall_t_(eaa, eaa_v, eab, eab_v, n, n_v, ec, ec_v).
    local f_v to ship:body:radius*s*desc_ang_dlt_(a_v, b_v, 0, s*t_v).

    local vv_a to dv*cosin(an).
    local vh_a to -dv*sinus(an).
    local v_a to (vv_a*vv + vh_a*vh)/sqrt(vv^2 + vh^2).
    local ap_a to desc_appe_(v, v_a, vh, vh_a, r, 0, body, 1).
    local pe_a to desc_appe_(v, v_a, vh, vh_a, r, 0, body, -1).
    local a_a to sign(vv)*desc_ang_(ap, ap_a, pe, pe_a, r, 0).
    local b_a to desc_ang_(ap, ap_a, pe, pe_a, ship:body:radius + base:TERRAINHEIGHT, 0).
    local n_a to desc_mean_motion_(ap, ap_a, pe, pe_a, body).
    local ec_a to desc_ecc_(ap, ap_a, pe, pe_a).
    local eaa_a to desc_eccan_(pi - a, -a_a, ec, ec_a).
    local eab_a to desc_eccan_(pi - b, -b_a, ec, ec_a).
    local t_a to desc_fall_t_(eaa, eaa_a, eab, eab_a, n, n_a, ec, ec_a).
    local f_a to ship:body:radius*s*desc_ang_dlt_(a_a, b_a, 0, s*t_a).

    local vv__a to -dv*sinus(an).
    local vh__a to -dv*cosin(an).
    local v__a to (vv__a*vv + vv_a^2 + vh__a*vh + vh_a^2)/sqrt(vv^2 + vh^2) - (vv_a*vv + vh_a*vh)^2/sqrt(vv^2 + vh^2)^3.
    local ap__a to desc_appe__(v, v_a, v__a, vh, vh_a, vh__a, r, 0, 0, body, 1).
    local pe__a to desc_appe__(v, v_a, v__a, vh, vh_a, vh__a, r, 0, 0, body, -1).
    local a__a to sign(vv)*desc_ang__(ap, ap_a, ap__a, pe, pe_a, pe__a, r, 0, 0).
    local b__a to desc_ang__(ap, ap_a, ap__a, pe, pe_a, pe__a, ship:body:radius + base:TERRAINHEIGHT, 0, 0).
    local n__a to desc_mean_motion__(ap, ap_a, ap__a, pe, pe_a, pe__a, body).
    local ec__a to desc_ecc__(ap, ap_a, ap__a, pe, pe_a, pe__a).
    local eaa__a to desc_eccan__(pi - a, -a_a, -a__a, ec, ec_a, ec__a).
    local eab__a to desc_eccan__(pi - b, -b_a, -b__a, ec, ec_a, ec__a).
    local t__a to desc_fall_t__(eaa, eaa_a, eaa__a, eab, eab_a, eab__a, n, n_a, n__a, ec, ec_a, ec__a).
    local f__a to ship:body:radius*s*desc_ang_dlt__(a__a, b__a, 0, s*t__a).

    ret(dv-f/f_v, an-f_a/f__a, -xz/t - vz).
}

function desc_ecc_ {
    parameter ap.
    parameter ap_.
    parameter pe.
    parameter pe_.

    return (ap_ - pe_)/(ap + pe) - (ap - pe)*(ap_ + pe_)/(ap + pe)^2.
}

function desc_ecc__ {
    parameter ap.
    parameter ap_.
    parameter ap__.
    parameter pe.
    parameter pe_.
    parameter pe__.

    return (ap__ - pe__)/(ap + pe) - 2*(ap_ - pe_)*(ap_ + pe_)/(ap + pe)^2 - (ap - pe)*(ap__ + pe__)/(ap + pe)^2 + 2*(ap - pe)*(ap_ + pe_)^2/(ap + pe)^3.
}

function desc_eccan_ {
    parameter ta.
    parameter ta_.
    parameter ec.
    parameter ec_.

    return 2*atan2_(sqrt(1 - ec)*sinus(ta/2), -ec_*sinus(ta/2)/2/sqrt(1 - ec) + sqrt(1 - ec)*cosin(ta/2)*ta_/2, sqrt(1 + ec)*cosin(ta/2), ec_*cosin(ta/2)/2/sqrt(1 + ec) - sqrt(1 + ec)*sinus(ta/2)*ta_/2).
}

function desc_eccan__ {
    parameter ta.
    parameter ta_.
    parameter ta__.
    parameter ec.
    parameter ec_.
    parameter ec__.

    return 2*atan2__(sqrt(1 - ec)*sinus(ta/2), -ec_*sinus(ta/2)/2/sqrt(1 - ec) + sqrt(1 - ec)*cosin(ta/2)*ta_/2, -ec__*sinus(ta/2)/2/sqrt(1 - ec) - sinus(ta/2)*ec_^2/4/sqrt(1 - ec)^3 - ec_*cosin(ta/2)*ta_/2/sqrt(1 - ec) - sqrt(1 - ec)*sinus(ta/2)*ta_^2/4 + sqrt(1 - ec)*cosin(ta/2)*ta__/2, sqrt(1 + ec)*cosin(ta/2), ec_*cosin(ta/2)/2/sqrt(1 + ec) - sqrt(1 + ec)*sinus(ta/2)*ta_/2, ec__*cosin(ta/2)/2/sqrt(1 + ec) - cosin(ta/2)*ec_^2/4/sqrt(1 + ec)^3 - ec_*sinus(ta/2)*ta_/2/sqrt(1 + ec) - sqrt(1 + ec)*cosin(ta/2)*ta_^2/4 - sqrt(1 + ec)*sinus(ta/2)*ta__/2).
}

function desc_fall_t_ {
    parameter eaa.
    parameter eaa_.
    parameter eab.
    parameter eab_.
    parameter n.
    parameter n_.
    parameter ec.
    parameter ec_.

    return (-desc_mean_(eaa, eaa_, ec, ec_) - desc_mean_(eab, eab_, ec, ec_))/n - (2*pi - calc_mean_ano(eaa, ec) - calc_mean_ano(eab, ec))*n_/n^2.
}

function desc_fall_t__ {
    parameter eaa.
    parameter eaa_.
    parameter eaa__.
    parameter eab.
    parameter eab_.
    parameter eab__.
    parameter n.
    parameter n_.
    parameter n__.
    parameter ec.
    parameter ec_.
    parameter ec__.

    return (-desc_mean__(eaa, eaa_, eaa__, ec, ec_, ec__) - desc_mean__(eab, eab_, eab__, ec, ec_, ec__))/n - 2*(-desc_mean_(eaa, eaa_, ec, ec_) - desc_mean_(eab, eab_, ec, ec_))*n_/n^2 - (2*pi - calc_mean_ano(eaa, ec) - calc_mean_ano(eab, ec))*n__/n^2 + 2*(2*pi - calc_mean_ano(eaa, ec) - calc_mean_ano(eab, ec))*n_^2/n^3.
}

function desc_mean_ {
    parameter ea.
    parameter ea_.
    parameter ec.
    parameter ec_.

    return ea_ - ec_*sinus(ea) - ec*cosin(ea)*ea_.
}

function desc_mean__ {
    parameter ea.
    parameter ea_.
    parameter ea__.
    parameter ec.
    parameter ec_.
    parameter ec__.

    return ea__ - ec__*sinus(ea) - 2*ec_*cosin(ea)*ea_ + ec*sinus(ea)*ea_^2 - ec*cosin(ea)*ea__.
}

function desc_mean_motion_ {
    parameter ap.
    parameter ap_.
    parameter pe.
    parameter pe_.
    parameter body.

    local a to (ap + pe)/2.
    local a_ to (ap_ + pe_)/2.
    return -body:mu*a_/2/sqrt(body:mu/a)/a^3 - sqrt(body:mu/a)*a_/a^2.
}

function desc_mean_motion__ {
    parameter ap.
    parameter ap_.
    parameter ap__.
    parameter pe.
    parameter pe_.
    parameter pe__.
    parameter body.

    local a to (ap + pe)/2.
    local a_ to (ap_ + pe_)/2.
    local a__ to (ap__ + pe__)/2.
    return -body:mu*a__/2/sqrt(body:mu/a)/a^3 - body:mu^2*a_^2/4/sqrt(body:mu/a)^3/a^5 + 8*body:mu*a_^2/4/sqrt(body:mu/a)/a^4 - sqrt(body:mu/a)*a__/a^2 + 2*sqrt(body:mu/a)*a_^2/a^3.
}