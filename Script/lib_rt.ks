rt_settarget().

function rt_settarget{
local ps to ship:parts.

for p in ps {
  local ms to p:allmodules.
  for m in ms {
    if m = "ModuleRTAntenna" {
      set m to p:GETMODULE("ModuleRTAntenna").
      //m:DOACTION("activate", true).
      m:SETFIELD("target", "mission-control").
      print "Set target".
    }
  }
  //print p:GETMODULE("ModuleRTAntenna").
}
}
//m:DOEVENT("activate").