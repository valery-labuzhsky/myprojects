@LAZYGLOBAL off.

parameter log_name.

run once lib_misc.

local log_file to not omit(log_name).

if log_file {
  log "poehali" to log_name.
  deletepath(log_name).
}

function log_log {
  parameter text.

  set text to TIME:CLOCK+": "+text.
  //set text to text.
  if log_file log text to log_name.
  print text.
}

