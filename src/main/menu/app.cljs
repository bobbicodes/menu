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

(defn logo [x]
   [:g 
    #_[:rect {:x x :y 20
               :width 530 :height 500
               :rx 25 :fill "#B0AFAB90"}]
       [:image {:href "img\\happy-hemp-trans.svg"
                :x (+ x 20)
                :y 30
                :width 500}]])
 
(defn svg-width [id]
  (.-width (.getBBox (.getElementById js/document id))))

(defn svg-height [id]
  (.-height (.getBBox (.getElementById js/document id))))

(def label-width (r/atom 1000))
(def label-height (r/atom 100))

(defonce label-str (r/atom "CBD Tinctures"))

(defn label [s]
  [:g
   [:rect {:x      (- (/ 3840 2) (/ @label-width 1.9))
           :y      (/ @label-height 10)
           :width  (* 1.05 @label-width)
           :height (* @label-height 0.85)
           :rx     65
           :fill   "#977F4760"}]
   [:text#label
    {:x           (- (/ 3840 2) (/ @label-width 2))
     :y           (* @label-height 0.75)
     :font-weight 1000
     :font-family "Brush Script MT"
     :fill        "white"
     :font-size   256}
    s]
   [:text
    {:x           (- (/ 3840 2) (/ @label-width 2))
     :y           (* @label-height 0.75)
     :font-family "Brush Script MT"
     :fill        "green"
     :font-size   256}
    s]])

 (defn svg-string [el]
  (let [serializer (js/XMLSerializer.)]
    (.serializeToString serializer (.getElementById js/document el))))

(defn draw [url composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        img    (js/Image.)
        _      (set! (.-src img) url)
        _      (set! (.-onload img)
                     #(do (set! (.-globalCompositeOperation ctx) composite)
                          (.drawImage ctx img 0 0)))]))

(defn draw-logo [url composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        img    (js/Image.)
        _      (set! (.-src img) url)
        _      (set! (.-onload img)
                     #(do (set! (.-globalCompositeOperation ctx) composite)
                          (.drawImage ctx img 0 0 500 500)
                          (.drawImage ctx img 3300 0 500 500)
                          (.drawImage ctx img 0 1650 500 500)
                          (.drawImage ctx img 3300 1650 500 500)))]))

(defn render-canvas [bg composite]
  (let [canvas   (.getElementById js/document "canvas")
        ctx      (.getContext canvas "2d")
        svg      (js/Blob. [(svg-string "svg-bg")] (clj->js {:type "image/svg+xml;charset=utf-8"}))
        url      (js/URL.createObjectURL svg)
        img      (js/Image.)
        _ (.clearRect ctx 0 0 3840 2160)
        _ (draw bg "source-over")
        _ (draw-logo "img\\happy-hemp-trans.svg" composite)
        _        (set! (.-src img) url)
        _        (set! (.-onload img)
                       #(do (set! (.-globalCompositeOperation ctx) "source-over") 
                          (.drawImage ctx img 0 0)))]
    url))

(comment  
  (reset! label-str "CBD Tinctures")
  (reset! !bg "img\\flower\\bg-flower.png")
  (reset! !bg "img\\tinctures\\bg-tinctures.png")
   (reset! !bg "img\\topicals\\bg-topicals.png")
  )
 
 (defn app []
   [:div#app
    {:style {:background-image (str "url(" @!bg ")")}}
    [:svg#svg-bg {:xmlns "http://www.w3.org/2000/svg"
                  :width    "100%"
              :view-box "0 0 3840 2160"
              :on-load (fn [_] 
                         (reset! label-width (svg-width "label"))
                         (reset! label-height (svg-height "label")))}
     [:image {:href @!bg}]
     [logo 0]
     [logo 3300]
     [label @label-str]]
    [render-canvas @!bg "source-over"]
    ])



(defn download-blob [file-name blob]
  (let [object-url (js/URL.createObjectURL blob)
        anchor-element
        (doto (js/document.createElement "a")
          (-> .-href (set! object-url))
          (-> .-download (set! file-name)))]
    (.appendChild (.-body js/document) anchor-element)
    (.click anchor-element)
    (.removeChild (.-body js/document) anchor-element)
    (js/URL.revokeObjectURL object-url)))

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
