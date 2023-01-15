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
              #(do (reset! !bg (-> % .-target .-result))
                   (set! (.. js/document -body -style -backgroundImage) (str "url(" (-> % .-target .-result) ")"))))))))

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
                 :width 200 :height 200}])))

(defn chroma [data r g b similarity]
  (doseq [i (filter #(and
                      (< (- r similarity) (aget data (- % 3)) (+ similarity r))
                      (< (- g similarity) (aget data (- % 2)) (+ similarity g))
                      (< (- b similarity) (aget data (- % 1)) (+ similarity b)))
                    (range 3 (.-length data) 4))]
    (aset data i 0))
  data)

(defn x-pos [x n]
  (let [cols (.ceil js/Math (.sqrt js/Math n))]
    (nth (cycle (range 1 (inc cols))) (dec x))))

(defn y-pos [x n]
  (let [cols (.ceil js/Math (.sqrt js/Math n))]
    (nth (mapcat #(repeat cols %) (range 1 (inc x))) x)))

(defn render-image [url n]
  (let [img (js/Image.)
        _ (set! (.-src img) url)
        canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        scale 0.125
        width (* (.-width img) scale)
        height (* (.-height img) scale)
        x (* width (dec (x-pos n (count @!files))))
        y (- (* height (y-pos n (count @!files))) height)
        _ (set! (.-onload img) (.drawImage ctx img x y width height))
        imageData (.getImageData ctx 0 0 (.-width canvas) (.-height canvas))
        data (.-data imageData)]
    (chroma data 255 255 255 15)
    (.putImageData ctx imageData 0 0)
    {:image-num n
     :scale scale
     :width width
     :height height
     :x x :y y
     :pos [(x-pos n (count @!files)) (y-pos n (count @!files))]}))

(comment
  (doseq [n (range 1 (count @!files))]
    (render-image (nth @!files n) n))

  (doseq [n (range 1 (count @!files))]
    (render-image (first @!files) n))
  )

(defn dimensions [url]
  (let [img (js/Image.)
        _ (set! (.-src img) url)]
    [(.-width img)
     (.-height img)]))

 (defn svg-width [el]
   (.-width (.getBBox (.getElementById js/document el))))

 (defn svg-height [el]
   (.-height (.getBBox (.getElementById js/document el))))

(defn logo [x]
   [:g [:rect {:x x :y 40
               :width 500 :height 500
               :rx 25 :fill "#c9d3dd90"}]
    [:image {:href "img\\happy-hemp-trans.svg"
             :width 500 :x x}]])

 (defn app []
   [:div#app
    {:style {:background-image (str "url(" @!bg ")")}}
    [import-bg]
    [import-images]
    [images @!files]
    [(fn []
        [:textarea {:rows      10
                    :cols      48
                    :value     (str (into [] (map dimensions @!files)))}])]
    [:svg {:width    "100%"
           :view-box "0 0 3840 2160"}
     [:image {:href "img\\tinctures\\bg-tinctures.png"}]
     [logo 0]
     [logo 3300]
     [:rect {:x      (- (/ 3840 2) (/ (svg-width "cbdtinturesgreen") 2)) 
             :y      40 
             :width  (svg-width "cbdtinturesgreen") 
             :height (svg-height "cbdtinturesgreen") 
             :rx     25
             :fill   "#c9d3dd90"}]
     [:text#cbdtinturesgreen 
      {:x           (- (/ 3840 2) (/ (svg-width "cbdtinturesgreen") 2))
       :y           280
       :font-family "Pacifico"
       :fill        "green"
       :font-size   192}
      "CBD Tinctures"]]])
 
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
