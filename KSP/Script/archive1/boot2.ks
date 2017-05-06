wait 3.

set ship:control:MAINTHROTTLE to 1.
stage.

set config:ipu to 2000.

global t to HEADING(90, 45).
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
  return list(0, normX(x, 0), 0, 0, time:seconds, 0, 200, 0, 0, 0, deb).
}

function seek {
  parameter arr.
  parameter x.
  parameter c.
  
  local state to arr[0].
  local ox to arr[1].
  local v to arr[2].
  local a to arr[3].
  local t to arr[4].
  local oc to arr[5].
  local acc to arr[6].
  local i to arr[7].
  local odc to arr[8].
  local za to arr[9].
  local deb to arr[10].
  
  local t2 to time:seconds.
  local tc to c.
  
  set x to normX(x, ox).
  
  if t < t2 {
    if state > 0 {
      local dt to t2 - t.
      local v2 to (x - ox) / dt.
      if state > 1 {
	local a2 to (v2 - v) / dt.
	local ta to oc * acc + za.
	local dc to c - oc.
	if deb
	{
	  print "dx = " + x.
	  print "v2 = " + v2.
	  print "a2 = " + a2.
	  //print " ".
	}
	if state > 2 {
	  set za to za + (a2 - ta) * 0.5.
	  if deb
	  {
	    //print "za = " + za.
	    //print " ".  
	  }
	}
	local fa to c * acc + za.
	set odc to dc.
	if state > 2 {
	  set ta to calcMA(x, v2).
	  local ma to za - acc.
	  local la to calcLA(v2, ma).
	  if ta > la {
	    set ta to calcXA(x, v2, ma).
	  } else {
	    set ma to za + acc.
	    set la to calcLA(v2, ma).
	    if ta < la {
	      set ta to calcXA(x, v2, ma).
	    }
	  }
	  if deb
	  {
	    //print "fa = " + fa.
	    //print "ta = " + ta.
	    print "acc = " + acc.
	    print "c = " + c.
	    print " ".
	  }
	  set dc to (ta - za)/ acc - c.
	}
	set tc to c + dc.
	set a to a2.
      }
      set v to v2.
    }
    set i to i + 1.
    set ox to x.
    set t to t2.
    set state to state + 1.
    if state > 3 set state to 3.
    
    set arr[0] to state.
    set arr[1] to ox.
    set arr[2] to v.
    set arr[3] to a.
    set arr[4] to t.
    set arr[5] to c.
    set arr[6] to acc.
    set arr[7] to i.
    set arr[8] to odc.
    set arr[9] to za.
    set arr[10] to deb.
  }
  return tc.
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
    local t1 to calcTA(v, a1, ax).
    local t2 to calcTA(v, a2, ax).
    if t1 < 0 return a2.
    else if t2 < 0 return a1.
    if (t1 < t2) {
      return a1.
    } else {
      return a2.
    }
  }
}

function calcTA {
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