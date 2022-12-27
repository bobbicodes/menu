(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [goog.object :as o]))

(defonce file-atom (r/atom nil))

(defn import-image []
  [:div
   [:h1 "Import image"]
   [:input#input
    {:type      "file"
     :on-change
     (fn [e]
       (let [dom    (o/get e "target")
             file   (o/getValueByKeys dom #js ["files" 0])
             reader (js/FileReader.)]
         (println file)
         (.readAsDataURL reader file)
         (set! (.-onload reader)
               #(reset! file-atom (-> % .-target .-result)))))}]])

(defn app []
  [:div#app
   [import-image]
   [:img {:src   @file-atom
          :width 400}]])

(defn render []
  (rdom/render [app]
            (.getElementById js/document "root")))

(defn ^:dev/after-load start []
  (render)
  (js/console.log "start"))

(defn ^:export init []
  (js/console.log "init")
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "stop"))
