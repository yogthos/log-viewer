(ns log-viewer.views.welcome
 (:require [log-viewer.views.common :as common]
           [noir.content.getting-started]
           [noir.session :as session])
 (:use log-viewer.views.helpers noir.core [hiccup core page form])
 )


(defpage "/" params
  (when (nil? (session/get :cur-view))
    (load-logs params))
  (common/layout
    [:h2 "Log: " log-file " up to " max-logs " results"]
    (log-filter-form (:filter-time params) (:filter-text params) (:filter-level params))
    (render-logs)))


(defpage [:post "/"] params  
  (navigate params)    
  (when (:reload params) (load-logs params))  
  (render "/" params))

