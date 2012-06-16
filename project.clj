(defproject log-viewer "0.1.0-SNAPSHOT"
            :description "log viewer for clj-log"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [clj-log "0.2"]
                           [noir "1.3.0-beta8" :exclude [org.clojure/clojure]]]
            :dev-dependencies [[lein-ring "0.7.1"]]
            :ring {:handler log-viewer.server/handler}
            :main log-viewer.server)

