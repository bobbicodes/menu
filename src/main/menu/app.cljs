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
(defn img
  "Takes an image url and returns a js Image object
   with its `src` property set to it."
  [url]
  (let [i (js/Image.)]
    (set! (.-src (js/Image.)) url)
    i))


(defn draw 
  "Takes an HTMLImageElement object and 
   draws it to the canvas. Returns the same object."
  [img]
  (let [canvas (.getElementById js/document "canvas")
        ctx (.getContext canvas "2d")
        _ (set! (.-onload img) (.drawImage ctx img 0 0))
        _ (.drawImage ctx img 0 0)]
    img))

(defn canvasData 
  "Takes an HTMLCanvasElement and returns an ImageData object, 
   uInt8Clamped array of RGBA values"
  [canvas]
  (.getImageData (.getContext canvas "2d") 0 0 
                 (.-width canvas) (.-height canvas)))

(defn imageData
  "Takes an HTMLCanvasElement and returns an ImageData object, 
   uInt8Clamped array of RGBA values"
  [canvas]
  (.getImageData (.getContext canvas "2d") 0 0
                 (.-width canvas) (.-height canvas)))

(defn chroma [data r g b similarity]
  (doseq [i (filter #(and
                      (< (- r similarity) (aget data (- % 3)) (+ similarity r))
                      (< (- g similarity) (aget data (- % 2)) (+ similarity g))
                      (< (- b similarity) (aget data (- % 1)) (+ similarity b)))
                    (range 3 (.-length data) 4))]
    (aset data i 0))
  data)

(defn chromakey [url r g b similarity]
  (let [img (js/Image.)
        _ (set! (.-src img) url)
        canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        _ (set! (.-onload img) (.drawImage ctx img 0 0))
        _  (.drawImage ctx img 0 0)
        imageData (.getImageData ctx 0 0 (.-width canvas) (.-height canvas))
        data (.-data imageData)]
    (chroma data r g b similarity)
    (.putImageData ctx imageData 0 0)
    imageData))

;(chromakey (first @!files) 255 255 255 15)

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
