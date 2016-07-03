parameter skip.

skip:add("compiled").

run once compiled.

delete "compiled.ks".
log "local compiled_list to lexicon()." to "compiled.ks".

local files to archive:files:values.
for f in files {
    if f:extension = "ks" {
        local name to pure_name(f).
        if skip:contains(name) {
            archive:delete(name+".ksm").
        } else {
            if not compiled_list:haskey(name) or f:size <> compiled_list[name] {
                compile f:name.
            }
            log "set compiled_list["+CHAR(34)+name+CHAR(34)+"] to "+f:size+"." to "compiled.ks".
        }
    }
}

function pure_name {
    parameter f.

    local name to f:name.
    return name:substring(0, name:length-f:extension:length-1).
}