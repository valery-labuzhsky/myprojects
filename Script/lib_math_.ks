@lazyglobal off.

run once lib_math.

function acos2_ {
    parameter s.
    parameter s_.
    parameter h.
    parameter h_.

    if h = 0 return 0.

    return acos_(s/h)*(s_/h - s*h_/h^2).
}

function acos2__ {
    parameter s.
    parameter s_.
    parameter s__.
    parameter h.
    parameter h_.
    parameter h__.

    if h = 0 {
        return 0.
    }
    return acos__(s/h)*(s_/h - s*h_/h^2)^2 + acos_(s/h)*(s__/h - 2*s_*h_/h^2 - s*h__/h^2 + 2*s*h_^2/h^3).
}

function acos__ {
    parameter x.

    return -x/sqrt(1 - x^2)^3.
}

function arccos_ {
    parameter x.

    return todeg(acos_(x)).
}

function acos_ {
    parameter x.

    return -1/sqrt(1-x^2).
}

function arctan2_ {
    parameter x.
    parameter x_.
    parameter y.
    parameter y_.

    return todeg(atan2_(x, x_, y, y_)).
}

function atan2_ {
    parameter x.
    parameter x_.
    parameter y.
    parameter y_.

    return (x_*y - x*y_)/(y^2+x^2).
}

function atan2__ {
    parameter x.
    parameter x_.
    parameter x__.
    parameter y.
    parameter y_.
    parameter y__.

    return (x__*y + 2*x_*y_ - x*y__)/(y^2 + x^2) - (x_*y - x*y_)*(2*y_*y + 2*x_*x)/(y^2 + x^2)^2.
}

function cosin_ {
    parameter a.

    return -sinus(a).
}

function sign_ { // TODO remove me
    parameter x.

    return 0.
}

function sinus_ {
    parameter a.

    return cosin(a).
}
