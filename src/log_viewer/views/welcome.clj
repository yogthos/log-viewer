(ns log-viewer.views.welcome
 (:require [log-viewer.views.common :as common]
           [noir.content.getting-started])
 (:use clj-log.core noir.core [hiccup core page form])
 (:import [java.util Calendar Date]))

(def log-file "../clj-log/test.log")

(declare display-message)

(defn display-map [m]
 (into [:table.map] (for [[k v] m] [:tr [:td (name k)] [:td (display-message v)]])))

(defn display-coll [xs]
 (into [:table] (for [x xs] [:tr [:td (display-message x)]])))

(defn display-message [message]
 (cond
   (map? message) (display-map message)
   (coll? message) (display-coll message)
   :else message))

(defn display-exception [ex]
  [:div.collapsable "Exception trace (click to toggle)" 
   [:div.collapsed (display-message ex)]])

(defn render-logs [& [filter]]
  (into
      [:table.logs [:tr [:th "level"] [:th "message"] [:th "time"]]]
      (for [[i log] (map-indexed vector (reverse (if filter (read-log log-file filter) (read-log log-file))))]
        (let [{:keys [ns time message level pattern exception]} log
              row-class (if (even? i) "even" "odd")
              message-td [:td (display-message message)]]
          [:tr {:class row-class} 
           [:td {:class level} level] 
           (if exception (conj message-td (display-exception exception)) message-td) 
           [:td time]]))))


(defn log-filter-form [filter-time filter-text filter-level]
 (form-to [:post "/"]
          [:table
           [:tr
            [:td "log severity"]
            [:td (drop-down "severity-selector" ["trace" "debug" "info" "warn" "error" "fatal" "all"] (or filter-level "all"))]]
           [:tr
            [:td (label "filter-text" "message filter")]
            [:td (text-field "filter-text" filter-text)]]
           [:tr
            [:td (label "filter-time" "logs after")]
            [:td (drop-down "filter-time" ["1 day" "2 days" "5 days" "all"] (or filter-time "all"))]]]

   (submit-button "view logs")))


(defn get-date [days]
 (let [cal (Calendar/getInstance)]
   (if (= "all" days)
     nil
     (.getTime
       (doto cal
         (.setTime (new Date))
         (.set Calendar/DAY_OF_YEAR
           (- (.get cal Calendar/DAY_OF_YEAR)
              (Integer/parseInt (.substring days 0 1)))))))))


(defpage "/" {filter-time :filter-time filter-text :filter-text filter-level :severity-selector}
 (common/layout
   [:h2 "Log: " log-file]
   (log-filter-form filter-time filter-text filter-level)
   (let [date (when filter-time (get-date filter-time))]
     (render-logs
       (fn [item]
         (let [{:keys [level message time]} item]
           
           (and
             (if (and filter-level (not= "all" filter-level))
               (= filter-level (name level)) true)
             (if (and filter-text (not-empty filter-text))
               (.contains (.toLowerCase (.toString message)) (.toLowerCase filter-text)) true)
             (if date (.after time date) true))))))))

(defpage [:post "/"] params (render "/" params))