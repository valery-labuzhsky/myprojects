function test {
    local y to c*(x - x0)^2 + y0.
    local y2_min_y1 to c*((x2 - x0)^2 - (x1 - x0)^2).
    local y2_min_y1 to c*(x2 - x1)*(x2 + x1 - 2*x0).
    local y2_min_y1 to c*((x2 - x1)*(x2 + x1) - 2*(x2 - x1)*x0).
    local dy_div_dx12 to c*(x2 + x1 - 2*x0).
    local dy_div_dx23 to c*(x3 + x2 - 2*x0).
    local dyx12_div_dyx23 to (x2 + x1 - 2*x0)/(x3 + x2 - 2*x0).
    local zero to x3*dyx12_div_dyx23 + x2*dyx12_div_dyx23 - 2*x0*dyx12_div_dyx23 - x2 - x1 + 2*x0.
    local a1 to 2*(1 - dyx12_div_dyx23). // x0
    local a2 to (x3 + x2)*dyx12_div_dyx23.
    local a3 to -(x2 + x1).

    local x0 to -(a3+a2)/a1.
}