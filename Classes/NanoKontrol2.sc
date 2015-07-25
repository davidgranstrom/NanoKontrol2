// ===========================================================================
// Title         : NanoKontrol2
// Description   : Controller class for Korg NanoKontrol 2
// Copyright (c) : David Granström 2015 
// ===========================================================================

NanoKontrol2 {
    var <faders, <knobs;
    var <topBtns, <midBtns, <lowBtns;

    var ctls;
    var ccFaders, ccKnobs;
    var ccTopBtns, ccMidBtns, ccLowBtns;
    var ccTransportBtns, ccMarkerBtns, ccTrackBtns, ccCycleBtn;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        faders  = List[];
        knobs   = List[];

        topBtns = List[];
        midBtns = List[];
        lowBtns = List[];

        ccFaders  = (0..7);
        ccKnobs   = (16..23);
        ccTopBtns = (32..39);
        ccMidBtns = (48..55);
        ccLowBtns = (64..71);

        ccTransportBtns = [ 43, 44, 42, 41, 45 ];
        ccMarkerBtns    = [ 60, 61, 62 ];
        ccTrackBtns     = [ 58, 59 ];
        ccCycleBtn      = 46;

        MIDIClient.init;
        MIDIIn.connectAll;

        this.assignCtls;
    }

    assignCtls {
        ccFaders.do {|cc, i|
            var key = ("fader" ++ (i+1)).asSymbol;
            var nk  = NK2Controller(key, cc);
            // easier incremental assignment
            faders.add(nk);
            // convenience method for accesing individual faders
            ctls.put(key, nk);
        };

        ccKnobs.do {|cc, i|
            var key = ("knob" ++ (i+1)).asSymbol;
            var nk  = NK2Controller(key, cc);
            knobs.add(nk);
            ctls.put(key, nk);
        };

        ccTopBtns.collect {|cc, i|
            var key = ("topBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            topBtns.add(nk);
            ctls.put(key, nk);
        };
        
        ccMidBtns.collect {|cc, i|
            var key = ("midBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            topBtns.add(nk);
            ctls.put(key, nk);
        };

        ccLowBtns.collect {|cc, i|
            var key = ("lowBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            topBtns.add(nk);
            ctls.put(key, nk);
        };

        [ [ 'bwBtn', 'fwdBtn', 'stopBtn', 'playBtn', 'recBtn' ], ccTransportBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc));
        };

        [ [ 'markerSetBtn', 'markerBwBtn', 'markerFwdBtn' ], ccMarkerBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc));
        };

        [ [ 'trackBwBtn', 'trackFwdBtn' ], ccTrackBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc));
        };

        ctls.put('cycleBtn', NK2Button('cycleBtn', ccCycleBtn));
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

NK2Controller {
    var key, cc;

    *new {|key, cc|
        ^super.newCopyArgs(("nk2_" ++ key).asSymbol, cc);
    }

    onChange_ {|func|
        MIDIdef.cc(key, func, cc);
    }

    free {
        MIDIdef.cc(key).free;
    }
}

NK2Button : NK2Controller {
    var key, cc;
    var <>onPress, <>onRelease;

    *new {|key, cc|
        ^super.newCopyArgs(("nk2_" ++ key).asSymbol, cc).init;
    }

    init {
        var func = {|val|
            if (val == 127) { onPress.(val) } { onRelease.(val) }
        };
        MIDIdef.cc(key, func, cc);
    }
}
