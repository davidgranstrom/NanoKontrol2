NanoKontrol2
============

Interface for using Korg NanoKONTROL2 with SuperCollider.

Basic usage
-----------

```
n = NanoKontrol2();

// register a function to be evaluated when fader1 is changed
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

External LED control
--------------------

Button LEDs can be controlled using MIDI if NanoKontrol2 is set to "external mode" in the Korg Kontrol Editor.

```
(
// Use \external to enable LED control.
// Remember to set "LED mode" in Korg Kontrol Editor to "external"
n = NanoKontrol2(\external);

// turn on leds
n.rBtns.do {|btn|
    btn.ledState = 1;
};

n.mBtns.do {|btn|
    btn.ledState = 1;
};

n.sBtns.do {|btn|
    btn.ledState = 1;
};
)

n.ledsOff; // turn leds off

// the button is passed as an argument for `onPress` and `onRelease`
n.rBtn1.onPress = {|val, btn|
    btn.ledState = 1;
};
```

Interface
---------

### Methods

`onChange` all controls (faders/knobs/buttons) can register a function using this method

`onPress` only register press on buttons

`onRelease` only register release on buttons

`free` unregisters a MIDI responder

`freeAll` unregisters all MIDI responders

`ledsOff` turn off all LEDs

*Note: `Cmd-.` removes all MIDI responders by default in SuperCollider*

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

## Credits

Based on `NanoKontrol.sc` by [jesusgollonet](https://github.com/jesusgollonet/NanoKontrol.sc)
