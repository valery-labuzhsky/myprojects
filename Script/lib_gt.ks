@LAZYGLOBAL off.

run once lib_calc.

// Gravity turn

function gt_a {
    parameter z.
    return 2*arctan(z).
}

function gt_z {
    parameter a.
    return tan(a/2).
}

function gt_g {
    return -calc_vacc(calc_abs_ap(), calc_abs_pe(), calc_abs_alt()).
}

function gt_n {
    parameter twr.
    parameter g.

    return twr/g.
}

function gt_yz {
    parameter n.
    parameter z.

    return z^(n-1)*(1+z^2).
}

function gt_c {
    parameter n.
    parameter v.
    parameter z.

    return v / gt_yz(n, z).
}

function gt_vel {
    parameter n.
    parameter c.
    parameter z.

    return c * gt_yz(n, z).
}

function gt_vvel {
    parameter n.
    parameter c.
    parameter z.

    return c * z^(n-1)*(1-z^2).
}

function gt_xz {
    parameter n.
    parameter z.

    return z^(2*n-2)*(2*n+2-z^4*(2*n-2))/(4*n^2-4).
}

function gt_alt_0 {
    parameter n.
    parameter c.
    parameter z.

    return (c^2/g)*gt_xz(n, z).
}

function gt_alt {
    parameter n.
    parameter c.
    parameter z0.
    parameter z1.

    return gt_alt_0(n, c, z1) - gt_alt_0(n, c, z0).
}

function gt_dz {
    parameter n.
    parameter z.

    return z^(2*n-1)*(2*n+1+z^2*(2*n-1))/(4*n^2-1).
}

function gt_dist_0 {
    parameter g.
    parameter n.
    parameter c.
    parameter z.

    return 2*(c^2/g)*gt_dz(n, z).
}

function gt_nt_a {
    parameter twr.
    parameter th.
    parameter a.
    parameter af.
    parameter v.
    parameter alt.

    if v < 1 return 0.

    local z to gt_z(a).
    if z <= 0 set z to tan(0.01).

    local zt to gt_z(af).
    local g to gt_g().
    local n to gt_n(twr, g).
    local x to gt_xz(n, z).
    local xt to gt_xz(n, zt).
    local dx to z^(2*n-3)*(1-z^4).
    local y to gt_yz(n, z).
    local dy to z^(n-2)*(n-1+(n+1)*z^2).

    local f to v^2*(xt-x)*y - (th - alt)*y^3*g.
    local df to -v^2*(2*dy*(xt-x) + y*dx).
    local fdf to f/df.

    if fdf < z {
        set z to z - fdf.
    } else {
        set z to z/2.
    }

    if z < 0 return 0.
    return gt_a(z).
}

function gt_nt_twr {
    parameter twr.
    parameter dh.
    parameter a.
    parameter v.

//log_log("dh = "+dh).
//log_log("a = "+a).
//log_log("v = "+v).
//log_log("twr = "+twr).

    local z to gt_z(a).
    local g to gt_g().
    local n to gt_n(twr, g).
    local x to gt_xz(n, z).
    local dx to z^(2*n-2)*((4*n-6)*(2*n+2)^2 - z^4*(4*n+2)*(2*n-2)^2)/(4*n^2-4)^2.
    local y to gt_yz(n, z).

    local f to v^2*x*g - dh*g^2*y^2.
    local df to v^2*(dx - 2*x).
    if df = 0 return twr.
    local fdf to f/df.

    if twr - fdf < g return (twr + g) / 2.

    set twr to twr - f/df.
//if twr < 0 set twr to 0.
    return twr.
}
