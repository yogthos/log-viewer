(ns log-viewer.views.helpers
  (:use [hiccup core page form] clj-log.core)
  (:require [noir.session :as session])
  (:import [java.util Calendar Date]))

(def log-file "../clj-log/test.log")
(def max-logs 1000)
(def logs-per-page 50)


(declare display-message)

(defn display-map [m]
  (into [:table.map] (for [[k v] m] [:tr [:td (when k (name k))] [:td (display-message v)]])))

(defn display-coll [xs]
 (into [:table] (for [x xs] [:tr [:td (display-message x)]])))

(defn display-exception [ex]
  [:div.collapsable "Exception trace (click to toggle)" 
   [:div.collapsed (display-message ex)]])

(defn display-message [message]
 (cond
   (map? message) (display-map message)
   (coll? message) (display-coll message)
   :else message))


(defn nav-form [direction]
  (form-to [:post "/"]
           (hidden-field "nav" (name direction))
           (submit-button (if (= :forward direction) ">" "<"))))


(defn position-in-logs []
  (let [log-count (count (session/get :logs))
        position (inc (session/get :position))
        results-per-page (+ position (dec logs-per-page))]
    (str "displaying " position " - " (if (> results-per-page log-count) log-count results-per-page) " of " log-count)))


(defn render-logs []   
  (into
    [:table.logs
     [:tr
      [:td  {:colspan "3"} 
       [:div.left  (nav-form :backward)] 
       [:div.left  (position-in-logs)] 
       [:div.right  (nav-form :forward)]]]
     [:tr [:th.level-sort "level"] [:th.time-sort "time"] [:th "message"]]
     ]
    (for [[i log] (session/get :cur-view)]
      (let [{:keys [ns time message level pattern exception]} log
            row-class (if (even? i) "even" "odd")
            message-td [:td (display-message message)]]
        [:tr {:class row-class}         
         [:td {:class (str "level " (name level))} level]
         [:td.time time]
         (if exception (conj message-td (display-exception exception)) message-td)]))))


(defn log-filter-form [filter-time filter-text filter-level]
  (form-to [:post "/"]
           [:table
            [:tr
             [:td "log severity"]
             [:td (drop-down "filter-level" ["trace" "debug" "info" "warn" "error" "fatal" "all"] (or filter-level "all"))]]
            [:tr
             [:td (label "filter-text" "message filter")]
             [:td (text-field "filter-text" filter-text)]]
            [:tr
             [:td (label "filter-time" "logs after")]
             [:td (drop-down "filter-time" ["1 day" "2 days" "5 days" "all"] (or filter-time "all"))]]]
           
           (hidden-field "reload" "true")
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


(defn navigate [{direction :nav}]
  (when direction
    (let [logs (session/get :logs)] 
      (if (> (count logs) logs-per-page)
        (let [logs-size (count logs)
              pos (session/get :position)
              offset (condp = direction
                       "start" 0
                       "backward" (- pos logs-per-page)
                       "forward"  (+ pos logs-per-page))          
              range-start (cond (< offset 0) 0                        
                                (>= offset logs-size) (dec logs-size)                             
                                :else offset)
              range-end (+ range-start logs-per-page)]          
          (session/put! :cur-view (subvec logs range-start (if (>= range-end logs-size) logs-size range-end)))
          (session/put! :position range-start))
        (session/put! :cur-view logs)))))


(defn log-filter [params]  
  (fn [item]
    (let [{:keys [filter-time filter-text filter-level]} params
          date (when filter-time (get-date filter-time))
          {:keys [level message time]} item]   
      (and
        (if (and filter-level (not= "all" filter-level))
          (= filter-level (name level)) true)
        (if (and filter-text (not-empty filter-text))
          (.contains (.toLowerCase (.toString message)) (.toLowerCase filter-text)) true)
        (if date (.after time date) true)))))


(defn load-logs [params]
  (session/put! :position 0)
  (session/put! :logs (vec (map-indexed vector (read-log log-file (log-filter params) max-logs))))
  (navigate {:nav "start"}))

