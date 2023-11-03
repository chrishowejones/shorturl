(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [promesa.core :as p]
            ["react-dom/client" :as rdom]
            [clojure.string :as str]))

;; define components using the `defnc` macro

(defnc app []
  (let [[state set-state] (hooks/use-state {:slug ""
                                            :url ""})
        fetch-slug (fn []
                     (p/let [response (js/fetch "/api/redirect/"
                                                (clj->js {:headers {:Content-Type "application/json"}
                                                          :method "POST"
                                                          :body (js/JSON.stringify #js {:url (:url state)})}))
                             json-response (.json response)
                             data (js->clj json-response :keywordize-keys true)]
                       (set-state assoc :slug (:slug data))))
        redirect-link (str (.-origin js/location) "/" (:slug state) "/")]
    (d/div {:class-name "bg-pink-100 grid place-items-center h-screen"}
     (if (and (:slug state) (not= "" (:slug state)))
       (d/div (d/a {:href redirect-link} redirect-link))
       (d/div
        (d/div
         {:class-name "py-5"}
         (d/h1 {:class-name "text-3xl font-bold underline"} "URL Shortcut"))
        (d/div
         {:class-name "columns-2"}
         ;; create elements out of components
         (d/input {:value (:url state)
                   :on-change #(set-state assoc :url (.. % -target -value))
                   :class-name "form-control border border-solid border-gray-600"
                   :placeholder "Enter URL"})
         (d/button {:on-click #(fetch-slug)
                    :class-name "border-2 rounded px-4 uppercase"}
                   "Shorten URL")))))))

;; start your app with your favorite React renderer
(defonce root (rdom/createRoot (js/document.getElementById "app")))


(defn ^:export init []
  (.render root ($ app)))
