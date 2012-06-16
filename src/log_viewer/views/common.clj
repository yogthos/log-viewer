(ns log-viewer.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "log-viewer"]
               (include-css "css/reset.css")
               (include-js "js/jquery.js"
                           "js/site.js")]
              [:body
               [:div#wrapper
                content]]))
