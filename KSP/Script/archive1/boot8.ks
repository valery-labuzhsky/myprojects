@LAZYGLOBAL off.

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

global lf to ship:facing.

when true then {
  local f to ship:facing:inverse * t.
  local d to lf:inverse * ship:facing.
  set SHIP:CONTROL:PITCH to -seek(pitch, -d:pitch, -f:pitch, -SHIP:CONTROL:PITCH).
  set SHIP:CONTROL:YAW to seek(yaw, -d:yaw, -f:yaw, SHIP:CONTROL:YAW).
  preserve.
}

wait until false.

function init {
  parameter x.
  parameter deb.
  return list(0, normX(x, 0), 0, time:seconds, 50, 0, deb, 0, 0, 0, 0, 0).
}

function log1 {
  parameter text.
  
  log text to log.txt.
  print text.
}

function seek {
  parameter arr.
  parameter dx.
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
  local a0 to arr[10].
  local oc to arr[11].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set x to normX(x, ox).
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to dx / dt.
    local a2 to (v2 - v) / dt.
    
    if state > 0 {
      local ta to calcTA(x, v2).
      
      if t0 = 0 {
	set t0 to t.
	set x0 to x.
	set v0 to v2.
      }
      
      local tune to false.
      if (t - t0 > 0.25) {
	set tune to true.
      }
      local sz to false.
      local tac to false.
      if tune {
	local tt to t - t0.
	if state = 1 {
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
	    local dac to (acc-1) * ac.
	    // a = c * ac + za
	    // a = c * (ac + dac) + za - c * dac
	    set za to za - c * dac.
	    set ac to ac * acc.
	  }
	  set za to za + zx * w0 * w0 * 0.5.
	} else if state = 2 or state = 4 {
	  local ca to c * ac + za.
	  local a to (v2 - v0) / tt.
	  set za to za + a - ca.
	  set state to state - 1.
	} else if state = 3 {
	  local ca to c * ac + za.
	  local a to (v2 - v0) / tt.
	  set za to za + (a - ca)/2.
	  if abs(c)*2 > 1 {
	    local dac to (a - ca) * 0.5 / c.
	    if dac > 0.5 * ac set dac to 0.5 * ac.
	    if dac < -0.25 * ac set dac to -0.25 * ac.
	    set ac to ac + dac.
	  }
	} else if state = 5 {
	  set tac to true.
	  set state to 3.
	}
	
        if deb log1("tune").
	
	set sz to true.
      }

      if (x * v2 < 0) {
	local s to 1.
	if (x < 0) set s to -1.
	local sx to s * x.
	local ba to v2 * v2 * 0.5 / sx.
	local ax to ac + s * za.
	  
	if ax < 0 {
	  if state < 3 set sz to true.
	  set state to 5.
	} else {
	  if ba > 0 and -s * v2 / ba < 0.5 {
	    if state > 2 {
	      if state = 5 set tac to true.
	      set state to 2.
	    }
	  } else if state < 3 and ba * 2 > ax {
	    set sz to true.
	    set state to 5.
	  } else if state > 2 and ba * 4 < ax {
	    if state = 5 set tac to true.
	    set state to 2.
	  }
	}
	  
	if state > 2 {
	  set ta to s * ba.
	}
      } else if state > 2 {
	if state = 5 set tac to true.
	set state to 2.
      }
      
      if tac {
	local dc to c - oc.
	if abs(dc) > 0 { 
	  local a to (v2 - v0) / (t - t0).
	  local dac to 0.5 * a / dc - ac * 0.5.
	  set za to za - c * dac.
	  set ac to ac + dac.
	}
      }
	
      set tc to (ta - za)/ac.
      
      if abs(tc) >= 1 {
	if state = 1 set state to 2.
	if state = 3 set state to 4.
      }

      if sz {
	set a0 to (v2 - v0) / (t - t0).
	set t0 to t.
	set x0 to x.
	set v0 to v2.
	set oc to c.
      }
      
      if deb {
	//log1("dt = " + dt).
	log1("dx = " + x).
	log1("v2 = " + v2).
	//log1("a2 = " + a2).
	log1("ac = " + ac).
	//log1("ta = " + ta).
	log1("c = " + c).
	log1("s = " + state).
	log1("za = " + za).
	log1(" ").
      }
    } else {
      set state to 1.
    }
    
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
    set arr[10] to a0.
    set arr[11] to oc.
  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
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
