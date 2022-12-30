(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [goog.object :as o]))

(defonce !files (r/atom []))
(defonce !bg (r/atom nil))

(defn upload-images [e]
    (let [dom    (o/get e "target")
          files (o/get dom "files")]
      (reset! !files [])
      (doseq [file files]
        (let [reader (js/FileReader.)]
          (.readAsDataURL reader file)
          (set! (.-onload reader)
                #(swap! !files conj (-> % .-target .-result)))))))

(defn upload-bg [e]
  (let [dom    (o/get e "target")
        files (o/get dom "files")]
    (reset! !files [])
    (doseq [file files]
      (let [reader (js/FileReader.)]
        (.readAsDataURL reader file)
        (set! (.-onload reader)
              #(reset! !bg (-> % .-target .-result)))))))

(defn import-bg []
  [:div
   [:h1 "Select background"]
   [:input#bg
    {:type      "file"
     :on-change upload-bg}]])

(defn import-images []
  [:div
   [:h1 "Import images"]
   [:input#input
    {:type      "file"
     :multiple true
     :on-change upload-images}]])

(defn images [files]
  (into [:div.flex-container]
        (for [file files]
          [:img.flex-item {:src   file
                 :width 200 :height 200
                 }])))

(defn app []
   [:div#app
     {:style {:background-image (str "url(" @!bg ")")}}
    [import-bg]
    [import-images]
    [images @!files]])

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
