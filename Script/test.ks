function test {
    local vh to vh0 + ah*t.
    local vv to vv0 + (av+g)*t.
    local g to g0 - vh^2/r.

    local dvv to (av+g)*dt.
    local dvv to (av + g0 - vh0^2/r)*dt - 2*vh0*t*ah*dt/r - ah^2*t^2*dt/r.
    local vv to vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3. // =0
    local dh to vv*dt.
    local dh to (vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3)*dt.
    local h to h0 + vv0*t + (av + g0 - vh0^2/r)/2*t^2 - vh0*ah/r/3*t^3 - ah^2/r/3/4*t^4.

    local t to dv/ah.
    local g0 to vho^2/r.
    local vh0 to vho - dv.
    local vv0 to (-3*av*r - 3*g0*r + 3*vh0^2 + 3*vh0*ah*t + ah^2*t^2)*t/3/r.
    local h to (12*h0*r*ah^2 - 6*r*dv^2*av - 4*vho*dv^3 + dv^4)/12/ah^2/r. // =0
    local av to (12*h0*r*ah^2 - 4*vho*dv^3 + dv^4)/6/r/dv^2.

    local vv to vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3. // =0
    local av to (-3*vv0*ah*r - 3*dv^2*vho + dv^3)/3/dv/r.

    local av_av to 6*vv0*ah*r*dv + 12*h0*r*ah^2 + 2*vho*dv^3 - dv^4. //=0
    local ah to (sqrt(b^2 - 4*c*a) - b)/2/a.
    local ah to (sqrt((9*vv0^2*r - 24*vho*h0*dv + 12*h0*dv^2)*r) - 3*vv0*r)*dv/12/h0/r.

    local g to dv*(2*vho-dv)/r.
    local ah to (sqrt(9*vv0^2 - 12*g*h0) - 3*vv0)*dv/12/h0.
    local ah to -g*dv/(sqrt(9*vv0^2 - 12*g*h0) + 3*vv0).

    local av to vv0*g/(sqrt(9*vv0^2 - 12*g*h0) + 3*vv0) - (vho*dv + r*g)/3/r. // !!!
    local av to -sqrt(9*vv0^2 - 12*g*h0)*g/3/(sqrt(9*vv0^2 - 12*g*h0) + 3*vv0) - vho*dv/3/r.

    local sqr to sqrt(9*vv0^2 - 12*g*h0).
    local div to sqt + 3*vv0.
    local ah to -g*dv/div.
    local av to vv0*g/div - (vho*dv + r*g)/3/r.
    local twr2 to (9*vv0^2*g^2*r^2 - 6*div*dv*vho*vv0*g*r + vho^2*dv^2*div^2 - 6*div*vv0*g^2*r^2 + 2*r*div^2*dv*vho*g + g^2*div^2*r^2 + 9*g^2*dv^2*r^2)/9/r^2/div^2.
    {
        local a to (-6*vho*dv^3 + dv^4 - 9*twr2*r^2 + 9*vho^2*dv^2).
        local b to 6*(3*vho^2*dv^2 - vho*dv^3 - 9*twr2*r^2)*vv0.
        local c to 9*(vv0^2*vho^2*dv^2 + 4*vho^2*dv^4 - 4*vho*dv^5 + dv^6 - 9*vv0^2*twr2*r^2).

        local x to 36*(36*twr2*r^2*vho^2*dv^2 - 36*vho^4*dv^4 - 36*twr2*r^2*vho*dv^3 + 60*vho^3*dv^5 + 10*vho*dv^7 - dv^8 + 9*twr2*r^2*dv^4 - 37*vho^2*dv^6 - 36*r^2*twr2*vho*vv0^2*dv + 9*vv0^2*twr2*r^2*dv^2 + 36*r^2*twr2*vho^2*vv0^2)*dv^2.
        local sqr to (sqrt(b^2 - 4*c*a) - b)/2/a.
    }
}

function test2 {
    local vh to vh0 + ah*t.
    local vv to vv0 + (av+g)*t.
    local g to g0 - vh^2/r.

    local dvv to (av+g)*dt.
    local dvv to (av + g0 - vh0^2/r)*dt - 2*vh0*t*ah*dt/r - ah^2*t^2*dt/r.
    local vv to vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3. // =0
    local dh to vv*dt.
    local dh to (vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3)*dt.
    local h to h0 + vv0*t + (av + g0 - vh0^2/r)/2*t^2 - vh0*ah/r/3*t^3 - ah^2/r/3/4*t^4.

    local t to dv/ah.
    local g0 to vho^2/r.
    local vh0 to vho - dv.
    local vho to (g*r/dv +dv)/2.
    local vv0 to (-3*av*r - 3*g0*r + 3*vh0^2 + 3*vh0*ah*t + ah^2*t^2)*t/3/r.
    local h to (12*h0*r*ah^2 - 6*r*dv^2*av - 2*g*r*dv^2 - dv^4)/12/ah^2/r. //=0

    local av to (12*h0*r*ah^2 - 2*g*r*dv^2 - dv^4)/6/r/dv^2. //=0

    local vv to vv0 + (av + g0 - vh0^2/r)*t - vh0*ah/r*t^2 - ah^2/r/3*t^3. // =0
    local av to (-6*vv0*r*ah - 3*g*r*dv - dv^3)/6/dv/r. // =0

    local av_av to 6*vv0*ah*dv + 12*h0*ah^2 + dv^2*g. // =0
    local ah to (sqrt(9*vv0^2 - 12*g*h0) - 3*vv0)*dv/12/h0.
    local ah to -g*dv/(sqrt(9*vv0^2 - 12*g*h0) + 3*vv0).

    local av to g*vv0/(sqrt(9*vv0^2 - 12*g*h0) + 3*vv0) - g/2 - dv^2/6/r.
    local av to g/(sqrt(9 - 12*g*h0/vv0^2) + 3) - g/2 - dv^2/6/r.

    {
        local ah to -g*dv.
        local av to (-3*sqrt(9*vv0^2 - 12*g*h0)*g*r - 3*r*g*vv0 - sqrt(9*vv0^2 - 12*g*h0)*dv^2 - 3*vv0*dv^2)/6/r.
    }

    local vv_ to vv0 + g*t.
    local h_ to h0 + vv0*t + g*t^2/2.

    local t to (vv_ - vv0)/g.
    local h_ to (2*h0*g + vv_^2 - vv0^2)/2/g. // = 0
    local vv0 to sqrt(2*h0*g + vv_^2).
    local x to (sqrt(b^2 - 4*c*a) - b)/2/a.
    {
        local sqr to sqrt(9*vv0^2 - 12*g*h0).
        local div to (sqr + 3*vv0).
        local ah to -g*dv/div.
        local gdv to - g/2 - dv^2/6/r.
        local av to g*vv0/div + gdv.

        local gdv_2_twr2 to gdv^2 - twr2.

        local _3_gdv_2_twr2_gdv_g to 3*gdv_2_twr2 + gdv*g. //gdv_2_twr2 -dv^2*gdv/3/r -2*twr2 // -((g + dv^2/r)*gdv/2 + 3*twr2).
        local g_2_18_gdv_2_twr2_6_gdv_g to (g^2 + 6*_3_gdv_2_twr2_gdv_g).
        local dv_2_g_2_vv__2_g_2_18_gdv_2_twr2_6_gdv_g to dv^2*g^2 + vv_^2*g_2_18_gdv_2_twr2_6_gdv_g.
        local _6_gdv_2_twr2_g_2_18_gdv_2_twr2_6_gdv_g to -6*gdv_2_twr2 + g_2_18_gdv_2_twr2_6_gdv_g. // g^2 + 12*gdv_2_twr2 + 6*gdv*g.

        local x6 to 4*(_6_gdv_2_twr2_g_2_18_gdv_2_twr2_6_gdv_g^2 - 12*_3_gdv_2_twr2_gdv_g^2)*g^2*h0^2.
        local x7 to 4*(-dv_2_g_2_vv__2_g_2_18_gdv_2_twr2_6_gdv_g*_6_gdv_2_twr2_g_2_18_gdv_2_twr2_6_gdv_g - 24*vv_^2*_3_gdv_2_twr2_gdv_g^2)*h0*g.
        local x8 to (dv^2 + vv_^2)*g^2*(dv_2_g_2_vv__2_g_2_18_gdv_2_twr2_6_gdv_g + 6*vv_^2*_3_gdv_2_twr2_gdv_g).
    }
}

function test3 {
    local y1 to a*x1^2 + b*x1 + c.
    local y2 to a*x2^2 + b*x2 + c.
    local y3 to a*x3^2 + b*x3 + c.

    local y1_y2 to a*x1^2 + b*x1 - a*x2^2 - b*x2. // y1 - y2
    local y1_y2_x1_x2 to a*(x1 + x2) + b. // (y1 - y2)/(x1 - x2)
    local y1_y3_x1_x3 to a*(x1 + x3) + b.
    local a to ((x2 - x3)*y1 - (x1 - x3)*y2 + (x1 - x2)*y3)/(x1 - x2)/(x1 - x3)/(x2 - x3).
    local b to (-(x2^2 - x3^2)*y1 + (x1^2 - x3^2)*y2 - (x1^2 - x2^2)*y3)/(x1 - x2)/(x1 - x3)/(x2 - x3).
    local c to ((x2 - x3)*x2*x3*y1 - (x1 - x3)*x1*x3*y2 + (x1 - x2)*x1*x2*y3)/(x1 - x2)/(x1 - x3)/(x2 - x3).

    local y to y0 + k*(x - x0)^2.
    local y to a*x^2 + b*x + c.

    local a to k.
    local b to -2*x0*k.
    local c to y0 + x0^2*k.

    local k to a.
    local x0 to -b/2/a.
    local y0 to c - k*x0^2.

    local dy_dx12 to a*(x1 + x2) + b. // (y1 - y2)/(x1 - x2)
    local dy_dx23 to a*(x2 + x3) + b. // (y2 - y3)/(x2 - x3)

    local k to (dy_dx13 - dy_dx12)/(x3 - x2).
    local x0 to x2 - dy_dx13/2/k. // (x1 + x3)/2 - dy_dx13/2/k. | x1 = x2 - dx, x3 = x2 + dx
    local y0 to y2 - k*(x2 - x0)^2.
}

function tau {
    local t_ to 1.
    local a_ to 0.
    local x0_ to 0.
    local vx0_ to 0.
    local ax_ to 0.

    local vx to vx0 + ax*t.
    local vx_ to ax.

    local x to x0 + vx0*t + ax*t^2/2.
    local x_ to vx.

    local vy_ to ay.

    local tau to sqrt(vx^2 + vy^2)/a.
    local p to -x/vx.

    local v to sqrt(vx^2 + vy^2).
    local v to tau*a.

    local ax_vx_ay_vy to a*v*cos(an). //(ax*vx + ay*vy)

    local tau_ to cos(an).
    {
        local p to -x/vx.
        local x to -p*vx.
        local p_ to (-vx - p*ax)/vx.

    }
}
