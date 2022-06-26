# Piano Addon

![Banner](.github/assets/banner.png)

This addon connects to your MIDI piano and displays the keyboard on your screen.<br>
There are two types of modules:<br>
- <b>Mini Piano</b>: displays a simple piano keyboard for your Windows taskbar<br>
- <b>Piano Tutorial</b>: displays a bigger piano keyboard with a timeline of pressed keys<br>

## Installation
This addon requires the core application [DesktopModules](https://github.com/LabyStudio/desktopmodules)

You can download the addon jar file [here](https://github.com/LabyStudio/piano-addon/releases/) <br>
Put the built jar file into the following directory: ``C:/Users/<name>/AppData/Roaming/DesktopModules/addons/``<br>
And then you are ready to launch the core application.

### Configuration:
To set up your MIDI device, you have to enable it in ``devices``.<br>
If ``show_mini_piano_on_detect`` is set to true, the mini piano will enable itself once it detects a MIDI device.<br>
You can additionally broadcast the MIDI messages to your other applications by setting ``broadcast_to_server`` to true.<br>
```json
{
  "devices": {
    "Real Time Sequencer": false,
    "AudioBox USB": false,
    "CASIO USB-MIDI": true
  },
  "show_mini_piano_on_detect": false,
  "broadcast_to_server": {
    "enabled": false,
    "address": "localhost",
    "port": 0
  }
}
```

## Run in IDE
Launch the main class ``StartPiano``