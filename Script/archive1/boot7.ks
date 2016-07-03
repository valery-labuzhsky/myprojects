wait 3.
switch to 0.
log1("Poehali!").
delete log.txt from 0.

set ship:control:MAINTHROTTLE to 1.
stage.

set config:ipu to 2000.

global t to HEADING(90, 90).
local f to ship:facing:inverse * t.
global pitch to init(-f:pitch, false).
global yaw to init(-f:yaw, true).

when true then {
  local f to ship:facing:inverse * t.
  set SHIP:CONTROL:PITCH to -seek(pitch, -f:pitch, -SHIP:CONTROL:PITCH).
  set SHIP:CONTROL:YAW to seek(yaw, -f:yaw, SHIP:CONTROL:YAW).
  preserve.
}

wait until false.

function init {
  parameter x.
  parameter deb.
  return list(0, normX(x, 0), 0, time:seconds, 50, 0, deb, 0, 0, 0, 0).
}

function log1 {
  parameter text.
  
  log text to log.txt.
  print text.
}

function seek {
  parameter arr.
  parameter x.
  parameter c.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local t to arr[3].
  local ac to arr[4].
  local za to arr[5].
  local deb to arr[6].
  local t0 to arr[7].
  local x0 to arr[8].
  local v0 to arr[9].
  local s to arr[10].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set x to normX(x, ox).
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to (x - ox) / dt.
    local a2 to (v2 - v) / dt.
    
    if state > 0 {
      set ta to calcTA(x, v2, ac, za).
      
      if t0 = 0 {
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }
      
      local tune to false.
      if (t - t0 > 0.25) {
	set tune to true.
      }
      if false {
      //if abs(x*2) < abs(x0) {
	set tune to true.
      }
      if false {
      //if abs(x0*2) < abs(x) {
	set tune to true.
      }
      if false {
      //if (s=0 and ox*x<=0) or (s=1 and v*v2<=0) {
	if s = 0 set s to 1.
	else set s to 0.
	set tune to true.
      }
      if tune {
	local tt to t - t0.
	local w0 to 2.
	local wt1 to 1 + w0 * tt.
	local e0 to constant():e ^ (-w0 * tt).
	local edx to (v0 * tt + wt1 * x0) * e0 - x.
	local zx to edx / (wt1 * e0 - 1).
	
	local dwn to v0 + x0 * w0.
	if abs(dwn) > 0.1 {
	  local dw to edx / (e0 * tt * tt * dwn).
	  set dw to dw / 2.
	  local nw to w0 + dw.
	  local acc to nw * nw / (w0 * w0).
	  if acc > 1.5 set acc to 1.5.
	  if acc < 0.75 set acc to 0.75.
	  set ac to ac * acc.
	}
        if deb log1("tune").
	
	set za to za + zx * w0 * w0 * 0.5.
	
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }

      set tc to (ta - za)/ac.

      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + v2).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	//log1("ta = " + ta).
	log1("c = " + c).
	//log1("s = " + s).
	log1("za = " + za).
	log1(" ").
      }
    }
    set state to state + 1.
    if state > 1 set state to 1.
    
    set v to v2.
    set ox to x.
    set t to t2.
    
    set arr[0] to state.
    set arr[1] to ox.
    set arr[2] to v.
    set arr[3] to t.
    set arr[4] to ac.
    set arr[5] to za.
    set arr[6] to deb.
    set arr[7] to t0.
    set arr[8] to x0.
    set arr[9] to v0.
    set arr[10] to s.
  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  parameter acc.
  parameter za.
  local t to 0.5.
  return -(v / t) - x / (t * t).
}

function normX {
  parameter x.
  parameter ox.
  
  until x - ox < 180 {
    set x to x - 360.
  }
  until x - ox > -180 {
    set x to x + 360.
  }
  return x.
}
