# Spigot HTTP message server plugin
-----

**Table of Contents**

- [Spigot HTTP message server plugin](#spigot-http-message-server-plugin)
  - [Overview](#overview)
  - [Installation](#installation)
  - [Plugin configuration](#plugin-configuration)
  - [Usage](#usage)
  - [License](#license)

## Overview

This plugin allows to run Minecraft [Spigot](https://www.spigotmc.org/) commands remotely

## Installation

1. Download latest release
2. Copy **messageserver-X.jar** file to **SpigotServer\plugins** folder
3. Start spigot server or send `reload` command from server console

## Plugin configuration

```yaml
plugin:
  print_debug: false  # true to print plugin debug messages; otherwise false
webserver:
  port: 8000          # number of port to listen
  threads_no: 3       # maximum number of threads in pool
  stop_delay: 2       # the maximum time in seconds to wait until exchanges have finished
  authorization:
    need: true        # do I need authorization?
    user: Admin       # user name (don't use ":" character)
    password: '123'   # password (don't use ":" character)
```

## Usage
Project [mcms](https://github.com/answering007/mcms) shows how to use it

## License

`messageserver` is distributed under the terms of the [MIT](https://spdx.org/licenses/MIT.html) license.
