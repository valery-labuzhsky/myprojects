wait 3.
switch to 0.
log1("Poehali!").
delete log.txt from 0.

set ship:control:MAINTHROTTLE to 1.
stage.

set config:ipu to 2000.

global t to HEADING(90, 90).
local f to ship:facing:inverse * t.
global pitch to init_ac(-f:pitch, false, list(100)).
global yaw to init_ac(-f:yaw, true, list(100)).

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
  return init_ac(x, deb, list(10, 0, 0, 0, 0, 0)).
}

function init_ac {
  parameter x.
  parameter deb.
  parameter ac.
  local afa to list().
  local aodc to list().
  from {local i to 0.} until i >= ac:length step {set i to i + 1.} do {
    afa:add(0).
    aodc:add(0).
  }
  return list(0, normX(x, 0), 0, time:seconds, 0, ac, afa, aodc, deb, 0, 0).
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
  local oc to arr[4].
  local aac to arr[5].
  local afa to arr[6].
  local aodc to arr[7].
  local deb to arr[8].
  local na to arr[9].
  local idc to arr[10].
  
  local t2 to time:seconds. 
  local tc to c.
  
  set x to normX(x, ox).
  
  if t < t2 {
    local dt to t2 - t.
    local v2 to (x - ox) / dt.
    local a2 to (v2 - v) / dt.
    
    if state > 0 {	
      local dc to c - oc.

      local ac to 0.
      local fa to a2.
      local err to a2 - na.
      set na to a2.
      local s to aac:length.
      local nidc to 0.
      from {local i to s-1.} until i < 0 step {set i to i - 1.} do {
	if idc > 0 { 
	  set aac[i] to aac[i] + 0.1 * err * aodc[i] / idc.
	}
	set ac to ac + aac[i].
	if i = 0 {
	  set aodc[0] to dc.
	  if ac < 1 set aac[0] to aac[0] + 1 - ac.
	} else {
	  set aodc[i] to aodc[i - 1].
	}
	set nidc to nidc + abs(aodc[i]).
	set na to na + aac[i] * aodc[i].
	if i = 0 {
	  set afa[0] to 0.
	} else {
	  set afa[i] to afa[i - 1].
	}
	set afa[i] to afa[i] + aac[s - i - 1] * dc.
	set fa to fa + afa[i].
      }
      set idc to nidc.
      
      //set acc to acc + 0.1 * (a2 - na) * odc.
      if ac < 1 set ac to 1.
      //set odc to dc.
      //set na to a2 + acc * odc.

      local za to a2 - ac * oc.
      set ta to calcTA(x, v2, ac, za).

      set dc to (ta - fa)/ac.

      if deb {
	//log1("dt = " + dt).
	//log1("dx = " + x).
	//log1("v2 = " + v2).
	//log1("a2 = " + a2).
	//log1("ac = " + ac).
	//log1("aac = " + aac).
	//log1("ta = " + ta).
	//log1("c = " + c).
	//log1(" ").
	print "x = " + x.
	print "aac = " + aac:dump.
	print "ac = " + ac.
      }

      set tc to c + dc.
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
    set arr[4] to c.
    set arr[5] to aac.
    set arr[6] to afa.
    set arr[7] to aodc.
    set arr[8] to deb.
    set arr[9] to na.
    set arr[10] to idc.
  }
  return tc.
}

function calcTA {
  parameter x.
  parameter v.
  parameter acc.
  parameter za.
  local ta to calcMA(x, v).
  local ma to za - acc.
  local la to calcLA(v, ma).
  if ta > la {
    set ta to calcXA(x, v, ma).
  } else {
    set ma to za + acc.
    set la to calcLA(v, ma).
    if ta < la {
      set ta to calcXA(x, v, ma).
    } else if abs(x) > 0.01 {
      local ba to v * v / (2 * x).
      if abs(ba) > 0 {
	local bt to -v / ba.
	if bt > 0 and bt < 1 {
	  return ba.
	}
      }
    }
  }
  return ta.
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

function calcSRA {
  parameter x.
  parameter v.
  parameter a.
  local t to 0.5.
  return a * a * t * t + 4 * a * v * t + 8 * a * x.
}

function calcXA {
  parameter x.
  parameter v.
  parameter ax.
  local t to 0.5.
  local sqrt to calcSRA(x, v, ax).
  if (sqrt < 0) {
     local ca to (-2 * v + ax * t) / ( 2 * t).
     return ca.
  } else {
    set sqrt to sqrt(sqrt).
    local a1 to (sqrt + ax * t - 2 * v) / t.
    local a2 to (-sqrt + ax * t - 2 * v) / t.
    local t1 to calcSA(v, a1, ax).
    local t2 to calcSA(v, a2, ax).
    if t1 < 0 return a2.
    else if t2 < 0 return a1.
    if (t1 < t2) {
      return a1.
    } else {
      return a2.
    }
  }
}

function calcSA {
  parameter v.
  parameter a.
  parameter ax.
  local t to 0.5.
  local ta to -a * t - v.
  if ax < 0 set ta to -ta.
  return ta.
}

function calcLA {
  parameter v.
  parameter ax.
  local t to 0.5.
  return (-ax - (v / t)).
}

function calcMA {
  parameter x.
  parameter v.
  
  local t to 0.5.
  return -(3 * v / (2 * t)) - x / (t * t).
}