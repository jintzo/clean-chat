# nice-chat

This is a very minimal mod that allows you to block certain words from chat.

## Use-Cases

* you want to stream on a no-rules server without having to hide the chat completely
* you don't want to have to `/ignore` every single user that is spamming
* you don't want to have to `/ignore` players that use anti-afk chat messages

## Typical Words

* `discordapp.com`
* `twitch.tv`
* `Hey I am new, anyone has coords to a farm?`

## Installation

This is currently intended for 1.12.2.

* Set up [Forge](http://files.minecraftforge.net/).
* Go to the Releases page and download the jar (or compile it yourself if you don't trust me, you know the drill)
* Put it in the mods folder

## Usage

Commands are prefixed with `?!`.

* `?!`: lists available commands
* `?! list`: lists blocked words
* `?! add`: add word to blocklist
* `?! remove`: remove word from blocklist

## Build it yourself

This is barely more than the default starter from Forge, so following the [default instructions](https://mcforge.readthedocs.io/en/latest/gettingstarted/#from-zero-to-modding) should be enough:

* clone the code
* `./gradlew.bat genIntellijRuns` (or the eclipse equivalent, see page above)
* `./gradlew.bat build` generates a jar file in `build/libs`
* copy the file to your `mods` directory