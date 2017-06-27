@LAZYGLOBAL off.
SET CONFIG:PAUSEONCOMPILE TO TRUE.

wait until ship:unpacked.
print "wait unpacked".

local main_proc to 0.
local all_processors to 0.
list processors in all_processors.
for processor in all_processors {
    if processor:part:uid <> core:part:uid {
        set main_proc to core.
        break.
    }
}

if main_proc<>0 {
    wait until NOT core:MESSAGES:EMPTY.
    local message TO core:MESSAGES:POP.
    print message.
} else {
    run once precompile.
}

run lib_stage.