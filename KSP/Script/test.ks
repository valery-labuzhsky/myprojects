run once lib_math.
run once lib_math_.
run once test_.

function test {
    local vw0 to v0 + w*r0.

    local vw1 to vw0*r0/r1.
    local v1 to (vw0*r0 - w*r1^2)/r1.

    local dv to v1 -v0.
    local a to dv/dt.

    local a to -(vh/r + 2*w)*vv.
}

function test2 {
    local vh0_ to 0.
    local a_ to 0.
    local dt_ to 1.
    local vh to vh0 + a*dt.
    local vh_ to a.
    local vv_ to 0.
    local sa to arctan2(vh, vv).
    local v to sqrt(vh^2 + vv^2).
    local v_ to a*vh/v.
    local sa_ to a*vv/v^2.
    local gd to gt_dist(twr, g, sa, v). // a - 0.1
    return gd.
}

function gt_dist {
    parameter twr.
    parameter g.
    parameter ang.
    parameter v.

    local n to gt_n(twr, g).
    local z to gt_z(ang).
    if z = 0 return 0.
    local s to 1.
    if z < 0 {
        set s to -1.
        set z to -z.
    }
    local c to v / gt_yz.
    local gtd to 2*v^2*(2*n + 1 + 2*n*z^2 - z^2)/(1 + z^2)^2/(4*n^2 - 1)*z/g.

    {
        local z2 to 0.
        local a to -2*w*vv.
        local sa_ to -2*w.
        local z to sa_/2*dt.
        local dt to dv/(n - 1)/g.
        local gtd to -2*v^2*w*dv/(n - 1)/(2*n - 1)/g^2.
        local igtd to -2/3*v^3*w/(n - 1)/(2*n - 1)/g^2.
    }
    {
        local a to (n - 1)*g.
        local dt to dv/(n - 1)/g.
    }
    return s*gtd.
}

function test3 {
    local gt_yz to z^(n-1)*(1+z^2).
    local zn1 to gt_yz/(1+z^2).
    local gt_dz to gt_yz^2*(2*n + 1 + 2*n*z^2 - z^2)/(1 + z^2)^2/(4*n^2 - 1)/z.

    local x11 to -z^2 - 1.
    local x12 to 2*(1 + z^2)*n.
    local x11 to (2*n - 1)*(1 + z^2).
    local x2 to 2.

    local gt_dz1 to gt_yz^2/(1 + z^2)/(2*n + 1)/z.
    local gt_dz2 to 2*gt_yz^2/(1 + z^2)^2/(4*n^2 - 1)/z.
    local gt_dz2 to (1 + 2/(1 + z^2)/(2*n - 1))*gt_yz^2/(1 + z^2)/(2*n + 1)/z.
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

function gt_dist_ {
    parameter twr.
    parameter g.
    parameter sa.
    parameter v.

    local sa_ to a*vv/v^2
            .
    local v_ to a*vh/v.

    local n to gt_n(twr, g).
    local z to gt_z(sa).
    local c to gt_c(n, v, z).
    local z_ to (1 + z^2)/2*sa_.
    local c_ to gt_c_(n, 0, v, v_, z, z_).
    return gt_dist_0_(g, 0, n, 0, c, c_, z, z_).
}

function gt_c {
    parameter n.
    parameter v.
    parameter z.

    return v / gt_yz(n, z).
}

function gt_z {
    parameter a.
    return tan(a/2).
}

function gt_yz {
    parameter n.
    parameter z.

    return z^(n-1)*(1+z^2).
}

function tan {
    parameter a.

    return todeg(tang(torad(a))).
}

