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
  (let [rows (.ceil js/Math (.sqrt js/Math n))
        cols (.ceil js/Math (/ n rows))]
    (nth (cycle (range 1 (inc cols))) (dec x))))

(defn y-pos [x n]
  (let [rows (.ceil js/Math (.sqrt js/Math n))
        cols (.ceil js/Math (/ n rows))]
(nth (mapcat #(repeat (inc cols) %) (range 1 (inc x))) x)))

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
