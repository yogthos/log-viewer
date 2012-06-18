# log-viewer

A web UI viewer for [clj-log](https://github.com/yogthos/clj-log), allows viewing and filtering the logs. Latest entries will be displayed at the top. Clicking a value in the log will set it as a filter. Multiple space separated filter items can be entered. 
logs containing any of the matches will be displayed. 

## Usage

set `log-file` in log-viewer.views.helpers to the location of your log 

```clojure
(def log-file "path/to/the/log")
```

```bash
lein deps
lein run
```

to build as a deployable WAR

```bash
lein ring uberwar
``` 

## License

Copyright (C) 2011 Yogthos

Distributed under the Eclipse Public License, the same as Clojure.

