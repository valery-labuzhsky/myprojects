@LAZYGLOBAL off.

SET CONFIG:PAUSEONCOMPILE TO TRUE.

run once precompile.

local all_processors to 0.
list processors in all_processors.
for processor in all_processors {
    if processor:part:uid <> core:part:uid {
        print "message send".
        processor:connection:sendmessage("compiled").
        break.
    }
}

run lib_log("main.log").
run once lib_asc.

run once lib_misc.
//open_terminal().

if ship:altitude < 99800 {
    asc_start(100000).
}
log_log("Out").
