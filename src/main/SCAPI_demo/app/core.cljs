(ns SCAPI-demo.app.core
  (:require [reagent.dom :as rdom]
            [SCAPI-demo.app.app :refer [app]]))

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
