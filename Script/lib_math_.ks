@lazyglobal off.

run once lib_math.

function arctan2_ {
    parameter x.
    parameter y.
    parameter x_.
    parameter y_.

    return todeg(atan2_(x, y, x_, y_)).
}

function atan2_ {
    parameter x.
    parameter y.
    parameter x_.
    parameter y_.

    return (y^3*x_ - y_/x)/(y^2 + x^2).
}

function cosin_ {
    parameter a.

    return -sinus(a).
}

function sinus_ {
    parameter a.

    return cosin(a).
}

function torad_ {
    parameter deg.

    return constant:degtorad.
}
