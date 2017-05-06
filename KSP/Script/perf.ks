local t to time:seconds.
local n to 0.
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
}

print "empty "+(2000/n).

set t to time:seconds.
set n to 0.
local nv to v(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set nv to v(1, 1, 1).
}

print "new v "+(2000/n).

set t to time:seconds.
set n to 0.
set nv to v(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set nv to v(1, 1, 1) + v(1, 1, 1).
}

print "new vv "+(2000/n).


set t to time:seconds.
set n to 0.
local c to 1.
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set c to c + c.
}

print "add cc "+(2000/n).

set t to time:seconds.
set n to 0.
set c to 1.
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set c to c + c + c.
}

print "add ccc "+(2000/n).

set t to time:seconds.
set n to 0.
local vec to v(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set vec to vec * 2.
}

print "mult vc "+(2000/n).

set t to time:seconds.
set n to 0.
set vec to v(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set vec to vec + vec.
}

print "add vv "+(2000/n).

set t to time:seconds.
set n to 0.
set vec to v(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set vec to vec + vec + vec.
}

print "add vvv "+(2000/n).

set t to time:seconds.
set n to 0.
local d to r(1, 1, 1).
wait until t <> time:seconds.
set t to time:seconds.
until t <> time:seconds {
    set n to n + 1.
    set d to d * d.
}

print "add dd "+(2000/n).
