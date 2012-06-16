(ns log-viewer.server
  (:use hiccup.middleware)
  (:require [noir.server :as server]
            [log-viewer.views welcome common]))

(server/load-views-ns 'log-viewer.views)
(def handler (wrap-base-url (server/gen-handler {:mode :prod, :ns 'log-viewer})))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode :ns 'log-viewer})))

;(-main)