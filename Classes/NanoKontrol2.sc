// ===========================================================================
// Title         : NanoKontrol2
// Description   : Controller class for Korg NanoKONTROL2
// Version       : 1.2
// Copyright (c) : David Granström 2015
// ===========================================================================

NanoKontrol2 {
    var <faders, <knobs;
    var <sBtns, <mBtns, <rBtns;
    var <>ledMode;

    var ctls, midiOut;
    var ccFaders, ccKnobs;
    var ccSBtns, ccMBtns, ccRBtns;
    var ccTransportBtns, ccMarkerBtns, ccTrackBtns, ccCycleBtn;

    *new {|ledMode=\internal|
        ^super.new.ledMode_(ledMode).init;
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

        if(ledMode == \external) {
            // Device/Port name might have to be edited to match your setup.
            midiOut = MIDIOut.newByName("nanoKONTROL2", "CTRL");
        };

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
            var nk  = NK2Button(key, cc, midiOut);
            sBtns.add(nk);
            ctls.put(key, nk);
        };

        ccMBtns.collect {|cc, i|
            var key = ("mBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc, midiOut);
            mBtns.add(nk);
            ctls.put(key, nk);
        };

        ccRBtns.collect {|cc, i|
            var key = ("rBtn" ++ (i+1)).asSymbol;
            var nk  = NK2Button(key, cc, midiOut);
            rBtns.add(nk);
            ctls.put(key, nk);
        };

        [ [ 'bwBtn', 'fwdBtn', 'stopBtn', 'playBtn', 'recBtn' ], ccTransportBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc, midiOut));
        };

        [ [ 'markerSetBtn', 'markerBwBtn', 'markerFwdBtn' ], ccMarkerBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc, midiOut));
        };

        [ [ 'trackBwBtn', 'trackFwdBtn' ], ccTrackBtns ].flopWith {|key, cc|
            ctls.put(key, NK2Button(key, cc, midiOut));
        };

        ctls.put('cycleBtn', NK2Button('cycleBtn', ccCycleBtn, midiOut));
    }

    freeAll {
        ctls.do(_.free);
    }

    ledsOff {
        ctls.do(_.ledOff);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

NK2Controller {
    var key, cc, midiOut;
    var state = 0;

    *new {|key, cc|
        ^super.newCopyArgs(("nk2_" ++ key).asSymbol, cc);
    }

    onChange_ {|func|
        MIDIdef.cc(key, func, cc);
    }

    ledOff {
        midiOut !? {
            midiOut.control(0, cc, 0);
            state = 0;
        };
    }

    free {
        MIDIdef.cc(key).free;
        this.ledOff;
    }
}

NK2Button : NK2Controller {
    var key, cc;

    *new {|key, cc, aMidiOut|
        ^super.newCopyArgs(("nk2_" ++ key).asSymbol, cc, aMidiOut);
    }

    onPress_ {|func|
        MIDIdef.cc(key ++ \On, {|val|
            if (val == 127) {
                func.(val, this)
            }
        }, cc);
    }

    onRelease_ {|func|
        MIDIdef.cc(key ++ \Off, {|val|
            if (val == 0) {
                func.(val, this)
            }
        }, cc);
    }

    ledState {
        ^state;
    }

    ledState_ {|val|
        val   = val.clip(0, 1);
        state = val;

        midiOut !? {
            midiOut.control(0, cc, 127 * val);
        };
    }
}
