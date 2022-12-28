(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [goog.object :as o]))

(defonce !files (r/atom []))

(defn import-images []
  [:div
   [:h1 "Import images"]
   [:input#input
    {:type      "file"
     :multiple true
     :on-change
     (fn [e]
       (let [dom    (o/get e "target")
             files (o/get dom "files")]
         (doseq [file files]
           (let [reader (js/FileReader.)]
             (.readAsDataURL reader file)
             (set! (.-onload reader)
                   #(swap! !files conj (-> % .-target .-result)))))))}]])

(defn app []
  [:div#app
   [import-images]
   (into [:div#images]
         (for [file @!files]
           [:img {:src   file
                  :width 400}]))])

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
