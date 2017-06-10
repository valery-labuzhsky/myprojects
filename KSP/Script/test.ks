function test {
    local corr to sum((ari - mar)*(ati - mat))/n/s1/s2.
    local corr to sum(ati*ari - mat*ari - ati*mar + mat*mar)/n/s1/s2.
    local corr to (sum(ati*ari) - sum(mat*ari) - sum(ati*mar) + sum(mat*mar))/s1/s2.
    local corr to (sum(ati*ari) - mar*mat*n)/sqrt(nsat2)/sqrt(nsar2).
    local corr2 to (sum(ati*ari) - mar*mat*n)^2/nsat2/nsar2.
}

function test2 {
    local ari to ati*k + ri.
    local mar to 0.
    local mat to 0.
    local mr to 0.
    local sat2 to sum(ati^2)/n.
    local sar2 to sum(ari^2)/n.
    local sar2 to sum((ati*k + ri)*ari)/n.
    local sar2 to k*sum(ati*ari)/n + sum(ri*ati*k)/n + sum(ri^2)/n.
    local sar2 to k*corr*sar*sat + sum(ri^2)/n.

    local sar2 to sum((ati*k + ri)^2)/n.
    local sar2 to k^2*sum(ati^2)/n + 2*k*sum(ri*ati)/n + sum(ri^2)/n.
    local sar2 to k^2*sum(ati^2)/n + sum(ri^2)/n.

    local sr2 to sar2*n - k^2*sat2.
    local sar2 to k*corr*sar*sat + sar2 - k^2*sat2.
    local zero to corr*sar*sat - k*sat2.
    local k to corr*sar/sat.

    local corr to 0.
}

function test3 {
    local a1 to +ac + za.
    local a2 to -ac + za.
    local t to ac > -za.
    local t to ac > za.

    local za to mra/2 + mta/2 + (za0 - mta)*acc.
    local ac to ac0 * acc.
    // acc > (mra + mta)/(ac0 - za0 + mta)/2
    // acc > -(mra + mta)/(ac0 - za0 + mta)/2
}