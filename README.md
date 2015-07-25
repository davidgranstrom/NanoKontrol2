NanoKontrol2
============

Interface for using Korg NanoKONTROL2 with SuperCollider. This class is essentially just a rewrite of [jesusgollonet/NanoKontrol.sc](https://github.com/jesusgollonet/NanoKontrol.sc)

Interface
---------

### Methods

`onChange` all controls (faders/knobs/buttons) can assign a function to this method

`onPress` only register press on buttons

`onRelease` only register release on buttons

`free` unregisters a MIDI responder

`freeAll` unregisters all MIDI responders

### Controller names

All controls on the NanoKONTROL2 are supported, see list of names below.

#### Faders/Knobs

* `fader1 .. 8`
* `knob1 .. 8`

#### Buttons

* `sBtn1 .. 8`
* `mBtn1 .. 8`
* `rBtn1 .. 8`

#### Transport buttons

* `bwBtn`
* `fwdBtn` 
* `stopBtn` 
* `playBtn` 
* `recBtn`
* `markerSetBtn` 
* `markerBwBtn` 
* `markerFwdBtn` 
* `trackBwBtn`
* `trackFwdBtn` 
* `cycleBtn`

#### Collections

* `faders`
* `knobs`
* `sBtns`
* `mBtns`
* `rBtns`

Basic usage
-----------

```
n = NanoKontrol2();

// register a function to be evaluted when fader1 is changed
n.fader1.onChange = {|val| (val/127).postln; }

// overwrite the previous assignment
n.fader1.onChange = {|val| val.linexp(0, 127, 20, 20000).postln; }

n.sBtn1.onPress = { "Hello, ".post; };
n.sBtn1.onRelease = { "NanoKONTROL2!".postln; };

```

Incremental assignment
----------------------

It is possible to incrementally assign faders, knobs, and the s/m/r buttons.

```
n = NanoKontrol2();

(
n.faders.do {|fader, i|
    fader.onChange = {|val|
        "This is fader % its value is %\n".postf(i+1, val);
    }
};

n.knobs.do {|knob, i|
    knob.onChange = {|val|
        "This is knob % its value is %\n".postf(i+1, val);
    }
};

n.rBtns.do {|rBtn, i|
    rBtn.onChange = {|val|
        "This is rBtn % its value is %\n".postf(i+1, val);
    }
};
)

```

Or just a selection of controls
```
// assign faders 1 .. 4
n.faders[..3].do {|fader, i| 
    fader.onChange = {|val|
        "This is fader % its value is %\n".postf(i+1, val);
    }
};
```

