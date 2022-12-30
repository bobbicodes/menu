(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [goog.object :as o]))

(defonce !files (r/atom []))
(defonce !bg (r/atom nil))
(defonce posX (r/atom 0))
(defonce dom-node (r/atom nil))
(def w (.-innerWidth js/window))
(def h (.-innerHeight js/window))
(def image (js/Image.))

(defn draw []
  (let [canvas (.-firstChild @dom-node)
        ctx    (.getContext canvas "2d")]
    (set! (.-width canvas) w)
    (set! (.-height canvas) h)
    (.fillRect ctx (- (/ w 2)) (- (/ h 2)) w h)
    (.drawImage ctx image 0 0 
                (.-width image) (.-height image)
                @posX -74 
                (/ (.-width image) 2) (/ (.-height image) 2))
    (.requestAnimationFrame js/window draw)))

(defn draw-canvas-contents [canvas]
    (set! (.-src image) (first @!files))
    (set! (.-onload image) draw))

(defn div-with-canvas []
    (r/create-class
     {:component-did-update
      (fn [this]
        (draw-canvas-contents (.-firstChild @dom-node)))

      :component-did-mount
      (fn [this]
        (reset! dom-node (rdom/dom-node this)))

      :reagent-render
      (fn []
        [:div.with-canvas
         [:canvas (if-let [node @dom-node]
                    {:width w
                     :height h})]])}))

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
                 :width 400 :height 400
                 }])))

(defn app []
   [:div#app
     {:style {:background-image (str "url(" @!bg ")")}}
    [import-bg]
    [import-images]
    
    [images @!files]
    [div-with-canvas]])

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
