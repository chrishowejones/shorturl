(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [promesa.core :as p]
            ["react-dom/client" :as rdom]))

;; define components using the `defnc` macro

(defnc app []
  (let [[state set-state] (hooks/use-state {:url ""})
        fetch-slug (fn []
                     (p/let [response (js/fetch "https://httpbin.org/uuid")
                             json-response (.json response)]
                       (println json-response)))]
    (d/div
     (d/h1 "Enter short URL:")
      ;; create elements out of components
      (d/input {:value (:url state)
                :on-change #(set-state assoc :url (.. % -target -value))})
      (d/button {:on-click #(fetch-slug)} "Shorten URL"))))

;; start your app with your favorite React renderer
(defonce root (rdom/createRoot (js/document.getElementById "app")))


(defn ^:export init []
  (.render root ($ app)))
