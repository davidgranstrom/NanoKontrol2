// ===========================================================================
// Title         : NanoKontrol2
// Description   : Controller class for Korg NanoKONTROL2
// Version       : 1.0alpha
// Copyright (c) : David Granström 2015 
// ===========================================================================

NanoKontrol2 {
    var <faders, <knobs;
    var <sBtns, <mBtns, <rBtns;

    var ctls;
    var ccFaders, ccKnobs;
    var ccSBtns, ccMBtns, ccRBtns;
    var ccTransportBtns, ccMarkerBtns, ccTrackBtns, ccCycleBtn;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        faders = List[];
        knobs  = List[];

        sBtns  = List[];
        mBtns  = List[];
        rBtns  = List[];

        ccFaders = (0..7);
        ccKnobs  = (16..23);
        ccSBtns  = (32..39);
        ccMBtns  = (48..55);
        ccRBtns  = (64..71);

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
            // convenience method for accessing individual faders
            ctls.put(key, nk);
        };

        ccKnobs.do {|cc, i|
            var key = ("knob" ++ (i+1)).asSymbol;
            var nk  = NK2Controller(key, cc);
            knobs.add(nk);
            ctls.put(key, nk);
        };

        ccSBtns.collect {|cc, i|
            var key = ("sBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            sBtns.add(nk);
            ctls.put(key, nk);
        };
        
        ccMBtns.collect {|cc, i|
            var key = ("mBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            mBtns.add(nk);
            ctls.put(key, nk);
        };

        ccRBtns.collect {|cc, i|
            var key = ("rBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc);
            rBtns.add(nk);
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
