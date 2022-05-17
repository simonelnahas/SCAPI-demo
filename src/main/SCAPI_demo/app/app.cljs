(ns SCAPI-demo.app.app
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]]
            [clojure.string :refer [blank?]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.skeleton :refer [skeleton]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent.core :as r]))

(def spreadsheetID "15NPv2oqI0Rjb63vRc3mFDdMrJdJ7UWdy3ExxA9gwcK8")
;; (def dev-api "http://localhost:5001/spreadsheet-model-api/us-central1/app/api/")
(def prod-api "https://sprdsht.to/api/")


(defn calculate-break-even [yearly-energy-usage response]
  (reset! response {:status :loading})
  (go (let [r (<! (http/get (str prod-api spreadsheetID)
                            {:with-credentials? false
                             :query-params {"yearly-energy-usage" yearly-energy-usage}}))]
        (reset! response {:status (if (:success r) :success :error)
                          :results (get-in r [:body :results])}))))

(defn display-response [response label unit value]
  [:div
   [:p label]
   (case (:status response)
     :success [:h1 (get-in response [:results value]) " " unit]
     :error [:h1 "error: " (get-in response [:error])]
     :loading [:h1 [skeleton]]
     [:h1 "0 " unit])])

(defn invalid-number? [n]
  (js/isNaN (js/parseInt n)))

(defn calculator []
  (let [response (r/atom nil)
        input (r/atom nil)]
    (fn []
      [:div.calculator
       [:div.kwh-input
        [text-field {:value @input
                     :on-change #(reset! input (.. % -target -value))
                     :error  (and (not (blank? @input)) (invalid-number? @input))
                     :style {:width "400px"}
                     :id :yearly-energy-usage
                     :label "Expected Yearly Electricity Usage"}]
        [:h3 "kWh"]]
       [button {:variant :outlined
                :disabled (invalid-number? @input)
                :on-click #(calculate-break-even @input response)}
        "Calculate"]
       [display-response @response "Payback period:" "years" :break-even-year]
       [display-response @response "Investment:" "DKK" :investment]])))


(defn app []
  [:div.app
   [:h1.company-title "HouseEnergy"]
   [:h2 "Solar Cell Payback Period Calculator"]
   [:div.container
    [calculator]]])